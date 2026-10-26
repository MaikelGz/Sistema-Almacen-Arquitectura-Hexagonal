package com.acme.inventory.infrastructure.mongo.adapter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.acme.inventory.domain.models.InventoryItem;
import com.acme.inventory.domain.models.Movement;
import com.acme.inventory.domain.ports.out.InventoryRepository;
import com.acme.inventory.infrastructure.mappers.InventoryMongoMapper;
import com.acme.inventory.infrastructure.mongo.documents.InventoryItemDocument;
import com.acme.inventory.infrastructure.mongo.documents.MovementDocument;
import com.acme.inventory.domain.utils.IdUtils;
import com.acme.inventory.domain.utils.Logger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

// For $expr support
import org.bson.Document;

@Repository
public class MongoInventoryRepository implements InventoryRepository {

    private final MongoTemplate mongo;
    // desacoplamos mediante el mapper, buena practica
    private final InventoryMongoMapper mapper;

    public MongoInventoryRepository(MongoTemplate mongo, InventoryMongoMapper mapper) {
        this.mongo = mongo;
        this.mapper = mapper;
    }

    @Override
    public InventoryItem save(InventoryItem item) {
        Logger.info("Guardando InventoryItem: SKU=" + item.getSku() + ", warehouse=" + item.getWarehouse());
        InventoryItemDocument savedDoc = mongo.save(mapper.toDocument(item), "items");
        return mapper.toDomain(savedDoc);
    }

    @Override
    public Movement save(Movement movement) {
        Logger.info("Guardando Movement: SKU=" + movement.getSku() + ", warehouse=" + movement.getWarehouse()
                + ", tipo=" + movement.getType());
        MovementDocument savedDoc = mongo.save(mapper.toDocument(movement), "movements");
        return mapper.toDomain(savedDoc);
    }

    @Override
    public Optional<InventoryItem> findBy(String sku, String warehouse) {
        InventoryItemDocument doc = mongo.findById(IdUtils.idOf(sku, warehouse), InventoryItemDocument.class);
        return Optional.ofNullable(doc).map(mapper::toDomain);
    }

    @Override
    public List<InventoryItem> findAll() {
        Logger.info("Buscando todos los InventoryItems");
        List<InventoryItemDocument> docs = mongo.findAll(InventoryItemDocument.class, "items");
        Logger.debug("Total items encontrados: " + docs.size());
        return docs.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String sku, String warehouse) {
        Logger.info("Intentando eliminar InventoryItem: SKU=" + sku + ", warehouse=" + warehouse);
        Query query = new Query(
                where("_id").is(IdUtils.idOf(sku, warehouse))
                        .and("onHand").is(0)
                        .and("reserved").is(0));
        var result = mongo.remove(query, InventoryItemDocument.class, "items");
        if (result.getDeletedCount() > 0) {
            Logger.info("Item eliminado correctamente: SKU=" + sku + ", warehouse=" + warehouse);
            return true;
        } else {
            Logger.info("No se eliminó el item. Puede que no exista o que onHand/reserved sean distintos de 0: SKU="
                    + sku + ", warehouse=" + warehouse);
            return false;
        }
    }

    @Override
    public Page<InventoryItem> search(String warehouse, String skuPrefix, Pageable p) {
        Logger.info("Buscando InventoryItems por warehouse=" + warehouse + ", skuPrefix=" + skuPrefix + ", page="
                + p.getPageNumber());
        Query query = new Query();
        if (warehouse != null && !warehouse.isBlank()) {
            query.addCriteria(where("warehouse").is(warehouse));
        }
        if (skuPrefix != null && !skuPrefix.isBlank()) {
            query.addCriteria(where("sku").regex("^" + skuPrefix));// usa la er ^abc, todo lo q empiece x abc
        }

        long total = mongo.count(query, InventoryItemDocument.class);
        query.with(p);// devolvera los elementos correspondientes a la página solicitada.
        List<InventoryItemDocument> docs = mongo.find(query, InventoryItemDocument.class);
        Logger.debug("Total encontrados en búsqueda: " + docs.size() + " de " + total);
        List<InventoryItem> content = docs.stream().map(mapper::toDomain).toList();
        return new PageImpl<>(content, p, total);
    }

    @Override
    public boolean applyMovement(Movement m) {
        Logger.info("Aplicando movimiento: SKU=" + m.getSku() + ", warehouse=" + m.getWarehouse() + ", tipo="
                + m.getType() + ", cantidad=" + m.getQuantity());

        Query query;
        Update update = new Update().set("updatedAt", Instant.now());

        switch (m.getType()) {
            case "IN":
                query = new Query(where("_id").is(IdUtils.idOf(m.getSku(), m.getWarehouse())));
                update.inc("onHand", m.getQuantity());
                break;
            case "OUT":
                query = new Query(where("_id").is(IdUtils.idOf(m.getSku(), m.getWarehouse())));
                query.addCriteria(Criteria.where("$expr").is(
                        new Document("$gte", List.of(
                                new Document("$subtract", List.of("$onHand", "$reserved")),
                                m.getQuantity()
                        ))
                ));
                update.inc("onHand", -m.getQuantity());
                break;
            case "RESERVATION":
                query = new Query(where("_id").is(IdUtils.idOf(m.getSku(), m.getWarehouse()))
                        .and("onHand").gte(m.getQuantity())
                        .and("reserved").gte(0));
                update.inc("reserved", m.getQuantity());
                update.inc("onHand", -m.getQuantity());
                break;
            case "RELEASE":
                query = new Query(where("_id").is(IdUtils.idOf(m.getSku(), m.getWarehouse()))
                        .and("reserved").gte(m.getQuantity()));
                update.inc("reserved", -m.getQuantity());
                update.inc("onHand", m.getQuantity());
                break;
            default:
                Logger.info("Tipo de movimiento no soportado: " + m.getType());
                throw new IllegalArgumentException("Tipo de movimiento no soportado: " + m.getType());
        }

        var result = mongo.updateFirst(query, update, InventoryItemDocument.class, "items");
        if (result.getModifiedCount() == 0) {
            Logger.info("No se pudo aplicar el movimiento (condiciones no cumplidas o item no existe): SKU="
                    + m.getSku() + ", warehouse=" + m.getWarehouse());
            return false;
        }

        mongo.insert(mapper.toDocument(m), "movements");
        Logger.debug("Movimiento insertado: " + m);

        return true;
    }

    // TODO: Cuidado con esto, para pruebas cada vez q se ejecuta (data initializer)
    @Override
    public void deleteAll() {
        Logger.info("Eliminando todos los InventoryItems y Movements");
        mongo.remove(new Query(), InventoryItemDocument.class);
        mongo.remove(new Query(), MovementDocument.class);
    }

    @Override
    public List<Movement> findAllMovements() {
        Logger.info("Buscando todos los Movements");
        List<MovementDocument> docs = mongo.findAll(MovementDocument.class, "movements");
        Logger.debug("Total movimientos encontrados: " + docs.size());
        return docs.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<InventoryItem> update(String sku, String warehouse, InventoryItem item) {
        Logger.info("Actualizando InventoryItem: SKU=" + sku + ", warehouse=" + warehouse);
        Query query = new Query(where("_id").is(IdUtils.idOf(sku, warehouse)));
        InventoryItemDocument existingDoc = mongo.findOne(query, InventoryItemDocument.class, "items");
        if (existingDoc == null) {
            Logger.info("No se encontró el item para actualizar: SKU=" + sku + ", warehouse=" + warehouse);
            return Optional.empty();
        }

        Update update = new Update();
        update.set("onHand", item.getOnHand());
        update.set("reserved", item.getReserved());
        update.set("updatedAt", Instant.now());

        mongo.updateFirst(query, update, InventoryItemDocument.class, "items");

        InventoryItemDocument updatedDoc = mongo.findOne(query, InventoryItemDocument.class, "items");
        Logger.debug("Item tras actualización: " + updatedDoc);
        return Optional.ofNullable(mapper.toDomain(updatedDoc));
    }

}

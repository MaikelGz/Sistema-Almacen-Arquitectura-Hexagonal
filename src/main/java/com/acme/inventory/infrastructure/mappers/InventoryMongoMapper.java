package com.acme.inventory.infrastructure.mappers;

import com.acme.inventory.domain.models.AuditInfo;
import com.acme.inventory.domain.models.InventoryItem;
import com.acme.inventory.domain.models.Movement;
import com.acme.inventory.infrastructure.mongo.documents.InventoryItemDocument;
import com.acme.inventory.infrastructure.mongo.documents.MovementDocument;
import com.acme.inventory.domain.utils.IdUtils;
import org.mapstruct.*;
import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = { Instant.class, IdUtils.class })
public interface InventoryMongoMapper {

    // warehouse,sku, no hace falta mapearlas, ya que son iguales
    @Mapping(target = "id", expression = "java(IdUtils.idOf(item.getSku(), item.getWarehouse()))")
    @Mapping(target = "updatedAt", expression = "java(item.getUpdatedAt() != null ? item.getUpdatedAt() : Instant.now())")
    @Mapping(target = "audit", source = "audit", qualifiedByName = "toEmbedded")
    @Mapping(target = "version", ignore = true)
    InventoryItemDocument toDocument(InventoryItem item);

    @Mapping(target = "audit", source = "audit", qualifiedByName = "toAuditInfo")
    InventoryItem toDomain(InventoryItemDocument doc);

    @Mapping(target = "id", expression = "java(generateMovementId())")
    MovementDocument toDocument(Movement movement);

    Movement toDomain(MovementDocument doc);

    @Named("toEmbedded")
    default InventoryItemDocument.AuditEmbedded toEmbedded(AuditInfo a) {
        if (a == null)
            return null;
        return new InventoryItemDocument.AuditEmbedded(a.getCreatedBy(), a.getCreatedAt());
    }

    @Named("toAuditInfo")
    default AuditInfo toAuditInfo(InventoryItemDocument.AuditEmbedded e) {
        if (e == null)
            return null;
        return new AuditInfo(e.getCreatedBy(), e.getCreatedAt());
    }

    default String generateMovementId() {
        return "MVT-" + UUID.randomUUID().toString();
    }
}

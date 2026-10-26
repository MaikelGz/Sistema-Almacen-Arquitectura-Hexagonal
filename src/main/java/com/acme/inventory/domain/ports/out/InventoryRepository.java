package com.acme.inventory.domain.ports.out;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.acme.inventory.domain.models.InventoryItem;
import com.acme.inventory.domain.models.Movement;

public interface InventoryRepository {
    Optional<InventoryItem> findBy(String sku, String warehouse);//busca x el sku y almacen
    
    InventoryItem save(InventoryItem item);
    Movement save(Movement movement1);

    Optional<InventoryItem> update(String sku, String warehouse, InventoryItem item);
    
    boolean delete(String sku, String warehouse);

    Page<InventoryItem> search(String warehouse, String skuPrefix, Pageable p);//busca items por almacen y prefijo sku
    
    boolean applyMovement(Movement m); 

    List<InventoryItem> findAll();
    List<Movement> findAllMovements();
    
    
    void deleteAll();
}

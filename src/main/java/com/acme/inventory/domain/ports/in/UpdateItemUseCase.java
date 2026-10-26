package com.acme.inventory.domain.ports.in;

import java.util.Optional;

import com.acme.inventory.domain.models.InventoryItem;

public interface UpdateItemUseCase {
    Optional<InventoryItem> updateItem(String sku, String warehouse, InventoryItem updatedItem);
}

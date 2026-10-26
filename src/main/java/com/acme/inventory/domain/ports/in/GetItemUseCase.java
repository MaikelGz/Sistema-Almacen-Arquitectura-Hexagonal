package com.acme.inventory.domain.ports.in;

import java.util.Optional;

import com.acme.inventory.domain.models.InventoryItem;

public interface GetItemUseCase {
    Optional<InventoryItem> getItem(String sku, String warehouse);
}

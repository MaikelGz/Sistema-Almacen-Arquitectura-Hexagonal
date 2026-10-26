package com.acme.inventory.domain.ports.in;

import com.acme.inventory.domain.models.InventoryItem;

public interface CreateItemUseCase {
    InventoryItem createItem(InventoryItem item);
}

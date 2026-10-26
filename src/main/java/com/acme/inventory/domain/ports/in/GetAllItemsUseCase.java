package com.acme.inventory.domain.ports.in;

import java.util.List;

import com.acme.inventory.domain.models.InventoryItem;

public interface GetAllItemsUseCase {
    List<InventoryItem> getAllItems();
}

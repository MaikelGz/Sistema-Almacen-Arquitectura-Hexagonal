package com.acme.inventory.application.usecases;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.acme.inventory.domain.models.InventoryItem;
import com.acme.inventory.domain.ports.in.UpdateItemUseCase;
import com.acme.inventory.domain.ports.out.InventoryRepository;

@Service
public class UpdateItemUseCaseImpl implements UpdateItemUseCase {

    private final InventoryRepository inventoryRepository;

    public UpdateItemUseCaseImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Optional<InventoryItem> updateItem(String sku, String warehouse,InventoryItem updatedItem) {
        return inventoryRepository.update(sku, warehouse,updatedItem);
    }

}

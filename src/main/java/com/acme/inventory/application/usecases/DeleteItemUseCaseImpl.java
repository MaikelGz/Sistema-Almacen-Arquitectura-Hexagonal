package com.acme.inventory.application.usecases;

import org.springframework.stereotype.Service;

import com.acme.inventory.domain.ports.in.DeleteItemUseCase;
import com.acme.inventory.domain.ports.out.InventoryRepository;

@Service
public class DeleteItemUseCaseImpl implements DeleteItemUseCase {

    private final InventoryRepository inventoryRepository;

    public DeleteItemUseCaseImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public boolean deleteItem(String sku, String warehouse) {
        return inventoryRepository.delete(sku, warehouse);
    }
}

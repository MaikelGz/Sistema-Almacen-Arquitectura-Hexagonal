package com.acme.inventory.application.usecases;

import org.springframework.stereotype.Service;

import com.acme.inventory.domain.models.InventoryItem;
import com.acme.inventory.domain.ports.in.CreateItemUseCase;
import com.acme.inventory.domain.ports.out.InventoryRepository;

@Service
public class CreateItemUseCaseImpl implements CreateItemUseCase {

    private final InventoryRepository inventoryRepository;

    public CreateItemUseCaseImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    
    @Override
    public InventoryItem createItem(InventoryItem item) {
        return inventoryRepository.save(item);
    }
}

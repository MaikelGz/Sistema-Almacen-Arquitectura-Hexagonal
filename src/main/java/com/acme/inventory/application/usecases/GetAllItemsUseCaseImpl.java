package com.acme.inventory.application.usecases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.acme.inventory.domain.models.InventoryItem;
import com.acme.inventory.domain.ports.in.GetAllItemsUseCase;
import com.acme.inventory.domain.ports.out.InventoryRepository;

@Service
public class GetAllItemsUseCaseImpl implements GetAllItemsUseCase {
    private final InventoryRepository inventoryRepository;

    public GetAllItemsUseCaseImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<InventoryItem> getAllItems() {
        return inventoryRepository.findAll();
    }

}

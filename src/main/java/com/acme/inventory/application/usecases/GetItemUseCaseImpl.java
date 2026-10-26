package com.acme.inventory.application.usecases;



import java.util.Optional;

import org.springframework.stereotype.Service;

import com.acme.inventory.domain.models.InventoryItem;
import com.acme.inventory.domain.ports.in.GetItemUseCase;
import com.acme.inventory.domain.ports.out.InventoryRepository;

@Service
public class GetItemUseCaseImpl implements GetItemUseCase {
    private final InventoryRepository inventoryRepository;

    public GetItemUseCaseImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Optional<InventoryItem> getItem(String sku, String warehouse) {
        return inventoryRepository.findBy(sku, warehouse);
    }

}

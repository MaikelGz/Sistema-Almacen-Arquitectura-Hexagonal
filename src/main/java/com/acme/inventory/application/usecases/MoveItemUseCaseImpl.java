package com.acme.inventory.application.usecases;

import org.springframework.stereotype.Service;

import com.acme.inventory.domain.models.Movement;
import com.acme.inventory.domain.ports.in.MoveItemUseCase;
import com.acme.inventory.domain.ports.out.InventoryRepository;

@Service
public class MoveItemUseCaseImpl implements MoveItemUseCase {

    private final InventoryRepository inventoryRepository;

    public MoveItemUseCaseImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public boolean moveItem(Movement movement) {
        return inventoryRepository.applyMovement(movement);

    }

}

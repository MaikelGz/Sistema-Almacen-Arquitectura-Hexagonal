package com.acme.inventory.application.usecases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.acme.inventory.domain.models.Movement;
import com.acme.inventory.domain.ports.in.GetAllMovementsUseCase;
import com.acme.inventory.domain.ports.out.InventoryRepository;

@Service
public class GetAllMovementsUseCaseImpl implements GetAllMovementsUseCase{
    private final InventoryRepository inventoryRepository;

    public GetAllMovementsUseCaseImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<Movement> getAllMovements() {
        return inventoryRepository.findAllMovements();
    }
}

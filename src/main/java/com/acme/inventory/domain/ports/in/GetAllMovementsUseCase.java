package com.acme.inventory.domain.ports.in;

import java.util.List;

import com.acme.inventory.domain.models.Movement;

public interface GetAllMovementsUseCase {
    List<Movement> getAllMovements();
}

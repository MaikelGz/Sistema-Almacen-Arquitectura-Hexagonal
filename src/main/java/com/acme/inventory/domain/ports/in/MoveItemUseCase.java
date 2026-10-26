package com.acme.inventory.domain.ports.in;

import com.acme.inventory.domain.models.Movement;

public interface MoveItemUseCase {
    boolean moveItem(Movement movement);
}

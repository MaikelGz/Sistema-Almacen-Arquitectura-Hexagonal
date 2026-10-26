package com.acme.inventory.application.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.acme.inventory.domain.models.InventoryItem;
import com.acme.inventory.domain.models.Movement;
import com.acme.inventory.domain.ports.in.CreateItemUseCase;
import com.acme.inventory.domain.ports.in.GetAllItemsUseCase;
import com.acme.inventory.domain.ports.in.GetAllMovementsUseCase;
import com.acme.inventory.domain.ports.in.GetItemUseCase;
import com.acme.inventory.domain.ports.in.MoveItemUseCase;
import com.acme.inventory.domain.ports.in.UpdateItemUseCase;
import com.acme.inventory.domain.ports.in.DeleteItemUseCase;
import com.acme.inventory.infrastructure.mappers.InventoryRestMapper;
import com.acme.inventory.infrastructure.rest.dto.InventoryItemDTO;
import com.acme.inventory.infrastructure.rest.dto.MovementDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final CreateItemUseCase createItemUseCase;
    private final GetAllItemsUseCase getAllItemsUseCase;    
    private final GetAllMovementsUseCase getAllMovementsUseCase;
    private final GetItemUseCase getItemUseCase;
    private final MoveItemUseCase moveItemUseCase;
    private final UpdateItemUseCase updateItemUseCase;
    private final DeleteItemUseCase deleteItemUseCase;
    private final InventoryRestMapper inventoryRestMapper;

    public InventoryItemDTO createItem(InventoryItemDTO itemDTO) {
        InventoryItem item = inventoryRestMapper.toDomain(itemDTO);
        InventoryItem created = createItemUseCase.createItem(item);
        return inventoryRestMapper.toDTO(created);
    }

    public Optional<InventoryItemDTO> getItem(String sku, String warehouse) {
        return getItemUseCase.getItem(sku, warehouse)
                .map(inventoryRestMapper::toDTO);
    }

    public Optional<InventoryItemDTO> updateItem(String sku, String warehouse, InventoryItemDTO updatedItemDTO) {
        InventoryItem updatedItem = inventoryRestMapper.toDomain(updatedItemDTO);
        return updateItemUseCase.updateItem(sku, warehouse, updatedItem)
                .map(inventoryRestMapper::toDTO);
    }

    public boolean moveItem(MovementDTO movementDTO) {
        Movement movement = inventoryRestMapper.toDomain(movementDTO);
        return moveItemUseCase.moveItem(movement);
    }

    public List<InventoryItemDTO> getAllItems() {
        return getAllItemsUseCase.getAllItems().stream()
                .map(inventoryRestMapper::toDTO)
                .toList();
    }

    public List<MovementDTO> getAllMovements() {
        return getAllMovementsUseCase.getAllMovements().stream()
                .map(inventoryRestMapper::toDTO)
                .toList();
    }

    public boolean deleteItem(String sku, String warehouse) {
        return deleteItemUseCase.deleteItem(sku, warehouse);
    }
}

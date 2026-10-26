package com.acme.inventory.infrastructure.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid;

import com.acme.inventory.application.services.InventoryService;
import com.acme.inventory.infrastructure.rest.dto.InventoryItemDTO;
import com.acme.inventory.infrastructure.rest.dto.MovementDTO;
import com.acme.inventory.domain.utils.Logger;

@RestController
@RequestMapping("/items")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // post para crear un item
    @PostMapping
    public ResponseEntity<InventoryItemDTO> createItem(@Valid @RequestBody InventoryItemDTO itemDTO) {
        Logger.info("POST /items - Creando item");
        Logger.debug("Payload recibido: " + itemDTO);
        InventoryItemDTO createdItemDTO = inventoryService.createItem(itemDTO);
        Logger.info(
                "Item creado con SKU: " + createdItemDTO.getSku() + ", warehouse: " + createdItemDTO.getWarehouse());
        return new ResponseEntity<>(createdItemDTO, HttpStatus.CREATED);
    }

    // get para obtener un item por sku y almacen
    @GetMapping("/{sku}/warehouse/{warehouse}")
    public ResponseEntity<InventoryItemDTO> getItem(@PathVariable String sku, @PathVariable String warehouse) {
        Logger.info("GET /items/" + sku + "/warehouse/" + warehouse + " - Consultando item");
        return inventoryService.getItem(sku, warehouse)
                .map(itemDTO -> {
                    Logger.info("Item encontrado: SKU=" + sku + ", warehouse=" + warehouse);
                    return new ResponseEntity<>(itemDTO, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    Logger.info("Item NO encontrado: SKU=" + sku + ", warehouse=" + warehouse);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    // update item x sku y almacen
    @PutMapping("/{sku}/warehouse/{warehouse}")
    public ResponseEntity<InventoryItemDTO> updateItem(
            @PathVariable String sku,
            @PathVariable String warehouse,
            @Valid @RequestBody InventoryItemDTO updatedItemDTO) {
        Logger.info("PUT /items/" + sku + "/warehouse/" + warehouse + " - Actualizando item");
        Logger.debug("Parámetros: sku=" + sku + ", warehouse=" + warehouse + ", payload: " + updatedItemDTO);
        return inventoryService.updateItem(sku, warehouse, updatedItemDTO)
                .map(itemDTO -> {
                    Logger.info("Item actualizado: SKU=" + sku + ", warehouse=" + warehouse);
                    return new ResponseEntity<>(itemDTO, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    Logger.info("Item NO encontrado para actualizar: SKU=" + sku + ", warehouse=" + warehouse);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    // post para aplicar un movimiento
    @PostMapping("/movements")
    public ResponseEntity<String> applyMovement(@RequestBody MovementDTO movementDTO) {
        Logger.info("POST /items/movements - Aplicando movimiento");
        Logger.debug("Payload recibido: " + movementDTO);
        boolean success = inventoryService.moveItem(movementDTO);
        if (success) {
            Logger.info("Movimiento aplicado correctamente para SKU: " + movementDTO.getSku() + ", warehouse: "
                    + movementDTO.getWarehouse());
            return ResponseEntity.status(HttpStatus.OK).body("Movement applied successfully");
        } else {
            Logger.info("Fallo al aplicar movimiento para SKU: " + movementDTO.getSku() + ", warehouse: "
                    + movementDTO.getWarehouse());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to apply movement");
        }
    }

    @DeleteMapping("/{sku}/warehouse/{warehouse}")
    public ResponseEntity<String> deleteItem(@PathVariable String sku, @PathVariable String warehouse) {
        Logger.info("DELETE /items/" + sku + "/warehouse/" + warehouse + " - Eliminando item");
        boolean success = inventoryService.deleteItem(sku, warehouse);
        if (success) {
            Logger.info("Item eliminado correctamente: SKU=" + sku + ", warehouse=" + warehouse);
            return new ResponseEntity<>("Item eliminado correctamente", HttpStatus.OK);
        } else {
            Logger.info("Item NO encontrado para eliminar: SKU=" + sku + ", warehouse=" + warehouse);
            return new ResponseEntity<>("Item no encontrado", HttpStatus.NOT_FOUND);
        }
    }
}

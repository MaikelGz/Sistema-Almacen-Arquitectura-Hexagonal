package com.acme.inventory.domain.models;

import java.time.Instant;

public class Movement {

    private String sku;
    private String warehouse;
    private String type; // tipo de movimiento (IN | OUT...)
    private int quantity;
    private String reason;
    private Instant occurredAt;

    public Movement(String sku, String warehouse, String type, int quantity, String reason, Instant occurredAt) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("El SKU no puede ser nulo o vacío");
        }
        if (warehouse == null || warehouse.isBlank()) {
            throw new IllegalArgumentException("El almacén no puede ser nulo o vacío");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser un número positivo");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("El tipo de movimiento debe ser 'IN' o 'OUT'");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("La razón del movimiento no puede ser nula o vacía");
        }

        this.sku = sku;
        this.warehouse = warehouse;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;

        if (occurredAt == null) {
            this.occurredAt = Instant.now();
        }else{
            this.occurredAt = occurredAt;
        }
    }


    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("El SKU no puede ser nulo o vacío");
        }
        this.sku = sku;
    }


    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        if (warehouse == null || warehouse.isBlank()) {
            throw new IllegalArgumentException("El almacén no puede ser nulo o vacío");
        }
        this.warehouse = warehouse;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null || type.isBlank() || (!type.equals("IN") && !type.equals("OUT"))) {
            throw new IllegalArgumentException("El tipo de movimiento debe ser 'IN' o 'OUT'");
        }
        this.type = type;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser un número positivo");
        }
        this.quantity = quantity;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("La razón del movimiento no puede ser nula o vacía");
        }
        this.reason = reason;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        if (occurredAt == null) {
            throw new IllegalArgumentException("La fecha y hora del movimiento no puede ser nula");
        }
        this.occurredAt = occurredAt;
    }
}


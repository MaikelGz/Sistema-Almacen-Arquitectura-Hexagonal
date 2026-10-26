package com.acme.inventory.domain.models;

import java.time.Instant;

public class InventoryItem {

    private String sku;
    private String warehouse;
    private int onHand;
    private int reserved;
    private Instant updatedAt;
    private AuditInfo audit;

    public InventoryItem(String sku, String warehouse, int onHand, int reserved) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("El SKU no puede ser nulo o vacío");
        }
        if (warehouse == null || warehouse.isBlank()) {
            throw new IllegalArgumentException("El almacén no puede ser nulo o vacío");
        }
        if (onHand < 0 ) {
            throw new IllegalArgumentException("La cantidad 'onHand' no puede ser negativa");
        }
        if (reserved < 0) {
            throw new IllegalArgumentException("La cantidad 'reserved' no puede ser negativa");
        }
          if (reserved > onHand) {
            throw new IllegalArgumentException("La cantidad 'reserved' no puede ser mayor que onHand");
        }

        this.sku = sku;
        this.warehouse = warehouse;
        this.onHand = onHand;
        this.reserved = reserved;
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

    public int getOnHand() {
        return onHand;
    }

    public void setOnHand(int onHand) {
        if (onHand < 0) {
            throw new IllegalArgumentException("La cantidad 'onHand' no puede ser negativa");
        }
        this.onHand = onHand;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        if (reserved < 0) {
            throw new IllegalArgumentException("La cantidad 'reserved' no puede ser negativa");
        }
        this.reserved = reserved;
    }

    public int getAvailable() {
        return onHand - reserved;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AuditInfo getAudit() {
        return audit;
    }

    public void setAudit(AuditInfo audit) {
        this.audit = audit;
    }

}

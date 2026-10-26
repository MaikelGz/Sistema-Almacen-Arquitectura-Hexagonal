package com.acme.inventory.infrastructure.rest.dto;

import java.time.Instant;

public class MovementDTO {
    private String sku;
    private String warehouse;
    private String type;
    private int quantity;
    private String reason;
    private Instant occurredAt;

    public MovementDTO() {}

    public MovementDTO(String sku, String warehouse, String type, int quantity, String reason, Instant occurredAt) {
        this.sku = sku;
        this.warehouse = warehouse;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.occurredAt = occurredAt;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}


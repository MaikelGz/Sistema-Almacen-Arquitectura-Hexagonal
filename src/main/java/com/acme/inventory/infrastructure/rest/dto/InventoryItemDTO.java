package com.acme.inventory.infrastructure.rest.dto;

import java.time.Instant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

public class InventoryItemDTO {
    @NotBlank
    private String sku;

    @NotBlank
    private String warehouse;

    @Min(0)
    private int onHand;

    @Min(0)
    private int reserved;

    private Instant updatedAt;
    private AuditInfoDTO audit;

    public InventoryItemDTO() {}

    public InventoryItemDTO(String sku, String warehouse, int onHand, int reserved, Instant updatedAt, AuditInfoDTO audit) {
        this.sku = sku;
        this.warehouse = warehouse;
        this.onHand = onHand;
        this.reserved = reserved;
        this.updatedAt = updatedAt;
        this.audit = audit;
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

    public int getOnHand() {
        return onHand;
    }

    public void setOnHand(int onHand) {
        this.onHand = onHand;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AuditInfoDTO getAudit() {
        return audit;
    }

    public void setAudit(AuditInfoDTO audit) {
        this.audit = audit;
    }

    public int getAvailable() {
        return onHand - reserved;
    }
}

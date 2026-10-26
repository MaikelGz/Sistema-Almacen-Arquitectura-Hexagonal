package com.acme.inventory.infrastructure.mongo.documents;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("items")
public class InventoryItemDocument {
    @Id
    private String id; // _id = sku + "::" + warehouse
    private String sku;
    private String warehouse;
    private int onHand;
    private int reserved;

    private Instant updatedAt;
    private AuditEmbedded audit;

    @Version
    private Long version;

    // para no depender del dominio, y deaclopar, uso esta en lugar de AuditInfo
    public static class AuditEmbedded {
        private String createdBy;
        private Instant createdAt;

        public AuditEmbedded(String createdBy, Instant createdAt) {
            this.createdBy = createdBy;
            this.createdAt = createdAt;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public AuditEmbedded getAudit() {
        return audit;
    }

    public void setAudit(AuditEmbedded audit) {
        this.audit = audit;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public int getAvailable() {
        return onHand - reserved;
    }

}

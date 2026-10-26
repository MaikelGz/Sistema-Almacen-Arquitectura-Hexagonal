package com.acme.inventory.infrastructure.rest.dto;

import java.time.Instant;

public class AuditInfoDTO {
    private String createdBy;
    private Instant createdAt;

    public AuditInfoDTO() {}

    public AuditInfoDTO(String createdBy, Instant createdAt) {
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



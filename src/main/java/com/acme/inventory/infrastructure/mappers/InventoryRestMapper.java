package com.acme.inventory.infrastructure.mappers;

import com.acme.inventory.domain.models.InventoryItem;
import com.acme.inventory.domain.models.Movement;
import com.acme.inventory.domain.models.AuditInfo;
import com.acme.inventory.infrastructure.rest.dto.InventoryItemDTO;
import com.acme.inventory.infrastructure.rest.dto.MovementDTO;
import com.acme.inventory.infrastructure.rest.dto.AuditInfoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryRestMapper {

    MovementDTO toDTO(Movement movement);

    Movement toDomain(MovementDTO dto);

    InventoryItemDTO toDTO(InventoryItem item);

    InventoryItem toDomain(InventoryItemDTO dto);

    AuditInfoDTO toAuditDTO(AuditInfo audit);

    AuditInfo toAuditDomain(AuditInfoDTO dto);
}

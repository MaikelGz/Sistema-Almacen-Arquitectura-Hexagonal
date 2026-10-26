package com.acme.inventory.infrastructure.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.acme.inventory.application.services.InventoryService;
import com.acme.inventory.infrastructure.rest.dto.AuditInfoDTO;
import com.acme.inventory.infrastructure.rest.dto.InventoryItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    private InventoryItemDTO buildDTO(String sku, String warehouse, int onHand, int reserved) {
        AuditInfoDTO audit = new AuditInfoDTO("user", Instant.parse("2023-01-01T00:00:00Z"));
        return new InventoryItemDTO(sku, warehouse, onHand, reserved, Instant.parse("2023-01-02T00:00:00Z"), audit);
    }

    @Test
    void createItem_ShouldReturn201() throws Exception {
        InventoryItemDTO itemDTO = buildDTO("ABC123", "MAD", 10, 2);
        when(inventoryService.createItem(any(InventoryItemDTO.class))).thenReturn(itemDTO);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("ABC123"))
                .andExpect(jsonPath("$.warehouse").value("MAD"))
                .andExpect(jsonPath("$.onHand").value(10))
                .andExpect(jsonPath("$.reserved").value(2))
                .andExpect(jsonPath("$.audit.createdBy").value("user"))
                .andExpect(jsonPath("$.audit.createdAt").value("2023-01-01T00:00:00Z"))
                .andExpect(jsonPath("$.updatedAt").value("2023-01-02T00:00:00Z"));
    }

    @Test
    void createItem_InvalidInputSKU_ShouldReturn400() throws Exception {
        InventoryItemDTO itemDTO = buildDTO("", "MAD", 5, 0);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_InvalidInputWarehouse_ShouldReturn400() throws Exception {
        InventoryItemDTO itemDTO = buildDTO("ABC123", "", 5, 0);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_InvalidInputOnHand_ShouldReturn400() throws Exception {
        InventoryItemDTO itemDTO = buildDTO("ABC123", "MAD", -5, 0);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_InvalidInputReserved_ShouldReturn400() throws Exception {
        InventoryItemDTO itemDTO = buildDTO("ABC123", "MAD", 5, -10);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItem_ShouldReturn200AndItem() throws Exception {
        InventoryItemDTO itemDTO = buildDTO("ABC123", "MAD", 10, 2);
        when(inventoryService.getItem("ABC123", "MAD")).thenReturn(Optional.of(itemDTO));

        mockMvc.perform(get("/items/ABC123/warehouse/MAD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("ABC123"))
                .andExpect(jsonPath("$.warehouse").value("MAD"))
                .andExpect(jsonPath("$.onHand").value(10))
                .andExpect(jsonPath("$.reserved").value(2))
                .andExpect(jsonPath("$.audit.createdBy").value("user"))
                .andExpect(jsonPath("$.audit.createdAt").value("2023-01-01T00:00:00Z"))
                .andExpect(jsonPath("$.updatedAt").value("2023-01-02T00:00:00Z"));
    }

    @Test
    void getItem_NotFound_ShouldReturn404() throws Exception {
        when(inventoryService.getItem(anyString(), anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/items/NOTFOUND/warehouse/MAD"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItem_ShouldReturn200() throws Exception {
        InventoryItemDTO updatedDTO = buildDTO("ABC123", "MAD", 20, 5);
        when(inventoryService.updateItem(anyString(), anyString(), any(InventoryItemDTO.class)))
                .thenReturn(Optional.of(updatedDTO));

        mockMvc.perform(put("/items/ABC123/warehouse/MAD")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("ABC123"))
                .andExpect(jsonPath("$.warehouse").value("MAD"))
                .andExpect(jsonPath("$.onHand").value(20))
                .andExpect(jsonPath("$.reserved").value(5))
                .andExpect(jsonPath("$.audit.createdBy").value("user"))
                .andExpect(jsonPath("$.audit.createdAt").value("2023-01-01T00:00:00Z"))
                .andExpect(jsonPath("$.updatedAt").value("2023-01-02T00:00:00Z"));
    }

    @Test
    void updateItem_WhenNotFound_ShouldReturn404() throws Exception {
        InventoryItemDTO updatedDTO = buildDTO("ABC123", "MAD", 20, 5);
        when(inventoryService.updateItem(anyString(), anyString(), any(InventoryItemDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/items/NOTFOUND/warehouse/MAD")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isNotFound());
    }
}

package com.acme.inventory.domain.models;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InventoryItemTest {

    @Test
    void crearItemValido() {
        InventoryItem item = new InventoryItem("SKU123", "WH1", 10, 2);
        assertEquals("SKU123", item.getSku());
        assertEquals("WH1", item.getWarehouse());
        assertEquals(10, item.getOnHand());
        assertEquals(2, item.getReserved());
        assertEquals(8, item.getAvailable());
    }

    @Test
    void noPermiteSkuNuloOVacio() {
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem(null, "WH1", 10, 2));
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("", "WH1", 10, 2));
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("   ", "WH1", 10, 2));
    }

    @Test
    void noPermiteWarehouseNuloOVacio() {
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("SKU123", null, 10, 2));
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("SKU123", "", 10, 2));
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("SKU123", "   ", 10, 2));
    }

    @Test
    void noPermiteOnHandNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("SKU123", "WH1", -1, 0));
    }

    @Test
    void noPermiteReservedNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("SKU123", "WH1", 10, -1));
    }

    @Test
    void noPermiteReservedMayorQueOnHand() {
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("SKU123", "WH1", 5, 6));
    }

    @Test
    void settersValidanValores() {
        InventoryItem item = new InventoryItem("SKU123", "WH1", 10, 2);

        assertThrows(IllegalArgumentException.class, () -> item.setSku(""));
        assertThrows(IllegalArgumentException.class, () -> item.setWarehouse(null));
        assertThrows(IllegalArgumentException.class, () -> item.setOnHand(-5));
        assertThrows(IllegalArgumentException.class, () -> item.setReserved(-1));
    }

    @Test
    void getAvailableCalculaCorrectamente() {
        InventoryItem item = new InventoryItem("SKU123", "WH1", 20, 5);
        assertEquals(15, item.getAvailable());
        item.setOnHand(12);
        item.setReserved(2);
        assertEquals(10, item.getAvailable());
    }

    @Test
    void setYGetUpdatedAtYAudit() {
        InventoryItem item = new InventoryItem("SKU123", "WH1", 10, 2);
        Instant now = Instant.now();
        item.setUpdatedAt(now);
        assertEquals(now, item.getUpdatedAt());

        AuditInfo audit = new AuditInfo("user", now);
        item.setAudit(audit);
        assertEquals(audit, item.getAudit());
    }
}

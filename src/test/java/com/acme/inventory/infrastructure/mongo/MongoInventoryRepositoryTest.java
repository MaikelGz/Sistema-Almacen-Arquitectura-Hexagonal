package com.acme.inventory.infrastructure.mongo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.acme.inventory.domain.models.InventoryItem;
import com.acme.inventory.domain.models.Movement;
import com.acme.inventory.infrastructure.mongo.adapter.MongoInventoryRepository;

import org.testcontainers.containers.MongoDBContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

@SpringBootTest
@Testcontainers
class MongoInventoryRepositoryTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getConnectionString);
        registry.add("spring.data.mongodb.database", () -> "inventory_test");
    }

    @Autowired
    MongoInventoryRepository repository;

    @Test
    void save_and_findBy_work() {
        InventoryItem saved = repository.save(new InventoryItem("SKU-IT-1", "MAD", 100, 10));
        assertThat(saved.getSku()).isEqualTo("SKU-IT-1");

        assertThat(repository.findBy("SKU-IT-1", "MAD"))
                .isPresent()
                .get()
                .extracting(InventoryItem::getAvailable)
                .isEqualTo(90);
    }

    @Test
    void search_returns_items_matching_sku_prefix() {
        repository.save(new InventoryItem("SKU-PR-7", "MAD", 50, 0));
        repository.save(new InventoryItem("SKU-PR-8", "MAD", 30, 0));
        repository.save(new InventoryItem("SKU-PR-9", "MAD", 20, 0));
        repository.save(new InventoryItem("SKU-ABC-1", "MAD", 100, 0));

        Pageable pageable = PageRequest.of(0, 10);
        Page<InventoryItem> result = repository.search("MAD", "SKU-PR", pageable);

        assertThat(result.getTotalElements()).isEqualTo(3); // debe haber 3 elementos con el prefijo "SKU-IT"
        assertThat(result.getContent()).extracting(InventoryItem::getSku)
                .containsExactlyInAnyOrder("SKU-PR-7", "SKU-PR-8", "SKU-PR-9");
    }

    @Test
    void delete_removes_inventory_item() {
        repository.save(new InventoryItem("SKU-IT-9", "MAD", 0, 0));

        repository.delete("SKU-IT-9", "MAD");

        Optional<InventoryItem> result = repository.findBy("SKU-IT-9", "MAD");
        assertThat(result).isNotPresent(); 
    }

    @Test
    void applyMovement_IN_increases_onHand() {
        repository.save(new InventoryItem("SKU-IT-3", "MAD", 10, 0));

        Movement in = new Movement("SKU-IT-3", "MAD", "IN", 5, "Compra", null);
        boolean ok = repository.applyMovement(in);

        assertThat(ok).isTrue();
        InventoryItem after = repository.findBy("SKU-IT-3", "MAD").orElseThrow();
        assertThat(after.getOnHand()).isEqualTo(15);
    }

    @Test
    void applyMovement_OUT_decreases_onHand_when_sufficient_available() {
        repository.save(new InventoryItem("SKU-IT-11", "MAD", 10, 2));

        Movement out = new Movement("SKU-IT-11", "MAD", "OUT", 5, "Venta", null);
        boolean ok = repository.applyMovement( out);

        assertThat(ok).isTrue();
        InventoryItem after = repository.findBy("SKU-IT-11", "MAD").orElseThrow();
        assertThat(after.getOnHand()).isEqualTo(5);
    }

    @Test
    void applyMovement_OUT_fails_when_insufficient_available() {
        repository.save(new InventoryItem("SKU-IT-2", "MAD", 5, 4)); 

        Movement out = new Movement("SKU-IT-2", "MAD", "OUT", 2, "Venta", null);
        boolean ok = repository.applyMovement( out);

        assertThat(ok).isFalse();

        InventoryItem after = repository.findBy("SKU-IT-2", "MAD").orElseThrow();
        assertThat(after.getOnHand()).isEqualTo(5);
        assertThat(after.getReserved()).isEqualTo(4);
    }

    @Test
    void applyMovement_throws_exception_when_type_is_invalid() {
        repository.save(new InventoryItem("SKU-IT-4", "MAD", 10, 0));

        Movement invalidMovement = new Movement("SKU-IT-4", "MAD", "TRANSFER", 5, "No existe", null);

        assertThatThrownBy(() -> repository.applyMovement(invalidMovement))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de movimiento no soportado");
    }

    @Test
    void applyMovement_RESERVATION_fails_when_insufficient_available() {
        repository.save(new InventoryItem("SKU-IT-5", "MAD", 3, 0)); 

        Movement reservation = new Movement("SKU-IT-5", "MAD", "RESERVATION", 5, "reservamos", null); 
                                                                                               
        boolean ok = repository.applyMovement(reservation);

        assertThat(ok).isFalse();

        InventoryItem after = repository.findBy("SKU-IT-5", "MAD").orElseThrow();
        assertThat(after.getReserved()).isEqualTo(0);
    }

    @Test
    void applyMovement_RELEASE_fails_when_insufficient_reserved() {
        repository.save(new InventoryItem("SKU-IT-6", "MAD", 10, 5)); 

        Movement release = new Movement("SKU-IT-6", "MAD", "RELEASE", 6, "desbloqueamos", null); 

        boolean ok = repository.applyMovement(release);

        assertThat(ok).isFalse();

        InventoryItem after = repository.findBy("SKU-IT-6", "MAD").orElseThrow();
        assertThat(after.getReserved()).isEqualTo(5); 
    }

}

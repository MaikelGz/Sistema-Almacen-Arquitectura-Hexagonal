package com.acme.inventory.infrastructure.mongo.config;

import com.acme.inventory.domain.models.InventoryItem;
import com.acme.inventory.domain.models.Movement;
import com.acme.inventory.domain.ports.out.InventoryRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DataInitializer implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    public DataInitializer(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        inventoryRepository.deleteAll();// TODO: Cuidado con esto, cada vez q se ejecuta 

        // crear algunos objetos InventoryItem para insertar en la base de datos
        InventoryItem item1 = new InventoryItem("SKU-001", "MAD", 100, 20);
        InventoryItem item2 = new InventoryItem("SKU-002", "BCN", 200, 30);
        InventoryItem item3 = new InventoryItem("SKU-003", "MAD", 150, 50);

        inventoryRepository.save(item1);
        inventoryRepository.save(item2);
        inventoryRepository.save(item3);

        Movement movement1 = new Movement("SKU-001", "MAD", "IN", 20, "PURCHASE", Instant.now());
        Movement movement2 = new Movement("SKU-001", "MAD", "OUT", 10, "SALE", Instant.now());

        inventoryRepository.save(movement1);
        inventoryRepository.save(movement2);

        System.out.println("Datos de prueba insertados correctamente en la base de datos.");
    }
}

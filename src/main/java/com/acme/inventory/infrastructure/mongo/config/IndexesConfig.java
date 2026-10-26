package com.acme.inventory.infrastructure.mongo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
public class IndexesConfig {

     @Bean
     CommandLineRunner ensureIndexes(MongoTemplate mongo) {
          return args -> {
               // items: _id ya es único (sku::warehouse)
               mongo.indexOps("items")
                         .ensureIndex(new Index().on("warehouse", Sort.Direction.ASC).named("idx_items_warehouse"));

               // movements: índices útiles para consultas/auditoría
               mongo.indexOps("movements")
                         .ensureIndex(new Index().on("sku", Sort.Direction.ASC).named("idx_movements_sku"));
               mongo.indexOps("movements")
                         .ensureIndex(new Index().on("warehouse", Sort.Direction.ASC).named("idx_movements_warehouse"));
               mongo.indexOps("movements")
                         .ensureIndex(
                                   new Index().on("occurredAt", Sort.Direction.DESC).named("idx_movements_occurredAt"));
          };
     }
}

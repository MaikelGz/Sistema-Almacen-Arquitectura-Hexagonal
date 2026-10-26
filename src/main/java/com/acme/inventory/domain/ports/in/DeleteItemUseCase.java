package com.acme.inventory.domain.ports.in;

public interface DeleteItemUseCase {
    boolean deleteItem(String sku, String warehouse);
}

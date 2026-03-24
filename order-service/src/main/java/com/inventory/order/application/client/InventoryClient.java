package com.inventory.order.application.client;

import java.util.UUID;

public interface InventoryClient {

    void reserveStock(UUID productId, Integer quantity);
}
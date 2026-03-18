package com.inventory.price.application.client;

import java.util.UUID;

public interface ProductClient {

    boolean existsById(UUID productId);
}
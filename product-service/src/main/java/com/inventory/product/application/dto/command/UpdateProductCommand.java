package com.inventory.product.application.dto.command;

import java.util.UUID;

public class UpdateProductCommand {

    private final UUID productId;
    private final String name;
    private final String description;
    private final String category;

    public UpdateProductCommand(UUID productId, String name, String description, String category) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
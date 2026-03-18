package com.inventory.product.application.dto.command;

public class CreateProductCommand {

    private final String name;
    private final String description;
    private final String sku;
    private final String category;

    public CreateProductCommand(String name, String description, String sku, String category) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSku() {
        return sku;
    }

    public String getCategory() {
        return category;
    }
}
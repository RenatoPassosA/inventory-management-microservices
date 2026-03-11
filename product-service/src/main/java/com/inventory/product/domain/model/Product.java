package com.inventory.product.domain.model;

import com.inventory.product.domain.enums.ProductStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Product {

    private UUID id;
    private String name;
    private String description;
    private String sku;
    private String category;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product(UUID id,
                   String name,
                   String description,
                   String sku,
                   String category,
                   ProductStatus status,
                   LocalDateTime createdAt,
                   LocalDateTime updatedAt) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
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

    public ProductStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void activate() {
        this.status = ProductStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }

    public void update(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }
}
package com.inventory.product.application.dto.response;

import com.inventory.product.domain.enums.ProductStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private String sku;
    private String category;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse(UUID id,
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
}
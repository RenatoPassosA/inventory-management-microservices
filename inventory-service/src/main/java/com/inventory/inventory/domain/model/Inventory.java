package com.inventory.inventory.domain.model;

import com.inventory.inventory.domain.exception.InventoryInsufficientStockException;
import com.inventory.inventory.domain.exception.InvalidInventoryMovementException;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Inventory {

    private UUID id;
    private UUID productId;
    private Integer quantityAvailable;
    private Integer quantityReserved;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Inventory(
            UUID id,
            UUID productId,
            Integer quantityAvailable,
            Integer quantityReserved,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        validateProductId(productId);
        validateNonNegative(quantityAvailable, "Available quantity cannot be negative");
        validateNonNegative(quantityReserved, "Reserved quantity cannot be negative");

        this.id = id;
        this.productId = productId;
        this.quantityAvailable = quantityAvailable;
        this.quantityReserved = quantityReserved;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Inventory create(UUID productId) {
        OffsetDateTime now = OffsetDateTime.now();

        return new Inventory(
                null,
                productId,
                0,
                0,
                now,
                now
        );
    }

    public void addStock(Integer quantity) {
        validatePositive(quantity, "Stock entry quantity must be greater than zero");
        this.quantityAvailable += quantity;
        touch();
    }

    public void removeStock(Integer quantity) {
        validatePositive(quantity, "Stock output quantity must be greater than zero");

        if (this.quantityAvailable < quantity) {
            throw new InventoryInsufficientStockException(
                    "Insufficient available stock. Available: " + this.quantityAvailable + ", requested: " + quantity
            );
        }

        this.quantityAvailable -= quantity;
        touch();
    }

    public void reserveStock(Integer quantity) {
        validatePositive(quantity, "Reserved quantity must be greater than zero");

        if (this.quantityAvailable < quantity) {
            throw new InventoryInsufficientStockException(
                    "Insufficient available stock to reserve. Available: " + this.quantityAvailable + ", requested: " + quantity
            );
        }

        this.quantityAvailable -= quantity;
        this.quantityReserved += quantity;
        touch();
    }

    public void releaseReservedStock(Integer quantity) {
        validatePositive(quantity, "Release quantity must be greater than zero");

        if (this.quantityReserved < quantity) {
            throw new InvalidInventoryMovementException(
                    "Cannot release more reserved stock than currently reserved. Reserved: "
                            + this.quantityReserved + ", requested: " + quantity
            );
        }

        this.quantityReserved -= quantity;
        this.quantityAvailable += quantity;
        touch();
    }

    public void confirmReservedStockOutput(Integer quantity) {
        validatePositive(quantity, "Confirmation quantity must be greater than zero");

        if (this.quantityReserved < quantity) {
            throw new InvalidInventoryMovementException(
                    "Cannot confirm output of more reserved stock than currently reserved. Reserved: "
                            + this.quantityReserved + ", requested: " + quantity
            );
        }

        this.quantityReserved -= quantity;
        touch();
    }

    public Integer getTotalQuantity() {
        return this.quantityAvailable + this.quantityReserved;
    }

    private void validateProductId(UUID productId) {
        if (productId == null) {
            throw new InvalidInventoryMovementException("Product id cannot be null");
        }
    }

    private void validatePositive(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new InvalidInventoryMovementException(message);
        }
    }

    private void validateNonNegative(Integer value, String message) {
        if (value == null || value < 0) {
            throw new InvalidInventoryMovementException(message);
        }
    }

    private void touch() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public Integer getQuantityReserved() {
        return quantityReserved;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory inventory)) return false;
        return Objects.equals(id, inventory.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
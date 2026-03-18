package com.inventory.inventory.domain.exception;

public class InventoryInsufficientStockException extends RuntimeException {

    public InventoryInsufficientStockException(String message) {
        super(message);
    }
}
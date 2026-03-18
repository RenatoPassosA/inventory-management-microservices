package com.inventory.inventory.domain.exception;

public class InvalidInventoryMovementException extends RuntimeException {

    public InvalidInventoryMovementException(String message) {
        super(message);
    }
}
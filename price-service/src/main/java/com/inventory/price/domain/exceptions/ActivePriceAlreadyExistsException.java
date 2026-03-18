package com.inventory.price.domain.exceptions;

public class ActivePriceAlreadyExistsException extends RuntimeException {
    public ActivePriceAlreadyExistsException(String message) {
        super(message);
    }
}
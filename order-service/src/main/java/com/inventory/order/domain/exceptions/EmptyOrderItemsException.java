package com.inventory.order.domain.exceptions;

public class EmptyOrderItemsException extends RuntimeException {

    public EmptyOrderItemsException(String message) {
        super(message);
    }
}
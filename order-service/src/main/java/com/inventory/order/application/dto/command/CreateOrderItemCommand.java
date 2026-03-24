package com.inventory.order.application.dto.command;

import java.util.UUID;

public class CreateOrderItemCommand {

    private UUID productId;
    private Integer quantity;

    public CreateOrderItemCommand() {
    }

    public CreateOrderItemCommand(UUID productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
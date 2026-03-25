package com.inventory.order.infrastructure.client.request;

import java.util.UUID;

public class ReserveStockRequest {

    private UUID productId;
    private Integer quantity;

    public ReserveStockRequest() {
    }

    public ReserveStockRequest(UUID productId, Integer quantity) {
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
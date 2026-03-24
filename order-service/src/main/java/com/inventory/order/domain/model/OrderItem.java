package com.inventory.order.domain.model;

import com.inventory.order.domain.exceptions.InvalidOrderItemException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class OrderItem {

    private UUID id;
    private UUID productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public OrderItem() {
    }

    public OrderItem(UUID id, UUID productId, Integer quantity, BigDecimal unitPrice) {
        validate(productId, quantity, unitPrice);

        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = calculateSubtotal();
    }

    public static OrderItem create(UUID productId, Integer quantity, BigDecimal unitPrice) {
        return new OrderItem(UUID.randomUUID(), productId, quantity, unitPrice);
    }

    private void validate(UUID productId, Integer quantity, BigDecimal unitPrice) {
        if (productId == null) {
            throw new InvalidOrderItemException("Product id must not be null.");
        }

        if (quantity == null || quantity <= 0) {
            throw new InvalidOrderItemException("Quantity must be greater than zero.");
        }

        if (unitPrice == null) {
            throw new InvalidOrderItemException("Unit price must not be null.");
        }

        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderItemException("Unit price must be greater than zero.");
        }
    }

    private BigDecimal calculateSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity.longValue()));
    }

    public void recalculateSubtotal() {
        validate(this.productId, this.quantity, this.unitPrice);
        this.subtotal = calculateSubtotal();
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setProductId(UUID productId) {
        validate(productId, this.quantity, this.unitPrice);
        this.productId = productId;
        this.subtotal = calculateSubtotal();
    }

    public void setQuantity(Integer quantity) {
        validate(this.productId, quantity, this.unitPrice);
        this.quantity = quantity;
        this.subtotal = calculateSubtotal();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        validate(this.productId, this.quantity, unitPrice);
        this.unitPrice = unitPrice;
        this.subtotal = calculateSubtotal();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem orderItem)) return false;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
package com.inventory.order.domain.model;

import com.inventory.order.domain.enums.OrderStatus;
import com.inventory.order.domain.exceptions.EmptyOrderItemsException;
import com.inventory.order.domain.exceptions.InvalidOrderException;
import com.inventory.order.domain.exceptions.InvalidOrderStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {

    private UUID id;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Order() {
    }

    public Order(UUID id,
                 List<OrderItem> items,
                 BigDecimal totalAmount,
                 OrderStatus status,
                 OffsetDateTime createdAt,
                 OffsetDateTime updatedAt) {

        validateItems(items);

        this.id = id;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount != null ? totalAmount : calculateTotalAmount();
        this.status = status != null ? status : OrderStatus.PENDING;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();

        validateState();
    }

    public static Order create(List<OrderItem> items) {
        OffsetDateTime now = OffsetDateTime.now();

        return new Order(
                UUID.randomUUID(),
                items,
                null,
                OrderStatus.PENDING,
                now,
                now
        );
    }

    private void validateItems(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new EmptyOrderItemsException("Order must contain at least one item.");
        }

        boolean hasNullItem = items.stream().anyMatch(Objects::isNull);
        if (hasNullItem) {
            throw new InvalidOrderException("Order must not contain null items.");
        }
    }

    private void validateState() {
        if (id == null) {
            throw new InvalidOrderException("Order id must not be null.");
        }

        validateItems(this.items);

        if (status == null) {
            throw new InvalidOrderException("Order status must not be null.");
        }

        if (createdAt == null) {
            throw new InvalidOrderException("Created at must not be null.");
        }

        if (updatedAt == null) {
            throw new InvalidOrderException("Updated at must not be null.");
        }

        BigDecimal calculatedTotal = calculateTotalAmount();

        if (totalAmount == null) {
            throw new InvalidOrderException("Total amount must not be null.");
        }

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderException("Total amount must be greater than zero.");
        }

        if (totalAmount.compareTo(calculatedTotal) != 0) {
            throw new InvalidOrderException("Total amount is inconsistent with order items.");
        }
    }

    public BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void recalculateTotalAmount() {
        this.totalAmount = calculateTotalAmount();
        this.updatedAt = OffsetDateTime.now();
        validateState();
    }

    public void confirm() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException("Cancelled order cannot be confirmed.");
        }

        if (this.status == OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusException("Order is already confirmed.");
        }

        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException("Order is already cancelled.");
        }

        this.status = OrderStatus.CANCELLED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new InvalidOrderException("Item must not be null.");
        }

        if (this.items == null) {
            this.items = new ArrayList<>();
        }

        this.items.add(item);
        this.totalAmount = calculateTotalAmount();
        this.updatedAt = OffsetDateTime.now();
        validateState();
    }

    public void replaceItems(List<OrderItem> items) {
        validateItems(items);
        this.items = new ArrayList<>(items);
        this.totalAmount = calculateTotalAmount();
        this.updatedAt = OffsetDateTime.now();
        validateState();
    }

    public UUID getId() {
        return id;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(UUID id) {
        this.id = id;
        validateState();
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        validateState();
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        validateState();
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
        validateState();
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
        validateState();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
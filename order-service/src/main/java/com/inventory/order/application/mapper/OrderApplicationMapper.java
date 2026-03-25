package com.inventory.order.application.mapper;

import com.inventory.order.application.dto.result.CreateOrderResult;
import com.inventory.order.application.dto.result.OrderItemResult;
import com.inventory.order.application.dto.result.OrderResult;
import com.inventory.order.domain.model.Order;
import com.inventory.order.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderApplicationMapper {

    public CreateOrderResult toCreateOrderResult(Order order) {
        if (order == null) {
            return null;
        }

        return new CreateOrderResult(
                order.getId(),
                toOrderItemResultList(order.getItems()),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public OrderResult toOrderResult(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderResult(
                order.getId(),
                toOrderItemResultList(order.getItems()),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public List<OrderItemResult> toOrderItemResultList(List<OrderItem> items) {
        if (items == null) {
            return List.of();
        }

        return items.stream()
                .map(this::toOrderItemResult)
                .toList();
    }

    public OrderItemResult toOrderItemResult(OrderItem item) {
        if (item == null) {
            return null;
        }

        return new OrderItemResult(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}
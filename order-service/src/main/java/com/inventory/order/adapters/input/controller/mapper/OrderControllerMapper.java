package com.inventory.order.adapters.input.controller.mapper;

import com.inventory.order.adapters.input.controller.request.CreateOrderItemRequest;
import com.inventory.order.adapters.input.controller.request.CreateOrderRequest;
import com.inventory.order.adapters.input.controller.response.CreateOrderResponse;
import com.inventory.order.adapters.input.controller.response.OrderItemResponse;
import com.inventory.order.adapters.input.controller.response.OrderResponse;
import com.inventory.order.application.dto.command.CreateOrderCommand;
import com.inventory.order.application.dto.command.CreateOrderItemCommand;
import com.inventory.order.application.dto.result.CreateOrderResult;
import com.inventory.order.application.dto.result.OrderItemResult;
import com.inventory.order.application.dto.result.OrderResult;

import java.util.List;

public class OrderControllerMapper {

    public CreateOrderCommand toCreateOrderCommand(CreateOrderRequest request) {
        if (request == null) {
            return null;
        }

        return new CreateOrderCommand(toCreateOrderItemCommandList(request.getItems()));
    }

    public List<CreateOrderItemCommand> toCreateOrderItemCommandList(List<CreateOrderItemRequest> items) {
        if (items == null) {
            return List.of();
        }

        return items.stream()
                .map(this::toCreateOrderItemCommand)
                .toList();
    }

    public CreateOrderItemCommand toCreateOrderItemCommand(CreateOrderItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }

        return new CreateOrderItemCommand(
                itemRequest.getProductId(),
                itemRequest.getQuantity()
        );
    }

    public CreateOrderResponse toCreateOrderResponse(CreateOrderResult result) {
        if (result == null) {
            return null;
        }

        return new CreateOrderResponse(
                result.getId(),
                toOrderItemResponseList(result.getItems()),
                result.getTotalAmount(),
                result.getStatus(),
                result.getCreatedAt(),
                result.getUpdatedAt()
        );
    }

    public OrderResponse toOrderResponse(OrderResult result) {
        if (result == null) {
            return null;
        }

        return new OrderResponse(
                result.getId(),
                toOrderItemResponseList(result.getItems()),
                result.getTotalAmount(),
                result.getStatus(),
                result.getCreatedAt(),
                result.getUpdatedAt()
        );
    }

    public List<OrderResponse> toOrderResponseList(List<OrderResult> results) {
        if (results == null) {
            return List.of();
        }

        return results.stream()
                .map(this::toOrderResponse)
                .toList();
    }

    public List<OrderItemResponse> toOrderItemResponseList(List<OrderItemResult> items) {
        if (items == null) {
            return List.of();
        }

        return items.stream()
                .map(this::toOrderItemResponse)
                .toList();
    }

    public OrderItemResponse toOrderItemResponse(OrderItemResult item) {
        if (item == null) {
            return null;
        }

        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}
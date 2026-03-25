package com.inventory.order.application.usecase.impl;

import com.inventory.order.application.client.InventoryClient;
import com.inventory.order.application.client.PriceClient;
import com.inventory.order.application.dto.command.CreateOrderCommand;
import com.inventory.order.application.dto.command.CreateOrderItemCommand;
import com.inventory.order.application.dto.result.CreateOrderResult;
import com.inventory.order.application.dto.result.OrderResult;
import com.inventory.order.application.mapper.OrderApplicationMapper;
import com.inventory.order.application.usecase.OrderUseCase;
import com.inventory.order.domain.exceptions.InvalidOrderException;
import com.inventory.order.domain.exceptions.OrderNotFoundException;
import com.inventory.order.domain.model.Order;
import com.inventory.order.domain.model.OrderItem;
import com.inventory.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Service
public class OrderUseCaseImpl implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final PriceClient priceClient;
    private final InventoryClient inventoryClient;
    private final OrderApplicationMapper orderApplicationMapper;

    public OrderUseCaseImpl(OrderRepository orderRepository,
                            PriceClient priceClient,
                            InventoryClient inventoryClient,
                            OrderApplicationMapper orderApplicationMapper) {
        this.orderRepository = orderRepository;
        this.priceClient = priceClient;
        this.inventoryClient = inventoryClient;
        this.orderApplicationMapper = orderApplicationMapper;
    }

    @Override
    public CreateOrderResult create(CreateOrderCommand command) {
        validateCreateCommand(command);

        List<OrderItem> items = command.getItems().stream()
                .map(this::buildOrderItem)
                .toList();

        reserveStock(items);

        Order order = Order.create(items);
        order.confirm();

        Order savedOrder = orderRepository.save(order);

        return orderApplicationMapper.toCreateOrderResult(savedOrder);
    }

    @Override
    public OrderResult getById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        return orderApplicationMapper.toOrderResult(order);
    }

    @Override
    public List<OrderResult> findAll() {
        return orderRepository.findAll().stream()
                .map(orderApplicationMapper::toOrderResult)
                .toList();
    }

    private void validateCreateCommand(CreateOrderCommand command) {
        if (command == null) {
            throw new InvalidOrderException("Create order command must not be null.");
        }

        if (command.getItems() == null || command.getItems().isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one item.");
        }
    }

    private OrderItem buildOrderItem(CreateOrderItemCommand itemCommand) {
        if (itemCommand == null) {
            throw new InvalidOrderException("Order item command must not be null.");
        }

        if (itemCommand.getProductId() == null) {
            throw new InvalidOrderException("Product id must not be null.");
        }

        if (itemCommand.getQuantity() == null || itemCommand.getQuantity() <= 0) {
            throw new InvalidOrderException("Quantity must be greater than zero.");
        }

        BigDecimal activePrice = priceClient.getActivePriceByProductId(itemCommand.getProductId());

        return OrderItem.create(
                itemCommand.getProductId(),
                itemCommand.getQuantity(),
                activePrice
        );
    }

    private void reserveStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            inventoryClient.reserveStock(item.getProductId(), item.getQuantity());
        }
    }
}
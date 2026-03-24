package com.inventory.order.application.usecase;

import com.inventory.order.application.dto.command.CreateOrderCommand;
import com.inventory.order.application.dto.result.CreateOrderResult;
import com.inventory.order.application.dto.result.OrderResult;

import java.util.List;
import java.util.UUID;

public interface OrderUseCase {

    CreateOrderResult create(CreateOrderCommand command);
    OrderResult getById(UUID id);
    List<OrderResult> findAll();
}
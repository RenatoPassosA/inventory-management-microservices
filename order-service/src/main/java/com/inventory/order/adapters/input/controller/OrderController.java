package com.inventory.order.adapters.input.controller;

import com.inventory.order.adapters.input.controller.mapper.OrderControllerMapper;
import com.inventory.order.adapters.input.controller.request.CreateOrderRequest;
import com.inventory.order.adapters.input.controller.response.CreateOrderResponse;
import com.inventory.order.adapters.input.controller.response.OrderResponse;
import com.inventory.order.application.dto.command.CreateOrderCommand;
import com.inventory.order.application.dto.result.CreateOrderResult;
import com.inventory.order.application.dto.result.OrderResult;
import com.inventory.order.application.usecase.OrderUseCase;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;
    private final OrderControllerMapper orderControllerMapper;

    public OrderController(OrderUseCase orderUseCase,
                           OrderControllerMapper orderControllerMapper) {
        this.orderUseCase = orderUseCase;
        this.orderControllerMapper = orderControllerMapper;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = orderControllerMapper.toCreateOrderCommand(request);
        CreateOrderResult result = orderUseCase.create(command);
        CreateOrderResponse response = orderControllerMapper.toCreateOrderResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable UUID id) {
        OrderResult result = orderUseCase.getById(id);
        OrderResponse response = orderControllerMapper.toOrderResponse(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        List<OrderResult> results = orderUseCase.findAll();
        List<OrderResponse> responses = orderControllerMapper.toOrderResponseList(results);

        return ResponseEntity.ok(responses);
    }
}
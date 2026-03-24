package com.inventory.order.domain.repository;

import com.inventory.order.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findAll();
}
package com.inventory.order.infrastructure.persistence.repository;

import com.inventory.order.domain.model.Order;
import com.inventory.order.domain.repository.OrderRepository;
import com.inventory.order.infrastructure.persistence.entity.OrderEntity;
import com.inventory.order.infrastructure.persistence.mapper.OrderEntityMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderEntityRepository implements OrderRepository {

    private final OrderRepositoryJpa orderRepositoryJpa;
    private final OrderEntityMapper orderEntityMapper;

    public OrderEntityRepository(OrderRepositoryJpa orderRepositoryJpa,
                               OrderEntityMapper orderEntityMapper) {
        this.orderRepositoryJpa = orderRepositoryJpa;
        this.orderEntityMapper = orderEntityMapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = orderEntityMapper.toEntity(order);
        OrderEntity savedEntity = orderRepositoryJpa.save(entity);
        return orderEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return orderRepositoryJpa.findById(id)
                .map(orderEntityMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return orderRepositoryJpa.findAll().stream()
                .map(orderEntityMapper::toDomain)
                .toList();
    }
}
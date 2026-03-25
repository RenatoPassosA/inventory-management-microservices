package com.inventory.order.infrastructure.persistence.repository;

import com.inventory.order.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepositoryJpa extends JpaRepository<OrderEntity, UUID> {
}
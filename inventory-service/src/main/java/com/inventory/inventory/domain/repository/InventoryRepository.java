package com.inventory.inventory.domain.repository;

import com.inventory.inventory.domain.model.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository {

    Inventory save(Inventory inventory);
    Optional<Inventory> findById(UUID id);
    Optional<Inventory> findByProductId(UUID productId);
    List<Inventory> findAll();
    boolean existsByProductId(UUID productId);
    void deleteById(UUID id);
}
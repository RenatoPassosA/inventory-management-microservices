package com.inventory.inventory.infrastructure.persistence.repository;

import com.inventory.inventory.infrastructure.persistence.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryEntityRepository extends JpaRepository<InventoryEntity, UUID> {

    Optional<InventoryEntity> findByProductId(UUID productId);
    boolean existsByProductId(UUID productId);
}
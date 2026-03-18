package com.inventory.price.infrastructure.persistence.repository;

import com.inventory.price.infrastructure.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceEntityRepository extends JpaRepository<PriceEntity, UUID> {

    Optional<PriceEntity> findByProductIdAndActiveTrue(UUID productId);
    boolean existsByProductIdAndActiveTrue(UUID productId);
    List<PriceEntity> findAllByProductId(UUID productId);
}
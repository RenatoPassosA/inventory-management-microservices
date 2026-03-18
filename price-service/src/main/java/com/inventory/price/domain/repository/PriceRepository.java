package com.inventory.price.domain.repository;

import com.inventory.price.domain.model.Price;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceRepository {

    Price save(Price price);
    Optional<Price> findById(UUID id);
    Optional<Price> findActiveByProductId(UUID productId);
    List<Price> findAllByProductId(UUID productId);
    void deactivateCurrentPrice(UUID productId);
    boolean existsActiveByProductId(UUID productId);
}
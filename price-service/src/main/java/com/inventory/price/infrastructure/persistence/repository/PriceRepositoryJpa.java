package com.inventory.price.infrastructure.persistence.repository;

import com.inventory.price.domain.model.Price;
import com.inventory.price.domain.repository.PriceRepository;
import com.inventory.price.infrastructure.persistence.entity.PriceEntity;
import com.inventory.price.infrastructure.persistence.mapper.PriceEntityMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PriceRepositoryJpa implements PriceRepository {

    private final PriceEntityRepository priceEntityRepository;

    public PriceRepositoryJpa(PriceEntityRepository priceEntityRepository) {
        this.priceEntityRepository = priceEntityRepository;
    }

    @Override
    public Price save(Price price) {
        PriceEntity entity = PriceEntityMapper.toEntity(price);
        PriceEntity saved = priceEntityRepository.save(entity);
        return PriceEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Price> findById(UUID id) {
        return priceEntityRepository.findById(id)
                .map(PriceEntityMapper::toDomain);
    }

    @Override
    public Optional<Price> findActiveByProductId(UUID productId) {
        return priceEntityRepository.findByProductIdAndActiveTrue(productId)
                .map(PriceEntityMapper::toDomain);
    }

    @Override
    public List<Price> findAllByProductId(UUID productId) {
        return priceEntityRepository.findAllByProductId(productId)
                .stream()
                .map(PriceEntityMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsActiveByProductId(UUID productId) {
        return priceEntityRepository.existsByProductIdAndActiveTrue(productId);
    }

    @Override
    public void deactivateCurrentPrice(UUID productId) {
        priceEntityRepository.findByProductIdAndActiveTrue(productId)
                .ifPresent(entity -> {
                    entity.setActive(false);
                    entity.setUpdatedAt(LocalDateTime.now());
                    priceEntityRepository.save(entity);
                });
    }
}
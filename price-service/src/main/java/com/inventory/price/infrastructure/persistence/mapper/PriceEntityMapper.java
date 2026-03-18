package com.inventory.price.infrastructure.persistence.mapper;

import com.inventory.price.domain.model.Price;
import com.inventory.price.infrastructure.persistence.entity.PriceEntity;

public class PriceEntityMapper {

    public static PriceEntity toEntity(Price price) {
        if (price == null) return null;

        return new PriceEntity(
                price.getId(),
                price.getProductId(),
                price.getPrice(),
                price.getCurrency(),
                price.isActive(),
                price.getCreatedAt(),
                price.getUpdatedAt()
        );
    }

    public static Price toDomain(PriceEntity entity) {
        if (entity == null) return null;

        return new Price(
                entity.getId(),
                entity.getProductId(),
                entity.getPrice(),
                entity.getCurrency(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
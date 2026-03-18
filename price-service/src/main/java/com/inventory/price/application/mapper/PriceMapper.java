package com.inventory.price.application.mapper;

import com.inventory.price.application.dto.result.PriceResult;
import com.inventory.price.domain.model.Price;

import java.util.List;

public class PriceMapper {

    public static PriceResult toResult(Price price) {
        if (price == null) return null;

        return new PriceResult(
                price.getId(),
                price.getProductId(),
                price.getPrice(),
                price.getCurrency(),
                price.isActive(),
                price.getCreatedAt(),
                price.getUpdatedAt()
        );
    }

    public static List<PriceResult> toResultList(List<Price> prices) {
        return prices.stream()
                .map(PriceMapper::toResult)
                .toList();
    }
}
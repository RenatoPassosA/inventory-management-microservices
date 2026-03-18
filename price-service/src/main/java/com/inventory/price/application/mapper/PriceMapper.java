package com.inventory.price.application.mapper;

import com.inventory.price.application.dto.response.PriceResponse;
import com.inventory.price.domain.model.Price;

import java.util.List;

public class PriceMapper {

    public static PriceResponse toResponse(Price price) {
        if (price == null) return null;

        return new PriceResponse(
                price.getId(),
                price.getProductId(),
                price.getPrice(),
                price.getCurrency(),
                price.isActive(),
                price.getCreatedAt(),
                price.getUpdatedAt()
        );
    }

    public static List<PriceResponse> toResponseList(List<Price> prices) {
        return prices.stream()
                .map(PriceMapper::toResponse)
                .toList();
    }
}
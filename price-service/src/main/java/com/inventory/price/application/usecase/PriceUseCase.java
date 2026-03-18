package com.inventory.price.application.usecase;

import com.inventory.price.application.dto.request.CreatePriceRequest;
import com.inventory.price.application.dto.request.UpdatePriceRequest;
import com.inventory.price.application.dto.response.PriceResponse;
import com.inventory.price.application.dto.response.PriceHistoryResponse;

import java.util.UUID;

public interface PriceUseCase {

    PriceResponse createPrice(CreatePriceRequest request);
    PriceResponse getCurrentPriceByProductId(UUID productId);
    PriceResponse updatePrice(UUID id, UpdatePriceRequest request);
    PriceHistoryResponse getPriceHistoryByProductId(UUID productId);
}
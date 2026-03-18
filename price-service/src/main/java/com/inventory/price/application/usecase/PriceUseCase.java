package com.inventory.price.application.usecase;

import java.util.UUID;

import com.inventory.price.application.dto.command.CreatePriceCommand;
import com.inventory.price.application.dto.command.UpdatePriceCommand;
import com.inventory.price.application.dto.result.PriceHistoryResult;
import com.inventory.price.application.dto.result.PriceResult;

public interface PriceUseCase {

    PriceResult createPrice(CreatePriceCommand request);
    PriceResult getCurrentPriceByProductId(UUID productId);
    PriceResult updatePrice(UUID id, UpdatePriceCommand request);
    PriceHistoryResult getPriceHistoryByProductId(UUID productId);
}
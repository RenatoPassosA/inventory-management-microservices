package com.inventory.price.adapters.input.controller.mapper;

import com.inventory.price.adapters.input.controller.request.CreatePriceRequest;
import com.inventory.price.adapters.input.controller.request.UpdatePriceRequest;
import com.inventory.price.adapters.input.controller.response.PriceHistoryResponse;
import com.inventory.price.adapters.input.controller.response.PriceResponse;
import com.inventory.price.application.dto.command.CreatePriceCommand;
import com.inventory.price.application.dto.command.UpdatePriceCommand;
import com.inventory.price.application.dto.result.PriceHistoryResult;
import com.inventory.price.application.dto.result.PriceResult;

import java.util.List;
import java.util.stream.Collectors;

public class PriceWebMapper {

    public static CreatePriceCommand toCommand(CreatePriceRequest request) {
        return new CreatePriceCommand(
                request.getProductId(),
                request.getPrice(),
                request.getCurrency()
        );
    }

    public static UpdatePriceCommand toCommand(UpdatePriceRequest request) {
        return new UpdatePriceCommand(
                request.getPrice(),
                request.getCurrency()
        );
    }

    public static PriceResponse toResponse(PriceResult result) {
        return new PriceResponse(
                result.getId(),
                result.getProductId(),
                result.getPrice(),
                result.getCurrency(),
                result.isActive(),
                result.getCreatedAt(),
                result.getUpdatedAt()
        );
    }

    public static PriceHistoryResponse toResponse(PriceHistoryResult result) {
        List<PriceResponse> prices = result.getPrices()
                .stream()
                .map(PriceWebMapper::toResponse)
                .collect(Collectors.toList());

        return new PriceHistoryResponse(
                result.getProductId(),
                prices
        );
    }
}
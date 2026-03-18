package com.inventory.inventory.adapter.input.controller.mapper;

import com.inventory.inventory.adapter.input.controller.response.InventoryResponse;
import com.inventory.inventory.application.dto.result.InventoryResult;

public class InventoryWebMapper {

    private InventoryWebMapper() {
    }

    public static InventoryResponse toResponse(InventoryResult result) {
        return new InventoryResponse(
                result.id(),
                result.productId(),
                result.quantityAvailable(),
                result.quantityReserved(),
                result.totalQuantity(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
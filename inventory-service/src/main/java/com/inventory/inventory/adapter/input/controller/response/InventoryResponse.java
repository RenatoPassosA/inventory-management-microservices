package com.inventory.inventory.adapter.input.controller.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID productId,
        Integer quantityAvailable,
        Integer quantityReserved,
        Integer totalQuantity,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
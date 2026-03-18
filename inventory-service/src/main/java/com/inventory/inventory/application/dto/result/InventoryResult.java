package com.inventory.inventory.application.dto.result;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryResult(UUID id,
                            UUID productId,
                            Integer quantityAvailable,
                            Integer quantityReserved,
                            Integer totalQuantity,
                            OffsetDateTime createdAt,
                            OffsetDateTime updatedAt) {
}
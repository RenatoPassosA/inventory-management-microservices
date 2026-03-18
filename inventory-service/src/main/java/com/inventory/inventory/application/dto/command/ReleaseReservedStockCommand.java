package com.inventory.inventory.application.dto.command;

import java.util.UUID;

public record ReleaseReservedStockCommand(UUID productId, Integer quantity) {
}
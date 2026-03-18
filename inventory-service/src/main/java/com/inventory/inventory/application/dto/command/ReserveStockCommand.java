package com.inventory.inventory.application.dto.command;

import java.util.UUID;

public record ReserveStockCommand(UUID productId, Integer quantity) {
}
package com.inventory.inventory.application.dto.command;

import java.util.UUID;

public record AddStockCommand(UUID productId, Integer quantity) {
}
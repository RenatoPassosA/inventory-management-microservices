package com.inventory.inventory.application.dto.command;

import java.util.UUID;

public record RemoveStockCommand(UUID productId, Integer quantity) {
}
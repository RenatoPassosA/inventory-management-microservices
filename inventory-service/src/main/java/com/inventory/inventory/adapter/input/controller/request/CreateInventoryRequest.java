package com.inventory.inventory.adapter.input.controller.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateInventoryRequest(
        @NotNull(message = "O productId é obrigatório")
        UUID productId) {
}
package com.inventory.inventory.adapter.input.controller.response;

import java.time.OffsetDateTime;

public record ErrorResponse(
        Integer status,
        String error,
        String message,
        OffsetDateTime timestamp
) {
}
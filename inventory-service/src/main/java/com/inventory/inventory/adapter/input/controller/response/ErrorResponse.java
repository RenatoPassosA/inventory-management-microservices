package com.inventory.inventory.adapter.input.controller.response;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(
        Integer status,
        String error,
        String message,
        List<String> details,
        OffsetDateTime timestamp
) {
}
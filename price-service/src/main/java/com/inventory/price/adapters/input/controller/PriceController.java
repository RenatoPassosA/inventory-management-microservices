package com.inventory.price.adapters.input.controller;

import com.inventory.price.application.dto.request.CreatePriceRequest;
import com.inventory.price.application.dto.request.UpdatePriceRequest;
import com.inventory.price.application.dto.response.PriceHistoryResponse;
import com.inventory.price.application.dto.response.PriceResponse;
import com.inventory.price.application.usecase.PriceUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceUseCase priceUseCase;

    public PriceController(PriceUseCase priceUseCase) {
        this.priceUseCase = priceUseCase;
    }

    @PostMapping
    public ResponseEntity<PriceResponse> createPrice(@Valid @RequestBody CreatePriceRequest request) {
        PriceResponse response = priceUseCase.createPrice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<PriceResponse> getCurrentPriceByProductId(@PathVariable UUID productId) {
        PriceResponse response = priceUseCase.getCurrentPriceByProductId(productId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceResponse> updatePrice(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePriceRequest request
    ) {
        PriceResponse response = priceUseCase.updatePrice(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{productId}")
    public ResponseEntity<PriceHistoryResponse> getPriceHistoryByProductId(@PathVariable UUID productId) {
        PriceHistoryResponse response = priceUseCase.getPriceHistoryByProductId(productId);
        return ResponseEntity.ok(response);
    }
}
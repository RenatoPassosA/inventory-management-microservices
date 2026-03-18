package com.inventory.price.adapters.input.controller;

import com.inventory.price.adapters.input.controller.mapper.PriceWebMapper;
import com.inventory.price.adapters.input.controller.request.CreatePriceRequest;
import com.inventory.price.adapters.input.controller.request.UpdatePriceRequest;
import com.inventory.price.adapters.input.controller.response.PriceHistoryResponse;
import com.inventory.price.adapters.input.controller.response.PriceResponse;
import com.inventory.price.application.usecase.PriceUseCase;
import com.inventory.price.application.dto.command.CreatePriceCommand;
import com.inventory.price.application.dto.command.UpdatePriceCommand;
import com.inventory.price.application.dto.result.PriceHistoryResult;
import com.inventory.price.application.dto.result.PriceResult;
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

        CreatePriceCommand command = PriceWebMapper.toCommand(request);
        PriceResult result = priceUseCase.createPrice(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PriceWebMapper.toResponse(result));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<PriceResponse> getCurrentPriceByProductId(@PathVariable UUID productId) {

        PriceResult result = priceUseCase.getCurrentPriceByProductId(productId);

        return ResponseEntity.ok(
                PriceWebMapper.toResponse(result)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceResponse> updatePrice(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePriceRequest request
    ) {

        UpdatePriceCommand command = PriceWebMapper.toCommand(request);
        PriceResult result = priceUseCase.updatePrice(id, command);

        return ResponseEntity.ok(
                PriceWebMapper.toResponse(result)
        );
    }

    @GetMapping("/history/{productId}")
    public ResponseEntity<PriceHistoryResponse> getPriceHistoryByProductId(@PathVariable UUID productId) {

        PriceHistoryResult result = priceUseCase.getPriceHistoryByProductId(productId);

        return ResponseEntity.ok(
                PriceWebMapper.toResponse(result)
        );
    }
}
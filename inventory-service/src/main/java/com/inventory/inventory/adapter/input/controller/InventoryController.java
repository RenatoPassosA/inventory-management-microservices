package com.inventory.inventory.adapter.input.controller;

import com.inventory.inventory.adapter.input.controller.mapper.InventoryWebMapper;
import com.inventory.inventory.adapter.input.controller.request.CreateInventoryRequest;
import com.inventory.inventory.adapter.input.controller.request.StockMovementRequest;
import com.inventory.inventory.adapter.input.controller.response.InventoryResponse;
import com.inventory.inventory.application.dto.command.AddStockCommand;
import com.inventory.inventory.application.dto.command.CreateInventoryCommand;
import com.inventory.inventory.application.dto.command.ReleaseReservedStockCommand;
import com.inventory.inventory.application.dto.command.RemoveStockCommand;
import com.inventory.inventory.application.dto.command.ReserveStockCommand;
import com.inventory.inventory.application.dto.result.InventoryResult;
import com.inventory.inventory.application.usecase.InventoryUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventories")
public class InventoryController {

    private final InventoryUseCase inventoryUseCase;

    public InventoryController(InventoryUseCase inventoryUseCase) {
        this.inventoryUseCase = inventoryUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse create(@RequestBody @Valid CreateInventoryRequest request) {
        InventoryResult result = inventoryUseCase.create(
                new CreateInventoryCommand(request.productId())
        );

        return InventoryWebMapper.toResponse(result);
    }

    @GetMapping
    public List<InventoryResponse> findAll() {
        return inventoryUseCase.findAll()
                .stream()
                .map(InventoryWebMapper::toResponse)
                .toList();
    }

    @GetMapping("/{inventoryId}")
    public InventoryResponse findById(@PathVariable UUID inventoryId) {
        InventoryResult result = inventoryUseCase.findById(inventoryId);
        return InventoryWebMapper.toResponse(result);
    }

    @GetMapping("/product/{productId}")
    public InventoryResponse findByProductId(@PathVariable UUID productId) {
        InventoryResult result = inventoryUseCase.findByProductId(productId);
        return InventoryWebMapper.toResponse(result);
    }

    @PatchMapping("/product/{productId}/add-stock")
    public InventoryResponse addStock(
            @PathVariable UUID productId,
            @RequestBody @Valid StockMovementRequest request
    ) {
        InventoryResult result = inventoryUseCase.addStock(
                new AddStockCommand(productId, request.quantity())
        );

        return InventoryWebMapper.toResponse(result);
    }

    @PatchMapping("/product/{productId}/remove-stock")
    public InventoryResponse removeStock(
            @PathVariable UUID productId,
            @RequestBody @Valid StockMovementRequest request
    ) {
        InventoryResult result = inventoryUseCase.removeStock(
                new RemoveStockCommand(productId, request.quantity())
        );

        return InventoryWebMapper.toResponse(result);
    }

    @PatchMapping("/product/{productId}/reserve-stock")
    public InventoryResponse reserveStock(
            @PathVariable UUID productId,
            @RequestBody @Valid StockMovementRequest request
    ) {
        InventoryResult result = inventoryUseCase.reserveStock(
                new ReserveStockCommand(productId, request.quantity())
        );

        return InventoryWebMapper.toResponse(result);
    }

    @PatchMapping("/product/{productId}/release-reserved-stock")
    public InventoryResponse releaseReservedStock(
            @PathVariable UUID productId,
            @RequestBody @Valid StockMovementRequest request
    ) {
        InventoryResult result = inventoryUseCase.releaseReservedStock(
                new ReleaseReservedStockCommand(productId, request.quantity())
        );

        return InventoryWebMapper.toResponse(result);
    }
}
package com.inventory.inventory.adapter.input.controller;

import com.inventory.inventory.adapter.input.controller.request.CreateInventoryRequest;
import com.inventory.inventory.adapter.input.controller.request.StockMovementRequest;
import com.inventory.inventory.application.dto.command.AddStockCommand;
import com.inventory.inventory.application.dto.command.CreateInventoryCommand;
import com.inventory.inventory.application.dto.command.ReleaseReservedStockCommand;
import com.inventory.inventory.application.dto.command.RemoveStockCommand;
import com.inventory.inventory.application.dto.command.ReserveStockCommand;
import com.inventory.inventory.application.dto.result.InventoryResult;
import com.inventory.inventory.application.usecase.InventoryUseCase;
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
    public InventoryResult create(@RequestBody CreateInventoryRequest request) {
        return inventoryUseCase.create(
                new CreateInventoryCommand(request.productId())
        );
    }

    @GetMapping
    public List<InventoryResult> findAll() {
        return inventoryUseCase.findAll();
    }

    @GetMapping("/{inventoryId}")
    public InventoryResult findById(@PathVariable UUID inventoryId) {
        return inventoryUseCase.findById(inventoryId);
    }

    @GetMapping("/product/{productId}")
    public InventoryResult findByProductId(@PathVariable UUID productId) {
        return inventoryUseCase.findByProductId(productId);
    }

    @PatchMapping("/product/{productId}/add-stock")
    public InventoryResult addStock(
            @PathVariable UUID productId,
            @RequestBody StockMovementRequest request
    ) {
        return inventoryUseCase.addStock(
                new AddStockCommand(productId, request.quantity())
        );
    }

    @PatchMapping("/product/{productId}/remove-stock")
    public InventoryResult removeStock(
            @PathVariable UUID productId,
            @RequestBody StockMovementRequest request
    ) {
        return inventoryUseCase.removeStock(
                new RemoveStockCommand(productId, request.quantity())
        );
    }

    @PatchMapping("/product/{productId}/reserve-stock")
    public InventoryResult reserveStock(
            @PathVariable UUID productId,
            @RequestBody StockMovementRequest request
    ) {
        return inventoryUseCase.reserveStock(
                new ReserveStockCommand(productId, request.quantity())
        );
    }

    @PatchMapping("/product/{productId}/release-reserved-stock")
    public InventoryResult releaseReservedStock(
            @PathVariable UUID productId,
            @RequestBody StockMovementRequest request
    ) {
        return inventoryUseCase.releaseReservedStock(
                new ReleaseReservedStockCommand(productId, request.quantity())
        );
    }
}
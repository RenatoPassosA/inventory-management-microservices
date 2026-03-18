package com.inventory.inventory.application.usecase;

import com.inventory.inventory.application.dto.command.AddStockCommand;
import com.inventory.inventory.application.dto.command.CreateInventoryCommand;
import com.inventory.inventory.application.dto.command.ReleaseReservedStockCommand;
import com.inventory.inventory.application.dto.command.RemoveStockCommand;
import com.inventory.inventory.application.dto.command.ReserveStockCommand;
import com.inventory.inventory.application.dto.result.InventoryResult;

import java.util.List;
import java.util.UUID;

public interface InventoryUseCase {

    InventoryResult create(CreateInventoryCommand command);
    InventoryResult findById(UUID inventoryId);
    InventoryResult findByProductId(UUID productId);
    List<InventoryResult> findAll();
    InventoryResult addStock(AddStockCommand command);
    InventoryResult removeStock(RemoveStockCommand command);
    InventoryResult reserveStock(ReserveStockCommand command);
    InventoryResult releaseReservedStock(ReleaseReservedStockCommand command);
}
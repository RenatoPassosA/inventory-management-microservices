package com.inventory.inventory.application.mapper;

import com.inventory.inventory.application.dto.result.InventoryResult;
import com.inventory.inventory.domain.model.Inventory;

public class InventoryApplicationMapper {

    private InventoryApplicationMapper() {
    }

    public static InventoryResult toResult(Inventory inventory) {
        return new InventoryResult(inventory.getId(),
                                inventory.getProductId(),
                                inventory.getQuantityAvailable(),
                                inventory.getQuantityReserved(),
                                inventory.getTotalQuantity(),
                                inventory.getCreatedAt(),
                                inventory.getUpdatedAt()
        );
    }
}
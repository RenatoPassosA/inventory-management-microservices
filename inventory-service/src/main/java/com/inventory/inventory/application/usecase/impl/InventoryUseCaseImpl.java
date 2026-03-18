package com.inventory.inventory.application.usecase.impl;

import com.inventory.inventory.application.dto.command.AddStockCommand;
import com.inventory.inventory.application.dto.command.CreateInventoryCommand;
import com.inventory.inventory.application.dto.command.ReleaseReservedStockCommand;
import com.inventory.inventory.application.dto.command.RemoveStockCommand;
import com.inventory.inventory.application.dto.command.ReserveStockCommand;
import com.inventory.inventory.application.dto.result.InventoryResult;
import com.inventory.inventory.application.mapper.InventoryApplicationMapper;
import com.inventory.inventory.application.usecase.InventoryUseCase;
import com.inventory.inventory.domain.exception.InventoryAlreadyExistsException;
import com.inventory.inventory.domain.exception.InventoryNotFoundException;
import com.inventory.inventory.domain.model.Inventory;
import com.inventory.inventory.domain.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InventoryUseCaseImpl implements InventoryUseCase {

    private final InventoryRepository inventoryRepository;

    public InventoryUseCaseImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public InventoryResult create(CreateInventoryCommand command) {
        if (inventoryRepository.existsByProductId(command.productId())) {
            throw new InventoryAlreadyExistsException(
                    "Inventory already exists for product id: " + command.productId()
            );
        }

        Inventory inventory = Inventory.create(command.productId());
        Inventory savedInventory = inventoryRepository.save(inventory);

        return InventoryApplicationMapper.toResult(savedInventory);
    }

    @Override
    public InventoryResult findById(UUID inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for id: " + inventoryId
                ));

        return InventoryApplicationMapper.toResult(inventory);
    }

    @Override
    public InventoryResult findByProductId(UUID productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product id: " + productId
                ));

        return InventoryApplicationMapper.toResult(inventory);
    }

    @Override
    public List<InventoryResult> findAll() {
        return inventoryRepository.findAll()
                .stream()
                .map(InventoryApplicationMapper::toResult)
                .toList();
    }

    @Override
    public InventoryResult addStock(AddStockCommand command) {
        Inventory inventory = inventoryRepository.findByProductId(command.productId())
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product id: " + command.productId()
                ));

        inventory.addStock(command.quantity());

        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryApplicationMapper.toResult(savedInventory);
    }

    @Override
    public InventoryResult removeStock(RemoveStockCommand command) {
        Inventory inventory = inventoryRepository.findByProductId(command.productId())
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product id: " + command.productId()
                ));

        inventory.removeStock(command.quantity());

        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryApplicationMapper.toResult(savedInventory);
    }

    @Override
    public InventoryResult reserveStock(ReserveStockCommand command) {
        Inventory inventory = inventoryRepository.findByProductId(command.productId())
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product id: " + command.productId()
                ));

        inventory.reserveStock(command.quantity());

        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryApplicationMapper.toResult(savedInventory);
    }

    @Override
    public InventoryResult releaseReservedStock(ReleaseReservedStockCommand command) {
        Inventory inventory = inventoryRepository.findByProductId(command.productId())
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product id: " + command.productId()
                ));

        inventory.releaseReservedStock(command.quantity());

        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryApplicationMapper.toResult(savedInventory);
    }
}
package com.inventory.inventory.infrastructure.persistence.repository;

import com.inventory.inventory.domain.model.Inventory;
import com.inventory.inventory.domain.repository.InventoryRepository;
import com.inventory.inventory.infrastructure.persistence.entity.InventoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InventoryEntityJpa implements InventoryRepository {

    private final InventoryEntityRepository inventoryEntityRepository;

    public InventoryEntityJpa(InventoryEntityRepository inventoryEntityRepository) {
        this.inventoryEntityRepository = inventoryEntityRepository;
    }

    @Override
    public Inventory save(Inventory inventory) {
        InventoryEntity entity = toEntity(inventory);

        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }

        InventoryEntity savedEntity = inventoryEntityRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Inventory> findById(UUID id) {
        return inventoryEntityRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Inventory> findByProductId(UUID productId) {
        return inventoryEntityRepository.findByProductId(productId)
                .map(this::toDomain);
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryEntityRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByProductId(UUID productId) {
        return inventoryEntityRepository.existsByProductId(productId);
    }

    @Override
    public void deleteById(UUID id) {
        inventoryEntityRepository.deleteById(id);
    }

    private Inventory toDomain(InventoryEntity entity) {
        return new Inventory(
                entity.getId(),
                entity.getProductId(),
                entity.getQuantityAvailable(),
                entity.getQuantityReserved(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private InventoryEntity toEntity(Inventory inventory) {
        return new InventoryEntity(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getQuantityAvailable(),
                inventory.getQuantityReserved(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }
}
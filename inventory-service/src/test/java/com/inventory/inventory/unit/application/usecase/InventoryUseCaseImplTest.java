package com.inventory.inventory.unit.application.usecase;

import com.inventory.inventory.application.dto.command.AddStockCommand;
import com.inventory.inventory.application.dto.command.CreateInventoryCommand;
import com.inventory.inventory.application.dto.command.ReleaseReservedStockCommand;
import com.inventory.inventory.application.dto.command.RemoveStockCommand;
import com.inventory.inventory.application.dto.command.ReserveStockCommand;
import com.inventory.inventory.application.dto.result.InventoryResult;
import com.inventory.inventory.application.usecase.impl.InventoryUseCaseImpl;
import com.inventory.inventory.domain.exception.InventoryAlreadyExistsException;
import com.inventory.inventory.domain.exception.InventoryInsufficientStockException;
import com.inventory.inventory.domain.exception.InventoryNotFoundException;
import com.inventory.inventory.domain.exception.InvalidInventoryMovementException;
import com.inventory.inventory.domain.model.Inventory;
import com.inventory.inventory.domain.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryUseCaseImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryUseCaseImpl inventoryUseCase;

    private UUID inventoryId;
    private UUID productId;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        inventoryId = UUID.randomUUID();
        productId = UUID.randomUUID();
        now = OffsetDateTime.now();
    }

    private Inventory buildInventory(Integer available, Integer reserved) {
        return new Inventory(
                inventoryId,
                productId,
                available,
                reserved,
                now,
                now
        );
    }

    @Test
    @DisplayName("Deve criar inventory com sucesso")
    void shouldCreateInventorySuccessfully() {
        CreateInventoryCommand command = new CreateInventoryCommand(productId);

        when(inventoryRepository.existsByProductId(productId)).thenReturn(false);
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> {
            Inventory inventory = invocation.getArgument(0);
            inventory.setId(inventoryId);
            inventory.setCreatedAt(now);
            inventory.setUpdatedAt(now);
            return inventory;
        });

        InventoryResult result = inventoryUseCase.create(command);

        assertNotNull(result);
        assertEquals(inventoryId, result.id());
        assertEquals(productId, result.productId());
        assertEquals(0, result.quantityAvailable());
        assertEquals(0, result.quantityReserved());
        assertEquals(0, result.totalQuantity());

        verify(inventoryRepository).existsByProductId(productId);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Não deve criar inventory duplicado")
    void shouldNotCreateDuplicatedInventory() {
        CreateInventoryCommand command = new CreateInventoryCommand(productId);

        when(inventoryRepository.existsByProductId(productId)).thenReturn(true);

        assertThrows(InventoryAlreadyExistsException.class,
                () -> inventoryUseCase.create(command));

        verify(inventoryRepository).existsByProductId(productId);
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar inventory por id com sucesso")
    void shouldFindInventoryByIdSuccessfully() {
        Inventory inventory = buildInventory(10, 2);

        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));

        InventoryResult result = inventoryUseCase.findById(inventoryId);

        assertNotNull(result);
        assertEquals(inventoryId, result.id());
        assertEquals(productId, result.productId());
        assertEquals(10, result.quantityAvailable());
        assertEquals(2, result.quantityReserved());
        assertEquals(12, result.totalQuantity());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar inventory inexistente por id")
    void shouldThrowWhenFindByIdNotFound() {
        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class,
                () -> inventoryUseCase.findById(inventoryId));
    }

    @Test
    @DisplayName("Deve buscar inventory por productId com sucesso")
    void shouldFindInventoryByProductIdSuccessfully() {
        Inventory inventory = buildInventory(8, 1);

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        InventoryResult result = inventoryUseCase.findByProductId(productId);

        assertNotNull(result);
        assertEquals(productId, result.productId());
        assertEquals(8, result.quantityAvailable());
        assertEquals(1, result.quantityReserved());
        assertEquals(9, result.totalQuantity());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar inventory inexistente por productId")
    void shouldThrowWhenFindByProductIdNotFound() {
        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class,
                () -> inventoryUseCase.findByProductId(productId));
    }

    @Test
    @DisplayName("Deve listar todos os inventories")
    void shouldFindAllInventories() {
        Inventory inventory1 = buildInventory(10, 0);
        Inventory inventory2 = new Inventory(
                UUID.randomUUID(),
                UUID.randomUUID(),
                5,
                3,
                now,
                now
        );

        when(inventoryRepository.findAll()).thenReturn(List.of(inventory1, inventory2));

        List<InventoryResult> results = inventoryUseCase.findAll();

        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Deve adicionar estoque com sucesso")
    void shouldAddStockSuccessfully() {
        Inventory inventory = buildInventory(10, 0);
        AddStockCommand command = new AddStockCommand(productId, 5);

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResult result = inventoryUseCase.addStock(command);

        assertEquals(15, result.quantityAvailable());
        assertEquals(0, result.quantityReserved());
        assertEquals(15, result.totalQuantity());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar estoque em inventory inexistente")
    void shouldThrowWhenAddStockInventoryNotFound() {
        AddStockCommand command = new AddStockCommand(productId, 5);

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class,
                () -> inventoryUseCase.addStock(command));
    }

    @Test
    @DisplayName("Deve remover estoque com sucesso")
    void shouldRemoveStockSuccessfully() {
        Inventory inventory = buildInventory(10, 0);
        RemoveStockCommand command = new RemoveStockCommand(productId, 4);

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResult result = inventoryUseCase.removeStock(command);

        assertEquals(6, result.quantityAvailable());
        assertEquals(0, result.quantityReserved());
        assertEquals(6, result.totalQuantity());
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover mais estoque do que disponível")
    void shouldThrowWhenRemovingMoreThanAvailable() {
        Inventory inventory = buildInventory(3, 0);
        RemoveStockCommand command = new RemoveStockCommand(productId, 5);

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        assertThrows(InventoryInsufficientStockException.class,
                () -> inventoryUseCase.removeStock(command));

        verify(inventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve reservar estoque com sucesso")
    void shouldReserveStockSuccessfully() {
        Inventory inventory = buildInventory(10, 1);
        ReserveStockCommand command = new ReserveStockCommand(productId, 4);

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResult result = inventoryUseCase.reserveStock(command);

        assertEquals(6, result.quantityAvailable());
        assertEquals(5, result.quantityReserved());
        assertEquals(11, result.totalQuantity());
    }

    @Test
    @DisplayName("Deve lançar exceção ao reservar mais do que disponível")
    void shouldThrowWhenReserveMoreThanAvailable() {
        Inventory inventory = buildInventory(2, 0);
        ReserveStockCommand command = new ReserveStockCommand(productId, 5);

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        assertThrows(InventoryInsufficientStockException.class,
                () -> inventoryUseCase.reserveStock(command));
    }

    @Test
    @DisplayName("Deve liberar estoque reservado com sucesso")
    void shouldReleaseReservedStockSuccessfully() {
        Inventory inventory = buildInventory(5, 4);
        ReleaseReservedStockCommand command = new ReleaseReservedStockCommand(productId, 3);

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResult result = inventoryUseCase.releaseReservedStock(command);

        assertEquals(8, result.quantityAvailable());
        assertEquals(1, result.quantityReserved());
        assertEquals(9, result.totalQuantity());
    }

    @Test
    @DisplayName("Deve lançar exceção ao liberar mais reserva do que existe")
    void shouldThrowWhenReleaseMoreThanReserved() {
        Inventory inventory = buildInventory(5, 2);
        ReleaseReservedStockCommand command = new ReleaseReservedStockCommand(productId, 4);

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        assertThrows(InvalidInventoryMovementException.class,
                () -> inventoryUseCase.releaseReservedStock(command));
    }
}
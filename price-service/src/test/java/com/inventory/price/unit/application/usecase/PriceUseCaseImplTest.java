package com.inventory.price.unit.application.usecase;

import com.inventory.price.application.client.ProductClient;
import com.inventory.price.application.dto.command.CreatePriceCommand;
import com.inventory.price.application.dto.command.UpdatePriceCommand;
import com.inventory.price.application.usecase.impl.PriceUseCaseImpl;
import com.inventory.price.application.dto.result.PriceHistoryResult;
import com.inventory.price.application.dto.result.PriceResult;
import com.inventory.price.domain.exceptions.ActivePriceAlreadyExistsException;
import com.inventory.price.domain.exceptions.InvalidPriceException;
import com.inventory.price.domain.exceptions.PriceNotFoundException;
import com.inventory.price.domain.exceptions.ProductNotFoundException;
import com.inventory.price.domain.model.Price;
import com.inventory.price.domain.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PriceUseCaseImplTest {

    private PriceRepository priceRepository;
    private ProductClient productClient;
    private PriceUseCaseImpl priceUseCase;

    @BeforeEach
    void setUp() {
        priceRepository = Mockito.mock(PriceRepository.class);
        productClient = Mockito.mock(ProductClient.class);
        priceUseCase = new PriceUseCaseImpl(priceRepository, productClient);
    }

    @Test
    void shouldCreatePriceSuccessfully() {
        UUID productId = UUID.randomUUID();

        CreatePriceCommand command = new CreatePriceCommand(
                productId,
                new BigDecimal("199.90"),
                "BRL"
        );

        Price savedPrice = new Price(
                UUID.randomUUID(),
                productId,
                command.getPrice(),
                command.getCurrency(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(productClient.existsById(productId)).thenReturn(true);
        when(priceRepository.existsActiveByProductId(productId)).thenReturn(false);
        when(priceRepository.save(any(Price.class))).thenReturn(savedPrice);

        PriceResult result = priceUseCase.createPrice(command);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(new BigDecimal("199.90"), result.getPrice());
        assertEquals("BRL", result.getCurrency());
        assertTrue(result.isActive());

        verify(productClient).existsById(productId);
        verify(priceRepository).existsActiveByProductId(productId);
        verify(priceRepository).save(any(Price.class));
    }

    @Test
    void shouldThrowWhenProductNotFoundOnCreatePrice() {
        UUID productId = UUID.randomUUID();

        CreatePriceCommand command = new CreatePriceCommand(
                productId,
                new BigDecimal("199.90"),
                "BRL"
        );

        when(productClient.existsById(productId)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> priceUseCase.createPrice(command));

        verify(productClient).existsById(productId);
        verify(priceRepository, never()).existsActiveByProductId(any(UUID.class));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void shouldThrowWhenActivePriceAlreadyExists() {
        UUID productId = UUID.randomUUID();

        CreatePriceCommand command = new CreatePriceCommand(
                productId,
                new BigDecimal("199.90"),
                "BRL"
        );

        when(productClient.existsById(productId)).thenReturn(true);
        when(priceRepository.existsActiveByProductId(productId)).thenReturn(true);

        assertThrows(ActivePriceAlreadyExistsException.class, () -> priceUseCase.createPrice(command));

        verify(productClient).existsById(productId);
        verify(priceRepository).existsActiveByProductId(productId);
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void shouldThrowWhenPriceIsInvalidOnCreate() {
        UUID productId = UUID.randomUUID();

        CreatePriceCommand command = new CreatePriceCommand(
                productId,
                BigDecimal.ZERO,
                "BRL"
        );

        when(productClient.existsById(productId)).thenReturn(true);

        assertThrows(InvalidPriceException.class, () -> priceUseCase.createPrice(command));

        verify(productClient).existsById(productId);
        verify(priceRepository, never()).existsActiveByProductId(any(UUID.class));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void shouldFindCurrentPriceByProductId() {
        UUID productId = UUID.randomUUID();

        Price price = new Price(
                UUID.randomUUID(),
                productId,
                new BigDecimal("99.90"),
                "BRL",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(priceRepository.findActiveByProductId(productId)).thenReturn(Optional.of(price));

        PriceResult result = priceUseCase.getCurrentPriceByProductId(productId);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(new BigDecimal("99.90"), result.getPrice());
        assertEquals("BRL", result.getCurrency());
    }

    @Test
    void shouldThrowWhenCurrentPriceNotFound() {
        UUID productId = UUID.randomUUID();

        when(priceRepository.findActiveByProductId(productId)).thenReturn(Optional.empty());

        assertThrows(PriceNotFoundException.class, () -> priceUseCase.getCurrentPriceByProductId(productId));

        verify(priceRepository).findActiveByProductId(productId);
    }

    @Test
    void shouldUpdatePriceSuccessfully() {
        UUID priceId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Price existingPrice = new Price(
                priceId,
                productId,
                new BigDecimal("99.90"),
                "BRL",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        UpdatePriceCommand command = new UpdatePriceCommand(
                new BigDecimal("129.90"),
                "BRL"
        );

        when(priceRepository.findById(priceId)).thenReturn(Optional.of(existingPrice));
        when(productClient.existsById(productId)).thenReturn(true);
        when(priceRepository.save(any(Price.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PriceResult result = priceUseCase.updatePrice(priceId, command);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(new BigDecimal("129.90"), result.getPrice());
        assertEquals("BRL", result.getCurrency());
        assertTrue(result.isActive());

        verify(priceRepository).findById(priceId);
        verify(productClient).existsById(productId);
        verify(priceRepository).deactivateCurrentPrice(productId);
        verify(priceRepository).save(any(Price.class));
    }

    @Test
    void shouldThrowWhenPriceNotFoundOnUpdate() {
        UUID priceId = UUID.randomUUID();

        UpdatePriceCommand command = new UpdatePriceCommand(
                new BigDecimal("129.90"),
                "BRL"
        );

        when(priceRepository.findById(priceId)).thenReturn(Optional.empty());

        assertThrows(PriceNotFoundException.class, () -> priceUseCase.updatePrice(priceId, command));

        verify(priceRepository).findById(priceId);
        verify(productClient, never()).existsById(any(UUID.class));
        verify(priceRepository, never()).deactivateCurrentPrice(any(UUID.class));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void shouldThrowWhenPriceIsInvalidOnUpdate() {
        UUID priceId = UUID.randomUUID();

        UpdatePriceCommand command = new UpdatePriceCommand(
                new BigDecimal("-10.00"),
                "BRL"
        );

        assertThrows(InvalidPriceException.class, () -> priceUseCase.updatePrice(priceId, command));

        verify(priceRepository, never()).findById(any(UUID.class));
        verify(priceRepository, never()).deactivateCurrentPrice(any(UUID.class));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void shouldThrowWhenProductNotFoundOnUpdate() {
        UUID priceId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Price existingPrice = new Price(
                priceId,
                productId,
                new BigDecimal("99.90"),
                "BRL",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        UpdatePriceCommand command = new UpdatePriceCommand(
                new BigDecimal("129.90"),
                "BRL"
        );

        when(priceRepository.findById(priceId)).thenReturn(Optional.of(existingPrice));
        when(productClient.existsById(productId)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> priceUseCase.updatePrice(priceId, command));

        verify(priceRepository).findById(priceId);
        verify(productClient).existsById(productId);
        verify(priceRepository, never()).deactivateCurrentPrice(any(UUID.class));
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void shouldGetPriceHistorySuccessfully() {
        UUID productId = UUID.randomUUID();

        Price oldPrice = new Price(
                UUID.randomUUID(),
                productId,
                new BigDecimal("79.90"),
                "BRL",
                false,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(5)
        );

        Price currentPrice = new Price(
                UUID.randomUUID(),
                productId,
                new BigDecimal("99.90"),
                "BRL",
                true,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1)
        );

        when(productClient.existsById(productId)).thenReturn(true);
        when(priceRepository.findAllByProductId(productId)).thenReturn(List.of(oldPrice, currentPrice));

        PriceHistoryResult result = priceUseCase.getPriceHistoryByProductId(productId);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(2, result.getPrices().size());

        verify(productClient).existsById(productId);
        verify(priceRepository).findAllByProductId(productId);
    }

    @Test
    void shouldThrowWhenProductNotFoundOnGetHistory() {
        UUID productId = UUID.randomUUID();

        when(productClient.existsById(productId)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> priceUseCase.getPriceHistoryByProductId(productId));

        verify(productClient).existsById(productId);
        verify(priceRepository, never()).findAllByProductId(any(UUID.class));
    }
}
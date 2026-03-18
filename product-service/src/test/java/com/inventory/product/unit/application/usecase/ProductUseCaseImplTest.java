package com.inventory.product.unit.application.usecase;

import com.inventory.product.application.dto.command.CreateProductCommand;
import com.inventory.product.application.dto.command.UpdateProductCommand;
import com.inventory.product.application.dto.result.ProductResult;
import com.inventory.product.application.usecase.impl.ProductUseCaseImpl;
import com.inventory.product.domain.enums.ProductStatus;
import com.inventory.product.domain.exceptions.ProductAlreadyExistsException;
import com.inventory.product.domain.exceptions.ProductNotFoundException;
import com.inventory.product.domain.model.Product;
import com.inventory.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductUseCaseImplTest {

    private ProductRepository productRepository;
    private ProductUseCaseImpl productUseCase;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        productUseCase = new ProductUseCaseImpl(productRepository);
    }

    @Test
    void shouldCreateProductSuccessfully() {
        CreateProductCommand command = new CreateProductCommand(
                "Notebook Dell",
                "Notebook para trabalho",
                "DELL-001",
                "INFORMATICA"
        );

        Product savedProduct = new Product(
                UUID.randomUUID(),
                command.getName(),
                command.getDescription(),
                command.getSku(),
                command.getCategory(),
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(productRepository.existsBySku("DELL-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResult result = productUseCase.create(command);

        assertNotNull(result);
        assertEquals("Notebook Dell", result.getName());
        assertEquals("DELL-001", result.getSku());

        verify(productRepository).existsBySku("DELL-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenSkuAlreadyExists() {
        CreateProductCommand command = new CreateProductCommand(
                "Notebook Dell",
                "Notebook para trabalho",
                "DELL-001",
                "INFORMATICA"
        );

        when(productRepository.existsBySku("DELL-001")).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> productUseCase.create(command));

        verify(productRepository).existsBySku("DELL-001");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldFindProductById() {
        UUID id = UUID.randomUUID();

        Product product = new Product(
                id,
                "Notebook Dell",
                "Notebook para trabalho",
                "DELL-001",
                "INFORMATICA",
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        ProductResult result = productUseCase.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void shouldThrowWhenProductNotFoundById() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productUseCase.findById(id));
    }

    @Test
    void shouldFindProductBySku() {
        Product product = new Product(
                UUID.randomUUID(),
                "Notebook Dell",
                "Notebook para trabalho",
                "DELL-001",
                "INFORMATICA",
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(productRepository.findBySku("DELL-001")).thenReturn(Optional.of(product));

        ProductResult result = productUseCase.findBySku("DELL-001");

        assertNotNull(result);
        assertEquals("DELL-001", result.getSku());
        assertEquals("Notebook Dell", result.getName());

        verify(productRepository).findBySku("DELL-001");
    }

    @Test
    void shouldThrowWhenProductNotFoundBySku() {
        when(productRepository.findBySku("SKU-INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productUseCase.findBySku("SKU-INEXISTENTE"));

        verify(productRepository).findBySku("SKU-INEXISTENTE");
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        UUID id = UUID.randomUUID();

        Product product = new Product(
                id,
                "Notebook Antigo",
                "Desc antiga",
                "DELL-001",
                "OLD",
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        UpdateProductCommand command = new UpdateProductCommand(
                id,
                "Notebook Novo",
                "Desc nova",
                "INFORMATICA"
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResult result = productUseCase.update(id, command);

        assertEquals("Notebook Novo", result.getName());
        assertEquals("INFORMATICA", result.getCategory());
    }

    @Test
    void shouldThrowWhenProductNotFoundOnUpdate() {
        UUID id = UUID.randomUUID();

        UpdateProductCommand command = new UpdateProductCommand(
                id,
                "Notebook Novo",
                "Descrição nova",
                "INFORMATICA"
        );

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productUseCase.update(id, command));

        verify(productRepository).findById(id);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        UUID id = UUID.randomUUID();

        Product product = new Product(
                id,
                "Notebook Dell",
                "Notebook para trabalho",
                "DELL-001",
                "INFORMATICA",
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productUseCase.deleteById(id));

        verify(productRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenProductNotFoundOnDelete() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productUseCase.deleteById(id));

        verify(productRepository).findById(id);
        verify(productRepository, never()).deleteById(any(UUID.class));
    }
}
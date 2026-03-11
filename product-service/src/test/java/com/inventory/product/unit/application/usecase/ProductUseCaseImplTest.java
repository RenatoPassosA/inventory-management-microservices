package com.inventory.product.unit.application.usecase;

import com.inventory.product.application.dto.request.CreateProductRequest;
import com.inventory.product.application.dto.request.UpdateProductRequest;
import com.inventory.product.application.dto.response.ProductResponse;
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
        CreateProductRequest request = new CreateProductRequest(
                "Notebook Dell",
                "Notebook para trabalho",
                "DELL-001",
                "INFORMATICA"
        );

        Product savedProduct = new Product(
                UUID.randomUUID(),
                request.getName(),
                request.getDescription(),
                request.getSku(),
                request.getCategory(),
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(productRepository.existsBySku("DELL-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse response = productUseCase.create(request);

        assertNotNull(response);
        assertEquals("Notebook Dell", response.getName());
        assertEquals("DELL-001", response.getSku());

        verify(productRepository).existsBySku("DELL-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenSkuAlreadyExists() {
        CreateProductRequest request = new CreateProductRequest(
                "Notebook Dell",
                "Notebook para trabalho",
                "DELL-001",
                "INFORMATICA"
        );

        when(productRepository.existsBySku("DELL-001")).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> productUseCase.create(request));

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

        ProductResponse response = productUseCase.findById(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
    }

    @Test
    void shouldThrowWhenProductNotFoundById() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productUseCase.findById(id));
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

        UpdateProductRequest request = new UpdateProductRequest(
                "Notebook Novo",
                "Desc nova",
                "INFORMATICA"
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productUseCase.update(id, request);

        assertEquals("Notebook Novo", response.getName());
        assertEquals("INFORMATICA", response.getCategory());
    }

    @Test
    void shouldThrowWhenProductNotFoundOnUpdate() {
        UUID id = UUID.randomUUID();

        UpdateProductRequest request = new UpdateProductRequest(
                "Notebook Novo",
                "Descrição nova",
                "INFORMATICA"
        );

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productUseCase.update(id, request));

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
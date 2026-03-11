package com.inventory.product.application.usecase.impl;

import com.inventory.product.application.dto.request.CreateProductRequest;
import com.inventory.product.application.dto.request.UpdateProductRequest;
import com.inventory.product.application.dto.response.ProductResponse;
import com.inventory.product.application.usecase.ProductApplicationService;
import com.inventory.product.domain.enums.ProductStatus;
import com.inventory.product.domain.exceptions.InvalidProductDataException;
import com.inventory.product.domain.exceptions.ProductAlreadyExistsException;
import com.inventory.product.domain.exceptions.ProductNotFoundException;
import com.inventory.product.domain.model.Product;
import com.inventory.product.domain.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductUseCaseImpl implements ProductApplicationService {

    private final ProductRepository productRepository;

    public ProductUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse create(CreateProductRequest request) {
        validateCreateRequest(request);

        if (productRepository.existsBySku(request.getSku())) {
            throw new ProductAlreadyExistsException("Já existe um produto cadastrado com o SKU informado.");
        }

        LocalDateTime now = LocalDateTime.now();

        Product product = new Product(
                UUID.randomUUID(),
                request.getName().trim(),
                request.getDescription(),
                request.getSku().trim(),
                request.getCategory().trim(),
                ProductStatus.ACTIVE,
                now,
                now
        );

        Product savedProduct = productRepository.save(product);
        return toResponse(savedProduct);
    }

    @Override
    public ProductResponse findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado."));

        return toResponse(product);
    }

    @Override
    public Page<ProductResponse> findAll(int page, int size) {
        Page<Product> productsPage = productRepository.findAll(PageRequest.of(page, size));

        List<ProductResponse> content = productsPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(content, productsPage.getPageable(), productsPage.getTotalElements());
    }

    @Override
    public ProductResponse findBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado para o SKU informado."));

        return toResponse(product);
    }

    @Override
    public ProductResponse update(UUID id, UpdateProductRequest request) {
        validateUpdateRequest(request);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado."));

        product.update(
                request.getName().trim(),
                request.getDescription(),
                request.getCategory().trim()
        );

        Product updatedProduct = productRepository.save(product);
        return toResponse(updatedProduct);
    }

    @Override
    public void deleteById(UUID id) {
        productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado."));

        productRepository.deleteById(id);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getCategory(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private void validateCreateRequest(CreateProductRequest request) {
        if (request == null) {
            throw new InvalidProductDataException("Os dados do produto são obrigatórios.");
        }

        if (isBlank(request.getName())) {
            throw new InvalidProductDataException("O nome do produto é obrigatório.");
        }

        if (isBlank(request.getSku())) {
            throw new InvalidProductDataException("O SKU do produto é obrigatório.");
        }

        if (isBlank(request.getCategory())) {
            throw new InvalidProductDataException("A categoria do produto é obrigatória.");
        }
    }

    private void validateUpdateRequest(UpdateProductRequest request) {
        if (request == null) {
            throw new InvalidProductDataException("Os dados do produto são obrigatórios.");
        }

        if (isBlank(request.getName())) {
            throw new InvalidProductDataException("O nome do produto é obrigatório.");
        }

        if (isBlank(request.getCategory())) {
            throw new InvalidProductDataException("A categoria do produto é obrigatória.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
package com.inventory.product.application.usecase;

import com.inventory.product.application.dto.request.CreateProductRequest;
import com.inventory.product.application.dto.request.UpdateProductRequest;
import com.inventory.product.application.dto.response.ProductResponse;

import java.util.UUID;

import org.springframework.data.domain.Page;

public interface ProductApplicationService {

    ProductResponse create(CreateProductRequest request);
    ProductResponse findById(UUID id);
    Page <ProductResponse> findAll(int page, int size);
    ProductResponse findBySku(String sku);
    ProductResponse update(UUID id, UpdateProductRequest request);
    void deleteById(UUID id);
}
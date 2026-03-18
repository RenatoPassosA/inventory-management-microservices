package com.inventory.product.application.usecase;

import com.inventory.product.application.dto.result.ProductResult;
import com.inventory.product.application.dto.command.CreateProductCommand;
import com.inventory.product.application.dto.command.UpdateProductCommand;

import java.util.UUID;

import org.springframework.data.domain.Page;

public interface ProductUseCase {

    ProductResult create(CreateProductCommand request);
    ProductResult findById(UUID id);
    Page <ProductResult> findAll(int page, int size);
    ProductResult findBySku(String sku);
    ProductResult update(UUID id, UpdateProductCommand request);
    void deleteById(UUID id);
}
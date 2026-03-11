package com.inventory.product.domain.repository;

import com.inventory.product.domain.model.Product;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Product save(Product product);
    Optional<Product> findById(UUID id);
    Optional<Product> findBySku(String sku);
    Page<Product> findAll(Pageable pageable);
    boolean existsBySku(String sku);
    void deleteById(UUID id);
}
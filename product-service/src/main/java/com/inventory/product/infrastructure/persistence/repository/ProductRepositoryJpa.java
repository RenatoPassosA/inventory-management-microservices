package com.inventory.product.infrastructure.persistence.repository;

import com.inventory.product.domain.model.Product;
import com.inventory.product.domain.repository.ProductRepository;
import com.inventory.product.infrastructure.persistence.entity.ProductEntity;
import com.inventory.product.infrastructure.persistence.mapper.ProductEntityMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductRepositoryJpa implements ProductRepository {

    private final ProductEntityRepository ProductEntityRepository;
    private final ProductEntityMapper productEntityMapper;

    public ProductRepositoryJpa(ProductEntityRepository ProductEntityRepository,
                                 ProductEntityMapper productEntityMapper) {
        this.ProductEntityRepository = ProductEntityRepository;
        this.productEntityMapper = productEntityMapper;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = productEntityMapper.toEntity(product);
        ProductEntity savedEntity = ProductEntityRepository.save(entity);
        return productEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return ProductEntityRepository.findById(id)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return ProductEntityRepository.findBySku(sku)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return ProductEntityRepository.findAll(pageable)
                .map(productEntityMapper::toDomain);
    }

    @Override
    public boolean existsBySku(String sku) {
        return ProductEntityRepository.existsBySku(sku);
    }

    @Override
    public void deleteById(UUID id) {
        ProductEntityRepository.deleteById(id);
    }
}
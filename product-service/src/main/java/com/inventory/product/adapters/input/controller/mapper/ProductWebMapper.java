package com.inventory.product.adapters.input.controller.mapper;

import com.inventory.product.adapters.input.controller.request.CreateProductRequest;
import com.inventory.product.adapters.input.controller.request.UpdateProductRequest;
import com.inventory.product.adapters.input.controller.response.ProductResponse;
import com.inventory.product.application.dto.command.CreateProductCommand;
import com.inventory.product.application.dto.command.UpdateProductCommand;
import com.inventory.product.application.dto.result.ProductResult;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class ProductWebMapper {

    public CreateProductCommand toCreateCommand(CreateProductRequest request) {
        if (request == null) {
            return null;
        }

        return new CreateProductCommand(
                request.getName(),
                request.getDescription(),
                request.getSku(),
                request.getCategory()
        );
    }

    public UpdateProductCommand toUpdateCommand(UUID id, UpdateProductRequest request) {
        if (request == null) {
            return null;
        }

        return new UpdateProductCommand(
                id,
                request.getName(),
                request.getDescription(),
                request.getCategory()
        );
    }

    public ProductResponse toResponse(ProductResult result) {
        if (result == null) {
            return null;
        }

        return new ProductResponse(
                result.getId(),
                result.getName(),
                result.getDescription(),
                result.getSku(),
                result.getCategory(),
                result.getStatus(),
                result.getCreatedAt(),
                result.getUpdatedAt()
        );
    }
}
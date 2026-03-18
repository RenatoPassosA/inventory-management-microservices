package com.inventory.product.adapters.input.controller;

import com.inventory.product.adapters.input.controller.mapper.ProductWebMapper;
import com.inventory.product.adapters.input.controller.request.CreateProductRequest;
import com.inventory.product.adapters.input.controller.request.UpdateProductRequest;
import com.inventory.product.adapters.input.controller.response.ProductResponse;
import com.inventory.product.application.dto.command.CreateProductCommand;
import com.inventory.product.application.dto.command.UpdateProductCommand;
import com.inventory.product.application.dto.result.ProductResult;
import com.inventory.product.application.usecase.ProductUseCase;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductUseCase productUseCase;
    private final ProductWebMapper productWebMapper;

    public ProductController(ProductUseCase productUseCase,
                             ProductWebMapper productWebMapper) {
        this.productUseCase = productUseCase;
        this.productWebMapper = productWebMapper;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        CreateProductCommand command = productWebMapper.toCreateCommand(request);
        ProductResult result = productUseCase.create(command);
        ProductResponse response = productWebMapper.toResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable UUID id) {
        ProductResult result = productUseCase.findById(id);
        ProductResponse response = productWebMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> findBySku(@PathVariable String sku) {
        ProductResult result = productUseCase.findBySku(sku);
        ProductResponse response = productWebMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Page<ProductResult> resultPage = productUseCase.findAll(page, size);
        Page<ProductResponse> responsePage = resultPage.map(productWebMapper::toResponse);

        return ResponseEntity.ok(responsePage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateProductRequest request) {
        UpdateProductCommand command = productWebMapper.toUpdateCommand(id, request);
        ProductResult result = productUseCase.update(id, command);
        ProductResponse response = productWebMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productUseCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
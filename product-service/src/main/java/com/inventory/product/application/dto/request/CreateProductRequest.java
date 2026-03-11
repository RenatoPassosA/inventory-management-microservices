package com.inventory.product.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateProductRequest {

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(max = 120, message = "O nome do produto deve ter no máximo 120 caracteres")
    private String name;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String description;

    @NotBlank(message = "O SKU do produto é obrigatório")
    @Size(max = 60, message = "O SKU do produto deve ter no máximo 60 caracteres")
    private String sku;

    @NotBlank(message = "A categoria do produto é obrigatória")
    @Size(max = 80, message = "A categoria deve ter no máximo 80 caracteres")
    private String category;

    public CreateProductRequest() {
    }

    public CreateProductRequest(String name, String description, String sku, String category) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSku() {
        return sku;
    }

    public String getCategory() {
        return category;
    }
}
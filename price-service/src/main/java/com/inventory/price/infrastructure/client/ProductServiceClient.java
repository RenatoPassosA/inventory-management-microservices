package com.inventory.price.infrastructure.client;

import com.inventory.price.application.client.ProductClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
public class ProductServiceClient implements ProductClient {

    private final WebClient webClient;
    private final String productServiceBaseUrl;

    public ProductServiceClient(WebClient webClient, @Value("${services.product-service.url}") String productServiceBaseUrl) {
        this.webClient = webClient;
        this.productServiceBaseUrl = productServiceBaseUrl;
    }

    @Override
    public boolean existsById(UUID productId) {
        try {
            webClient.get()
                    .uri(productServiceBaseUrl + "/products/{id}", productId)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            response -> {
                                throw new RuntimeException("Product not found");
                            }
                    )
                    .onStatus(
                            HttpStatusCode::is5xxServerError,
                            response -> {
                                throw new RuntimeException("Error communicating with product-service");
                            }
                    )
                    .toBodilessEntity()
                    .block();

            return true;
        } catch (RuntimeException ex) {
            if ("Product not found".equals(ex.getMessage())) {
                return false;
            }
            throw ex;
        }
    }
}
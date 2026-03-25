package com.inventory.order.infrastructure.client;

import com.inventory.order.application.client.InventoryClient;
import com.inventory.order.domain.exceptions.InvalidOrderException;
import com.inventory.order.infrastructure.client.request.ReserveStockRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
public class InventoryClientImpl implements InventoryClient {

    private final WebClient webClient;
    private final String inventoryServiceBaseUrl;

    public InventoryClientImpl(WebClient webClient,
                               @Value("${services.inventory-service.url}") String inventoryServiceBaseUrl) {
        this.webClient = webClient;
        this.inventoryServiceBaseUrl = inventoryServiceBaseUrl;
    }

    @Override
    public void reserveStock(UUID productId, Integer quantity) {
        ReserveStockRequest request = new ReserveStockRequest(productId, quantity);

        webClient.post()
                .uri(inventoryServiceBaseUrl + "/inventory/reserve")
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> {
                            throw new InvalidOrderException("Unable to reserve stock for product: " + productId);
                        }
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> {
                            throw new RuntimeException("Error communicating with inventory-service");
                        }
                )
                .toBodilessEntity()
                .block();
    }
}
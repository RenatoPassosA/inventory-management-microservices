package com.inventory.order.infrastructure.client;

import com.inventory.order.application.client.InventoryClient;
import com.inventory.order.domain.exceptions.InvalidOrderException;
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
        webClient.patch()
                .uri(inventoryServiceBaseUrl + "/inventories/product/{productId}/reserve-stock", productId)
                .bodyValue(java.util.Map.of("quantity", quantity))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(body -> new InvalidOrderException(
                                        "Inventory-service returned error. Status="
                                                + clientResponse.statusCode().value()
                                                + ", body=" + body
                                ))
                )
                .toBodilessEntity()
                .block();
    }
}
package com.inventory.order.infrastructure.client;

import com.inventory.order.application.client.PriceClient;
import com.inventory.order.domain.exceptions.InvalidOrderException;
import com.inventory.order.infrastructure.client.response.PriceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PriceClientImpl implements PriceClient {

    private final WebClient webClient;
    private final String priceServiceBaseUrl;

    public PriceClientImpl(WebClient webClient,
                           @Value("${services.price-service.url}") String priceServiceBaseUrl) {
        this.webClient = webClient;
        this.priceServiceBaseUrl = priceServiceBaseUrl;
    }

    @Override
    public BigDecimal getActivePriceByProductId(UUID productId) {
        PriceResponse response = webClient.get()
                .uri(priceServiceBaseUrl + "/prices/active/{productId}", productId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> {
                            throw new InvalidOrderException("Active price not found for product: " + productId);
                        }
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> {
                            throw new RuntimeException("Error communicating with price-service");
                        }
                )
                .bodyToMono(PriceResponse.class)
                .block();

        if (response == null || response.getPrice() == null) {
            throw new InvalidOrderException("Active price not found for product: " + productId);
        }

        return response.getPrice();
    }
}
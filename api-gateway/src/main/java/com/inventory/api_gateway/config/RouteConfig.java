package com.inventory.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    @Value("${services.price-service.url}")
    private String priceServiceUrl;

    @Value("${services.inventory-service.url}")
    private String inventoryServiceUrl;

    @Value("${services.order-service.url}")
    private String orderServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", route -> route
                        .path("/products/**")
                        .uri(productServiceUrl))
                .route("price-service", route -> route
                        .path("/prices/**")
                        .uri(priceServiceUrl))
                .route("inventory-service", route -> route
                        .path("/inventories/**")
                        .uri(inventoryServiceUrl))
                .route("order-service", route -> route
                        .path("/orders/**")
                        .uri(orderServiceUrl))
                .build();
    }
}
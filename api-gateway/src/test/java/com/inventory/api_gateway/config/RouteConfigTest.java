package com.inventory.api_gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class RouteConfigTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void shouldLoadRouteLocator() {
        assertNotNull(routeLocator);
    }

    @Test
    void shouldRegisterAllGatewayRoutes() {
        List<Route> routes = routeLocator.getRoutes().collectList().block();

        assertNotNull(routes);

        List<String> routeIds = routes.stream()
                .map(Route::getId)
                .collect(Collectors.toList());

        assertTrue(routeIds.contains("product-service"));
        assertTrue(routeIds.contains("price-service"));
        assertTrue(routeIds.contains("inventory-service"));
        assertTrue(routeIds.contains("order-service"));
    }
}
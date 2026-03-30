package com.inventory.api_gateway.integration;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GatewayRoutingIntegrationTest {

    private static DisposableServer productServer;
    private static DisposableServer priceServer;
    private static DisposableServer inventoryServer;
    private static DisposableServer orderServer;

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeAll
    static void setUpServers() {
        productServer = HttpServer.create()
                .port(0)
                .route(routes -> routes
                        .get("/products", (request, response) ->
                                response.header("Content-Type", "application/json")
                                        .sendString(Mono.just("""
                                                {"service":"product-service","method":"GET","path":"/products"}
                                                """))
                        )
                        .get("/products/123", (request, response) ->
                                response.header("Content-Type", "application/json")
                                        .sendString(Mono.just("""
                                                {"service":"product-service","method":"GET","path":"/products/123"}
                                                """))
                        )
                )
                .bindNow();

        priceServer = HttpServer.create()
                .port(0)
                .route(routes -> routes
                        .get("/prices/product/abc", (request, response) ->
                                response.header("Content-Type", "application/json")
                                        .sendString(Mono.just("""
                                                {"service":"price-service","method":"GET","path":"/prices/product/abc"}
                                                """))
                        )
                )
                .bindNow();

        inventoryServer = HttpServer.create()
                .port(0)
                .route(routes -> routes
                        .route(req -> req.method().name().equals("PATCH")
                                        && req.uri().equals("/inventories/product/abc/reserve-stock"),
                                (request, response) ->
                                        response.status(400)
                                                .header("Content-Type", "application/json")
                                                .sendString(Mono.just("""
                                                        {"status":400,"error":"Bad Request","message":"Insufficient available stock"}
                                                        """))
                        )
                )
                .bindNow();

        orderServer = HttpServer.create()
                .port(0)
                .route(routes -> routes
                        .post("/orders", (request, response) ->
                                response.header("Content-Type", "application/json")
                                        .sendString(Mono.just("""
                                                {"service":"order-service","method":"POST","path":"/orders","status":"CONFIRMED"}
                                                """))
                        )
                )
                .bindNow();
    }

    @AfterAll
    static void tearDownServers() {
        if (productServer != null) {
            productServer.disposeNow();
        }
        if (priceServer != null) {
            priceServer.disposeNow();
        }
        if (inventoryServer != null) {
            inventoryServer.disposeNow();
        }
        if (orderServer != null) {
            orderServer.disposeNow();
        }
    }

    @BeforeEach
    void setUpClient() {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("services.product-service.url", () -> "http://localhost:" + productServer.port());
        registry.add("services.price-service.url", () -> "http://localhost:" + priceServer.port());
        registry.add("services.inventory-service.url", () -> "http://localhost:" + inventoryServer.port());
        registry.add("services.order-service.url", () -> "http://localhost:" + orderServer.port());
    }

    @Test
    void shouldRouteGetRequestToProductService() {
        webTestClient.get()
                .uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.service").isEqualTo("product-service")
                .jsonPath("$.method").isEqualTo("GET")
                .jsonPath("$.path").isEqualTo("/products");
    }

    @Test
    void shouldRouteGetRequestWithPathVariableToProductService() {
        webTestClient.get()
                .uri("/products/123")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.service").isEqualTo("product-service")
                .jsonPath("$.method").isEqualTo("GET")
                .jsonPath("$.path").isEqualTo("/products/123");
    }

    @Test
    void shouldRoutePostRequestToOrderService() {
        webTestClient.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "items": [
                            {
                              "productId": "abc",
                              "quantity": 2
                            }
                          ]
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.service").isEqualTo("order-service")
                .jsonPath("$.method").isEqualTo("POST")
                .jsonPath("$.path").isEqualTo("/orders")
                .jsonPath("$.status").isEqualTo("CONFIRMED");
    }

    @Test
    void shouldPropagateErrorFromInventoryService() {
        webTestClient.patch()
                .uri("/inventories/product/abc/reserve-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "quantity": 2
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").isEqualTo("Insufficient available stock");
    }

    @Test
    void shouldExposeHealthEndpoint() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }
}
package com.inventory.price.integration;

import com.inventory.price.application.client.ProductClient;
import com.inventory.price.infrastructure.persistence.repository.PriceEntityRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PriceControllerIntegrationTest {

    @Autowired
    private PriceEntityRepository priceEntityRepository;

    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("pricedb_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductClient productClient;

    @BeforeEach
    void setUp() {
        priceEntityRepository.deleteAll();
        Mockito.reset(productClient);
    }

    @Test
    void shouldCreateAndGetCurrentPrice() throws Exception {
        String productId = UUID.randomUUID().toString();

        when(productClient.existsById(UUID.fromString(productId))).thenReturn(true);

        String body = """
            {
              "productId": "%s",
              "price": 199.90,
              "currency": "BRL"
            }
            """.formatted(productId);

        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.price").value(199.90))
                .andExpect(jsonPath("$.currency").value("BRL"))
                .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(get("/prices/product/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.price").value(199.90))
                .andExpect(jsonPath("$.currency").value("BRL"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturn404WhenProductNotFoundOnCreate() throws Exception {
        String productId = UUID.randomUUID().toString();

        when(productClient.existsById(UUID.fromString(productId))).thenReturn(false);

        String body = """
            {
              "productId": "%s",
              "price": 199.90,
              "currency": "BRL"
            }
            """.formatted(productId);

        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenCreatePayloadIsInvalid() throws Exception {
        String body = """
            {
              "productId": null,
              "price": 0,
              "currency": ""
            }
            """;

        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenCurrentPriceNotFound() throws Exception {
        mockMvc.perform(get("/prices/product/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdatePriceSuccessfully() throws Exception {
        String productId = UUID.randomUUID().toString();

        when(productClient.existsById(UUID.fromString(productId))).thenReturn(true);

        String createBody = """
            {
              "productId": "%s",
              "price": 99.90,
              "currency": "BRL"
            }
            """.formatted(productId);

        String response = mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        String updateBody = """
            {
              "price": 129.90,
              "currency": "BRL"
            }
            """;

        mockMvc.perform(put("/prices/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.price").value(129.90))
                .andExpect(jsonPath("$.currency").value("BRL"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturn404WhenPriceNotFoundOnUpdate() throws Exception {
        String updateBody = """
            {
              "price": 129.90,
              "currency": "BRL"
            }
            """;

        mockMvc.perform(put("/prices/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenUpdatePayloadIsInvalid() throws Exception {
        String updateBody = """
            {
              "price": -10,
              "currency": ""
            }
            """;

        mockMvc.perform(put("/prices/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnPriceHistorySuccessfully() throws Exception {
        String productId = UUID.randomUUID().toString();

        when(productClient.existsById(UUID.fromString(productId))).thenReturn(true);

        String createBody = """
            {
              "productId": "%s",
              "price": 99.90,
              "currency": "BRL"
            }
            """.formatted(productId);

        String response = mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        String updateBody = """
            {
              "price": 149.90,
              "currency": "BRL"
            }
            """;

        mockMvc.perform(put("/prices/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/prices/history/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.prices.length()").value(2));
    }

    @Test
    void shouldReturn404WhenProductNotFoundOnGetHistory() throws Exception {
        String productId = UUID.randomUUID().toString();

        when(productClient.existsById(UUID.fromString(productId))).thenReturn(false);

        mockMvc.perform(get("/prices/history/" + productId))
                .andExpect(status().isNotFound());
    }
}
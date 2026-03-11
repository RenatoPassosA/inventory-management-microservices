package com.inventory.product.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductControllerIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("productdb_test")
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

    @Test
    void shouldCreateAndListProduct() throws Exception {
        String body = """
            {
              "name": "Notebook Dell",
              "description": "Notebook para trabalho",
              "sku": "DELL-001",
              "category": "INFORMATICA"
            }
            """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Notebook Dell"))
                .andExpect(jsonPath("$.sku").value("DELL-001"));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].sku").value("DELL-001"));
    }

    @Test
    void shouldFindProductBySku() throws Exception {
        String body = """
            {
              "name": "Mouse Logitech",
              "description": "Mouse sem fio",
              "sku": "MOUSE-001",
              "category": "PERIFERICOS"
            }
            """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/products/sku/MOUSE-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mouse Logitech"));
    }

    @Test
    void shouldReturn400WhenCreatePayloadIsInvalid() throws Exception {
        String body = """
            {
              "name": "",
              "description": "Teste",
              "sku": "",
              "category": ""
            }
            """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenProductNotFoundById() throws Exception {
        mockMvc.perform(get("/products/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenProductNotFoundBySku() throws Exception {
        mockMvc.perform(get("/products/sku/SKU-INEXISTENTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        String createBody = """
            {
            "name": "Notebook Dell",
            "description": "Notebook para trabalho",
            "sku": "DELL-002",
            "category": "INFORMATICA"
            }
            """;

        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        String updateBody = """
            {
            "name": "Notebook Dell Atualizado",
            "description": "Notebook atualizado",
            "category": "ELETRONICOS"
            }
            """;

        mockMvc.perform(put("/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook Dell Atualizado"))
                .andExpect(jsonPath("$.category").value("ELETRONICOS"));
    }

    @Test
    void shouldDeleteProductSuccessfully() throws Exception {
        String createBody = """
            {
            "name": "Teclado Mecânico",
            "description": "Teclado gamer",
            "sku": "KEYBOARD-001",
            "category": "PERIFERICOS"
            }
            """;

        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/products/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/products/" + id))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldReturnPaginatedProducts() throws Exception {
        String body1 = """
            {
            "name": "Produto 1",
            "description": "Desc 1",
            "sku": "SKU-101",
            "category": "CAT1"
            }
            """;

        String body2 = """
            {
            "name": "Produto 2",
            "description": "Desc 2",
            "sku": "SKU-102",
            "category": "CAT1"
            }
            """;

        String body3 = """
            {
            "name": "Produto 3",
            "description": "Desc 3",
            "sku": "SKU-103",
            "category": "CAT1"
            }
            """;

        mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON).content(body1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON).content(body2))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON).content(body3))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/products?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.size").value(2));
    }
}
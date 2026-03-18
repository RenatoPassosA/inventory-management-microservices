package com.inventory.inventory.integration;

import com.inventory.inventory.infrastructure.persistence.entity.InventoryEntity;
import com.inventory.inventory.infrastructure.persistence.repository.InventoryEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class InventoryControllerIT {

    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("inventory_test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryEntityRepository inventoryEntityRepository;

    @BeforeEach
    void cleanUp() {
        inventoryEntityRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar inventory com sucesso")
    void shouldCreateInventorySuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();

        String body = """
                {
                  "productId": "%s"
                }
                """.formatted(productId);

        mockMvc.perform(post("/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.quantityAvailable").value(0))
                .andExpect(jsonPath("$.quantityReserved").value(0))
                .andExpect(jsonPath("$.totalQuantity").value(0));
    }

    @Test
    @DisplayName("Não deve criar inventory duplicado")
    void shouldNotCreateDuplicatedInventory() throws Exception {
        UUID productId = UUID.randomUUID();

        InventoryEntity entity = new InventoryEntity(
                UUID.randomUUID(),
                productId,
                0,
                0,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        inventoryEntityRepository.save(entity);

        String body = """
                {
                  "productId": "%s"
                }
                """.formatted(productId);

        mockMvc.perform(post("/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve validar request inválido ao criar inventory")
    void shouldReturnBadRequestWhenCreateInventoryRequestIsInvalid() throws Exception {
        String body = """
                {
                  "productId": null
                }
                """;

        mockMvc.perform(post("/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("Deve buscar inventory por productId")
    void shouldFindInventoryByProductId() throws Exception {
        UUID productId = UUID.randomUUID();

        InventoryEntity entity = new InventoryEntity(
                UUID.randomUUID(),
                productId,
                10,
                2,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        inventoryEntityRepository.save(entity);

        mockMvc.perform(get("/inventories/product/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId.toString()))
                .andExpect(jsonPath("$.quantityAvailable").value(10))
                .andExpect(jsonPath("$.quantityReserved").value(2))
                .andExpect(jsonPath("$.totalQuantity").value(12));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar inventory inexistente por productId")
    void shouldReturnNotFoundWhenInventoryByProductIdDoesNotExist() throws Exception {
        UUID productId = UUID.randomUUID();

        mockMvc.perform(get("/inventories/product/{productId}", productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve adicionar estoque com sucesso")
    void shouldAddStockSuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();

        InventoryEntity entity = new InventoryEntity(
                UUID.randomUUID(),
                productId,
                10,
                0,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        inventoryEntityRepository.save(entity);

        String body = """
                {
                  "quantity": 5
                }
                """;

        mockMvc.perform(patch("/inventories/product/{productId}/add-stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantityAvailable").value(15))
                .andExpect(jsonPath("$.quantityReserved").value(0))
                .andExpect(jsonPath("$.totalQuantity").value(15));
    }

    @Test
    @DisplayName("Deve remover estoque com sucesso")
    void shouldRemoveStockSuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();

        InventoryEntity entity = new InventoryEntity(
                UUID.randomUUID(),
                productId,
                10,
                0,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        inventoryEntityRepository.save(entity);

        String body = """
                {
                  "quantity": 4
                }
                """;

        mockMvc.perform(patch("/inventories/product/{productId}/remove-stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantityAvailable").value(6))
                .andExpect(jsonPath("$.totalQuantity").value(6));
    }

    @Test
    @DisplayName("Deve retornar 400 ao remover mais estoque do que disponível")
    void shouldReturnBadRequestWhenRemovingMoreThanAvailable() throws Exception {
        UUID productId = UUID.randomUUID();

        InventoryEntity entity = new InventoryEntity(
                UUID.randomUUID(),
                productId,
                2,
                0,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        inventoryEntityRepository.save(entity);

        String body = """
                {
                  "quantity": 5
                }
                """;

        mockMvc.perform(patch("/inventories/product/{productId}/remove-stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve reservar estoque com sucesso")
    void shouldReserveStockSuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();

        InventoryEntity entity = new InventoryEntity(
                UUID.randomUUID(),
                productId,
                10,
                1,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        inventoryEntityRepository.save(entity);

        String body = """
                {
                  "quantity": 3
                }
                """;

        mockMvc.perform(patch("/inventories/product/{productId}/reserve-stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantityAvailable").value(7))
                .andExpect(jsonPath("$.quantityReserved").value(4))
                .andExpect(jsonPath("$.totalQuantity").value(11));
    }

    @Test
    @DisplayName("Deve liberar estoque reservado com sucesso")
    void shouldReleaseReservedStockSuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();

        InventoryEntity entity = new InventoryEntity(
                UUID.randomUUID(),
                productId,
                5,
                4,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        inventoryEntityRepository.save(entity);

        String body = """
                {
                  "quantity": 2
                }
                """;

        mockMvc.perform(patch("/inventories/product/{productId}/release-reserved-stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantityAvailable").value(7))
                .andExpect(jsonPath("$.quantityReserved").value(2))
                .andExpect(jsonPath("$.totalQuantity").value(9));
    }

    @Test
    @DisplayName("Deve retornar 400 quando quantity for inválida")
    void shouldReturnBadRequestWhenQuantityIsInvalid() throws Exception {
        UUID productId = UUID.randomUUID();

        InventoryEntity entity = new InventoryEntity(
                UUID.randomUUID(),
                productId,
                10,
                0,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        inventoryEntityRepository.save(entity);

        String body = """
                {
                  "quantity": 0
                }
                """;

        mockMvc.perform(patch("/inventories/product/{productId}/add-stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray());
    }
}
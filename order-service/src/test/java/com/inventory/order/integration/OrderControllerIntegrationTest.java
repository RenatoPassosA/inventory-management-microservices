package com.inventory.order.integration;

import com.inventory.order.application.client.InventoryClient;
import com.inventory.order.application.client.PriceClient;
import com.inventory.order.domain.enums.OrderStatus;
import com.inventory.order.domain.model.Order;
import com.inventory.order.domain.model.OrderItem;
import com.inventory.order.domain.repository.OrderRepository;
import com.inventory.order.infrastructure.persistence.repository.OrderRepositoryJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5435/orderdb",
        "spring.datasource.username=postgres",
        "spring.datasource.password=postgres",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.jpa.hibernate.ddl-auto=update",
        "services.price-service.url=http://localhost:8082",
        "services.inventory-service.url=http://localhost:8083"
})
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderRepositoryJpa orderJpaRepository;

    @MockitoBean
    private PriceClient priceClient;

    @MockitoBean
    private InventoryClient inventoryClient;

    @BeforeEach
    void setUp() {
        orderJpaRepository.deleteAll();
        Mockito.reset(priceClient, inventoryClient);
    }

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        String productId = UUID.randomUUID().toString();

        when(priceClient.getActivePriceByProductId(UUID.fromString(productId)))
                .thenReturn(BigDecimal.valueOf(19.90));

        doNothing().when(inventoryClient).reserveStock(UUID.fromString(productId), 2);

        String body = """
            {
              "items": [
                {
                  "productId": "%s",
                  "quantity": 2
                }
              ]
            }
            """.formatted(productId);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalAmount").value(39.80))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId").value(productId))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].unitPrice").value(19.90))
                .andExpect(jsonPath("$.items[0].subtotal").value(39.80));
    }

    @Test
    void shouldReturn400WhenCreatePayloadIsInvalid() throws Exception {
        String body = """
            {
              "items": [
                {
                  "productId": null,
                  "quantity": 0
                }
              ]
            }
            """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message", containsString("productId")))
                .andExpect(jsonPath("$.message", containsString("quantity")));
    }

    @Test
    void shouldGetOrderByIdSuccessfully() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Order savedOrder = orderRepository.save(
                buildOrder(
                        orderId,
                        List.of(new OrderItem(UUID.randomUUID(), productId, 2, BigDecimal.valueOf(15.00))),
                        BigDecimal.valueOf(30.00),
                        OrderStatus.CONFIRMED
                )
        );

        mockMvc.perform(get("/orders/{id}", savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId().toString()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalAmount").value(30.00))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    void shouldReturn404WhenOrderDoesNotExist() throws Exception {
        UUID orderId = UUID.randomUUID();

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order Not Found"))
                .andExpect(jsonPath("$.message").value("Order not found with id: " + orderId));
    }

    @Test
    void shouldFindAllOrdersSuccessfully() throws Exception {
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        orderRepository.save(
                buildOrder(
                        UUID.randomUUID(),
                        List.of(new OrderItem(UUID.randomUUID(), productId1, 1, BigDecimal.valueOf(10.00))),
                        BigDecimal.valueOf(10.00),
                        OrderStatus.CONFIRMED
                )
        );

        orderRepository.save(
                buildOrder(
                        UUID.randomUUID(),
                        List.of(new OrderItem(UUID.randomUUID(), productId2, 2, BigDecimal.valueOf(20.00))),
                        BigDecimal.valueOf(40.00),
                        OrderStatus.CONFIRMED
                )
        );

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    private Order buildOrder(UUID orderId,
                             List<OrderItem> items,
                             BigDecimal totalAmount,
                             OrderStatus status) {
        OffsetDateTime now = OffsetDateTime.now();

        return new Order(
                orderId,
                items,
                totalAmount,
                status,
                now,
                now
        );
    }
}
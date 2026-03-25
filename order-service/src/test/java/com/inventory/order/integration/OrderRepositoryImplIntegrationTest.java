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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5435/orderdb",
        "spring.datasource.username=postgres",
        "spring.datasource.password=postgres",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.jpa.hibernate.ddl-auto=update",
        "services.price-service.url=http://localhost:8082",
        "services.inventory-service.url=http://localhost:8083"
})
class OrderRepositoryImplIntegrationTest {

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
    }

    @Test
    void shouldSaveOrderSuccessfully() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Order order = buildOrder(
                orderId,
                List.of(new OrderItem(UUID.randomUUID(), productId, 2, BigDecimal.valueOf(25.00))),
                BigDecimal.valueOf(50.00),
                OrderStatus.CONFIRMED
        );

        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder);
        assertEquals(orderId, savedOrder.getId());
        assertEquals(OrderStatus.CONFIRMED, savedOrder.getStatus());
        assertEquals(BigDecimal.valueOf(50.00), savedOrder.getTotalAmount());
        assertEquals(1, savedOrder.getItems().size());
    }

    @Test
    void shouldFindOrderByIdSuccessfully() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Order order = buildOrder(
                orderId,
                List.of(new OrderItem(UUID.randomUUID(), productId, 1, BigDecimal.valueOf(12.00))),
                BigDecimal.valueOf(12.00),
                OrderStatus.CONFIRMED
        );

        orderRepository.save(order);

        Optional<Order> foundOrder = orderRepository.findById(orderId);

        assertTrue(foundOrder.isPresent());
        assertEquals(orderId, foundOrder.get().getId());
        assertEquals(1, foundOrder.get().getItems().size());
        assertEquals(new BigDecimal("12.00"), foundOrder.get().getTotalAmount());
    }

    @Test
    void shouldReturnEmptyWhenOrderDoesNotExist() {
        Optional<Order> foundOrder = orderRepository.findById(UUID.randomUUID());
        assertTrue(foundOrder.isEmpty());
    }

    @Test
    void shouldFindAllOrdersSuccessfully() {
        Order order1 = buildOrder(
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(10.00))),
                BigDecimal.valueOf(10.00),
                OrderStatus.CONFIRMED
        );

        Order order2 = buildOrder(
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(20.00))),
                BigDecimal.valueOf(40.00),
                OrderStatus.CONFIRMED
        );

        orderRepository.save(order1);
        orderRepository.save(order2);

        List<Order> orders = orderRepository.findAll();

        assertEquals(2, orders.size());
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
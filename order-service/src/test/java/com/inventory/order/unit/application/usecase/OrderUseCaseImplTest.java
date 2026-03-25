package com.inventory.order.unit.application.usecase;

import com.inventory.order.application.client.InventoryClient;
import com.inventory.order.application.client.PriceClient;
import com.inventory.order.application.dto.command.CreateOrderCommand;
import com.inventory.order.application.dto.command.CreateOrderItemCommand;
import com.inventory.order.application.mapper.OrderApplicationMapper;
import com.inventory.order.application.dto.result.CreateOrderResult;
import com.inventory.order.application.dto.result.OrderResult;
import com.inventory.order.application.usecase.impl.OrderUseCaseImpl;
import com.inventory.order.domain.enums.OrderStatus;
import com.inventory.order.domain.exceptions.InvalidOrderException;
import com.inventory.order.domain.exceptions.InvalidOrderItemException;
import com.inventory.order.domain.exceptions.OrderNotFoundException;
import com.inventory.order.domain.model.Order;
import com.inventory.order.domain.model.OrderItem;
import com.inventory.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderUseCaseImplTest {

    private OrderRepository orderRepository;
    private PriceClient priceClient;
    private InventoryClient inventoryClient;
    private OrderApplicationMapper orderApplicationMapper;
    private OrderUseCaseImpl orderUseCase;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        priceClient = Mockito.mock(PriceClient.class);
        inventoryClient = Mockito.mock(InventoryClient.class);
        orderApplicationMapper = new OrderApplicationMapper();

        orderUseCase = new OrderUseCaseImpl(
                orderRepository,
                priceClient,
                inventoryClient,
                orderApplicationMapper
        );
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        UUID productId = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                List.of(new CreateOrderItemCommand(productId, 2))
        );

        when(priceClient.getActivePriceByProductId(productId))
                .thenReturn(BigDecimal.valueOf(25.00));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderResult result = orderUseCase.create(command);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertEquals(1, result.getItems().size());
        assertEquals(BigDecimal.valueOf(50.00), result.getTotalAmount());
        assertEquals(productId, result.getItems().get(0).getProductId());
        assertEquals(2, result.getItems().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(25.00), result.getItems().get(0).getUnitPrice());
        assertEquals(BigDecimal.valueOf(50.00), result.getItems().get(0).getSubtotal());

        verify(priceClient, times(1)).getActivePriceByProductId(productId);
        verify(inventoryClient, times(1)).reserveStock(productId, 2);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldCreateOrderSuccessfullyWithMultipleItems() {
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                List.of(
                        new CreateOrderItemCommand(productId1, 2),
                        new CreateOrderItemCommand(productId2, 3)
                )
        );

        when(priceClient.getActivePriceByProductId(productId1))
                .thenReturn(BigDecimal.valueOf(10.00));
        when(priceClient.getActivePriceByProductId(productId2))
                .thenReturn(BigDecimal.valueOf(20.00));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderResult result = orderUseCase.create(command);

        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertEquals(2, result.getItems().size());
        assertEquals(BigDecimal.valueOf(80.00), result.getTotalAmount());

        verify(inventoryClient).reserveStock(productId1, 2);
        verify(inventoryClient).reserveStock(productId2, 3);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenCreateCommandIsNull() {
        InvalidOrderException exception = assertThrows(
                InvalidOrderException.class,
                () -> orderUseCase.create(null)
        );

        assertEquals("Create order command must not be null.", exception.getMessage());

        verifyNoInteractions(priceClient, inventoryClient, orderRepository);
    }

    @Test
    void shouldThrowExceptionWhenOrderItemsAreEmpty() {
        CreateOrderCommand command = new CreateOrderCommand(List.of());

        InvalidOrderException exception = assertThrows(
                InvalidOrderException.class,
                () -> orderUseCase.create(command)
        );

        assertEquals("Order must contain at least one item.", exception.getMessage());

        verifyNoInteractions(priceClient, inventoryClient, orderRepository);
    }

    @Test
    void shouldThrowExceptionWhenOrderItemCommandIsNull() {
        CreateOrderCommand command = new CreateOrderCommand(
                java.util.Collections.singletonList(null)
        );

        InvalidOrderException exception = assertThrows(
                InvalidOrderException.class,
                () -> orderUseCase.create(command)
        );

        assertEquals("Order item command must not be null.", exception.getMessage());

        verifyNoInteractions(orderRepository, inventoryClient);
        }

    @Test
    void shouldThrowExceptionWhenProductIdIsNull() {
        CreateOrderCommand command = new CreateOrderCommand(
                List.of(new CreateOrderItemCommand(null, 2))
        );

        InvalidOrderException exception = assertThrows(
                InvalidOrderException.class,
                () -> orderUseCase.create(command)
        );

        assertEquals("Product id must not be null.", exception.getMessage());

        verifyNoInteractions(priceClient, inventoryClient, orderRepository);
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsInvalid() {
        UUID productId = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                List.of(new CreateOrderItemCommand(productId, 0))
        );

        InvalidOrderException exception = assertThrows(
                InvalidOrderException.class,
                () -> orderUseCase.create(command)
        );

        assertEquals("Quantity must be greater than zero.", exception.getMessage());

        verifyNoInteractions(priceClient, inventoryClient, orderRepository);
    }

    @Test
    void shouldThrowExceptionWhenPriceClientReturnsNullPrice() {
        UUID productId = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                List.of(new CreateOrderItemCommand(productId, 2))
        );

        when(priceClient.getActivePriceByProductId(productId)).thenReturn(null);

        assertThrows(
                InvalidOrderItemException.class,
                () -> orderUseCase.create(command)
        );

        verify(priceClient, times(1)).getActivePriceByProductId(productId);
        verifyNoInteractions(inventoryClient, orderRepository);
    }

    @Test
    void shouldThrowExceptionWhenReserveStockFails() {
        UUID productId = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                List.of(new CreateOrderItemCommand(productId, 2))
        );

        when(priceClient.getActivePriceByProductId(productId))
                .thenReturn(BigDecimal.valueOf(30.00));

        doThrow(new InvalidOrderException("Unable to reserve stock for product: " + productId))
                .when(inventoryClient).reserveStock(productId, 2);

        InvalidOrderException exception = assertThrows(
                InvalidOrderException.class,
                () -> orderUseCase.create(command)
        );

        assertEquals("Unable to reserve stock for product: " + productId, exception.getMessage());

        verify(priceClient, times(1)).getActivePriceByProductId(productId);
        verify(inventoryClient, times(1)).reserveStock(productId, 2);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldGetOrderByIdSuccessfully() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Order order = buildOrder(
                orderId,
                List.of(new OrderItem(UUID.randomUUID(), productId, 2, BigDecimal.valueOf(15.00))),
                BigDecimal.valueOf(30.00),
                OrderStatus.CONFIRMED
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderResult result = orderUseCase.getById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        assertEquals(BigDecimal.valueOf(30.00), result.getTotalAmount());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void shouldThrowExceptionWhenOrderByIdDoesNotExist() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderUseCase.getById(orderId)
        );

        assertEquals("Order not found with id: " + orderId, exception.getMessage());
    }

    @Test
    void shouldFindAllOrdersSuccessfully() {
        UUID orderId1 = UUID.randomUUID();
        UUID orderId2 = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        Order order1 = buildOrder(
                orderId1,
                List.of(new OrderItem(UUID.randomUUID(), productId1, 1, BigDecimal.valueOf(10.00))),
                BigDecimal.valueOf(10.00),
                OrderStatus.CONFIRMED
        );

        Order order2 = buildOrder(
                orderId2,
                List.of(new OrderItem(UUID.randomUUID(), productId2, 2, BigDecimal.valueOf(20.00))),
                BigDecimal.valueOf(40.00),
                OrderStatus.CONFIRMED
        );

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<OrderResult> results = orderUseCase.findAll();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(orderId1, results.get(0).getId());
        assertEquals(orderId2, results.get(1).getId());
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
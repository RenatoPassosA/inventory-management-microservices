package com.inventory.order.infrastructure.persistence.mapper;

import com.inventory.order.domain.model.Order;
import com.inventory.order.domain.model.OrderItem;
import com.inventory.order.infrastructure.persistence.entity.OrderEntity;
import com.inventory.order.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class OrderEntityMapper {

    public OrderEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(order.getId());
        orderEntity.setTotalAmount(order.getTotalAmount());
        orderEntity.setStatus(order.getStatus());
        orderEntity.setCreatedAt(order.getCreatedAt());
        orderEntity.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemEntity> itemEntities = toOrderItemEntityList(order.getItems(), orderEntity);
        orderEntity.setItems(itemEntities);

        return orderEntity;
    }

    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Order(
                entity.getId(),
                toOrderItemDomainList(entity.getItems()),
                entity.getTotalAmount(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<OrderItemEntity> toOrderItemEntityList(List<OrderItem> items, OrderEntity orderEntity) {
        if (items == null) {
            return new ArrayList<>();
        }

        return items.stream()
                .map(item -> toOrderItemEntity(item, orderEntity))
                .toList();
    }

    public OrderItemEntity toOrderItemEntity(OrderItem item, OrderEntity orderEntity) {
        if (item == null) {
            return null;
        }

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(item.getId());
        entity.setProductId(item.getProductId());
        entity.setQuantity(item.getQuantity());
        entity.setUnitPrice(item.getUnitPrice());
        entity.setSubtotal(item.getSubtotal());
        entity.setOrder(orderEntity);

        return entity;
    }

    public List<OrderItem> toOrderItemDomainList(List<OrderItemEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::toOrderItemDomain)
                .toList();
    }

    public OrderItem toOrderItemDomain(OrderItemEntity entity) {
        if (entity == null) {
            return null;
        }

        return new OrderItem(
                entity.getId(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }
}
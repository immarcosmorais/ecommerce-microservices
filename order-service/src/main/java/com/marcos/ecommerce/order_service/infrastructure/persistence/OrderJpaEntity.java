package com.marcos.ecommerce.order_service.infrastructure.persistence;

import com.marcos.ecommerce.order_service.domain.model.Order;
import com.marcos.ecommerce.order_service.domain.model.OrderStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderJpaEntity extends AbstractEntity {

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    protected OrderJpaEntity() {
    }

    public static OrderJpaEntity fromDomain(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.id = order.getId();
        entity.customerId = order.getCustomerId();
        entity.status = order.getStatus();
        entity.createdAt = order.getCreatedAt();
        entity.updatedAt = order.getUpdatedAt();
        entity.items = order.getItems().stream().map(OrderItemJpaEntity::fromDomain).toList();
        return entity;
    }

    public Order toDomain() {
        Order order = new Order(this.customerId);
        order.setId(this.id);
        order.setStatus(this.status);
        order.setCreatedAt(this.createdAt);
        order.setUpdatedAt(this.updatedAt);
        this.items.stream().map(OrderItemJpaEntity::toDomain).forEach(item -> order.getItems().add(item));
        return order;
    }

    public Long getCustomerId() {
        return customerId;
    }
}

package com.marcos.ecommerce.orchestrator_service.infrastructure.persistence;

import com.marcos.ecommerce.orchestrator_service.domain.model.OrderSaga;
import com.marcos.ecommerce.orchestrator_service.domain.model.SagaStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_saga")
public class OrderSagaJpaEntity extends AbstractEntity {

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SagaStatus status;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    protected OrderSagaJpaEntity() {
    }

    public static OrderSagaJpaEntity fromDomain(OrderSaga orderSaga) {
        OrderSagaJpaEntity sagaJpaEntity = new OrderSagaJpaEntity();
        sagaJpaEntity.id = orderSaga.getId();
        sagaJpaEntity.orderId = orderSaga.getOrderId();
        sagaJpaEntity.status = orderSaga.getStatus();
        sagaJpaEntity.createdAt = orderSaga.getCreatedAt();
        sagaJpaEntity.updatedAt = orderSaga.getUpdatedAt();
        sagaJpaEntity.totalAmount = orderSaga.getTotalAmount();
        return sagaJpaEntity;
    }

    public OrderSaga toDomain() {
        OrderSaga saga = new OrderSaga(this.orderId, this.status, this.totalAmount);
        saga.setId(this.id);
        return saga;
    }

    public Long getOrderId() {
        return orderId;
    }

    public SagaStatus getStatus() {
        return status;
    }
}

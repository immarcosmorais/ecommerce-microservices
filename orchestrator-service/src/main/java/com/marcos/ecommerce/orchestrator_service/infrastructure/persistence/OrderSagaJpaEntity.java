package com.marcos.ecommerce.orchestrator_service.infrastructure.persistence;

import com.marcos.ecommerce.orchestrator_service.domain.model.OrderSaga;
import com.marcos.ecommerce.orchestrator_service.domain.model.SagaStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "order_saga")
public class OrderSagaJpaEntity extends AbstractEntity {

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SagaStatus status;

    protected OrderSagaJpaEntity() {}

    public static OrderSagaJpaEntity fromDomain(OrderSaga orderSaga) {
        OrderSagaJpaEntity sagaJpaEntity = new OrderSagaJpaEntity();
        sagaJpaEntity.orderId = orderSaga.getOrderId();
        sagaJpaEntity.status = orderSaga.getStatus();
        sagaJpaEntity.createdAt = orderSaga.getCreatedAt();
        sagaJpaEntity.updatedAt = orderSaga.getUpdatedAt();
        return sagaJpaEntity;
    }

    public OrderSaga toDomain() {
        OrderSaga saga = new OrderSaga(this.orderId, this.status);
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

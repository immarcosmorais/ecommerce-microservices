package com.marcos.ecommerce.orchestrator_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderSaga extends AbstractModel {

    private final Long orderId;
    private SagaStatus status;
    private final BigDecimal totalAmount;
    private final List<SagaItem> items;

    public OrderSaga(Long orderId, BigDecimal totalAmount, List<SagaItem> items) {
        this.orderId = orderId;
        this.items = items;
        this.status = SagaStatus.STARTED;
        this.totalAmount = totalAmount;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public OrderSaga(Long orderId, SagaStatus status, BigDecimal totalAmount, List<SagaItem> items) {
        this.orderId = orderId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = items;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private void transitionTo(SagaStatus target, SagaStatus... allowedFrom) {
        for (SagaStatus from : allowedFrom) {
            if (this.status == from) {
                this.status = target;
                this.updatedAt = LocalDateTime.now();
                return;
            }
        }
        throw new IllegalStateException(String.format("Transição de saga inválida: de %s para %s", this.status, target));
    }

    public void markStockReserved() {
        transitionTo(SagaStatus.STOCK_RESERVED, SagaStatus.STARTED);
    }

    public void markPaid() {
        transitionTo(SagaStatus.PAID, SagaStatus.STOCK_RESERVED);
    }

    public void startCompensation() {
        transitionTo(SagaStatus.COMPENSATING, SagaStatus.STOCK_RESERVED, SagaStatus.PAID);
    }

    public void cancel() {
        transitionTo(SagaStatus.CANCELLED, SagaStatus.COMPENSATING, SagaStatus.STARTED);
    }

    public Long getOrderId() {
        return orderId;
    }

    public SagaStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<SagaItem> getItems() {
        return items;
    }

    public void complete() {
        transitionTo(SagaStatus.COMPLETED, SagaStatus.PAID);
    }

    public record SagaItem(Long productId, int quantity) {
    }
}

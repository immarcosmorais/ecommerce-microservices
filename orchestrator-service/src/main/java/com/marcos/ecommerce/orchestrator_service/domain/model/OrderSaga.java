package com.marcos.ecommerce.orchestrator_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSaga extends AbstractModel{

    private Long orderId;
    private SagaStatus status;
    private BigDecimal totalAmount;

    public OrderSaga(Long orderId, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.status = SagaStatus.STARTED;
        this.totalAmount = totalAmount;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public OrderSaga(Long orderId, SagaStatus status, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private void transitionTo(SagaStatus target, SagaStatus... allowedFrom){
        for(SagaStatus from : allowedFrom){
            if(this.status == from){
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

    public void markPaid(){
        transitionTo(SagaStatus.PAID, SagaStatus.STOCK_RESERVED);
    }

    public void startCompensation(){
        transitionTo(SagaStatus.COMPENSATING, SagaStatus.STOCK_RESERVED, SagaStatus.PAID);
    }

    public void cancel(){
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

    public void complete() {
        transitionTo(SagaStatus.COMPLETED, SagaStatus.PAID);
    }
}

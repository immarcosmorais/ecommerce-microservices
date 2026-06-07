package com.marcos.ecommerce.orchestrator_service.domain.model;

import java.time.LocalDateTime;

public class OrderSaga extends AbstractModel{

    private Long orderId;
    private SagaStatus status;

    public OrderSaga(Long orderId){
        this.orderId = orderId;
        this.status = SagaStatus.STARTED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public OrderSaga(Long orderId, SagaStatus status) {
        this.orderId = orderId;
        this.status = status;
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

    public void markStockReversed(){
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
}

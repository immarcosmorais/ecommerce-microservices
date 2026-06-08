package com.marcos.ecommerce.orchestrator_service.infrastructure.messaging;

import com.marcos.ecommerce.orchestrator_service.application.messaging.event.OrderCreatedEvent;
import com.marcos.ecommerce.orchestrator_service.application.messaging.event.PaymentApprovedEvent;
import com.marcos.ecommerce.orchestrator_service.application.messaging.event.StockReservedEvent;
import com.marcos.ecommerce.orchestrator_service.application.service.OrderSagaOrchestrator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SagaEventListener {

    private final OrderSagaOrchestrator orchestrator;

    public SagaEventListener(OrderSagaOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @KafkaListener(topics = "order-created")
    public void onOrderCreated(OrderCreatedEvent event) {
        orchestrator.start(event);
    }

    @KafkaListener(topics = "stock-reserved")
    public void onStockReserved(StockReservedEvent event) {
        orchestrator.onStockReserved(event);
    }

    @KafkaListener(topics = "payment-approved")
    public void onPaymentApproved(PaymentApprovedEvent event) {
        orchestrator.onPaymentApproved(event);
    }

}

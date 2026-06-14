package com.marcos.ecommerce.orchestrator_service.infrastructure.messaging;

import com.marcos.ecommerce.orchestrator_service.application.messaging.event.OrderCreatedEvent;
import com.marcos.ecommerce.orchestrator_service.application.messaging.event.PaymentApprovedEvent;
import com.marcos.ecommerce.orchestrator_service.application.messaging.event.StockReservedEvent;
import com.marcos.ecommerce.orchestrator_service.application.service.OrderSagaOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SagaEventListener {

    private final OrderSagaOrchestrator orchestrator;
    private static final Logger log = LoggerFactory.getLogger(SagaEventListener.class);

    public SagaEventListener(OrderSagaOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @KafkaListener(topics = "order-created")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Received event with key {} from topic order-create", event.orderId());
        orchestrator.start(event);
    }

    @KafkaListener(topics = "stock-reserved")
    public void onStockReserved(StockReservedEvent event) {
        log.info("Received event with key {} from topic stock-reserved", event.orderId());
        orchestrator.onStockReserved(event);
    }

    @KafkaListener(topics = "payment-approved")
    public void onPaymentApproved(PaymentApprovedEvent event) {
        log.info("Received event with key {} from topic payment-approved", event.orderId());
        orchestrator.onPaymentApproved(event);
    }

}

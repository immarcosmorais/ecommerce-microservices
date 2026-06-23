package com.marcos.ecommerce.order_service.infrastructure.messaging;

import com.marcos.ecommerce.order_service.application.messaging.command.CancelOrderCommand;
import com.marcos.ecommerce.order_service.application.usecase.CancelOrderUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CancelOrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(CancelOrderConsumer.class);
    private final CancelOrderUseCase cancelOrderUseCase;

    public CancelOrderConsumer(CancelOrderUseCase cancelOrderUseCase) {
        this.cancelOrderUseCase = cancelOrderUseCase;
    }

    @KafkaListener(topics = "cancel-order", groupId = "order-group")
    public void onCancelOrder(CancelOrderCommand command) {
        log.info("Received command from topic cancel-order, orderId={}", command.orderId());
        cancelOrderUseCase.execute(command.orderId());
    }
}
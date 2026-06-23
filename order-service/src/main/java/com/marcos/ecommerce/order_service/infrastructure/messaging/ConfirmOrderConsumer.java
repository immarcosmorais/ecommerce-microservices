package com.marcos.ecommerce.order_service.infrastructure.messaging;

import com.marcos.ecommerce.order_service.application.messaging.command.ConfirmOrderCommand;
import com.marcos.ecommerce.order_service.application.usecase.ConfirmOrderUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConfirmOrderConsumer {

    private final ConfirmOrderUseCase confirmOrderUseCase;
    private static final Logger log = LoggerFactory.getLogger(ConfirmOrderConsumer.class);

    public ConfirmOrderConsumer(ConfirmOrderUseCase confirmOrderUseCase) {
        this.confirmOrderUseCase = confirmOrderUseCase;
    }

    @KafkaListener(topics = "confirm-order", groupId = "order-group")
    public void onConfirmOrder(ConfirmOrderCommand command) {
        log.info("Received command from topic confirm-order {}", command.orderId());
        this.confirmOrderUseCase.execute(command.orderId());
    }

}

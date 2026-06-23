package com.marcos.ecommerce.payment_service.infrastructure.messaging;

import com.marcos.ecommerce.payment_service.application.messaging.command.ProcessPaymentCommand;
import com.marcos.ecommerce.payment_service.application.messaging.event.PaymentApprovedEvent;
import com.marcos.ecommerce.payment_service.application.messaging.event.PaymentFailedEvent;
import com.marcos.ecommerce.payment_service.application.port.PaymentEventPublisher;
import com.marcos.ecommerce.payment_service.application.usecase.ProcessPaymentUseCase;
import com.marcos.ecommerce.payment_service.infrastructure.resilience.ResilientProcessPaymentUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessPaymentConsumer {

    private final ResilientProcessPaymentUseCase processPaymentUseCase;
    private final PaymentEventPublisher publisher;
    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentConsumer.class);

    public ProcessPaymentConsumer(
            ResilientProcessPaymentUseCase processPaymentUseCase,
            PaymentEventPublisher publisher
    ) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.publisher = publisher;
    }

    @KafkaListener(topics = "process-payment", groupId = "payment-group")
    public void onProcessPayment(ProcessPaymentCommand command) {
        log.info("Received command from topic process-payment {}", command.orderId());
        boolean approved = processPaymentUseCase.execute(command.orderId(), command.amount());
        if (approved) {
            publisher.paymentApproved(new PaymentApprovedEvent(command.orderId()));
        } else {
            log.info("Payment FAILED for orderId={}, amount={}", command.orderId(), command.amount());
            publisher.paymentFailed(new PaymentFailedEvent(command.orderId()));
        }
    }

}

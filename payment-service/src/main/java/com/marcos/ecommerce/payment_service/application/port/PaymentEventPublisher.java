package com.marcos.ecommerce.payment_service.application.port;

import com.marcos.ecommerce.payment_service.application.messaging.event.PaymentApprovedEvent;
import com.marcos.ecommerce.payment_service.application.messaging.event.PaymentFailedEvent;

public interface PaymentEventPublisher {
    void paymentApproved(PaymentApprovedEvent event);

    void paymentFailed(PaymentFailedEvent event);
}

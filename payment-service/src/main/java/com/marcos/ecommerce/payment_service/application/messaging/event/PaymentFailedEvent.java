package com.marcos.ecommerce.payment_service.application.messaging.event;

public record PaymentFailedEvent(Long orderId) {
}

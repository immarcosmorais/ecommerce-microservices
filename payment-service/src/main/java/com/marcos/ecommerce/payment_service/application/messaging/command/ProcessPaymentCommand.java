package com.marcos.ecommerce.payment_service.application.messaging.command;

import java.math.BigDecimal;

public record ProcessPaymentCommand(Long orderId, BigDecimal amount) {
}

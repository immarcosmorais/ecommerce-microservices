package com.marcos.ecommerce.orchestrator_service.application.messaging.command;

import java.math.BigDecimal;

public record ProcessPaymentCommand(Long orderId, BigDecimal amount) {
}

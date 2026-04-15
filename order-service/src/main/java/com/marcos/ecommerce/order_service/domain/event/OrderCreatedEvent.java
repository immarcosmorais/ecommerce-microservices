package com.marcos.ecommerce.order_service.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        BigDecimal total,
        int totalItems,
        LocalDateTime occurredAt
) {
    public OrderCreatedEvent(Long orderId, Long customerId, BigDecimal total, int totalItems) {
        this(orderId, customerId, total, totalItems, LocalDateTime.now());
    }
}

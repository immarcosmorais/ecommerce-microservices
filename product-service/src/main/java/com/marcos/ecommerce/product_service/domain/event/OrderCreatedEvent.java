package com.marcos.ecommerce.product_service.domain.event;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        List<OrderItemEventDto> items,
        BigDecimal totalAmount
) {
    public record OrderItemEventDto(
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal unitPrice
    ) {
    }
}

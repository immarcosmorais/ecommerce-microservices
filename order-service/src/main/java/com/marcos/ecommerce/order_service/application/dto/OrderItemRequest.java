package com.marcos.ecommerce.order_service.application.dto;

import java.math.BigDecimal;

public record OrderItemRequest(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {}

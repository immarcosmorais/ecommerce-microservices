package com.marcos.ecommerce.order_service.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        String status,
        List<OrderItemResponse> items,
        BigDecimal total,
        int totalItems,
        LocalDateTime createdAt
) {}

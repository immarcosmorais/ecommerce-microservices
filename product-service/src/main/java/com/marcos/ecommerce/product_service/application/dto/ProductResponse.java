package com.marcos.ecommerce.product_service.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer StockQuantity,
        boolean available,
        LocalDateTime createdAt
) {
}

package com.marcos.ecommerce.order_service.application.dto;

import java.util.List;

public record CreateOrderRequest(
        Long customerId,
        List<OrderItemRequest> items
) {}


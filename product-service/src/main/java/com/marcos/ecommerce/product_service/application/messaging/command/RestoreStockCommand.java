package com.marcos.ecommerce.product_service.application.messaging.command;

import java.util.List;

public record RestoreStockCommand(Long orderId, List<Item> items) {
    public record Item(Long productId, int quantity) {
    }
}

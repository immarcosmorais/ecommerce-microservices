package com.marcos.ecommerce.orchestrator_service.application.messaging.command;

import java.util.List;

public record ReserveStockCommand(Long orderId, List<Item> items) {
    public record Item(Long productId, int quantity) {
    }
}

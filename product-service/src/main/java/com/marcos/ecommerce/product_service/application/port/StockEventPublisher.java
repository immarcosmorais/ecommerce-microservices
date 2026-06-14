package com.marcos.ecommerce.product_service.application.port;

import com.marcos.ecommerce.product_service.application.messaging.event.StockReservedEvent;

public interface StockEventPublisher {
    void stockReserved(StockReservedEvent event);
}

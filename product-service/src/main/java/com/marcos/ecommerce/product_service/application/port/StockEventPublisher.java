package com.marcos.ecommerce.product_service.application.port;

public interface StockEventPublisher {
    void stockReserved(Long orderId);
}

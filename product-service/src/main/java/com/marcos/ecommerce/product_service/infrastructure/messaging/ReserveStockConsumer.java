package com.marcos.ecommerce.product_service.infrastructure.messaging;

import com.marcos.ecommerce.product_service.application.messaging.command.ReserveStockCommand;
import com.marcos.ecommerce.product_service.application.port.StockEventPublisher;
import com.marcos.ecommerce.product_service.application.usecase.DecreaseStockUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReserveStockConsumer {

    private final DecreaseStockUseCase decreaseStockUseCase;
    private final StockEventPublisher publisher;

    public ReserveStockConsumer(DecreaseStockUseCase decreaseStockUseCase, StockEventPublisher publisher) {
        this.decreaseStockUseCase = decreaseStockUseCase;
        this.publisher = publisher;
    }

    @KafkaListener(topics = "reserve-stock", groupId = "product-group")
    public void onReserveStock(ReserveStockCommand command) {
        command.items().forEach(item -> {
            decreaseStockUseCase.execute(item.productId(), item.quantity());
        });
        publisher.stockReserved(command.orderId());
    }

}

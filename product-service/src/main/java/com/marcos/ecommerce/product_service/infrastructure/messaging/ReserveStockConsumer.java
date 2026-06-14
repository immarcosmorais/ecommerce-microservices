package com.marcos.ecommerce.product_service.infrastructure.messaging;

import com.marcos.ecommerce.product_service.application.messaging.command.ReserveStockCommand;
import com.marcos.ecommerce.product_service.application.messaging.event.StockReservedEvent;
import com.marcos.ecommerce.product_service.application.port.StockEventPublisher;
import com.marcos.ecommerce.product_service.application.usecase.DecreaseStockUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReserveStockConsumer {

    private final DecreaseStockUseCase decreaseStockUseCase;
    private final StockEventPublisher publisher;
    private static final Logger log = LoggerFactory.getLogger(ReserveStockConsumer.class);

    public ReserveStockConsumer(DecreaseStockUseCase decreaseStockUseCase, StockEventPublisher publisher) {
        this.decreaseStockUseCase = decreaseStockUseCase;
        this.publisher = publisher;
    }

    @KafkaListener(topics = "reserve-stock", groupId = "product-group")
    public void onReserveStock(ReserveStockCommand command) {
        log.info("Received command from topic reserve-stock {}", command.orderId());
        command.items().forEach(item -> {
            decreaseStockUseCase.execute(item.productId(), item.quantity());
        });
        publisher.stockReserved(new StockReservedEvent(command.orderId()));
    }

}

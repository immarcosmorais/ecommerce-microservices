package com.marcos.ecommerce.product_service.infrastructure.messaging;

import com.marcos.ecommerce.product_service.application.messaging.command.RestoreStockCommand;
import com.marcos.ecommerce.product_service.application.usecase.RestoreStockUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class RestoreStockConsumer {

    private static final Logger log = LoggerFactory.getLogger(RestoreStockConsumer.class);
    private final RestoreStockUseCase restoreStockUseCase;

    public RestoreStockConsumer(RestoreStockUseCase restoreStockUseCase) {
        this.restoreStockUseCase = restoreStockUseCase;
    }

    @KafkaListener(topics = "restore-stock", groupId = "product-group")
    public void onRestoreStock(RestoreStockCommand command) {
        log.info("Received command from topic restore-stock, orderId={}", command.orderId());
        command.items().forEach(item ->
                restoreStockUseCase.execute(item.productId(), item.quantity()));
    }

}

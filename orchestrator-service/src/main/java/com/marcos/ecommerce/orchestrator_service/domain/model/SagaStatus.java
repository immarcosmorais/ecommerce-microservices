package com.marcos.ecommerce.orchestrator_service.domain.model;

public enum SagaStatus {
    STARTED,
    STOCK_RESERVED,
    PAID,
    COMPLETED,
    COMPENSATING,
    CANCELLED
}

package com.marcos.ecommerce.product_service.domain.model;

import java.time.LocalDateTime;

public abstract class AbstractModel {
    protected Long id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

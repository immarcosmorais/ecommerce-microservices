package com.marcos.ecommerce.product_service.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
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

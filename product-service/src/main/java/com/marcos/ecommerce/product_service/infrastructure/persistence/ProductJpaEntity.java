package com.marcos.ecommerce.product_service.infrastructure.persistence;

import com.marcos.ecommerce.product_service.domain.model.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class ProductJpaEntity extends AbstractEntity{

    @Column(nullable = false, length = 255)
    private String name;
    @Column(length = 1000)
    private String description;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    protected ProductJpaEntity() {}

    public static ProductJpaEntity fromDomain(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.id = product.getId();
        entity.name = product.getName();
        entity.description = product.getDescription();
        entity.price = product.getPrice();
        entity.stockQuantity = product.getStockQuantity();
        entity.createdAt = product.getCreatedAt();
        entity.updatedAt = product.getUpdatedAt();
        return entity;
    }

    public Product toDomain() {
        Product product = new Product(
                this.name,
                this.description,
                this.price,
                this.stockQuantity
        );
        product.setId(this.id);
        return product;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }
}

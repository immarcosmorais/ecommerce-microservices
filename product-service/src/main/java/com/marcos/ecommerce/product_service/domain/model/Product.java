package com.marcos.ecommerce.product_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static com.marcos.ecommerce.product_service.domain.model.ValidateFields.*;

public class Product extends AbstractModel {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;

    protected Product() {}

    public Product(String name, String description, BigDecimal price, Integer stockQuantity) {
        validateString(name, "name");
        validateBigDecimal(price, "price");
        validateInteger(stockQuantity, "stockQuantity");
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDetails(String name, String description, BigDecimal price){
        validateString(name, "name");
        validateBigDecimal(price, "price");
        this.name = name;
        this.description = description;
        this.price = price;
        this.updatedAt = LocalDateTime.now();
    }

    public void decreaseStock(int quantity){
        if (quantity < 1){
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (this.stockQuantity < quantity){
            throw new IllegalStateException(
                    String.format("Insufficient stock. Available %d. Requested: %d", this.stockQuantity, quantity)
            );
        }
        this.stockQuantity -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseStock(int quantity){
        if (quantity < 1){
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.stockQuantity += quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAvailable(){
        return this.stockQuantity > 0;
    }

    public String getName() {
        return name;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setId(long id){
        this.id = id;
    }
}

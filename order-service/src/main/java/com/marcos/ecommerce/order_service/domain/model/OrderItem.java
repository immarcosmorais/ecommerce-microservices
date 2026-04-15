package com.marcos.ecommerce.order_service.domain.model;

import java.math.BigDecimal;

public class OrderItem {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;

    protected OrderItem(){}

    public OrderItem(Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
        ValidateFields.validateLong(productId, "productId");
        ValidateFields.validateString(productName, "productName");
        ValidateFields.validateInteger(quantity, "quantity");
        ValidateFields.validateBigDecimal(unitPrice, "unitPrice");
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

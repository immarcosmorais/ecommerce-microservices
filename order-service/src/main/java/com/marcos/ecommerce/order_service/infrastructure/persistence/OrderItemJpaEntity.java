package com.marcos.ecommerce.order_service.infrastructure.persistence;

import com.marcos.ecommerce.order_service.domain.model.OrderItem;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    protected OrderItemJpaEntity() {
    }

    public static OrderItemJpaEntity fromDomain(OrderItem item) {
        OrderItemJpaEntity entity = new OrderItemJpaEntity();
        entity.id = item.getId();
        entity.productId = item.getProductId();
        entity.productName = item.getProductName();
        entity.quantity = item.getQuantity();
        entity.unitPrice = item.getUnitPrice();
        return entity;
    }

    public OrderItem toDomain() {
        OrderItem item = new OrderItem(
                this.productId,
                this.productName,
                this.quantity,
                this.unitPrice
        );
        item.setId(this.id);
        return item;
    }

    public Long getId() {
        return id;
    }
}

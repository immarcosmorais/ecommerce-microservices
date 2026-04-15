package com.marcos.ecommerce.order_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order extends AbstractModel{

    private Long customerId;
    private OrderStatus status;
    private List<OrderItem> items;

    protected Order(){}

    public void addItem(Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot add items to a non-pending order");
        }
        for (OrderItem item : items) {
            if (item.getProductId().equals(productId)) {
                throw new IllegalStateException(
                        "Product " + productId + " already in order. Remove it first to change quantity."
                );
            }
        }
        OrderItem newItem = new OrderItem(productId, productName, quantity, unitPrice);
        this.items.add(newItem);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeItem(Long productId) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot remove items from a non-pending order");
        }
        boolean removed = items.removeIf(item -> item.getProductId().equals(productId));
        if (!removed) {
            throw new IllegalStateException("Product " + productId + " not found in order");
        }
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal calculateTotal() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void changeStatus(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s", this.status, newStatus)
            );
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm an order with no items");
        }
        changeStatus(OrderStatus.CONFIRMED);
    }

    public void cancel() {
        changeStatus(OrderStatus.CANCELLED);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int totalItems() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    public Order(Long customerId) {
        ValidateFields.validateId(customerId, "customerId");
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {

        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }


}

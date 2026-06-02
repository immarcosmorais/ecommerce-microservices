package com.marcos.ecommerce.order_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("Order — testes de domínio")
class OrderTest {

    private Order buildOrder() {
        return new Order(1L);
    }

    private void addItem(Order order, Long productId, int quantity, String price) {
        order.addItem(productId, "Produto " + productId, quantity, new BigDecimal(price));
    }

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("deve criar pedido com status PENDING")
        void shouldCreateOrderWithPendingStatus() {
            Order order = buildOrder();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getItems().isEmpty()).isTrue();
            assertThat(order.getCustomerId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve lançar exceção quando customerId é nulo")
        void shouldThrowWhenCustomerIdIsNull() {
            assertThatThrownBy(() -> new Order(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("addItem")
    class AddItem {

        @Test
        @DisplayName("deve adicionar item corretamente")
        void shouldAddItem() {
            Order order = buildOrder();
            addItem(order, 10L, 2, "500.00");
            assertThat(order.getItems().size()).isEqualTo(1);
            assertThat(order.getItems().get(0).getProductId()).isEqualTo(10L);
            assertThat(order.getItems().get(0).getQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("deve lançar exceção ao adicionar produto duplicado")
        void shouldThrowWhenAddingDuplicateProduct() {
            Order order = buildOrder();
            addItem(order, 10L, 1, "100.00");

            assertThatThrownBy(() -> addItem(order, 10L, 2, "100.00"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already in order");
        }

        @Test
        @DisplayName("deve lançar exceção ao adicionar item em pedido não-PENDING")
        void shouldThrowWhenOrderIsNotPending() {
            Order order = buildOrder();
            addItem(order, 10L, 1, "100.00");
            order.confirm();

            assertThatThrownBy(() -> addItem(order, 20L, 1, "200.00"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("non-pending");
        }
    }

    @Nested
    @DisplayName("confirm")
    class Confirm {

        @Test
        @DisplayName("deve confirmar pedido com itens")
        void shouldConfirmOrderWithItems() {
            Order order = buildOrder();
            addItem(order, 1L, 2, "100.00");
            order.confirm();

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        }

        @Test
        @DisplayName("deve lançar exceção ao confirmar pedido sem itens")
        void shouldThrowWhenConfirmingEmptyOrder() {
            Order order = buildOrder();
            assertThatThrownBy(order::confirm)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("no items");
        }
    }

    @Nested
    @DisplayName("cancel")
    class Cancel {

        @Test
        @DisplayName("deve cancelar pedido PENDING")
        void shouldCancelPendingOrder() {
            Order order = buildOrder();
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("deve cancelar pedido CONFIRMED")
        void shouldCancelConfirmedOrder() {
            Order order = buildOrder();
            addItem(order, 1L, 1, "100.00");
            order.confirm();
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("deve lançar exceção ao tentar cancelar pedido já CANCELLED")
        void shouldThrowWhenCancellingAlreadyCancelledOrder() {
            Order order = buildOrder();
            order.cancel();
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot transition");
        }

        @Test
        @DisplayName("deve lançar exceção ao tentar cancelar pedido DELIVERED")
        void shouldThrowWhenCancellingDeliveredOrder() {
            Order order = buildOrder();
            order.setStatus(OrderStatus.DELIVERED);
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("calculateTotal")
    class CalculateTotal {

        @Test
        @DisplayName("deve calcular total corretamente com múltiplos itens")
        void shouldCalculateTotalCorrectly() {
            Order order = buildOrder();
            addItem(order, 1L, 2, "100.00"); // 200.00
            addItem(order, 2L, 3, "50.00");  // 150.00

            assertThat(order.calculateTotal()).isEqualByComparingTo("350.00");
        }

        @Test
        @DisplayName("deve retornar zero para pedido sem itens")
        void shouldReturnZeroForEmptyOrder() {
            Order order = buildOrder();
            assertThat(order.calculateTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("OrderStatus — transições")
    class StatusTransitions {

        @Test
        @DisplayName("PENDING pode ir para CONFIRMED e CANCELLED")
        void pendingCanTransitionToConfirmedOrCancelled() {
            assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.CONFIRMED)).isTrue();
            assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
            assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.SHIPPED)).isFalse();
        }

        @Test
        @DisplayName("DELIVERED não pode fazer nenhuma transição")
        void deliveredCannotTransition() {
            for (OrderStatus status : OrderStatus.values()) {
                assertThat(OrderStatus.DELIVERED.canTransitionTo(status)).isFalse();
            }
        }

        @Test
        @DisplayName("CANCELLED não pode fazer nenhuma transição")
        void cancelledCannotTransition() {
            for (OrderStatus status : OrderStatus.values()) {
                assertThat(OrderStatus.CANCELLED.canTransitionTo(status)).isFalse();
            }
        }
    }

}
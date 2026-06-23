package com.marcos.ecommerce.product_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@DisplayName("Product — testes de domínio")
class ProductTest {

    private static Product buildProduct(Integer stockQuantity) {
        Product product = new Product("Notebook", "Desc", new BigDecimal("5000.00"), stockQuantity);
        product.setId(1L);
        return product;
    }

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("deve criar produto com dados válidos")
        void shouldCreateProduct() {
            Product product = buildProduct(10);
            assertThat(product.getName()).isEqualTo("Notebook");
            assertThat(product.getPrice()).isEqualByComparingTo("5000.00");
            assertThat(product.getStockQuantity()).isEqualTo(10);
            assertThat(product.isAvailable()).isTrue();
            assertThat(product.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção quando nome é nulo")
        void shouldThrowWhenNameIsNull() {
            assertThatThrownBy(() -> new Product(null, "desc", BigDecimal.TEN, 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("deve lançar exceção quando preço é nulo")
        void shouldThrowWhenPriceIsNull() {
            assertThatThrownBy(() -> new Product("Nome", "desc", null, 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("price");
        }

        @Test
        @DisplayName("deve lançar exceção quando quantidade é nula")
        void shouldThrowWhenStockIsNull() {
            assertThatThrownBy(() -> new Product("Nome", "desc", BigDecimal.TEN, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("stockQuantity");
        }
    }

    @Nested
    @DisplayName("Estoque — decreaseStock")
    class DecreaseStock {

        @Test
        @DisplayName("deve diminuir estoque corretamente")
        void shouldDecreaseStock() {
            Product product = buildProduct(10);
            product.decreaseStock(3);
            assertThat(product.getStockQuantity()).isEqualTo(7);
        }

        @Test
        @DisplayName("deve zerar estoque e marcar como indisponível")
        void shouldMarkUnavailableWhenStockReachesZero() {
            Product product = buildProduct(10);
            product.decreaseStock(10);
            assertThat(product.getStockQuantity()).isZero();
            assertThat(product.isAvailable()).isFalse();
        }

        @Test
        @DisplayName("deve lançar exceção quando quantidade requisitada excede estoque")
        void shouldThrowWhenInsufficientStock() {
            Product product = buildProduct(10);
            assertThatThrownBy(() -> product.decreaseStock(15))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Insufficient stock");
        }

        @Test
        @DisplayName("deve lançar exceção quando quantidade é zero")
        void shouldThrowWhenQuantityIsZero() {
            Product product = buildProduct(10);
            assertThatThrownBy(() -> product.decreaseStock(0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("greater than zero");
        }

        @Test
        @DisplayName("deve lançar exceção quando quantidade é negativa")
        void shouldThrowWhenQuantityIsNegative() {
            Product product = buildProduct(10);
            assertThatThrownBy(() -> product.decreaseStock(-1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Estoque — increaseStock")
    class IncreaseStock {

        @Test
        @DisplayName("deve aumentar estoque corretamente")
        void shouldIncreaseStock() {
            Product product = buildProduct(5);
            product.increaseStock(10);
            assertThat(product.getStockQuantity()).isEqualTo(15);
        }

        @Test
        @DisplayName("deve lançar exceção quando quantidade é zero")
        void shouldThrowWhenQuantityIsZero() {
            Product product = buildProduct(5);
            assertThatThrownBy(() -> product.increaseStock(0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("updateDetails")
    class UpdateDetails {

        @Test
        @DisplayName("deve atualizar dados do produto")
        void shouldUpdateDetails() {
            Product product = buildProduct(5);
            product.updateDetails("Novo Nome", "Nova descrição", new BigDecimal("9999.00"));
            assertThat(product.getName()).isEqualTo("Novo Nome");
            assertThat(product.getPrice()).isEqualByComparingTo("9999.00");
        }

        @Test
        @DisplayName("deve lançar exceção quando novo nome é nulo")
        void shouldThrowWhenNewNameIsNull() {
            Product product = buildProduct(5);
            assertThatThrownBy(() -> product.updateDetails(null, "desc", BigDecimal.TEN))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

}
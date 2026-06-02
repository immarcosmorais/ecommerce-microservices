package com.marcos.ecommerce.product_service.application.usecase;

import com.marcos.ecommerce.product_service.domain.exception.ProductNotFoundException;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;
import com.marcos.ecommerce.product_service.util.ProductCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DecreaseStockUseCase")
class DecreaseStockUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private DecreaseStockUseCase useCase;

    public static Product buildProduct() {
        Product product = new Product("Notebook", "Desc", new BigDecimal("5000.00"), 10);
        product.setId(1L);
        return product;
    }

    @BeforeEach
    void setUp() {
        // CC3: use case agora usa productRepository internamente (renomeado)
        useCase = new DecreaseStockUseCase(productRepository);
    }

    @Test
    @DisplayName("deve diminuir estoque e salvar produto atualizado")
    void shouldDecreaseStockAndSaveUpdatedProduct() {
        // Arrange
        Product product = buildProduct(); // estoque = 10
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        useCase.execute(1L, 5);

        // Assert — verifica colaboração e estado final do domínio
        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
        assertThat(product.getStockQuantity()).isEqualTo(5); // 10 - 5 = 5
    }

    @Test
    @DisplayName("deve lançar ProductNotFoundException quando produto não existe e não salvar")
    void shouldThrowProductNotFoundExceptionWhenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(99L, 3))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve lançar IllegalStateException quando estoque é insuficiente e não salvar")
    void shouldThrowIllegalStateExceptionWhenInsufficientStockAndNeverSave() {
        // Arrange — produto com apenas 2 unidades
        Product product = new Product("Produto", "desc", BigDecimal.TEN, 2);
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act + Assert — tenta diminuir 10, mas só há 2
        assertThatThrownBy(() -> useCase.execute(1L, 10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve lançar IllegalArgumentException quando quantidade é zero e não salvar")
    void shouldThrowIllegalArgumentExceptionWhenQuantityIsZeroAndNeverSave() {
        // Arrange
        Product product = buildProduct();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act + Assert — quantity = 0 é inválido pela regra de domínio
        assertThatThrownBy(() -> useCase.execute(1L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve zerar estoque e o produto deve ficar indisponível após diminuição total")
    void shouldMarkProductUnavailableWhenStockReachesZero() {
        // Arrange
        Product product = new Product("Item", "desc", BigDecimal.TEN, 3);
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        useCase.execute(1L, 3);

        // Assert
        assertThat(product.getStockQuantity()).isZero();
        assertThat(product.isAvailable()).isFalse();
        verify(productRepository).save(product);
    }
}

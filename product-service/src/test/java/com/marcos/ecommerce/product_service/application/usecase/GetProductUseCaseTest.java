package com.marcos.ecommerce.product_service.application.usecase;

import com.marcos.ecommerce.product_service.application.dto.PagedResponse;
import com.marcos.ecommerce.product_service.application.dto.ProductResponse;
import com.marcos.ecommerce.product_service.domain.exception.ProductNotFoundException;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.PageResult;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetProductUseCase")
class GetProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private GetProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetProductUseCase(productRepository);
    }

    public static Product buildProduct() {
        Product product = new Product("Notebook", "Desc", new BigDecimal("5000.00"), 10);
        product.setId(1L);
        return product;
    }

    // ─── findById ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("deve buscar produto por id e retornar response com dados corretos")
    void shouldFindByIdAndReturnResponse() {
        // Arrange
        Product savedProduct = buildProduct();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(savedProduct));

        // Act — T6: corrigido finById → findById
        ProductResponse response = useCase.findById(1L);

        // Assert — T5: corrigido StockQuantity() → stockQuantity()
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Notebook");
        assertThat(response.price()).isEqualByComparingTo("5000.00");
        assertThat(response.stockQuantity()).isEqualTo(10);
        assertThat(response.available()).isTrue();
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("deve lançar ProductNotFoundException quando produto não existe")
    void shouldThrowProductNotFoundExceptionWhenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act + Assert — T6: corrigido finById → findById
        assertThatThrownBy(() -> useCase.findById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve chamar repository.findById exatamente uma vez sem mais interações")
    void shouldCallFindByIdExactlyOnce() {
        // Arrange
        Product savedProduct = buildProduct();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(savedProduct));

        // Act
        useCase.findById(1L);

        // Assert — GetProductUseCase.findById só chama findById, verifyNoMoreInteractions é válido
        verify(productRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(productRepository);
    }

    // ─── findAll ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deve retornar todos os produtos paginados em response")
    void shouldFindAllProductsAndReturnPagedResponse() {
        // Arrange
        Product savedProduct = buildProduct();
        PageResult<Product> page = new PageResult<>(List.of(savedProduct), 0, 1, 1L, 1);
        when(productRepository.findAll(anyInt(), anyInt())).thenReturn(page);

        // Act
        PagedResponse<ProductResponse> pageResponse = useCase.findAll(0, 1);

        // Assert
        assertThat(pageResponse.content().isEmpty()).isFalse();
        assertThat(pageResponse.content().size()).isEqualTo(1);
        assertThat(pageResponse.totalPages()).isEqualTo(1);
        assertThat(pageResponse.totalElements()).isEqualTo(1L);
        assertThat(pageResponse.first()).isTrue();
        assertThat(pageResponse.last()).isTrue();
        verify(productRepository, times(1)).findAll(anyInt(), anyInt());
    }

    @Test
    @DisplayName("deve retornar página vazia quando não há produtos")
    void shouldReturnEmptyPageWhenNoProductsExist() {
        // Arrange
        PageResult<Product> emptyPage = new PageResult<>(List.of(), 0, 10, 0L, 0);
        when(productRepository.findAll(anyInt(), anyInt())).thenReturn(emptyPage);

        // Act
        PagedResponse<ProductResponse> pageResponse = useCase.findAll(0, 10);

        // Assert
        assertThat(pageResponse.content().isEmpty()).isTrue();
        assertThat(pageResponse.totalElements()).isZero();
    }
}

package com.marcos.ecommerce.product_service.application.usecase;

import com.marcos.ecommerce.product_service.domain.exception.ProductNotFoundException;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteProductUseCase")
class DeleteProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private DeleteProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteProductUseCase(productRepository);
    }

    @Test
    @DisplayName("deve verificar existência e deletar produto quando ele existe")
    void shouldDeleteProductWhenItExists() {
        // Arrange — T1: corrigido: 1L como argumento real, não anyLong()
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act
        useCase.execute(1L);

        // Assert — verifica ambas as interações esperadas
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("deve lançar ProductNotFoundException quando produto não existe e não chamar deleteById")
    void shouldThrowProductNotFoundExceptionWhenProductDoesNotExist() {
        // Arrange
        when(productRepository.existsById(99L)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deve chamar existsById e deleteById exatamente uma vez cada")
    void shouldCallExistsByIdAndDeleteByIdExactlyOnce() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act
        useCase.execute(1L);

        // Assert
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(productRepository);
    }
}

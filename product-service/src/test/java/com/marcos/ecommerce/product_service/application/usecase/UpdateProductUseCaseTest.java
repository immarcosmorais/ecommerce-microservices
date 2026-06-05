package com.marcos.ecommerce.product_service.application.usecase;

import com.marcos.ecommerce.product_service.application.dto.PostProductRequest;
import com.marcos.ecommerce.product_service.application.dto.ProductResponse;
import com.marcos.ecommerce.product_service.application.dto.PutProductRequest;
import com.marcos.ecommerce.product_service.domain.exception.ProductNotFoundException;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductUseCase")
class UpdateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private UpdateProductUseCase useCase;

    @BeforeEach
    void setUp() {
        this.useCase = new UpdateProductUseCase(productRepository);
    }

    public static Product buildProduct() {
        Product product = new Product("Notebook", "Desc", new BigDecimal("5000.00"), 10);
        product.setId(1L);
        return product;
    }

    public static PostProductRequest buildPostRequest() {
        return new PostProductRequest("Notebook", "Desc", new BigDecimal("5000.00"), 10);
    }

    public static PutProductRequest buildPutRequest() {
        return new PutProductRequest("Notebook", "Desc", new BigDecimal("5000.00"));
    }

    @Test
    @DisplayName("deve atualizar produto e retornar response com dados corretos")
    void shouldUpdateProductAndReturnResponse() {
        // Arrange
        Product productSaved = buildProduct() ;
        PutProductRequest request = buildPutRequest();
        when(productRepository.findById(any())).thenReturn(Optional.of(productSaved));
        when(productRepository.save(any(Product.class))).thenReturn(productSaved);

        // Act
        ProductResponse response = useCase.execute(1L, request);

        // Assert — T5: corrigido StockQuantity() → stockQuantity()
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Notebook");
        assertThat(response.price()).isEqualByComparingTo("5000.00");
        assertThat(response.stockQuantity()).isEqualTo(10);
        assertThat(response.available()).isTrue();
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("deve aplicar os novos dados ao domínio antes de salvar")
    void shouldApplyNewDataToDomainBeforeSaving() {
        // Arrange — ArgumentCaptor inspeciona o produto passado ao save
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        Product existingProduct = buildProduct() ;
        PutProductRequest request = new PutProductRequest("Teclado Mecânico", "Nova desc", new BigDecimal("799.00"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(captor.capture())).thenReturn(existingProduct);

        // Act
        useCase.execute(1L, request);

        // Assert
        Product captured = captor.getValue();
        assertThat(captured.getName()).isEqualTo("Teclado Mecânico");
        assertThat(captured.getPrice()).isEqualByComparingTo("799.00");
    }

    @Test
    @DisplayName("deve chamar findById e save exatamente uma vez cada")
    void shouldCallFindByIdAndSaveExactlyOnce() {
        // Arrange
        PutProductRequest request = buildPutRequest();
        Product productSaved = buildProduct() ;
        when(productRepository.save(any(Product.class))).thenReturn(productSaved);
        when(productRepository.findById(any())).thenReturn(Optional.of(productSaved));

        // Act
        useCase.execute(1L, request);

        // T3/T4: ambas as interações verificadas antes de verifyNoMoreInteractions
        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("deve lançar ProductNotFoundException quando produto não existe e não chamar save")
    void shouldThrowProductNotFoundExceptionAndNeverSaveWhenProductDoesNotExist() {
        // Arrange
        PutProductRequest request = buildPutRequest();
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(99L, request))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
        verify(productRepository, never()).save(any());
    }
}

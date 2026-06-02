package com.marcos.ecommerce.product_service.application.usecase;

import com.marcos.ecommerce.product_service.application.dto.ProductRequest;
import com.marcos.ecommerce.product_service.application.dto.ProductResponse;
import com.marcos.ecommerce.product_service.domain.model.Product;
import com.marcos.ecommerce.product_service.domain.repository.ProductRepository;
import com.marcos.ecommerce.product_service.util.ProductCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProductUseCase")
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private CreateProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateProductUseCase(productRepository);
    }

    public static Product buildProduct() {
        Product product = new Product("Notebook", "Desc", new BigDecimal("5000.00"), 10);
        product.setId(1L);
        return product;
    }

    public static ProductRequest buildRequest() {
        return new ProductRequest("Notebook", "Desc", new BigDecimal("5000.00"), 10);
    }

    @Test
    @DisplayName("deve criar produto e retornar response com dados corretos")
    void shouldCreateAProductAndReturnAResponse() {
        // Arrange
        Product savedProduct = buildProduct();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        ProductRequest request = buildRequest();

        // Act
        ProductResponse response = useCase.execute(request);

        // Assert — T5: corrigido StockQuantity() → stockQuantity()
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Notebook");
        assertThat(response.price()).isEqualByComparingTo("5000.00");
        assertThat(response.stockQuantity()).isEqualTo(10);
        assertThat(response.available()).isTrue();
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("deve chamar repository.save exatamente uma vez sem mais interações")
    void shouldCallSaveExactlyOnce() {
        // Arrange
        ProductRequest request = buildRequest();
        Product savedProduct = buildProduct();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        useCase.execute(request);

        // Assert — CreateProductUseCase só chama save, verifyNoMoreInteractions é válido aqui
        verify(productRepository, times(1)).save(any());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName("deve mapear corretamente os campos do request para o domínio")
    void shouldMapRequestFieldsToDomain() {
        // Arrange — ArgumentCaptor inspeciona o objeto passado ao repositório
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        Product savedProduct = buildProduct();
        when(productRepository.save(captor.capture())).thenReturn(savedProduct);

        // Act
        useCase.execute(buildRequest());

        // Assert
        Product captured = captor.getValue();
        assertThat(captured.getName()).isEqualTo("Notebook");
        assertThat(captured.getPrice()).isEqualByComparingTo("5000.00");
        assertThat(captured.getStockQuantity()).isEqualTo(10);
    }
}

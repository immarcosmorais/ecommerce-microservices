package com.marcos.ecommerce.product_service.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcos.ecommerce.product_service.application.dto.PagedResponse;
import com.marcos.ecommerce.product_service.application.dto.PostProductRequest;
import com.marcos.ecommerce.product_service.application.dto.ProductResponse;
import com.marcos.ecommerce.product_service.application.usecase.*;
import com.marcos.ecommerce.product_service.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest carrega apenas a camada web: Controller + ControllerAdvice + filtros MVC
// Use cases são substituídos por @MockitoBean para isolamento total
@WebMvcTest(ProductController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
@DisplayName("ProductController — testes de camada web")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @MockitoBean
    private GetProductUseCase getProductUseCase;

    @MockitoBean
    private UpdateProductUseCase updateProductUseCase;

    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;

    @MockitoBean
    private DecreaseStockUseCase decreaseStockUseCase;

    // Fábrica reutilizável de ProductResponse para os testes
    private ProductResponse buildResponse(Long id, String name) {
        return new ProductResponse(id, name, "Desc", new BigDecimal("999.99"), 10, true, LocalDateTime.now());
    }

    // ─── POST /api/products ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/products — deve criar produto e retornar 201 com body correto")
    void shouldCreateProductAndReturn201() throws Exception {
        // Arrange
        PostProductRequest request = new PostProductRequest("Notebook", "Desc", new BigDecimal("999.99"), 10);
        ProductResponse response = buildResponse(1L, "Notebook");
        when(createProductUseCase.execute(any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("POST /api/products — deve retornar 400 quando nome é nulo ou vazio")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // Arrange — request com nome em branco dispara @NotBlank
        PostProductRequest invalidRequest = new PostProductRequest("", "Desc", new BigDecimal("999.99"), 10);

        // Act + Assert — @Valid + GlobalExceptionHandler retornam 400 com detalhes
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages").isArray());

        // O use case nunca deve ser chamado quando a validação falha
        verifyNoInteractions(createProductUseCase);
    }

    @Test
    @DisplayName("POST /api/products — deve retornar 400 quando preço é nulo")
    void shouldReturn400WhenPriceIsNull() throws Exception {
        // Arrange — price null dispara @NotNull
        PostProductRequest invalidRequest = new PostProductRequest("Notebook", "Desc", null, 10);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(createProductUseCase);
    }

    @Test
    @DisplayName("POST /api/products — deve retornar 400 quando estoque é nulo")
    void shouldReturn400WhenStockQuantityIsNull() throws Exception {
        // Arrange — stockQuantity null dispara @NotNull
        PostProductRequest invalidRequest = new PostProductRequest("Notebook", "Desc", new BigDecimal("999.99"), null);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(createProductUseCase);
    }

    // ─── GET /api/products/{id} ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/products/{id} — deve retornar produto existente com 200")
    void shouldReturnProductByIdWith200() throws Exception {
        // T6: corrigido finById → findById
        when(getProductUseCase.findById(1L)).thenReturn(buildResponse(1L, "Mouse"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Mouse"))
                // CC1: campo serializado como stockQuantity (S minúsculo)
                .andExpect(jsonPath("$.stockQuantity").value(10));
    }

    @Test
    @DisplayName("GET /api/products/{id} — deve retornar 404 para produto inexistente")
    void shouldReturn404WhenProductNotFound() throws Exception {
        // T6: corrigido finById → findById
        when(getProductUseCase.findById(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    // ─── GET /api/products ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/products — deve retornar página de produtos com 200")
    void shouldReturnPagedProductsWith200() throws Exception {
        // Arrange
        List<ProductResponse> items = List.of(
                buildResponse(1L, "Produto A"),
                buildResponse(2L, "Produto B")
        );
        PagedResponse<ProductResponse> page = new PagedResponse<>(items, 0, 10, 2L, 1, true, true);
        when(getProductUseCase.findAll(anyInt(), anyInt())).thenReturn(page);

        // Act + Assert
        mockMvc.perform(get("/api/products?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true));
    }

    // ─── PUT /api/products/{id} ──────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/products/{id} — deve atualizar produto e retornar 200")
    void shouldUpdateProductAndReturn200() throws Exception {
        // Arrange
        PostProductRequest request = new PostProductRequest("Novo Nome", "Nova Desc", new BigDecimal("1999.99"), 5);
        ProductResponse response = buildResponse(1L, "Novo Nome");
        when(updateProductUseCase.execute(eq(1L), any())).thenReturn(response);

        // Act + Assert
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Nome"));
    }

    @Test
    @DisplayName("PUT /api/products/{id} — deve retornar 400 quando dados são inválidos")
    void shouldReturn400WhenUpdateRequestIsInvalid() throws Exception {
        // Arrange — preço negativo dispara @Positive
        PostProductRequest invalidRequest = new PostProductRequest("Produto", "Desc", new BigDecimal("-1.00"), 5);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(updateProductUseCase);
    }

    @Test
    @DisplayName("PUT /api/products/{id} — deve retornar 404 quando produto não existe")
    void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
        // Arrange
        PostProductRequest request = new PostProductRequest("Nome", "Desc", new BigDecimal("100.00"), 1);
        when(updateProductUseCase.execute(eq(99L), any())).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(put("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ─── DELETE /api/products/{id} ───────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/products/{id} — deve retornar 204 quando produto existe")
    void shouldDeleteProductAndReturn204() throws Exception {
        doNothing().when(deleteProductUseCase).execute(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(deleteProductUseCase, times(1)).execute(1L);
    }

    @Test
    @DisplayName("DELETE /api/products/{id} — deve retornar 404 quando produto não existe")
    void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
        doThrow(new ProductNotFoundException(99L)).when(deleteProductUseCase).execute(99L);

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}

package com.marcos.ecommerce.order_service.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcos.ecommerce.order_service.application.dto.*;
import com.marcos.ecommerce.order_service.application.usecase.*;
import com.marcos.ecommerce.order_service.domain.exception.OrderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest; // ← novo pacote Spring Boot 4.x
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

@WebMvcTest(OrderController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
@DisplayName("OrderController — testes de endpoint")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateOrderUseCase createOrderUseCase;

    @MockitoBean
    private GetOrderUseCase getOrderUseCase;

    @MockitoBean
    private CancelOrderUseCase cancelOrderUseCase;

    private OrderResponse buildOrderResponse(Long id, String status) {
        List<OrderItemResponse> items = List.of(
                new OrderItemResponse(1L, "Notebook", 2, new BigDecimal("5000.00"), new BigDecimal("10000.00"))
        );
        return new OrderResponse(id, 10L, status, items, new BigDecimal("10000.00"), 2, LocalDateTime.now());
    }

    private CreateOrderRequest buildCreateRequest() {
        List<OrderItemRequest> items = List.of(
                new OrderItemRequest(1L, "Notebook", 2, new BigDecimal("5000.00"))
        );
        return new CreateOrderRequest(10L, items);
    }

    @Test
    @DisplayName("POST /api/orders — deve criar pedido e retornar 201")
    void shouldCreateOrderAndReturn201() throws Exception {
        OrderResponse response = buildOrderResponse(1L, "CONFIRMED");
        when(createOrderUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.total").value(10000.00));
    }

    @Test
    @DisplayName("GET /api/orders/{id} — deve retornar pedido com 200")
    void shouldReturnOrderById() throws Exception {
        when(getOrderUseCase.findById(1L)).thenReturn(buildOrderResponse(1L, "CONFIRMED"));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerId").value(10L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} — deve retornar 404 para pedido inexistente")
    void shouldReturn404WhenOrderNotFound() throws Exception {
        when(getOrderUseCase.findById(99L)).thenThrow(new OrderNotFoundException(99L));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/orders — deve retornar página paginada com 200")
    void shouldReturnPagedOrders() throws Exception {
        List<OrderResponse> items = List.of(buildOrderResponse(1L, "CONFIRMED"));
        PagedResponse<OrderResponse> page = new PagedResponse<>(items, 0, 10, 1L, 1, true, true);

        when(getOrderUseCase.findAll(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/orders?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    @DisplayName("PATCH /api/orders/{id}/cancel — deve cancelar pedido e retornar 200")
    void shouldCancelOrderAndReturn200() throws Exception {
        OrderResponse cancelled = buildOrderResponse(1L, "CANCELLED");
        when(cancelOrderUseCase.execute(1L)).thenReturn(cancelled);

        mockMvc.perform(patch("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(cancelOrderUseCase, times(1)).execute(1L);
    }

    @Test
    @DisplayName("PATCH /api/orders/{id}/cancel — deve retornar 404 para pedido inexistente")
    void shouldReturn404WhenCancellingNonExistentOrder() throws Exception {
        when(cancelOrderUseCase.execute(99L)).thenThrow(new OrderNotFoundException(99L));

        mockMvc.perform(patch("/api/orders/99/cancel"))
                .andExpect(status().isNotFound());
    }

}
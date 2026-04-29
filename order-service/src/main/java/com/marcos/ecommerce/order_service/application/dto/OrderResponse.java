package com.marcos.ecommerce.order_service.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Dados de resposta de um pedido")
public record OrderResponse(

        @Schema(description = "ID único do pedido", example = "1")
        Long id,

        @Schema(description = "ID do cliente", example = "10")
        Long customerId,

        @Schema(description = "Status do pedido", example = "PENDING",
                allowableValues = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"})
        String status,

        @Schema(description = "Itens do pedido")
        List<OrderItemResponse> items,

        @Schema(description = "Valor total do pedido", example = "2599.98")
        BigDecimal total,

        @Schema(description = "Quantidade total de itens", example = "3")
        int totalItems,

        @Schema(description = "Data e hora de criação do pedido")
        LocalDateTime createdAt

) {}

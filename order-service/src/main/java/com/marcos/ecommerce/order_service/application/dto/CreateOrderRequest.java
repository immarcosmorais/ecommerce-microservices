package com.marcos.ecommerce.order_service.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Dados para criação de um novo pedido")
public record CreateOrderRequest(
        @Schema(description = "ID do cliente que está fazendo o pedido", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        Long customerId,
        @Schema(description = "Lista de itens do pedido", requiredMode = Schema.RequiredMode.REQUIRED)
        List<OrderItemRequest> items
) {}


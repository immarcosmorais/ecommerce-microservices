package com.marcos.ecommerce.product_service.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Dados para criação ou atualização de produto")
public record ProductRequest(
        @Schema(description = "Nome do produto", example = "Notebook Dell XPS 15", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Descrição detalhada do produto", example = "Notebook premium com processador Intel i9")
        String description,
        @Schema(description = "Preço unitário do produto", example = "8999.99", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal price,
        @Schema(description = "Quantidade inicial em estoque", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer stockQuantity
) {
}

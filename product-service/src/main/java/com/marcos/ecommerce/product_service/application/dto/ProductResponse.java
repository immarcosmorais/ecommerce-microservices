package com.marcos.ecommerce.product_service.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados de resposta de um produto")
public record ProductResponse(

        @Schema(description = "Identificador único do produto", example = "1")
        Long id,

        @Schema(description = "Nome do produto", example = "Notebook Dell XPS 15")
        String name,

        @Schema(description = "Descrição do produto", example = "Notebook premium com processador Intel i9")
        String description,

        @Schema(description = "Preço unitário", example = "8999.99")
        BigDecimal price,

        @Schema(description = "Quantidade em estoque", example = "45")
        Integer StockQuantity,

        @Schema(description = "Indica se o produto está disponível (estoque > 0)", example = "true")
        boolean available,

        @Schema(description = "Data e hora de criação do produto")
        LocalDateTime createdAt

) {
}

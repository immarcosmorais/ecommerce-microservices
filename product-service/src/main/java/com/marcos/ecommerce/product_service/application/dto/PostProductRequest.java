package com.marcos.ecommerce.product_service.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para criação ou atualização de produto")
public record PostProductRequest(

        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(max = 255, message = "O nome não pode exceder 255 caracteres")
        @Schema(description = "Nome do produto", example = "Notebook Dell XPS 15", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Size(max = 1000, message = "A descrição não pode exceder 1000 caracteres")
        @Schema(description = "Descrição detalhada do produto", example = "Notebook premium com processador Intel i9")
        String description,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        @Schema(description = "Preço unitário do produto", example = "8999.99", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal price,

        @NotNull(message = "A quantidade em estoque é obrigatória")
        @Positive(message = "A quantidade em estoque deve ser maior que zero")
        @Schema(description = "Quantidade inicial em estoque", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer stockQuantity
) {
}

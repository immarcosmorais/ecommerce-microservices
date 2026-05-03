package com.marcos.ecommerce.product_service.application.dto;

import com.marcos.ecommerce.product_service.domain.model.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resposta paginada genérica")
public record PagedResponse<T>(

        @Schema(description = "Itens da página atual")
        List<T> content,

        @Schema(description = "Número da página atual (zero-based)", example = "0")
        int page,

        @Schema(description = "Tamanho da página", example = "10")
        int size,

        @Schema(description = "Total de elementos em todas as páginas", example = "150")
        long totalElements,

        @Schema(description = "Total de páginas", example = "15")
        int totalPages,

        @Schema(description = "É a primeira página?", example = "true")
        boolean first,

        @Schema(description = "É a última página?", example = "false")
        boolean last
) {

    public static <T> PagedResponse<T> from(PageResult<T> pageResult) {
        return new PagedResponse<>(
                pageResult.content(),
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages(),
                pageResult.isFirst(),
                pageResult.isLast()
        );
    }
}

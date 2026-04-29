package com.marcos.ecommerce.product_service.infrastructure.rest;

import com.marcos.ecommerce.product_service.application.dto.ProductRequest;
import com.marcos.ecommerce.product_service.application.dto.ProductResponse;
import com.marcos.ecommerce.product_service.application.usecase.CreateProductUseCase;
import com.marcos.ecommerce.product_service.application.usecase.DeleteProductUseCase;
import com.marcos.ecommerce.product_service.application.usecase.GetProductUseCase;
import com.marcos.ecommerce.product_service.application.usecase.UpdateProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Operações de gerenciamento de produtos e estoque")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    public ProductController(
            CreateProductUseCase createProductUseCase,
            GetProductUseCase getProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            DeleteProductUseCase deleteProductUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    @Operation(summary = "Criar produto", description = "Cria um novo produto no catálogo com estoque inicial")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Produto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request){
        ProductResponse response = this.createProductUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto pelo seu identificador único")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            )
    })

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id){
        ProductResponse response = getProductUseCase.finById(id);
        return  ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todos os produtos", description = "Retorna a lista completa de produtos do catálogo")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll(){
        List<ProductResponse> response = getProductUseCase.findAll();
        return  ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable long id,
            @RequestBody ProductRequest request
    ){
        ProductResponse response = this.updateProductUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remover produto", description = "Remove um produto do catálogo pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        this.deleteProductUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

}

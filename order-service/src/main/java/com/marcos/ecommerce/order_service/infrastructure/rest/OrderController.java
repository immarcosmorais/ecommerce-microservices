package com.marcos.ecommerce.order_service.infrastructure.rest;

import com.marcos.ecommerce.order_service.application.dto.CreateOrderRequest;
import com.marcos.ecommerce.order_service.application.dto.OrderResponse;
import com.marcos.ecommerce.order_service.application.usecase.CancelOrderUseCase;
import com.marcos.ecommerce.order_service.application.usecase.CreateOrderUseCase;
import com.marcos.ecommerce.order_service.application.usecase.GetOrderUseCase;
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
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Operações de gerenciamento de pedidos")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    public OrderController(
            CreateOrderUseCase createOrderUseCase,
            GetOrderUseCase getOrderUseCase,
            CancelOrderUseCase cancelOrderUseCase
    ) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
    }

    @Operation(summary = "Criar pedido", description = "Cria um novo pedido e publica o evento OrderCreated no Kafka")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos no pedido",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createOrderUseCase.execute(request));
    }

    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido pelo seu identificador único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(getOrderUseCase.findById(id));
    }

    @Operation(summary = "Listar todos os pedidos", description = "Retorna todos os pedidos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        return ResponseEntity.ok(getOrderUseCase.findAll());
    }

    @Operation(summary = "Buscar pedidos por cliente", description = "Retorna todos os pedidos de um cliente específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "404", description = "Nenhum pedido encontrado para este cliente",
                    content = @Content)
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> findByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(getOrderUseCase.findByCustomerId(customerId));
    }

    @Operation(summary = "Cancelar pedido",
            description = "Cancela um pedido. Só é possível cancelar pedidos com status PENDING ou CONFIRMED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida",
                    content = @Content)
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(cancelOrderUseCase.execute(id));
    }

}

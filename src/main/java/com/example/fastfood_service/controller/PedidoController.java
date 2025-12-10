package com.example.fastfood_service.controller;

import com.example.fastfood_service.model.Pedido;
import com.example.fastfood_service.service.PedidoService;
import com.example.fastfood_service.service.PedidoService.EstadisticasPedidos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@Tag(name = "Pedidos", description = "API para gestionar pedidos de comida rápida")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService servicio;

    public PedidoController(PedidoService servicio) {
        this.servicio = servicio;
    }

    // 4.1 Registrar
    @Operation(summary = "Registrar un nuevo pedido")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Pedido pedidoRequest) {
        try {
            Pedido creado = servicio.crearPedido(pedidoRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 4.2 Listar todos
    @Operation(summary = "Listar todos los pedidos")
    @GetMapping
    public ResponseEntity<Pedido[]> listar() {
        return ResponseEntity.ok(servicio.listarTodos());
    }

    // 4.3 Obtener por id
    @Operation(summary = "Obtener un pedido por id")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable int id) {
        try {
            Pedido pedido = servicio.buscarPorId(id);
            return ResponseEntity.ok(pedido);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 4.4 Cancelar pedido
    @Operation(summary = "Cancelar un pedido por id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelar(@PathVariable int id) {
        try {
            Pedido cancelado = servicio.cancelarPedido(id);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Pedido cancelado correctamente",
                    "pedido", cancelado
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 4.5 Despachar siguiente pedido
    @Operation(summary = "Despachar el siguiente pedido en la cola")
    @PostMapping("/despachar")
    public ResponseEntity<?> despachar() {
        try {
            Pedido despachado = servicio.despacharSiguiente();
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Pedido despachado correctamente",
                    "pedido", despachado
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 4.6 Estadísticas
    @Operation(summary = "Obtener estadísticas de pedidos")
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasPedidos> estadisticas() {
        return ResponseEntity.ok(servicio.obtenerEstadisticas());
    }

    // 4.7 Total recursivo
    @Operation(summary = "Calcular monto total de pedidos usando un método recursivo")
    @GetMapping("/total-recursivo")
    public ResponseEntity<Map<String, Double>> totalRecursivo() {
        double total = servicio.calcularTotalMontoRecursivo();
        return ResponseEntity.ok(Map.of("totalMontoRecursivo", total));
    }

    // 4.8 Rollback
    @Operation(summary = "Realizar rollback de la última operación")
    @PostMapping("/rollback")
    public ResponseEntity<?> rollback() {
        try {
            Pedido pedido = servicio.rollbackUltimaOperacion();
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Rollback realizado correctamente",
                    "pedido", pedido
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

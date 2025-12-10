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

// Aquí se definen los endpoints que se consumen desde el cliente
@Tag(name = "Pedidos", description = "API para gestionar pedidos de comida rápida")
@RestController
@RequestMapping("/api/pedidos") // Prefijo común para todos los endpoints de este controlador
public class PedidoController {

    // Servicio donde está la lógica de negocio
    private final PedidoService servicio;

    // Inyección de dependencia por constructor
    public PedidoController(PedidoService servicio) {
        this.servicio = servicio;
    }

    //  Registrar un nuevo pedido
    @Operation(summary = "Registrar nuevo pedido")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Pedido pedidoRequest) {
        try {
            // Delegamos al servicio la creación del pedido
            Pedido creado = servicio.crearPedido(pedidoRequest);
            // Devolvemos 201 CREATED con el pedido creado en el cuerpo
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            // Si hay error de validación, devolvemos 400 BAD REQUEST con un mensaje de error
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    //  Listar todos los pedidos
    @Operation(summary = "Listar todos los pedidos")
    @GetMapping
    public ResponseEntity<Pedido[]> listar() {
        // Obtenemos todos los pedidos y devolvemos 200 OK
        return ResponseEntity.ok(servicio.listarTodos());
    }

    // Obtener un pedido por su id
    @Operation(summary = "Obtener pedido por id")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable int id) {
        try {
            // Buscamos el pedido por id
            Pedido pedido = servicio.buscarPorId(id);
            // Si se encuentra, devolvemos 200 OK con el pedido
            return ResponseEntity.ok(pedido);
        } catch (NoSuchElementException e) {
            // Si no se encuentra, devolvemos 404 NOT FOUND con un mensaje de error
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //  Cancelar un pedido por id
    @Operation(summary = "Cancelar pedido por id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelar(@PathVariable int id) {
        try {
            // Delegamos al servicio la cancelación del pedido
            Pedido cancelado = servicio.cancelarPedido(id);
            // Devolvemos 200 OK con un mensaje y el pedido ya cancelado
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Pedido cancelado correctamente",
                    "pedido", cancelado
            ));
        } catch (NoSuchElementException e) {
            // Si no existe el pedido, devolvemos 404 NOT FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //  Despachar el siguiente pedido en la cola
    @Operation(summary = "Despachar el siguiente pedido")
    @PostMapping("/despachar")
    public ResponseEntity<?> despachar() {
        try {
            // Pedimos al servicio que despache el siguiente pedido en la cola
            Pedido despachado = servicio.despacharSiguiente();
            // Devolvemos 200 OK con mensaje y el pedido despachado
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Pedido despachado correctamente",
                    "pedido", despachado
            ));
        } catch (IllegalStateException e) {
            // Si no hay pedidos por despachar, devolvemos 409 CONFLICT con el error
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener estadísticas de los pedidos
    @Operation(summary = "Estadísticas de pedidos")
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasPedidos> estadisticas() {
        // Obtenemos las estadísticas del servicio y las devolvemos con 200 OK
        return ResponseEntity.ok(servicio.obtenerEstadisticas());
    }

    //  Calcular el monto total de los pedidos usando el método recursivo
    @Operation(summary = "Calcular monto total de pedidos")
    @GetMapping("/total-recursivo")
    public ResponseEntity<Map<String, Double>> totalRecursivo() {
        // Calculamos el total recursivo en el servicio
        double total = servicio.calcularTotalMontoRecursivo();
        // Devolvemos un JSON con la propiedad "totalMontoRecursivo"
        return ResponseEntity.ok(Map.of("totalMontoRecursivo", total));
    }

    //  Realizar rollback de la última operación registrada en el historial
    @Operation(summary = "Realizar rollback ")
    @PostMapping("/rollback")
    public ResponseEntity<?> rollback() {
        try {
            // Pedimos al servicio que revierta la última operación
            Pedido pedido = servicio.rollbackUltimaOperacion();
            // Devolvemos 200 OK con mensaje y el pedido afectado por el rollback
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Rollback realizado correctamente",
                    "pedido", pedido
            ));
        } catch (IllegalStateException e) {
            // Si no hay operaciones para revertir, devolvemos 409 CONFLICT
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

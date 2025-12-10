package com.example.fastfood_service.service;

import com.example.fastfood_service.datastructures.HistorialStack;
import com.example.fastfood_service.datastructures.PedidoQueue;
import com.example.fastfood_service.datastructures.SinglyLinkedList;
import com.example.fastfood_service.model.HistorialOperacion;
import com.example.fastfood_service.model.Pedido;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class PedidoService {

    private final SinglyLinkedList pedidos = new SinglyLinkedList();
    private final PedidoQueue colaPendientes = new PedidoQueue();
    private final HistorialStack historial = new HistorialStack();

    private int nextId = 1;

    // -------- Validación --------
    private void validar(Pedido pedido) {
        if (pedido.getNombreCliente() == null || pedido.getNombreCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("nombreCliente no puede ser vacío");
        }
        if (pedido.getDescripcion() == null || pedido.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("descripcion no puede ser vacía");
        }
        if (pedido.getMonto() <= 0) {
            throw new IllegalArgumentException("monto debe ser mayor a 0");
        }
    }

    // -------- 4.1 Registrar pedido --------
    public Pedido crearPedido(Pedido body) {
        validar(body);

        Pedido pedido = new Pedido(
                nextId++,
                body.getNombreCliente(),
                body.getDescripcion(),
                body.getMonto(),
                "REGISTRADO"
        );

        pedidos.add(pedido);
        colaPendientes.enqueue(pedido);

        historial.push(new HistorialOperacion("CREAR", null, new Pedido(pedido)));

        return pedido;
    }

    // -------- 4.2 Listar todos --------
    public Pedido[] listarTodos() {
        return pedidos.toArray();
    }

    // -------- 4.3 Buscar por id --------
    public Pedido buscarPorId(int id) {
        Pedido p = pedidos.findById(id);
        if (p == null) {
            throw new NoSuchElementException("Pedido no encontrado");
        }
        return p;
    }

    // -------- 4.4 Cancelar pedido --------
    public Pedido cancelarPedido(int id) {
        Pedido pedido = pedidos.findById(id);
        if (pedido == null) {
            throw new NoSuchElementException("Pedido no encontrado");
        }

        Pedido antes = new Pedido(pedido);
        pedido.setEstado("CANCELADO");
        Pedido despues = new Pedido(pedido);

        colaPendientes.removeById(id);

        historial.push(new HistorialOperacion("CANCELAR", antes, despues));

        return pedido;
    }

    // -------- 4.5 Despachar siguiente --------
    public Pedido despacharSiguiente() {
        if (colaPendientes.isEmpty()) {
            throw new IllegalStateException("No hay pedidos por despachar");
        }

        Pedido pedido = colaPendientes.dequeue();
        if (pedido == null) {
            throw new IllegalStateException("No hay pedidos por despachar");
        }

        Pedido antes = new Pedido(pedido);
        pedido.setEstado("DESPACHADO");
        Pedido despues = new Pedido(pedido);

        historial.push(new HistorialOperacion("DESPACHAR", antes, despues));

        return pedido;
    }

    // -------- 4.6 Estadísticas --------
    public EstadisticasPedidos obtenerEstadisticas() {
        Pedido[] arr = pedidos.toArray();
        int total = arr.length;
        double totalMonto = 0.0;
        double totalMontoSinCancelados = 0.0;
        int registrados = 0;
        int despachados = 0;
        int cancelados = 0;

        for (Pedido p : arr) {
            double monto = p.getMonto();
            String estado = p.getEstado();

            totalMonto += monto;

            if (!"CANCELADO".equals(estado)) {
                totalMontoSinCancelados += monto;
            }

            if ("REGISTRADO".equals(estado)) {
                registrados++;
            } else if ("DESPACHADO".equals(estado)) {
                despachados++;
            } else if ("CANCELADO".equals(estado)) {
                cancelados++;
            }
        }

        return new EstadisticasPedidos(
                total,
                totalMonto,
                totalMontoSinCancelados,
                registrados,
                despachados,
                cancelados
        );
    }

    public static class EstadisticasPedidos {
        private int totalPedidos;
        private double totalMonto;
        private double totalMontoSinCancelados;
        private int totalRegistrados;
        private int totalDespachados;
        private int totalCancelados;

        public EstadisticasPedidos(int totalPedidos,
                                   double totalMonto,
                                   double totalMontoSinCancelados,
                                   int totalRegistrados,
                                   int totalDespachados,
                                   int totalCancelados) {
            this.totalPedidos = totalPedidos;
            this.totalMonto = totalMonto;
            this.totalMontoSinCancelados = totalMontoSinCancelados;
            this.totalRegistrados = totalRegistrados;
            this.totalDespachados = totalDespachados;
            this.totalCancelados = totalCancelados;
        }

        public int getTotalPedidos() { return totalPedidos; }
        public double getTotalMonto() { return totalMonto; }
        public double getTotalMontoSinCancelados() { return totalMontoSinCancelados; }
        public int getTotalRegistrados() { return totalRegistrados; }
        public int getTotalDespachados() { return totalDespachados; }
        public int getTotalCancelados() { return totalCancelados; }
    }

    // -------- 4.7 Total recursivo --------
    public double calcularTotalMontoRecursivo() {
        Pedido[] arr = pedidos.toArray();
        return calcularTotalRecursivo(arr, 0);
    }

    private double calcularTotalRecursivo(Pedido[] arr, int index) {
        if (index >= arr.length) {
            return 0.0;
        }
        return arr[index].getMonto() + calcularTotalRecursivo(arr, index + 1);
    }

    // -------- 4.8 Rollback --------
    public Pedido rollbackUltimaOperacion() {
        if (historial.isEmpty()) {
            throw new IllegalStateException("No hay operaciones para revertir");
        }

        HistorialOperacion op = historial.pop();
        String tipo = op.getTipoOperacion();

        switch (tipo) {
            case "CREAR": {
                Pedido creado = op.getPedidoDespues();
                pedidos.removeById(creado.getId());
                colaPendientes.removeById(creado.getId());
                return creado;
            }
            case "CANCELAR": {
                Pedido antes = op.getPedidoAntes();
                Pedido actual = pedidos.findById(antes.getId());
                if (actual == null) {
                    throw new IllegalStateException("No se encontró el pedido para rollback");
                }
                actual.setEstado(antes.getEstado());

                String estadoAnterior = antes.getEstado();
                if ("REGISTRADO".equals(estadoAnterior) || "EN_PREPARACION".equals(estadoAnterior)) {
                    // por si acaso ya estaba en cola, lo saco y luego lo vuelvo a meter
                    colaPendientes.removeById(actual.getId());
                    colaPendientes.enqueue(actual);
                }
                return actual;
            }
            case "DESPACHAR": {
                Pedido antes = op.getPedidoAntes();
                Pedido actual = pedidos.findById(antes.getId());
                if (actual == null) {
                    throw new IllegalStateException("No se encontró el pedido para rollback");
                }

                // regresamos el estado anterior
                actual.setEstado(antes.getEstado());

                String estadoAnterior = antes.getEstado();
                if ("REGISTRADO".equals(estadoAnterior) || "EN_PREPARACION".equals(estadoAnterior)) {
                    // queremos que la cola quede 1,2,3 igual que antes
                    colaPendientes.removeById(actual.getId()); // defensivo
                    colaPendientes.enqueueFront(actual);       // lo mandamos al frente
                }

                return actual;
            }
            default:
                throw new IllegalStateException("Tipo de operación desconocido: " + tipo);
        }
    }
}

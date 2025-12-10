package com.example.fastfood_service.service;

import com.example.fastfood_service.datastructures.HistorialStack;
import com.example.fastfood_service.datastructures.PedidoQueue;
import com.example.fastfood_service.datastructures.SinglyLinkedList;
import com.example.fastfood_service.model.HistorialOperacion;
import com.example.fastfood_service.model.Pedido;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

// Aquí se usan las (lista, cola, pila)
@Service
public class PedidoService {

    // Lista simplemente enlazada donde se almacenan TODOS los pedidos del sistema
    private final SinglyLinkedList pedidos = new SinglyLinkedList();

    // Cola de pedidos pendientes por despachar
    private final PedidoQueue colaPendientes = new PedidoQueue();

    // Pila de historial para poder hacer rollback de la última operación
    private final HistorialStack historial = new HistorialStack();

    // Contador para generar IDs
    private int nextId = 1;

    // Validación
    private void validar(Pedido pedido) {
        // Validar nombre del cliente
        if (pedido.getNombreCliente() == null || pedido.getNombreCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("nombreCliente no puede ser vacío");
        }
        // Validar descripción del pedido
        if (pedido.getDescripcion() == null || pedido.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("descripcion no puede ser vacía");
        }
        // Validar monto positivo
        if (pedido.getMonto() <= 0) {
            throw new IllegalArgumentException("monto debe ser mayor a 0");
        }
    }

    // Registrar pedido
    public Pedido crearPedido(Pedido body) {
        // Primero validamos la información recibida
        validar(body);

        // Creamos un nuevo objeto Pedido con:
        Pedido pedido = new Pedido(
                nextId++,
                body.getNombreCliente(),
                body.getDescripcion(),
                body.getMonto(),
                "REGISTRADO"
        );

        // Agregamos el pedido a la lista simplemente enlazada
        pedidos.add(pedido);

        // Encolamos el pedido en la cola de pendientes
        colaPendientes.enqueue(pedido);

        // Guardamos la operación en el historial (pila)
        // tipoOperacion = "CREAR"
        // pedidoAntes = null (no existía antes)
        // pedidoDespues = copia del pedido recién creado
        historial.push(new HistorialOperacion("CREAR", null, new Pedido(pedido)));

        // Devolvemos el pedido creado (se enviará al cliente como respuesta)
        return pedido;
    }

    // Listar todos
    // Devuelve todos los pedidos como un arreglo
    public Pedido[] listarTodos() {
        // Convertimos la lista enlazada a un arreglo de Pedido y lo retornamos
        return pedidos.toArray();
    }

    //  Buscar por id
    // Busca un pedido por su id, si no existe lanza excepción
    public Pedido buscarPorId(int id) {
        // Buscamos en la lista simplemente enlazada
        Pedido p = pedidos.findById(id);
        if (p == null) {
            // Si no se encontró, lanzamos una excepción que el controller transformará en 404
            throw new NoSuchElementException("Pedido no encontrado");
        }
        return p;
    }

    // Cancelar pedido
    // Cambia el estado de un pedido a CANCELADO, lo saca de la cola y registra la operación
    public Pedido cancelarPedido(int id) {
        // Buscamos el pedido en la lista principal
        Pedido pedido = pedidos.findById(id);
        if (pedido == null) {
            throw new NoSuchElementException("Pedido no encontrado");
        }

        // Creamos una copia del estado ANTES de cancelar (para rollback)
        Pedido antes = new Pedido(pedido);

        // Cambiamos el estado a CANCELADO
        pedido.setEstado("CANCELADO");

        // Creamos otra copia del estado DESPUÉS de cancelar
        Pedido despues = new Pedido(pedido);

        // Quitamos el pedido de la cola de pendientes (si estaba ahí)
        colaPendientes.removeById(id);

        // Registramos la operación en el historial:
        // tipoOperacion = "CANCELAR"
        // pedidoAntes = antes (estado previo)
        // pedidoDespues = despues (ya cancelado)
        historial.push(new HistorialOperacion("CANCELAR", antes, despues));

        // Devolvemos el pedido en su estado actual (CANCELADO)
        return pedido;
    }

    //Despachar siguiente
    // Toma el siguiente pedido en la cola, lo marca como DESPACHADO y lo registra en el historial
    public Pedido despacharSiguiente() {
        // Si la cola está vacía, no hay nada que despachar
        if (colaPendientes.isEmpty()) {
            throw new IllegalStateException("No hay pedidos por despachar");
        }

        // Sacamos el siguiente pedido en la cola (el primero en entrar)
        Pedido pedido = colaPendientes.dequeue();
        if (pedido == null) {
            // Revisión adicional: en caso de inconsistencia
            throw new IllegalStateException("No hay pedidos por despachar");
        }

        // Copiamos el estado antes de despachar
        Pedido antes = new Pedido(pedido);

        // Cambiamos su estado a DESPACHADO
        pedido.setEstado("DESPACHADO");

        // Copiamos el estado después de despachar
        Pedido despues = new Pedido(pedido);

        // Registramos la operación DESPACHAR en el historial
        historial.push(new HistorialOperacion("DESPACHAR", antes, despues));

        // Devolvemos el pedido ya despachado
        return pedido;
    }

    //  Estadísticas
    // Calcula estadísticas básicas sobre los pedidos
    public EstadisticasPedidos obtenerEstadisticas() {
        // Obtenemos todos los pedidos como arreglo
        Pedido[] arr = pedidos.toArray();

        int total = arr.length;
        double totalMonto = 0.0;
        double totalMontoSinCancelados = 0.0;
        int registrados = 0;
        int despachados = 0;
        int cancelados = 0;

        // Recorremos todos los pedidos
        for (Pedido p : arr) {
            double monto = p.getMonto();
            String estado = p.getEstado();

            // Sumamos siempre al totalMonto
            totalMonto += monto;

            // Solo sumamos al totalMontoSinCancelados si el estado NO es CANCELADO
            if (!"CANCELADO".equals(estado)) {
                totalMontoSinCancelados += monto;
            }

            // Contamos según el estado
            if ("REGISTRADO".equals(estado)) {
                registrados++;
            } else if ("DESPACHADO".equals(estado)) {
                despachados++;
            } else if ("CANCELADO".equals(estado)) {
                cancelados++;
            }
        }

        // Devolvemos un objeto con todas las estadísticas calculadas
        return new EstadisticasPedidos(
                total,
                totalMonto,
                totalMontoSinCancelados,
                registrados,
                despachados,
                cancelados
        );
    }

    // Clase interna que representa el DTO de estadísticas que devolvemos al controller
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

    // Total recursivo
    // Calcula el monto total de todos los pedidos
    public double calcularTotalMontoRecursivo() {
        // Pasamos la lista a arreglo para facilitar el recorrido recursivo
        Pedido[] arr = pedidos.toArray();
        // Llamamos al método recursivo comenzando en el índice 0
        return calcularTotalRecursivo(arr, 0);
    }

    // Método recursivo privado: suma los montos desde "index" hasta el final del arreglo
    private double calcularTotalRecursivo(Pedido[] arr, int index) {
        // Caso base: si ya nos pasamos del último índice, regresamos 0.0
        if (index >= arr.length) {
            return 0.0;
        }
        // Caso recursivo: monto actual + suma del resto
        return arr[index].getMonto() + calcularTotalRecursivo(arr, index + 1);
    }

    // Rollback
    // Deshace la ÚLTIMA operación realizada (CREAR, CANCELAR o DESPACHAR)
    public Pedido rollbackUltimaOperacion() {
        // Si la pila de historial está vacía, no hay nada que revertir
        if (historial.isEmpty()) {
            throw new IllegalStateException("No hay operaciones para revertir");
        }

        // Sacamos la última operación del historial (top de la pila)
        HistorialOperacion op = historial.pop();
        String tipo = op.getTipoOperacion();

        // Según el tipo de operación, aplicamos una lógica distinta de rollback
        switch (tipo) {
            case "CREAR": {
                // Si la operación fue CREAR, el rollback es eliminar el pedido creado
                Pedido creado = op.getPedidoDespues();
                // Lo quitamos de la lista principal
                pedidos.removeById(creado.getId());
                // Y también de la cola de pendientes (por si está ahí)
                colaPendientes.removeById(creado.getId());
                return creado;
            }
            case "CANCELAR": {
                // Si fue CANCELAR, tenemos que regresar el pedido a su estado anterior
                Pedido antes = op.getPedidoAntes();
                // Buscamos el pedido actual en la lista
                Pedido actual = pedidos.findById(antes.getId());
                if (actual == null) {
                    throw new IllegalStateException("No se encontró el pedido para rollback");
                }
                // Restauramos el estado anterior (por ejemplo, de CANCELADO a REGISTRADO)
                actual.setEstado(antes.getEstado());

                String estadoAnterior = antes.getEstado();
                // Si antes era un pedido pendiente, debe estar en la cola
                if ("REGISTRADO".equals(estadoAnterior) || "EN_PREPARACION".equals(estadoAnterior)) {
                    // Por si acaso ya estuviera en la cola, lo removemos primero
                    colaPendientes.removeById(actual.getId());
                    // Y lo volvemos a encolar al final
                    colaPendientes.enqueue(actual);
                }
                return actual;
            }
            case "DESPACHAR": {
                // Si fue DESPACHAR, hay que regresarlo a su estado anterior
                Pedido antes = op.getPedidoAntes();
                // Buscamos el pedido actual en la lista
                Pedido actual = pedidos.findById(antes.getId());
                if (actual == null) {
                    throw new IllegalStateException("No se encontró el pedido para rollback");
                }

                // Restauramos el estado que tenía antes de ser despachado
                actual.setEstado(antes.getEstado());

                String estadoAnterior = antes.getEstado();
                // Si antes era un pedido pendiente, debe regresar a la cola de pendientes
                if ("REGISTRADO".equals(estadoAnterior) || "EN_PREPARACION".equals(estadoAnterior)) {
                    // Por si estuviera en la cola, lo removemos
                    colaPendientes.removeById(actual.getId());
                    // Lo encolamos al FRENTE para conservar el orden 1,2,3 como estaba
                    colaPendientes.enqueueFront(actual);
                }

                return actual;
            }
            default:
                // Si llega aquí, se trata de un tipo de operación que no conocemos
                throw new IllegalStateException("Tipo de operación desconocido: " + tipo);
        }
    }
}

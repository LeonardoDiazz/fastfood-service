package com.example.fastfood_service.model;

// Se usa para poder deshacer (rollback) cambios, guardando el "antes" y el "después"
public class HistorialOperacion {

    // Tipo de operación realizada: CREAR, CANCELAR, DESPACHAR
    private String tipoOperacion;

    private Pedido pedidoAntes;

    private Pedido pedidoDespues;

    public HistorialOperacion(String tipoOperacion, Pedido pedidoAntes, Pedido pedidoDespues) {
        this.tipoOperacion = tipoOperacion;
        this.pedidoAntes = pedidoAntes;
        this.pedidoDespues = pedidoDespues;
    }

    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public Pedido getPedidoAntes() { return pedidoAntes; }
    public void setPedidoAntes(Pedido pedidoAntes) { this.pedidoAntes = pedidoAntes; }

    public Pedido getPedidoDespues() { return pedidoDespues; }
    public void setPedidoDespues(Pedido pedidoDespues) { this.pedidoDespues = pedidoDespues; }
}

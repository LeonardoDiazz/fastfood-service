package com.example.fastfood_service.model;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// Indicamos el orden en que queremos que salgan los campos cuando se convierta a JSON
@JsonPropertyOrder({ "id", "nombreCliente", "descripcion", "monto", "estado" })
public class Pedido {

    private int id;

    private String nombreCliente;

    private String descripcion;

    private double monto;

    private String estado;

    public Pedido() {
    }

    public Pedido(int id, String nombreCliente, String descripcion, double monto, String estado) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.descripcion = descripcion;
        this.monto = monto;
        this.estado = estado;
    }

    // Se usa para guardar el "antes" y "después" en el historial (rollback)
    public Pedido(Pedido other) {
        this.id = other.id;
        this.nombreCliente = other.nombreCliente;
        this.descripcion = other.descripcion;
        this.monto = other.monto;
        this.estado = other.estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

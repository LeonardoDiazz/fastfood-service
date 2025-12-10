package com.example.fastfood_service.datastructures;
import com.example.fastfood_service.model.HistorialOperacion;

public class HistorialStack {


    private static class Node {
        HistorialOperacion data;
        Node next;

        Node(HistorialOperacion data) {
            this.data = data;
        }
    }

    // Puntero al tope de la pila (última operación registrada)
    private Node top;

    // Apilar (push): agrega una nueva operación en el tope de la pila
    public void push(HistorialOperacion op) {
        Node nuevo = new Node(op);
        // El nuevo nodo apunta al que antes era el tope
        nuevo.next = top;
        // Ahora el nuevo nodo se convierte en el tope de la pila
        top = nuevo;
    }

    // Desapilar (pop): saca y devuelve la última operación de la pila
    public HistorialOperacion pop() {
        if (top == null) {
            // Pila vacía, no hay nada que desapilar
            return null;
        }
        // Guardamos la operación del nodo actual
        HistorialOperacion op = top.data;
        // Movemos el tope al siguiente nodo
        top = top.next;
        // Devolvemos la operación que estaba en el tope
        return op;
    }

    // Indica si la pila está vacía
    public boolean isEmpty() {
        return top == null;
    }
}

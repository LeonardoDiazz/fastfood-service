package com.example.fastfood_service.datastructures;
import com.example.fastfood_service.model.Pedido;

public class PedidoQueue {


    private static class Node {
        Pedido data;
        Node next;

        Node(Pedido data) {
            this.data = data;
        }
    }

    // Puntero al primer elemento de la cola (el siguiente en salir)
    private Node front;

    // Puntero al último elemento de la cola (el último que entró)
    private Node rear;

    // Encolar al final: agrega un pedido al final de la cola (comportamiento FIFO normal)
    public void enqueue(Pedido pedido) {
        Node nuevo = new Node(pedido);
        if (rear == null) {
            // Si la cola está vacía, front y rear apuntan al mismo nodo
            front = rear = nuevo;
        } else {
            // La cola ya tiene elementos, se enlaza al final
            rear.next = nuevo;
            rear = nuevo;
        }
    }

    // Desencolar: saca y devuelve el pedido que está al frente de la cola
    public Pedido dequeue() {
        if (front == null) {
            // Cola vacía
            return null;
        }
        // Guardamos el pedido del nodo de adelante
        Pedido p = front.data;
        // Avanzamos el puntero front al siguiente nodo
        front = front.next;
        // Si después de avanzar ya no hay nodos, también actualizamos rear
        if (front == null) {
            rear = null;
        }
        return p;
    }

    // Indica si la cola está vacía (no hay elementos)
    public boolean isEmpty() {
        return front == null;
    }

    // Elimina un pedido de la cola buscando por id
    // Se usa al cancelar o al hacer rollback de una creación
    public void removeById(int id) {
        Node actual = front;
        Node anterior = null;

        while (actual != null) {
            if (actual.data.getId() == id) {
                // Caso: el nodo a eliminar es el primero
                if (anterior == null) {
                    front = actual.next;
                } else {
                    // Saltamos el nodo actual
                    anterior.next = actual.next;
                }
                // Si el nodo eliminado era el último, actualizamos rear
                if (actual == rear) {
                    rear = anterior;
                }
                return;
            }
            // Avanzamos en la lista
            anterior = actual;
            actual = actual.next;
        }
    }

    // Encolar al frente: agrega un pedido al inicio de la cola
    // Se usa en el rollback de DESPACHAR para que el pedido vuelva a estar primero en la fila
    public void enqueueFront(Pedido pedido) {
        Node nuevo = new Node(pedido);
        if (front == null) {
            // Cola vacía: front y rear apuntan al mismo nodo
            front = rear = nuevo;
        } else {
            // Insertamos el nuevo nodo antes del actual front
            nuevo.next = front;
            front = nuevo;
        }
    }
}

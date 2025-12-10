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

    private Node front;
    private Node rear;

    public void enqueue(Pedido pedido) {
        Node nuevo = new Node(pedido);
        if (rear == null) {
            front = rear = nuevo;
        } else {
            rear.next = nuevo;
            rear = nuevo;
        }
    }

    public Pedido dequeue() {
        if (front == null) {
            return null;
        }
        Pedido p = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        return p;
    }

    public boolean isEmpty() {
        return front == null;
    }

    // Eliminar un pedido de la cola por id (para cancelar / rollback)
    public void removeById(int id) {
        Node actual = front;
        Node anterior = null;

        while (actual != null) {
            if (actual.data.getId() == id) {
                if (anterior == null) {
                    front = actual.next;
                } else {
                    anterior.next = actual.next;
                }
                if (actual == rear) {
                    rear = anterior;
                }
                return;
            }
            anterior = actual;
            actual = actual.next;
        }
    }
}

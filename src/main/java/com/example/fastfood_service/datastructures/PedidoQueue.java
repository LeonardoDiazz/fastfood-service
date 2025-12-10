package com.example.fastfood_service.datastructures;
import com.example.fastfood_service.model.Pedido;

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

    // Encolar al final
    public void enqueue(Pedido pedido) {
        Node nuevo = new Node(pedido);
        if (rear == null) {
            front = rear = nuevo;
        } else {
            rear.next = nuevo;
            rear = nuevo;
        }
    }

    // Desencolar
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

    // 👈 ESTE método es el que debe existir
    public void enqueueFront(Pedido pedido) {
        Node nuevo = new Node(pedido);
        if (front == null) {
            front = rear = nuevo;
        } else {
            nuevo.next = front;
            front = nuevo;
        }
    }
}

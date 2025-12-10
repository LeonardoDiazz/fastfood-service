package com.example.fastfood_service.datastructures;
import com.example.fastfood_service.model.Pedido;

public class SinglyLinkedList {

    private static class Node {
        Pedido data;
        Node next;

        Node(Pedido data) {
            this.data = data;
        }
    }

    private Node head;
    private int size;

    // Agregar al final
    public void add(Pedido pedido) {
        Node nuevo = new Node(pedido);
        if (head == null) {
            head = nuevo;
        } else {
            Node actual = head;
            while (actual.next != null) {
                actual = actual.next;
            }
            actual.next = nuevo;
        }
        size++;
    }

    // Buscar por id
    public Pedido findById(int id) {
        Node actual = head;
        while (actual != null) {
            if (actual.data.getId() == id) {
                return actual.data;
            }
            actual = actual.next;
        }
        return null;
    }

    // Eliminar por id (para rollback de CREAR)
    public boolean removeById(int id) {
        Node actual = head;
        Node anterior = null;

        while (actual != null) {
            if (actual.data.getId() == id) {
                if (anterior == null) {
                    head = actual.next;
                } else {
                    anterior.next = actual.next;
                }
                size--;
                return true;
            }
            anterior = actual;
            actual = actual.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public Pedido[] toArray() {
        Pedido[] arr = new Pedido[size];
        Node actual = head;
        int i = 0;
        while (actual != null) {
            arr[i++] = actual.data;
            actual = actual.next;
        }
        return arr;
    }
}

package com.example.fastfood_service.datastructures;
import com.example.fastfood_service.model.Pedido;

public class SinglyLinkedList {

    // Nodo interno de la lista
    private static class Node {
        Pedido data;
        Node next;

        Node(Pedido data) {
            this.data = data;
        }
    }

    // Puntero al primer nodo de la lista
    private Node head;

    // Cantidad de elementos almacenados en la lista
    private int size;

    // Agregar un pedido al final de la lista
    public void add(Pedido pedido) {
        Node nuevo = new Node(pedido);
        if (head == null) {
            // Si la lista está vacía, el nuevo nodo se convierte en la cabeza
            head = nuevo;
        } else {
            // Si ya hay elementos, recorremos hasta el último nodo
            Node actual = head;
            while (actual.next != null) {
                actual = actual.next;
            }
            // Enlazamos el último nodo con el nuevo
            actual.next = nuevo;
        }
        // Incrementamos el tamaño de la lista
        size++;
    }

    // Buscar un pedido por su id
    public Pedido findById(int id) {
        Node actual = head;
        // Recorremos la lista desde la cabeza
        while (actual != null) {
            if (actual.data.getId() == id) {
                // Si encontramos el id, devolvemos el pedido
                return actual.data;
            }
            actual = actual.next;
        }
        // Si no se encontró, devolvemos null
        return null;
    }

    // Eliminar un pedido por id (se usa, por ejemplo, en el rollback de CREAR)
    public boolean removeById(int id) {
        Node actual = head;
        Node anterior = null;

        while (actual != null) {
            if (actual.data.getId() == id) {
                // Caso: el nodo a eliminar es la cabeza
                if (anterior == null) {
                    head = actual.next;
                } else {
                    // Caso: el nodo está en medio o al final
                    anterior.next = actual.next;
                }
                // Disminuimos el tamaño de la lista
                size--;
                return true;
            }
            // Avanzamos en la lista
            anterior = actual;
            actual = actual.next;
        }
        // No se encontró el id
        return false;
    }

    // Devuelve el número de elementos en la lista
    public int size() {
        return size;
    }

    // Convierte la lista en un arreglo de pedidos
    public Pedido[] toArray() {
        Pedido[] arr = new Pedido[size];
        Node actual = head;
        int i = 0;
        // Recorremos la lista copiando cada pedido al arreglo
        while (actual != null) {
            arr[i++] = actual.data;
            actual = actual.next;
        }
        return arr;
    }
}

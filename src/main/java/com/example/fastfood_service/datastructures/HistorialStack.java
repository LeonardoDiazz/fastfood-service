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

    private Node top;

    public void push(HistorialOperacion op) {
        Node nuevo = new Node(op);
        nuevo.next = top;
        top = nuevo;
    }

    public HistorialOperacion pop() {
        if (top == null) {
            return null;
        }
        HistorialOperacion op = top.data;
        top = top.next;
        return op;
    }

    public boolean isEmpty() {
        return top == null;
    }
}

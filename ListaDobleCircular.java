package org.example.practica4;

import java.util.Random;

public class ListaDobleCircular<T> {
    private Nodo<T> cabeza;
    private int tamano;

    public ListaDobleCircular() {
        this.cabeza = null;
        this.tamano = 0;
    }

    public void insertar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
            cabeza.setSiguiente(cabeza);
            cabeza.setAnterior(cabeza);
        } else {
            Nodo<T> ultimo = cabeza.getAnterior();
            nuevo.setSiguiente(cabeza);
            nuevo.setAnterior(ultimo);
            cabeza.setAnterior(nuevo);
            ultimo.setSiguiente(nuevo);
        }
        tamano++;
    }

    public T extraerInicio() {
        if (estaVacia()) return null;
        T dato = cabeza.getDato();
        if (tamano == 1) {
            cabeza = null;
        } else {
            Nodo<T> ultimo = cabeza.getAnterior();
            cabeza = cabeza.getSiguiente();
            cabeza.setAnterior(ultimo);
            ultimo.setSiguiente(cabeza);
        }
        tamano--;
        return dato;
    }

    public void rotarSiguiente() {
        if (cabeza != null) {
            cabeza = cabeza.getSiguiente();
        }
    }

    public void mezclar() {
        if (tamano <= 1) return;
        Object[] arreglo = new Object[tamano];
        Nodo<T> temp = cabeza;
        for (int i = 0; i < tamano; i++) {
            arreglo[i] = temp.getDato();
            temp = temp.getSiguiente();
        }

        Random rand = new Random();
        for (int i = tamano - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Object aux = arreglo[i];
            arreglo[i] = arreglo[j];
            arreglo[j] = aux;
        }

        temp = cabeza;
        for (int i = 0; i < tamano; i++) {
            T dato = (T) arreglo[i];
            temp.setDato(dato);
            temp = temp.getSiguiente();
        }
    }

    public int getTamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }
}
package org.example.practica4;

public class ListaSimple<T> {
    private Nodo<T> cabeza;
    private int tamano;

    public ListaSimple() {
        this.cabeza = null;
        this.tamano = 0;
    }

    public void insertarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
        tamano++;
    }

    public void insertarFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            cabeza = nuevo;
        } else {
            Nodo<T> temp = cabeza;
            while (temp.getSiguiente() != null) {
                temp = temp.getSiguiente();
            }
            temp.setSiguiente(nuevo);
        }
        tamano++;
    }

    public T eliminarFinal() {
        if (estaVacia()) return null;
        if (cabeza.getSiguiente() == null) {
            T dato = cabeza.getDato();
            cabeza = null;
            tamano--;
            return dato;
        }
        Nodo<T> temp = cabeza;
        while (temp.getSiguiente().getSiguiente() != null) {
            temp = temp.getSiguiente();
        }
        T dato = temp.getSiguiente().getDato();
        temp.setSiguiente(null);
        tamano--;
        return dato;
    }

    public T eliminarInicio() {
        if (estaVacia()) return null;
        T dato = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        tamano--;
        return dato;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano) return null;
        Nodo<T> temp = cabeza;
        for (int i = 0; i < indice; i++) {
            temp = temp.getSiguiente();
        }
        return temp.getDato();
    }

    public T obtenerFinal() {
        if (estaVacia()) return null;
        return obtener(tamano - 1);
    }

    public int getTamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public void vaciar() {
        cabeza = null;
        tamano = 0;
    }
}
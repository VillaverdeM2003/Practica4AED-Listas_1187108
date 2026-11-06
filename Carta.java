package org.example.practica4;

public class Carta implements Comparable<Carta> {
    private final Palo palo;
    private final int valor;
    private boolean bocaArriba;

    public Carta(Palo palo, int valor) {
        this.palo = palo;
        this.valor = valor;
        this.bocaArriba = true;
    }

    public Palo getPalo() {
        return palo;
    }

    public int getValor() {
        return valor;
    }

    public boolean isBocaArriba() {
        return bocaArriba;
    }

    public void setBocaArriba(boolean bocaArriba) {
        this.bocaArriba = bocaArriba;
    }

    @Override
    public int compareTo(Carta otra) {
        if (otra == null) return 1;
        if (this.valor != otra.valor) {
            return Integer.compare(this.valor, otra.valor);
        }
        return this.palo.compareTo(otra.palo);
    }

    @Override
    public String toString() {
        String valStr;
        switch (valor) {
            case 1: valStr = "A"; break;
            case 11: valStr = "J"; break;
            case 12: valStr = "Q"; break;
            case 13: valStr = "K"; break;
            default: valStr = String.valueOf(valor); break;
        }
        return valStr + palo.getSimbolo();
    }
}
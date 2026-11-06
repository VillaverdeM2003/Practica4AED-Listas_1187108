package org.example.practica4;

public class Movimiento {
    public enum TipoOrigenDestino {
        COLUMNA, RESERVA, CIMENTACION
    }

    private final TipoOrigenDestino origenTipo;
    private final int origenIndice;
    private final TipoOrigenDestino destinoTipo;
    private final int destinoIndice;
    private final Carta carta;
    private final int cantidad;

    public Movimiento(TipoOrigenDestino origenTipo, int origenIndice,
                      TipoOrigenDestino destinoTipo, int destinoIndice, Carta carta, int cantidad) {
        this.origenTipo = origenTipo;
        this.origenIndice = origenIndice;
        this.destinoTipo = destinoTipo;
        this.destinoIndice = destinoIndice;
        this.carta = carta;
        this.cantidad = cantidad;
    }

    public Movimiento(TipoOrigenDestino origenTipo, int origenIndice,
                      TipoOrigenDestino destinoTipo, int destinoIndice, Carta carta) {
        this(origenTipo, origenIndice, destinoTipo, destinoIndice, carta, 1);
    }

    public TipoOrigenDestino getOrigenTipo() {
        return origenTipo;
    }
    public int getOrigenIndice() {
        return origenIndice;
    }
    public TipoOrigenDestino getDestinoTipo() {
        return destinoTipo;
    }
    public int getDestinoIndice() {
        return destinoIndice;
    }
    public Carta getCarta() {
        return carta;
    }
    public int getCantidad() {
        return cantidad; }


    public String toString() {
        return "Mover " + (cantidad > 1 ? cantidad + " cartas (" + carta + ") de " : carta + " de ")
                + origenTipo + "[" + (origenIndice + 1) + "] a " + destinoTipo + "[" + (destinoIndice + 1) + "]";
    }
}
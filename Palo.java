package org.example.practica4;

public enum Palo {

    TREBOL("Tréboles", "♣", "clubs"),
    DIAMANTE("Diamantes", "♦", "diamonds"),
    CORAZON("Corazones", "♥", "hearts"),
    PICA("Picas", "♠", "spades");

    private final String nombre;
    private final String simbolo;
    private final String nombreFilename;

    Palo(String nombre, String simbolo, String nombreFilename) {
        this.nombre = nombre;
        this.simbolo = simbolo;
        this.nombreFilename = nombreFilename;
    }

    public String getNombre() {
        return nombre;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public String getNombreFilename() {
        return nombreFilename;
    }
}
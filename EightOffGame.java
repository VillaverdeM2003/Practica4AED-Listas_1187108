package org.example.practica4;

public class EightOffGame {
    private ListaSimple<Carta>[] columnas;
    private ListaSimple<Carta>[] reserva;
    private ListaSimple<Carta>[] cimentaciones;
    private ListaDobleCircular<Carta> baraja;
    private ListaSimple<Movimiento> historial;

    public EightOffGame() {
        columnas = new ListaSimple[8];
        for (int i = 0; i < 8; i++) columnas[i] = new ListaSimple<>();

        reserva = new ListaSimple[8];
        for (int i = 0; i < 8; i++) reserva[i] = new ListaSimple<>();

        cimentaciones = new ListaSimple[4];
        for (int i = 0; i < 4; i++) cimentaciones[i] = new ListaSimple<>();

        baraja = new ListaDobleCircular<>();
        historial = new ListaSimple<>();
    }

    public int contarReservasLibres() {
        int libres = 0;
        for (int i = 0; i < 8; i++) {
            if (reserva[i].estaVacia()) libres++;
        }
        return libres;
    }

    public boolean esSecuenciaValida(int colIdx, int startIdx) {
        ListaSimple<Carta> col = columnas[colIdx];
        if (startIdx < 0 || startIdx >= col.getTamano()) return false;

        for (int i = startIdx; i < col.getTamano() - 1; i++) {
            Carta actual = col.obtener(i);
            Carta siguiente = col.obtener(i + 1);
            if (actual.getPalo() != siguiente.getPalo() || actual.getValor() != siguiente.getValor() + 1) {
                return false;
            }
        }
        return true;
    }

    public boolean moverSecuencia(int origCol, int startIdx, Movimiento.TipoOrigenDestino destTipo, int destIdx) {
        if (!esSecuenciaValida(origCol, startIdx)) return false;

        ListaSimple<Carta> colOrig = columnas[origCol];
        int cantidad = colOrig.getTamano() - startIdx;
        int maxPermitido = 1 + contarReservasLibres();

        if (cantidad > maxPermitido) return false;

        Carta cartaBase = colOrig.obtener(startIdx);
        boolean valido = false;

        if (destTipo == Movimiento.TipoOrigenDestino.COLUMNA) {
            if (origCol == destIdx) return false;
            ListaSimple<Carta> colDest = columnas[destIdx];
            if (colDest.estaVacia()) {
                valido = (cartaBase.getValor() == 13);
            } else {
                Carta topeDest = colDest.obtenerFinal();
                valido = (topeDest.getPalo() == cartaBase.getPalo() && cartaBase.getValor() == topeDest.getValor() - 1);
            }
        } else if (destTipo == Movimiento.TipoOrigenDestino.RESERVA) {
            if (cantidad == 1) {
                valido = reserva[destIdx].estaVacia();
            }
        } else if (destTipo == Movimiento.TipoOrigenDestino.CIMENTACION) {
            if (cantidad == 1) {
                valido = esValidoCimentacion(cartaBase, destIdx);
            }
        }

        if (valido) {
            ListaSimple<Carta> temp = new ListaSimple<>();
            for (int i = 0; i < cantidad; i++) {
                temp.insertarInicio(colOrig.eliminarFinal());
            }

            for (int i = 0; i < cantidad; i++) {
                Carta c = temp.eliminarInicio();
                if (destTipo == Movimiento.TipoOrigenDestino.COLUMNA) {
                    columnas[destIdx].insertarFinal(c);
                } else if (destTipo == Movimiento.TipoOrigenDestino.RESERVA) {
                    reserva[destIdx].insertarFinal(c);
                } else if (destTipo == Movimiento.TipoOrigenDestino.CIMENTACION) {
                    cimentaciones[destIdx].insertarFinal(c);
                }
            }

            historial.insertarFinal(new Movimiento(Movimiento.TipoOrigenDestino.COLUMNA, origCol, destTipo, destIdx, cartaBase, cantidad));
            return true;
        }

        return false;
    }

    public void iniciarJuego() {
        for (int i = 0; i < 8; i++) columnas[i].vaciar();
        for (int i = 0; i < 8; i++) reserva[i].vaciar();
        for (int i = 0; i < 4; i++) cimentaciones[i].vaciar();
        historial.vaciar();
        baraja = new ListaDobleCircular<>();

        for (Palo palo : Palo.values()) {
            for (int val = 1; val <= 13; val++) {
                baraja.insertar(new Carta(palo, val));
            }
        }

        baraja.mezclar();

        for (int col = 0; col < 8; col++) {
            for (int c = 0; c < 6; c++) {
                Carta carta = baraja.extraerInicio();
                columnas[col].insertarFinal(carta);
            }
        }

        for (int res = 0; res < 4; res++) {
            Carta carta = baraja.extraerInicio();
            reserva[res].insertarFinal(carta);
        }
    }

    public boolean moverCarta(Movimiento.TipoOrigenDestino origTipo, int origIdx,
                              Movimiento.TipoOrigenDestino destTipo, int destIdx) {
        if (origTipo == Movimiento.TipoOrigenDestino.COLUMNA) {
            int startIdx = columnas[origIdx].getTamano() - 1;
            return moverSecuencia(origIdx, startIdx, destTipo, destIdx);
        }

        Carta cartaAMover = obtenerCartaOrigen(origTipo, origIdx);
        if (cartaAMover == null) return false;

        boolean valido = false;
        if (destTipo == Movimiento.TipoOrigenDestino.CIMENTACION) {
            valido = esValidoCimentacion(cartaAMover, destIdx);
        } else if (destTipo == Movimiento.TipoOrigenDestino.COLUMNA) {
            valido = esValidoColumna(cartaAMover, destIdx);
        } else if (destTipo == Movimiento.TipoOrigenDestino.RESERVA) {
            valido = esValidoReserva(destIdx);
        }

        if (valido) {
            extraerCartaOrigen(origTipo, origIdx);
            insertarCartaDestino(destTipo, destIdx, cartaAMover);
            historial.insertarFinal(new Movimiento(origTipo, origIdx, destTipo, destIdx, cartaAMover, 1));
            return true;
        }

        return false;
    }

    private Carta obtenerCartaOrigen(Movimiento.TipoOrigenDestino origTipo, int origIdx) {
        if (origTipo == Movimiento.TipoOrigenDestino.COLUMNA) {
            return columnas[origIdx].obtenerFinal();
        } else if (origTipo == Movimiento.TipoOrigenDestino.RESERVA) {
            return reserva[origIdx].obtenerFinal();
        }
        return null;
    }

    private void extraerCartaOrigen(Movimiento.TipoOrigenDestino origTipo, int origIdx) {
        if (origTipo == Movimiento.TipoOrigenDestino.COLUMNA) {
            columnas[origIdx].eliminarFinal();
        } else if (origTipo == Movimiento.TipoOrigenDestino.RESERVA) {
            reserva[origIdx].eliminarFinal();
        }
    }

    private void insertarCartaDestino(Movimiento.TipoOrigenDestino destTipo, int destIdx, Carta carta) {
        if (destTipo == Movimiento.TipoOrigenDestino.COLUMNA) {
            columnas[destIdx].insertarFinal(carta);
        } else if (destTipo == Movimiento.TipoOrigenDestino.RESERVA) {
            reserva[destIdx].insertarFinal(carta);
        } else if (destTipo == Movimiento.TipoOrigenDestino.CIMENTACION) {
            cimentaciones[destIdx].insertarFinal(carta);
        }
    }

    public boolean esValidoCimentacion(Carta carta, int destIdx) {
        ListaSimple<Carta> cim = cimentaciones[destIdx];
        if (cim.estaVacia()) {
            return carta.getValor() == 1;
        }
        Carta tope = cim.obtenerFinal();
        return tope.getPalo() == carta.getPalo() && carta.getValor() == tope.getValor() + 1;
    }

    public boolean esValidoColumna(Carta carta, int destIdx) {
        ListaSimple<Carta> col = columnas[destIdx];
        if (col.estaVacia()) {
            return carta.getValor() == 13;
        }
        Carta tope = col.obtenerFinal();
        return tope.getPalo() == carta.getPalo() && carta.getValor() == tope.getValor() - 1;
    }

    public boolean esValidoReserva(int destIdx) {
        return reserva[destIdx].estaVacia();
    }

    public Movimiento obtenerPista() {
        Movimiento ultimoMov = historial.estaVacia() ? null : historial.obtenerFinal();


        for (int c = 0; c < 8; c++) {
            Carta carta = columnas[c].obtenerFinal();
            if (carta != null) {
                for (int f = 0; f < 4; f++) {
                    if (esValidoCimentacion(carta, f)) {
                        return new Movimiento(Movimiento.TipoOrigenDestino.COLUMNA, c, Movimiento.TipoOrigenDestino.CIMENTACION, f, carta);
                    }
                }
            }
        }
        for (int r = 0; r < 8; r++) {
            Carta carta = reserva[r].obtenerFinal();
            if (carta != null) {
                for (int f = 0; f < 4; f++) {
                    if (esValidoCimentacion(carta, f)) {
                        return new Movimiento(Movimiento.TipoOrigenDestino.RESERVA, r, Movimiento.TipoOrigenDestino.CIMENTACION, f, carta);
                    }
                }
            }
        }


        for (int c = 0; c < 8; c++) {
            ListaSimple<Carta> col = columnas[c];
            if (col.estaVacia()) continue;

            for (int startIdx = 0; startIdx < col.getTamano(); startIdx++) {
                if (esSecuenciaValida(c, startIdx)) {
                    Carta cartaBase = col.obtener(startIdx);
                    int cantidad = col.getTamano() - startIdx;
                    int maxPermitido = 1 + contarReservasLibres();

                    if (cantidad <= maxPermitido) {
                        for (int destCol = 0; destCol < 8; destCol++) {
                            if (c == destCol) continue;


                            if (columnas[destCol].estaVacia() && startIdx == 0 && cartaBase.getValor() == 13) {
                                continue;
                            }

                            if (esValidoColumna(cartaBase, destCol)) {
                                if (!esMovimientoInverso(ultimoMov, Movimiento.TipoOrigenDestino.COLUMNA, c, Movimiento.TipoOrigenDestino.COLUMNA, destCol)) {
                                    return new Movimiento(Movimiento.TipoOrigenDestino.COLUMNA, c, Movimiento.TipoOrigenDestino.COLUMNA, destCol, cartaBase, cantidad);
                                }
                            }
                        }
                    }
                }
            }
        }


        for (int r = 0; r < 8; r++) {
            Carta carta = reserva[r].obtenerFinal();
            if (carta != null) {
                for (int destCol = 0; destCol < 8; destCol++) {
                    if (esValidoColumna(carta, destCol)) {
                        if (!esMovimientoInverso(ultimoMov, Movimiento.TipoOrigenDestino.RESERVA, r, Movimiento.TipoOrigenDestino.COLUMNA, destCol)) {
                            return new Movimiento(Movimiento.TipoOrigenDestino.RESERVA, r, Movimiento.TipoOrigenDestino.COLUMNA, destCol, carta);
                        }
                    }
                }
            }
        }


        for (int c = 0; c < 8; c++) {
            ListaSimple<Carta> col = columnas[c];

            if (col.estaVacia() || (col.getTamano() == 1 && col.obtenerFinal().getValor() == 13)) {
                continue;
            }

            Carta carta = col.obtenerFinal();
            for (int r = 0; r < 8; r++) {
                if (esValidoReserva(r)) {
                    if (!esMovimientoInverso(ultimoMov, Movimiento.TipoOrigenDestino.COLUMNA, c, Movimiento.TipoOrigenDestino.RESERVA, r)) {
                        return new Movimiento(Movimiento.TipoOrigenDestino.COLUMNA, c, Movimiento.TipoOrigenDestino.RESERVA, r, carta);
                    }
                }
            }
        }

        return null;
    }

    private boolean esMovimientoInverso(Movimiento ultimo, Movimiento.TipoOrigenDestino origT, int origI, Movimiento.TipoOrigenDestino destT, int destI) {
        if (ultimo == null) return false;
        return ultimo.getOrigenTipo() == destT && ultimo.getOrigenIndice() == destI &&
                ultimo.getDestinoTipo() == origT && ultimo.getDestinoIndice() == origI;
    }

    public boolean esGameOver() {
        if (juegoGanado()) return false;
        return obtenerPista() == null;
    }

    public boolean juegoGanado() {
        int totalCimentadas = 0;
        for (int f = 0; f < 4; f++) {
            totalCimentadas += cimentaciones[f].getTamano();
        }
        return totalCimentadas == 52;
    }

    public boolean deshacerMovimiento() {
        if (historial.estaVacia()) return false;
        Movimiento ult = historial.eliminarFinal();
        int cantidad = ult.getCantidad();

        ListaSimple<Carta> temp = new ListaSimple<>();

        for (int i = 0; i < cantidad; i++) {
            Carta c = null;
            if (ult.getDestinoTipo() == Movimiento.TipoOrigenDestino.COLUMNA) {
                c = columnas[ult.getDestinoIndice()].eliminarFinal();
            } else if (ult.getDestinoTipo() == Movimiento.TipoOrigenDestino.RESERVA) {
                c = reserva[ult.getDestinoIndice()].eliminarFinal();
            } else if (ult.getDestinoTipo() == Movimiento.TipoOrigenDestino.CIMENTACION) {
                c = cimentaciones[ult.getDestinoIndice()].eliminarFinal();
            }
            temp.insertarInicio(c);
        }

        for (int i = 0; i < cantidad; i++) {
            Carta c = temp.eliminarInicio();
            if (ult.getOrigenTipo() == Movimiento.TipoOrigenDestino.COLUMNA) {
                columnas[ult.getOrigenIndice()].insertarFinal(c);
            } else if (ult.getOrigenTipo() == Movimiento.TipoOrigenDestino.RESERVA) {
                reserva[ult.getOrigenIndice()].insertarFinal(c);
            }
        }

        return true;
    }

    public ListaSimple<Carta>[] getColumnas() {
        return columnas;
    }
    public ListaSimple<Carta>[] getReserva() {
        return reserva;
    }
    public ListaSimple<Carta>[] getCimentaciones() {
        return cimentaciones;
    }
}
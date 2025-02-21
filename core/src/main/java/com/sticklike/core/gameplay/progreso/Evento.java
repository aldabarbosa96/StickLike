package com.sticklike.core.gameplay.progreso;

/**
 * Clase base de los eventos del juego. Aplica los eventos según el nivel requerido por el jugador
 */
public class Evento implements Comparable<Evento> {
    private Runnable efectoEvento;
    private String nombreEvento;
    private int nivelRequerido;

    public Evento(String nombreEvento, Runnable efectoEvento, int nivelRequerido) {
        this.nombreEvento = nombreEvento;
        this.efectoEvento = efectoEvento;
        this.nivelRequerido = nivelRequerido;
    }

    public void applyEvento() {
        efectoEvento.run();
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public int getNivelRequerido() {
        return nivelRequerido;
    }

    @Override
    public int compareTo(Evento otro) {
        // Ordenar de menor a mayor nivelRequerido
        return Integer.compare(this.nivelRequerido, otro.nivelRequerido);
    }
}

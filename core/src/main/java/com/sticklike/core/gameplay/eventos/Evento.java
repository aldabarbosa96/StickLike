package com.sticklike.core.gameplay.eventos;

import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;

public class Evento implements Comparable<Evento> {
    private Runnable efectoEvento;
    private String nombreEvento;
    private int nivelRequerido;
    private SistemaDeNiveles sistemaDeNiveles;

    public Evento(String nombreEvento, SistemaDeNiveles sistemaDeNiveles, Runnable efectoEvento, int nivelRequerido) {
        this.nombreEvento = nombreEvento;
        this.efectoEvento = efectoEvento;
        this.nivelRequerido = nivelRequerido;
        this.sistemaDeNiveles = sistemaDeNiveles;
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

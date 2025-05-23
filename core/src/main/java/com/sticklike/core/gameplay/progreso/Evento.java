package com.sticklike.core.gameplay.progreso;

/**
 * Clase base de los eventos del juego. Aplica los eventos según el nivel requerido por el jugador
 */
public class Evento implements Comparable<Evento> {
    private float id;
    private Runnable efectoEvento;
    private String nombreEvento;
    private int nivelRequerido;

    public Evento(String nombreEvento, Runnable efectoEvento, int nivelRequerido, float id) {
        this.nombreEvento = nombreEvento;
        this.efectoEvento = efectoEvento;
        this.nivelRequerido = nivelRequerido;
        this.id = id;
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
        int cmp = Integer.compare(this.nivelRequerido, otro.nivelRequerido);
        if (cmp == 0) {
            // Si ambos eventos tienen el mismo nivel, hacemos un desempate
            if (this.id == 4 && otro.id == 5) {
                return -1; // El boss tiene prioridad
            } else if (this.id == 5 && otro.id == 4) {
                return 1;  // El boss tiene prioridad, por lo tanto el evento alarma queda después
            }
            // Si no es uno de estos casos, se ordenan lexicográficamente
            return this.nombreEvento.compareTo(otro.nombreEvento);
        }
        return cmp;
    }

    public float getId() {
        return id;
    }
}

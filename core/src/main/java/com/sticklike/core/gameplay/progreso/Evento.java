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
        int cmp = Integer.compare(this.nivelRequerido, otro.nivelRequerido);
        if (cmp == 0) {
            // Si ambos eventos tienen el mismo nivel, hacemos un desempate
            if (this.nombreEvento.equals("BOSSPOLLA Aparece") && otro.nombreEvento.equals("Alarma Aparece")) {
                return -1; // El boss tiene prioridad
            } else if (this.nombreEvento.equals("Alarma Aparece") && otro.nombreEvento.equals("BOSSPOLLA Aparece")) {
                return 1;  // El boss tiene prioridad, por lo tanto el evento alarma queda después
            }
            // Si no es uno de estos casos, se ordenan lexicográficamente
            return this.nombreEvento.compareTo(otro.nombreEvento);
        }
        return cmp;
    }
}

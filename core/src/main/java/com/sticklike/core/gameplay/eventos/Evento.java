package com.sticklike.core.gameplay.eventos;

public class Evento implements Comparable<Evento> {
    private Runnable efectoEvento;
    private String nombreEvento;
    private float tiempoActivacion;

    public Evento(String nombreEvento, float tiempoActivacion, Runnable efectoEvento) {
        this.nombreEvento = nombreEvento;
        this.efectoEvento = efectoEvento;
        this.tiempoActivacion = tiempoActivacion;
    }

    public void applyEvento() {
        efectoEvento.run();
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public float getTiempoActivacion() {
        return tiempoActivacion;
    }

    @Override
    public int compareTo(Evento otroEvento) {
        return Float.compare(this.tiempoActivacion, otroEvento.tiempoActivacion);
    }
}

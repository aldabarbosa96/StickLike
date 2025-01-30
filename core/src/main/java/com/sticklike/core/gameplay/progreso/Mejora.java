package com.sticklike.core.gameplay.progreso;

/**
 * Clase base de las mejoras. Se definen y se aplica su efecto
 */
public class Mejora {
    private final String nombreMejora;
    private final String descripcionMejora;
    private final Runnable efectoMejora;
    private int usosRestantes;

    public Mejora(String nombreMejora, String descripcionMejora, Runnable efectoMejora, int usosMaximos) {
        this.nombreMejora = nombreMejora;
        this.descripcionMejora = descripcionMejora;
        this.efectoMejora = efectoMejora;
        this.usosRestantes = usosMaximos;
    }

    public String getNombreMejora() {
        return nombreMejora;
    }

    public String getDescripcionMejora() {
        return descripcionMejora;
    }

    public void apply() { // aplica el efecto de la mejora ejecutando el objeto Runnable
        if (usosRestantes > 0) {
            efectoMejora.run();
            usosRestantes--;
        } else {
            throw new IllegalStateException("Esta mejora ya no está disponible.");
        }
    }
    public boolean estaDisponible() {
        return usosRestantes > 0;
    }
}

package com.sticklike.core.gameplay.mejoras;

/**
 * Clase base de las mejoras. Se definen y se aplica su efecto
 */
public class Mejora {
    private final String nombreMejora;
    private final String descripcionMejora;
    private final Runnable efectoMejora;
    private int usosRestantes;


    /**
     * Inicializa un nueva mejora
     * @param nombreMejora Nombre que queramos darle a la mejora
     * @param descripcionMejora Descripcion que queramos darle a la mejora
     * @param efectoMejora Efecto que provoca la mejora
     */
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

    /**
     * Aplica el efecto de mejora ejecutando el objeto Runnable
     */
    public void apply() {
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

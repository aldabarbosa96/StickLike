package com.sticklike.core.gameplay.mejoras;

/**
 * Clase base de las mejoras. Se definen y se aplica su efecto
 */
public class Mejora {
    private final String nombreMejora;
    private final String descripcionMejora;
    private final Runnable efectoMejora;

    /**
     * Inicializa un nueva mejora
     * @param nombreMejora Nombre que queramos darle a la mejora
     * @param descripcionMejora Descripcion que queramos darle a la mejora
     * @param efectoMejora Efecto que provoca la mejora
     */
    public Mejora(String nombreMejora, String descripcionMejora, Runnable efectoMejora) {
        this.nombreMejora = nombreMejora;
        this.descripcionMejora = descripcionMejora;
        this.efectoMejora = efectoMejora;
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
    public void apply(){
        efectoMejora.run();
    }
}

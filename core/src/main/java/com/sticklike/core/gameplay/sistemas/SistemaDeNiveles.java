package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.entidades.jugador.Jugador;

/**
 * Esta clase gestiona la experiencia y los niveles del {@link Jugador}.
 * Cada vez que el jugador acumula la suficiente XP para subir de nivel, se llama al {@link SistemaDeMejoras}
 * para ofrecer nuevas mejoras
 */
public class SistemaDeNiveles {
    private final Jugador jugador;
    private final SistemaDeMejoras sistemaDeMejoras;
    private float xpActual = 0f;
    private float xpHastaSiguienteNivel = 100f;
    private int nivelActual = 1;

    /**
     * Crea un nuevo SistemaDeNiveles, asociando un {@link Jugador} y un {@link SistemaDeMejoras}
     * para manejar las subidas de nivel y las mejoras a ofrecer
     *
     * @param jugador            jugador cuyo nivel y experiencia se gestionan
     * @param sistemaDeMejoras controlador de mejoras, para mostrar las mejoras al subir de nivel
     */
    public SistemaDeNiveles(Jugador jugador, SistemaDeMejoras sistemaDeMejoras) {
        this.jugador = jugador;
        this.sistemaDeMejoras = sistemaDeMejoras;
    }

    /**
     * Añade la cantidad de experiencia especificada al jugador
     * Si la XP acumulada supera la requerida para el siguiente nivel, llama a subirDeNivel()
     *
     * @param amount cantidad de experiencia a sumar
     */
    public void agregarXP(float amount) {
        xpActual += amount;
        if (xpActual >= xpHastaSiguienteNivel) {
            subirDeNivel();
        }
    }

    /**
     * Lógica de subida de nivel. Incrementa el nivel en 1, aumenta la XP requerida para el siguiente nivel en un 50%
     * y delega en {@link SistemaDeMejoras} para mostrar nuevas mejoras
     */
    private void subirDeNivel() {
        xpActual -= xpHastaSiguienteNivel;
        nivelActual++;
        xpHastaSiguienteNivel *= 1.55f;

        // Delegamos en ControladorMejoras para manejar las mejoras
        if (sistemaDeMejoras != null) {
            sistemaDeMejoras.anyadirMejorasAlPopUp();
        }
    }

    public int getNivelActual() {
        return nivelActual;
    }

    public float getXpActual() {
        return xpActual;
    }

    public float getXpHastaSiguienteNivel() {
        return xpHastaSiguienteNivel;
    }
}

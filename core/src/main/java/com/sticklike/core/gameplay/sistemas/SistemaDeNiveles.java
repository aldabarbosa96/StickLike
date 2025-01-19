package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.managers.ControladorMejoras;

/**
 * Esta clase gestiona la experiencia y los niveles del {@link Jugador}.
 * Cada vez que el jugador acumula la suficiente XP para subir de nivel, se llama al {@link ControladorMejoras}
 * para ofrecer nuevas mejoras
 */
public class SistemaDeNiveles {
    private final Jugador jugador;
    private final ControladorMejoras controladorMejoras;
    private float xpActual = 0f;
    private float xpHastaSiguienteNivel = 100f;
    private int nivelActual = 1;

    /**
     * Crea un nuevo SistemaDeNiveles, asociando un {@link Jugador} y un {@link ControladorMejoras}
     * para manejar las subidas de nivel y las mejoras a ofrecer
     *
     * @param jugador            jugador cuyo nivel y experiencia se gestionan
     * @param controladorMejoras controlador de mejoras, para mostrar las mejoras al subir de nivel
     */
    public SistemaDeNiveles(Jugador jugador, ControladorMejoras controladorMejoras) {
        this.jugador = jugador;
        this.controladorMejoras = controladorMejoras;
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
     * y delega en {@link ControladorMejoras} para mostrar nuevas mejoras
     */
    private void subirDeNivel() {
        xpActual -= xpHastaSiguienteNivel;
        nivelActual++;
        xpHastaSiguienteNivel *= 1.55f;

        // Delegamos en ControladorMejoras para manejar las mejoras
        if (controladorMejoras != null) {
            controladorMejoras.anyadirMejorasAlPopUp();
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

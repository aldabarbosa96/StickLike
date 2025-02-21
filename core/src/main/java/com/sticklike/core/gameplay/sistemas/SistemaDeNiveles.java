package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.entidades.jugador.Jugador;

/**
 * Gestiona la experiencia y el nivel del jugador.
 * Al subir de nivel, aumenta la vida y activa el sistema de mejoras.
 */

public class SistemaDeNiveles {
    private final Jugador jugador;
    private final SistemaDeMejoras sistemaDeMejoras;
    private float xpActual = 0f;
    private float xpHastaSiguienteNivel = 100f;
    private int nivelActual = 1;

    public SistemaDeNiveles(Jugador jugador, SistemaDeMejoras sistemaDeMejoras) {
        this.jugador = jugador;
        this.sistemaDeMejoras = sistemaDeMejoras;
    }

    public void agregarXP(float amount) {
        xpActual += amount;
        while (xpActual >= xpHastaSiguienteNivel) {
            subirDeNivel();
        }
    }

    private void subirDeNivel() {
        xpActual -= xpHastaSiguienteNivel;
        nivelActual++;
        xpHastaSiguienteNivel *= 1.5f;
        jugador.setVidaMax(jugador.getMaxVidaJugador() + 1.5f);
        if (jugador.getVidaJugador() < jugador.getMaxVidaJugador()) {
            jugador.setVidaJugador(jugador.getVidaJugador() + 2);
        } else if (jugador.getVidaJugador() > jugador.getMaxVidaJugador()) {
            jugador.setVidaJugador(jugador.getMaxVidaJugador());

        }
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

    public SistemaDeMejoras getSistemaDeMejoras() {
        return sistemaDeMejoras;
    }
}

package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.entidades.jugador.Jugador;

/**
 * Esta clase gestiona la experiencia y los niveles del {@link Jugador}
 * Cada vez que el jugador acumula la suficiente XP para subir de nivel, se llama al {@link SistemaDeMejoras} para ofrecer nuevas mejoras
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

    public void agregarXP(float amount ) {
        xpActual += amount;
        if (xpActual >= xpHastaSiguienteNivel) {
            subirDeNivel();

        }
    }

    private void subirDeNivel() {
        xpActual -= xpHastaSiguienteNivel;
        nivelActual++;
        xpHastaSiguienteNivel *= 1.5f;
        //jugador.setVidaMax(jugador.getMaxVidaJugador() + 5); todo --> valorar si se le incrementa también la vida máxima
        if (!(jugador.getVidaJugador() >= jugador.getMaxVidaJugador())) {
            jugador.setVidaJugador(jugador.getVidaJugador() + 2);
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

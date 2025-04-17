package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.pantallas.popUps.PopUpMejoras;

/**
 * Gestiona la experiencia y el nivel del jugador.
 * Al subir de nivel, aumenta la vida y activa el sistema de mejoras.
 */

public class SistemaDeNiveles {
    private final Jugador jugador;
    private final SistemaDeMejoras sistemaDeMejoras;
    private final PopUpMejoras popUpMejoras;
    private float xpActual = 0f;
    private float xpHastaSiguienteNivel = 100f;
    private int nivelActual = 1;
    private int nivelesPendientes = 0;

    public SistemaDeNiveles(Jugador jugador, SistemaDeMejoras sistemaDeMejoras, PopUpMejoras popUpMejoras) {
        this.jugador = jugador;
        this.sistemaDeMejoras = sistemaDeMejoras;
        this.popUpMejoras = popUpMejoras;
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

        xpHastaSiguienteNivel = 50 * nivelActual * nivelActual + 50;

        // Aumento de vida (puedes mantener tu lógica actual o ajustarla)
        jugador.setVidaMax(jugador.getMaxVidaJugador() + 1.5f);
        if (jugador.getVidaJugador() < jugador.getMaxVidaJugador()) {
            jugador.setVidaJugador(jugador.getVidaJugador() + 2);
        } else if (jugador.getVidaJugador() > jugador.getMaxVidaJugador()) {
            jugador.setVidaJugador(jugador.getMaxVidaJugador());
        }

        nivelesPendientes++;

        if (!popUpMejoras.isPopUpAbierto()) {
            sistemaDeMejoras.anyadirMejorasAlPopUp();
        }
    }


    public void procesarNivelPendiente() {
        if (nivelesPendientes > 0) {
            nivelesPendientes--;
            if (nivelesPendientes > 0 && !popUpMejoras.isPopUpAbierto()) {
                sistemaDeMejoras.anyadirMejorasAlPopUp();
            }
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

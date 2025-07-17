package com.sticklike.core.gameplay.sistemas;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;
import com.sticklike.core.gameplay.sistemas.eventBus.bus.LevelUpEvent;
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

    private final float multiplicadorXP;

    public SistemaDeNiveles(Jugador jugador, SistemaDeMejoras sistemaDeMejoras, PopUpMejoras popUpMejoras) {
        this(jugador, sistemaDeMejoras, popUpMejoras, 1f);
    }

    public SistemaDeNiveles(Jugador jugador, SistemaDeMejoras sistemaDeMejoras, PopUpMejoras popUpMejoras, float multiplicadorXP) {
        this.jugador = jugador;
        this.sistemaDeMejoras = sistemaDeMejoras;
        this.popUpMejoras = popUpMejoras;
        this.multiplicadorXP = multiplicadorXP;
    }

    public void agregarXP(float amount) {
        xpActual += amount * multiplicadorXP;
        while (xpActual >= xpHastaSiguienteNivel) {
            subirDeNivel();
        }
    }

    private void subirDeNivel() {
        xpActual -= xpHastaSiguienteNivel;
        nivelActual++;
        GameEventBus.publish(new LevelUpEvent(nivelActual));

        xpHastaSiguienteNivel = 50 * nivelActual * nivelActual + 50;

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

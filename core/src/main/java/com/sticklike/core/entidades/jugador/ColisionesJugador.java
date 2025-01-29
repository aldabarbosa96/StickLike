package com.sticklike.core.entidades.jugador;

import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.gameplay.managers.ControladorEnemigos;

import static com.sticklike.core.utilidades.GestorConstantes.*;


public class ColisionesJugador {
    /**
     * Verifica colisiones con los enemigos y aplica daño si es necesario.
     */
    public void verificarColisionesConEnemigos(ControladorEnemigos controladorEnemigos, Jugador jugador, ControladorAudio controladorAudio) {
        if (controladorEnemigos != null) {
            for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
                if (enColision(enemigo, jugador) && enemigo.puedeAplicarDanyo()) {
                    float damage = enemigo.getDamageAmount();
                    recibeDanyo(damage, jugador, controladorAudio);
                    enemigo.reseteaTemporizadorDanyo();
                }
            }
        }
    }

    public boolean enColision(Enemigo enemigo, Jugador jugador) {
        return jugador.getSprite().getBoundingRectangle().overlaps(enemigo.getSprite().getBoundingRectangle());
    }

    public void recibeDanyo(float cantidad, Jugador jugador, ControladorAudio controladorAudio) {
        if (jugador.estaVivo()) return;
        jugador.restarVidaJugador(cantidad * (1 - jugador.getResistenciaJugador()));
        controladorAudio.reproducirEfecto("recibeDanyo", AUDIO_DANYO);


        if (jugador.getVidaJugador() <= 0) {
            controladorAudio.reproducirEfecto("muerteJugador", AUDIO_MUERTE);
            jugador.setVidaJugador(0);
            jugador.muere();

        } else {
            jugador.getAnimacionesJugador().activarParpadeoJugador(PARPADEO_JUGADOR);
        }
    }

}

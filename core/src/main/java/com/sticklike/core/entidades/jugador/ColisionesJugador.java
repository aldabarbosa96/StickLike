package com.sticklike.core.entidades.jugador;

import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.gameplay.managers.ControladorEnemigos;


public class ColisionesJugador {
    /**
     * Verifica colisiones con los enemigos y aplica daño si es necesario.
     */
    public void verificarColisionesConEnemigos(ControladorEnemigos controladorEnemigos, Jugador jugador, ControladorAudio controladorAudio) {
        if (controladorEnemigos != null) {
            for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
                if (enColision(enemigo, jugador) && enemigo.puedeAplicarDanyo()) {
                    recibeDanyo(2,jugador,controladorAudio);
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
        jugador.restarVidaJugador(cantidad);
        controladorAudio.reproducirEfecto("recibeDanyo",0.9f);


        if (jugador.getVidaJugador() <= 0) {
            controladorAudio.reproducirEfecto("muerteJugador",0.7f);
            jugador.setVidaJugador(0);
            jugador.muere();

        } else {
            jugador.getAnimacionesJugador().activarParpadeo(0.2f); // tiempo parpadeo
        }
    }

}

package com.sticklike.core.entidades.jugador;

import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.gameplay.managers.ControladorEnemigos;

public class ColisionesJugador {
    /**
     * Verifica colisiones con los enemigos y aplica daño si es necesario.
     */
    public void verificarColisionesConEnemigos(ControladorEnemigos controladorEnemigos, Jugador jugador) {
        if (controladorEnemigos != null) {
            for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
                if (enColision(enemigo, jugador) && enemigo.puedeAplicarDanyo()) {
                    recibeDanyo(2,jugador);
                    enemigo.reseteaTemporizadorDanyo();
                }
            }
        }
    }

    public boolean enColision(Enemigo enemigo, Jugador jugador) {
        return jugador.getSprite().getBoundingRectangle().overlaps(enemigo.getSprite().getBoundingRectangle());
    }

    public void recibeDanyo(float cantidad, Jugador jugador) {
        if (jugador.estaVivo()) return;
        jugador.restarVidaJugador(cantidad);
        if (jugador.getVidaJugador() <= 0) {
            jugador.setVidaJugador(0);
            jugador.muere();
        }
    }

}

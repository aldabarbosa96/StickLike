package com.sticklike.core.logics.actions;

import com.sticklike.core.entities.Enemigo;
import com.sticklike.core.entities.Jugador;
import com.sticklike.core.managers.ControladorEnemigos;

public class ColisionesJugador {
    /**
     * Verifica colisiones con los enemigos y aplica daño si es necesario.
     */
    public void verificarColisionesConEnemigos(ControladorEnemigos controladorEnemigos, DesplazamientoJugador movimientoController, Jugador jugador) {
        if (controladorEnemigos != null) {
            for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
                if (movimientoController.enColision(enemigo, jugador) && enemigo.puedeAplicarDanyo()) {
                    recibeDanyo(2,jugador);
                    enemigo.reseteaTemporizadorDanyo();
                }
            }
        }
    }

    // Recibe daño
    public void recibeDanyo(float amountm, Jugador jugador) {
        if (jugador.estaMuerto()) return;
        jugador.restarVidaJugador(amountm);
        if (jugador.getVidaJugador() <= 0) {
            jugador.setVidaJugador(0);
            jugador.muere();
        }
    }
}

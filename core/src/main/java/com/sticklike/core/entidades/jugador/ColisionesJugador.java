package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.pools.RectanglePoolManager;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Clase controladora de las colisiones del jugador. Verifica la colisión con escala ligeramente reducida y aplica el daño si procede
 */
public class ColisionesJugador {

    public void verificarColisionesConEnemigos(ControladorEnemigos controladorEnemigos, Jugador jugador, GestorDeAudio gestorDeAudio) {
        if (controladorEnemigos != null) {
            for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
                if (enColision(enemigo, jugador) && enemigo.puedeAplicarDanyo()) {
                    float damage = enemigo.getDamageAmount();
                    recibeDanyo(damage, jugador, gestorDeAudio);
                    enemigo.reseteaTemporizadorDanyo();
                }
            }
        }
    }

    public boolean enColision(Enemigo enemigo, Jugador jugador) {
        Rectangle fullRect = enemigo.getSprite().getBoundingRectangle();
        float scaleX = 0.5f;
        float scaleY = 0.33f;

        float newWidth = fullRect.width * scaleX;
        float newHeight = fullRect.height * scaleY;

        // Desplazamos el rect para que quede centrado
        float offsetX = (fullRect.width - newWidth) / 2f;
        float offsetY = (fullRect.height - newHeight) / 2f;

        // Creamos la caja de colisión reducida a partir de la pool de rectángulos
        Rectangle enemyHitbox = RectanglePoolManager.obtenerRectangulo(fullRect.x + offsetX, fullRect.y + offsetY, newWidth, newHeight); // Caja del jugador (valorar si se modifica en un futuro)
        Rectangle playerRect = jugador.getSprite().getBoundingRectangle();

        boolean overlaps = playerRect.overlaps(enemyHitbox);

        // Liberamos el objeto Rectangle para su reutilización
        RectanglePoolManager.liberarRectangulo(enemyHitbox);

        return overlaps;
    }

    public void recibeDanyo(float cantidad, Jugador jugador, GestorDeAudio gestorDeAudio) {
        if (jugador.estaMuerto()) return;
        if (jugador.isInvulnerable()) return;
        jugador.restarVidaJugador(cantidad * (1 - jugador.getResistenciaJugador()));
        gestorDeAudio.reproducirEfecto("recibeDanyo", AUDIO_DANYO);


        if (jugador.getVidaJugador() <= 0) {
            gestorDeAudio.reproducirEfecto("muerteJugador", AUDIO_MUERTE);
            jugador.setVidaJugador(0);
            jugador.muere();

        } else {
            jugador.getAnimacionesJugador().activarParpadeoJugador(PARPADEO_JUGADOR_DANYO);
        }
    }

}

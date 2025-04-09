package com.sticklike.core.entidades.enemigos.ia;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Clase base para el movimiento de los enemigos; gestiona knockback y delega el movimiento específico a las subclases.
 */

public abstract class MovimientoBaseEnemigos {
    private boolean canKnockback;
    protected float knockbackVelX = VELOCIDAD_EMPUJE;
    protected float knockbackVelY = VELOCIDAD_EMPUJE;
    protected float knockbackTimer = TEMPORIZADOR_EMPUJE;
    protected float knockbackDuration = DURACION_EMPUJE;

    public MovimientoBaseEnemigos(boolean canKnockback) {
        this.canKnockback = canKnockback;
    }

    public final void actualizarMovimiento(float delta, Sprite sprite, Jugador jugador) {
        if (knockbackTimer > 0) { // se comprueba si puede aplicarse el knock-back
            float moveX = knockbackVelX * delta;
            float moveY = knockbackVelY * delta;
            sprite.translate(moveX, moveY);

            knockbackTimer -= delta;
            if (knockbackTimer < 0) {
                knockbackTimer = 0;
            }
        } else {
            // si no hay knock-back realiza movimiento específico
            actualizarMovimientoEspecifico(delta, sprite, jugador);
        }
    }

    protected abstract void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador);

    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        if (!canKnockback) {
            return;
        }
        knockbackVelX = dirX * fuerza;
        knockbackVelY = dirY * fuerza;
        knockbackTimer = knockbackDuration;
    }

    public final void actualizarSoloKnockback(float delta, Sprite sprite, boolean mantenerKnockback) { // para aplicar knockback durante la animación de muerte
        if (knockbackTimer > 0 || mantenerKnockback) {
            float moveX = knockbackVelX * 1.25f * delta;
            float moveY = knockbackVelY * 1.25f * delta;
            sprite.translate(moveX, moveY);
            if (!mantenerKnockback) {
                knockbackTimer -= delta;
                if (knockbackTimer < 0) {
                    knockbackTimer = 0;
                }
            }
        }
    }

}

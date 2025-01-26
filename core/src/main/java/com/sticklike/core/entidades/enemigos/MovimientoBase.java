package com.sticklike.core.entidades.enemigos;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;
import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Clase base para manejar movimiento de enemigos.
 * Incluye lógica de knockback opcional y un método abstracto para la IA específica de cada enemigo
 */
public abstract class MovimientoBase {
    // Knockback
    private boolean canKnockback;
    protected float knockbackVelX = VELOCIDAD_EMPUJE;
    protected float knockbackVelY = VELOCIDAD_EMPUJE;
    protected float knockbackTimer = TEMPORIZADOR_EMPUJE;
    protected float knockbackDuration = DURACION_EMPUJE;

    public MovimientoBase(boolean canKnockback) {
        this.canKnockback = canKnockback;
    }

    /**
     * Método principal que actualiza el movimiento.
     * - Si hay knockback activo, aplica su lógica.
     * - Si no, llama al método abstracto de la IA concreta
     */
    public final void actualizarMovimiento(float delta, Sprite sprite, Jugador jugador) {
        if (knockbackTimer > 0) {
            // Aplicamos empuje
            float moveX = knockbackVelX * delta;
            float moveY = knockbackVelY * delta;
            sprite.translate(moveX, moveY);

            knockbackTimer -= delta;
            if (knockbackTimer < 0) {
                knockbackTimer = 0;
            }
        } else {
            // Movimiento específico
            actualizarMovimientoEspecifico(delta, sprite, jugador);
        }
    }

    /**
     * Método abstracto que cada subclase debe implementar para su IA de movimiento concreta
     */
    protected abstract void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador);

    /**
     * Aplica knockback si el enemigo lo permite.
     */
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        if (!canKnockback) {
            // Si este enemigo no sufre knockback, ignoramos.
            return;
        }
        knockbackVelX = dirX * fuerza;
        knockbackVelY = dirY * fuerza;
        knockbackTimer = knockbackDuration;
    }
}

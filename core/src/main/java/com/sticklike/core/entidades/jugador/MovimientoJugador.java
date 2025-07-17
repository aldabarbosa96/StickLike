package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.InputsJugador.ResultadoInput;
import com.sticklike.core.entidades.renderizado.particulas.ParticleManager;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class MovimientoJugador {
    private final Vector2 direccion = new Vector2();
    private float particleTimer = 0f;
    private static final float PARTICLE_INTERVAL = 0.025f;

    public void mover(Jugador jugador, ResultadoInput resInput, float delta) {
        // 1) Construir el vector de dirección
        direccion.set(resInput.movX, resInput.movY);

        // 2) Si hay movimiento, normalizamos y tratamos partículas
        if (direccion.len2() > 0f) {
            // normalizar dirección
            direccion.nor();

            // reducir el timer
            particleTimer -= delta;
            if (particleTimer <= 0f) {
                // calcular centro de spawn
                float cx = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
                float cy = jugador.getSprite().getY();
                // spawn del efecto
                ParticleManager.get().obtainEffect("player", cx, cy, true);
                // resetear timer
                particleTimer = PARTICLE_INTERVAL;
            }
        } else {
            // sin movimiento, mantener timer a cero para el próximo inicio
            particleTimer = 0f;
        }

        // 3) Aplicar velocidad y delta
        direccion.scl(jugador.getVelocidadJugador() * delta);

        // 4) Trasladar el sprite
        jugador.getSprite().translate(direccion.x, direccion.y);

        // 5) Clamp a los límites del mapa
        var sprite = jugador.getSprite();
        float minX = MAP_MIN_X + MARGEN_LIMITES_MAPA;
        float maxX = MAP_MAX_X - MARGEN_LIMITES_MAPA - sprite.getWidth();
        float minY = MAP_MIN_Y + MARGEN_LIMITES_MAPA;
        float maxY = MAP_MAX_Y - MARGEN_LIMITES_MAPA - sprite.getHeight();

        float clampedX = MathUtils.clamp(sprite.getX(), minX, maxX);
        float clampedY = MathUtils.clamp(sprite.getY(), minY, maxY);
        sprite.setPosition(clampedX, clampedY);
    }
}

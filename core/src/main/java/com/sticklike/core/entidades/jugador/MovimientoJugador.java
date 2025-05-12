package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.InputsJugador.ResultadoInput;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Esta clase se encarga de la lógica de movimiento del jugador:
 * normalizar cualquier vector de entrada, aplicar velocidad y modificar la posición del sprite.
 */
public class MovimientoJugador {
    // Vector de trabajo (para no crear uno nuevo cada frame)
    private final Vector2 direccion = new Vector2();

    public void mover(Jugador jugador, ResultadoInput resInput, float delta) {
        /* -------- Paso 1: construimos el vector de dirección -------- */
        direccion.set(resInput.movX, resInput.movY);

        /* -------- Paso 2: normalizamos si hay entrada -------- */
        if (direccion.len2() > 0f) {
            direccion.nor();
        }

        /* -------- Paso 3: aplicamos velocidad y delta -------- */
        direccion.scl(jugador.getVelocidadJugador() * delta);

        /* -------- Paso 4: trasladamos el sprite -------- */
        jugador.getSprite().translate(direccion.x, direccion.y);

        /* -------- Paso 5: clamp a los límites del mapa -------- */
        var sprite = jugador.getSprite();

        float minX = MAP_MIN_X + MARGEN_LIMITES_MAPA;
        float maxX = MAP_MAX_X - MARGEN_LIMITES_MAPA - sprite.getWidth();
        float minY = MAP_MIN_Y + MARGEN_LIMITES_MAPA;
        float maxY = MAP_MAX_Y - MARGEN_LIMITES_MAPA - sprite.getHeight();

        // Ajustamos posición si nos hemos salido
        float clampedX = MathUtils.clamp(sprite.getX(), minX, maxX);
        float clampedY = MathUtils.clamp(sprite.getY(), minY, maxY);
        sprite.setPosition(clampedX, clampedY);
    }
}

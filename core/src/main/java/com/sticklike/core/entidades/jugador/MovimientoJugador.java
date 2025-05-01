package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.InputsJugador.ResultadoInput;

/**
 * Esta clase se encarga de la lógica de movimiento del jugador:
 * normalizar cualquier vector de entrada, aplicar velocidad y
 * modificar la posición del sprite.
 */
public class MovimientoJugador {
    // Vector de trabajo (para no crear uno nuevo cada frame)
    private final Vector2 direccion = new Vector2();

    public void mover(Jugador jugador, ResultadoInput resInput, float delta) {
        // 1) Construimos el vector de dirección
        direccion.set(resInput.movX, resInput.movY);

        // 2) Si hay entrada (no es 0,0) normalizamos a longitud 1
        if (direccion.len2() > 0f) {
            direccion.nor();
        }

        // 3) Escalamos por velocidad y delta time
        direccion.scl(jugador.getVelocidadJugador() * delta);

        // 4) Movemos el sprite
        jugador.getSprite().translate(direccion.x, direccion.y);
    }
}

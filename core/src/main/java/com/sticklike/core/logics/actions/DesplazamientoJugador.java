package com.sticklike.core.logics.actions;

import com.sticklike.core.entities.Enemigo;
import com.sticklike.core.entities.Jugador;
import com.sticklike.core.logics.inputs.InputsJugador.ResultadoInput;

/**
 * {@code MovimientoJugador} se encarga de la lógica de movimiento
 * del jugador: normalizar diagonales, aplicar velocidad y
 * modificar la posición del sprite.
 */
public class DesplazamientoJugador {

    /**
     * Método principal que calcula y aplica el movimiento del jugador,
     * en base al input y a la velocidad. Se encarga también de la
     * normalización al moverse en diagonal.
     *
     * @param jugador   referencia al Jugador, para acceder a su velocidad y sprite
     * @param resInput  resultado del input (movX, movY, etc.)
     * @param delta     tiempo transcurrido desde el último frame
     */
    public void mover(Jugador jugador, ResultadoInput resInput, float delta) {
        // Tomamos el movX, movY tal como los da la clase interna ResultadoInput
        float movX = resInput.movX;
        float movY = resInput.movY;

        // Normalizamos si hay diagonal
        if (movX != 0 && movY != 0) {
            float factor = (float)(1 / Math.sqrt(2));
            movX *= factor;
            movY *= factor;
        }

        // Aplicamos la velocidad del jugador
        float finalX = movX * jugador.getVelocidadJugador() * delta;
        float finalY = movY * jugador.getVelocidadJugador() * delta;

        // Movemos el sprite
        jugador.getSprite().translate(finalX, finalY);
    }
    public boolean enColision(Enemigo enemigo, Jugador jugador) {
        return jugador.getSprite().getBoundingRectangle().overlaps(enemigo.getSprite().getBoundingRectangle());
    }
}

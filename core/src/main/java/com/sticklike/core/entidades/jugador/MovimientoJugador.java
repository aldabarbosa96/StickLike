package com.sticklike.core.entidades.jugador;

import com.sticklike.core.entidades.jugador.InputsJugador.ResultadoInput;

/**
 * Esta clase se encarga de la lógica de movimiento del jugador: normalizar diagonales, aplicar velocidad y
 * modificar la posición del sprite.
 */
public class MovimientoJugador {
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
        jugador.getSprite().translate(finalX, finalY);
    }

}

package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.jugador.DesplazamientoJugador;

/**
 * Clase que gestiona la entrada de teclas para controlar el movimiento
 * de un jugador. No conoce los atributos del jugador (velocidad, etc.),
 * solo reporta la intención de movimiento.
 */
public class InputsJugador {
    /**
     * @param delta tiempo transcurrido desde el último frame
     * @return un objeto {@link ResultadoInput} con los valores de movimiento (x, y)
     *         y la dirección horizontal (LEFT, RIGHT, IDLE).
     */
    public ResultadoInput procesarInput(float delta) {
        float movX = 0;
        float movY = 0;
        Direction direction = Direction.IDLE;

        boolean pressLeft  = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean pressRight = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean pressUp    = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean pressDown  = Gdx.input.isKeyPressed(Input.Keys.S);

        // Movimiento horizontal
        if (pressLeft && !pressRight) {
            movX = -1;
            direction = Direction.LEFT;
        } else if (pressRight && !pressLeft) {
            movX = 1;
            direction = Direction.RIGHT;
        } else {
            direction = Direction.IDLE;
        }

        // Movimiento vertical
        if (pressUp && !pressDown) {
            movY = 1;
        } else if (pressDown && !pressUp) {
            movY = -1;
        }

        // Devolvemos un InputResult con movX, movY y la dirección horizontal
        return new ResultadoInput(movX, movY, direction);
    }
    public void procesarInputYMovimiento(float delta, DesplazamientoJugador desplazamientoJugador, Jugador jugador) {
        ResultadoInput result = this.procesarInput(delta);
        desplazamientoJugador.mover(jugador, result, delta);
        jugador.setDireccionActual(result.direction);
    }

    /**
     * Enum para indicar la dirección horizontal: LEFT, RIGHT o IDLE.
     * todo --> incluir diagonales en un futuro
     */
    public enum Direction {
        LEFT, RIGHT, IDLE
    }

    /**
     * Clase contenedora del resultado del input:
     * - movX, movY (normalizados o no)
     * - direction (LEFT, RIGHT, IDLE)
     */
    public static class ResultadoInput {
        public float movX;
        public float movY;
        public Direction direction;

        public ResultadoInput(float movX, float movY, Direction direction) {
            this.movX = movX;
            this.movY = movY;
            this.direction = direction;
        }
    }
}

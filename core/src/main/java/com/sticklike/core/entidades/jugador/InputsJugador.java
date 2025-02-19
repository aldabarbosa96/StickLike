package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;

/**
 * Clase que gestiona la entrada de teclas y mando para controlar el movimiento del jugador.
 * Detecta entradas del teclado y de un gamepad, permitiendo su uso simultáneo
 */
public class InputsJugador extends ControllerAdapter {
    private float movX = 0;
    private float movY = 0;
    private Direction direction = Direction.IDLE;
    private Controller mando; // Referencia al mando conectado

    public InputsJugador() {
        if (!Controllers.getControllers().isEmpty()) {
            mando = Controllers.getControllers().first();
            mando.addListener(this);
        }
    }

    public ResultadoInput procesarInput(float delta) {
        float tecladoX = procesarInputTecladoX();
        float tecladoY = procesarInputTecladoY();
        float mandoX = procesarInputMandoX();
        float mandoY = procesarInputMandoY();

        // Sumamos los inputs de teclado y mando
        movX = tecladoX + mandoX;
        movY = tecladoY + mandoY;

        // NormalizaMOS valores para evitar que la suma sea mayor a 1 en diagonal
        if (movX > 1) movX = 1;
        if (movX < -1) movX = -1;
        if (movY > 1) movY = 1;
        if (movY < -1) movY = -1;

        if (movX < 0) {
            direction = Direction.LEFT;
        } else if (movX > 0) {
            direction = Direction.RIGHT;
        } else {
            direction = Direction.IDLE;
        }

        return new ResultadoInput(movX, movY, direction);
    }

    private float procesarInputTecladoX() {
        boolean pressLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean pressRight = Gdx.input.isKeyPressed(Input.Keys.D);

        if (pressLeft && !pressRight) return -1;
        if (pressRight && !pressLeft) return 1;
        return 0;
    }

    private float procesarInputTecladoY() {
        boolean pressUp = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean pressDown = Gdx.input.isKeyPressed(Input.Keys.S);

        if (pressUp && !pressDown) return 1;
        if (pressDown && !pressUp) return -1;
        return 0;
    }


    private float procesarInputMandoX() {
        if (mando == null) return 0;
        float axisX = mando.getAxis(0); // Stick izquierdo horizontal

        if (axisX < -0.2f) return -1;
        if (axisX > 0.2f) return 1;
        return 0;
    }

    private float procesarInputMandoY() {
        if (mando == null) return 0;
        float axisY = mando.getAxis(1); // Stick izquierdo vertical

        if (axisY < -0.2f) return 1;
        if (axisY > 0.2f) return -1;
        return 0;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        return true;
    }

    public void procesarInputYMovimiento(float delta, MovimientoJugador movimientoJugador, Jugador jugador) {
        ResultadoInput result = this.procesarInput(delta);
        movimientoJugador.mover(jugador, result, delta);
        jugador.setDireccionActual(result.direction);
    }


    /**
     * Enum para indicar la dirección horizontal: LEFT, RIGHT o IDLE.
     */
    public enum Direction {
        LEFT, RIGHT, IDLE
    }

    /**
     * Clase contenedora del resultado del input:
     * - movX, movY (normalizados)
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

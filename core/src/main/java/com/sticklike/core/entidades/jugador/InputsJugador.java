package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;

/**
 * Clase que gestiona la entrada de teclas y mando para controlar el movimiento del jugador.
 * Detecta entradas del teclado y de un gamepad, permitiendo su uso simultáneo.
 */
public class InputsJugador extends ControllerAdapter {
    private float movX = 0;
    private float movY = 0;
    private Direction direction = Direction.IDLE;
    private Controller mando; // Referencia al mando conectado

    public InputsJugador() {
        // Solo registramos el listener si NO estamos en Android
        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            Controllers.addListener(this);
            if (!Controllers.getControllers().isEmpty()) {
                mando = Controllers.getControllers().first();
                mando.addListener(this);
            }
        }
    }

    public ResultadoInput procesarInput() {
        float tecladoX = procesarInputTecladoX();
        float tecladoY = procesarInputTecladoY();
        float mandoX = procesarInputMandoX();
        float mandoY = procesarInputMandoY();

        // Sumamos los inputs de teclado y mando
        movX = tecladoX + mandoX;
        movY = tecladoY + mandoY;

        // Normalizamos para evitar valores mayores a 1
        movX = Math.max(-1, Math.min(1, movX));
        movY = Math.max(-1, Math.min(1, movY));

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
        boolean pressLeft = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean pressRight = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (pressLeft && !pressRight) return -1;
        if (pressRight && !pressLeft) return 1;
        return 0;
    }

    private float procesarInputTecladoY() {
        boolean pressUp = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean pressDown = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);

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
        // Puedes ajustar la lógica según lo que se necesite hacer cuando se presione un botón
        return true;
    }

    public void procesarInputYMovimiento(float delta, MovimientoJugador movimientoJugador, Jugador jugador) {
        ResultadoInput result = this.procesarInput();
        movimientoJugador.mover(jugador, result, delta);
        jugador.setDireccionActual(result.direction);
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * Enum para indicar la dirección horizontal: LEFT, RIGHT o IDLE.
     */
    public enum Direction {
        LEFT, RIGHT, IDLE
    }

    /**
     * Clase contenedora del resultado del input: movX, movY (normalizados) y direction.
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

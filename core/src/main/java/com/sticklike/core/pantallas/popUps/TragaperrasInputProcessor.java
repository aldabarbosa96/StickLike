package com.sticklike.core.pantallas.popUps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.sticklike.core.entidades.mobiliario.tragaperras.TragaperrasLogic;
import com.sticklike.core.pantallas.juego.VentanaJuego1;

public class TragaperrasInputProcessor extends ControllerAdapter implements InputProcessor {

    private final VentanaJuego1 ventanaJuego1;
    private final PopUpTragaperras view;
    private TragaperrasLogic logic;
    private boolean axisLock = false;

    public TragaperrasInputProcessor(VentanaJuego1 ventanaJuego1, PopUpTragaperras view) {
        this.ventanaJuego1 = ventanaJuego1;
        this.view = view;
        // El propio pop-up nos avisa cuando se pulsa “Salir”
        view.setOnExitListener(this::hide);
    }

    public void show(TragaperrasLogic logic) {
        this.logic = logic;
        view.build(logic);

        // Pausa global
        ventanaJuego1.setPausado(true);
        ventanaJuego1.getMenuPause().bloquearInputs(true);
        ventanaJuego1.getRenderHUDComponents().pausarTemporizador();

        // Inputs: primero la UI del pop-up y luego nosotros
        InputMultiplexer im = new InputMultiplexer(this, view.getUiStage());
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(this);
    }

    public void hide() {
        Controllers.removeListener(this);
        Gdx.input.setInputProcessor(null);

        view.clear(); // cierra la ventana y limpia el Stage
        ventanaJuego1.getMenuPause().bloquearInputs(false);
        ventanaJuego1.setPausado(false);
        ventanaJuego1.getRenderHUDComponents().reanudarTemporizador();
    }

    /* ---------- TECLADO ---------- */
    @Override
    public boolean keyDown(int key) {
        switch (key) {
            case Input.Keys.ENTER, Input.Keys.SPACE -> {
                logic.spin();
                return true;
            }
            case Input.Keys.ESCAPE, Input.Keys.BACK -> {
                hide();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }

    /* ---------- PAD ---------- */
    @Override
    public boolean buttonDown(Controller c, int button) {
        if (button == 0) {
            logic.spin();
            return true;
        }        // botón A / Cruz
        if (button == 1) {
            hide();
            return true;
        }        // botón B / Círculo
        return false;
    }


    @Override
    public boolean axisMoved(Controller c, int axis, float value) {
        if (axis == 1) {
            if (Math.abs(value) < 0.2f) {
                axisLock = false;
                return false;
            }
            if (axisLock) return false;
            if (value < -0.5f) logic.spin();
            if (value > 0.5f) hide();
            axisLock = true;
            return true;
        }
        return false;
    }
}

package com.sticklike.core.entidades.mobiliario.tragaperras;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.sticklike.core.pantallas.juego.VentanaJuego1;

/**
 * Gestiona teclado y mando para el pop-up de la tragaperras.
 */
public class TragaperrasInputProcessor extends ControllerAdapter implements InputProcessor {

    private final VentanaJuego1 vj1;
    private final PopUpTragaperras view;
    private TragaperrasLogic logic;
    private boolean axisLock = false;

    public TragaperrasInputProcessor(VentanaJuego1 vj1, PopUpTragaperras view) {
        this.vj1 = vj1;
        this.view = view;
    }

    /* -------------------------------------------------- */
    /*  Mostrar / ocultar                                */
    /* -------------------------------------------------- */

    public void show(TragaperrasLogic logic) {
        this.logic = logic;
        view.build(logic);

        vj1.setPausado(true);
        vj1.getMenuPause().bloquearInputs(true);

        InputMultiplexer im = new InputMultiplexer(this, view.getUiStage());
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(this);
    }

    private void hide() {
        Controllers.removeListener(this);
        Gdx.input.setInputProcessor(null);

        view.clear();
        vj1.getMenuPause().bloquearInputs(false);
        vj1.setPausado(false);
    }

    /* -------------------------------------------------- */
    /*  Teclado                                           */
    /* -------------------------------------------------- */

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE -> {
                hide();
                return true;
            }
            case Input.Keys.ENTER, Input.Keys.NUMPAD_ENTER -> {
                logic.spin();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int code) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    /* -------------------------------------------------- */
    /*  Ratón / touch (no usamos)                         */
    /* -------------------------------------------------- */

    @Override
    public boolean touchDown(int x, int y, int p, int b) {
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int p, int b) {
        return false;
    }

    @Override
    public boolean touchCancelled(int x, int y, int p, int b) {
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int p) {
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        return false;
    }

    @Override
    public boolean scrolled(float ax, float ay) {
        return false;
    }

    /* -------------------------------------------------- */
    /*  Game-pad                                          */
    /* -------------------------------------------------- */

    @Override
    public boolean axisMoved(Controller c, int axis, float value) {
        if (axis == 1) {            // stick vertical
            if (Math.abs(value) < 0.2f) {
                axisLock = false;
                return false;
            }
            if (axisLock) return false;
            if (value < -0.5f || value > 0.5f) { /* reserved for futuro */ }
            axisLock = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean buttonDown(Controller c, int button) {
        if (button == 0) {          // A / Cross
            logic.spin();
            return true;
        }
        if (button == 1) {          // B / Circle
            hide();
            return true;
        }
        return false;
    }
}

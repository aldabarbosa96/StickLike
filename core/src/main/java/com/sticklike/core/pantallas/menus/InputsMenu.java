package com.sticklike.core.pantallas.menus;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.BUTTON_START;

public class InputsMenu extends InputAdapter implements ControllerListener {

    public interface MenuInputListener {
        void onNavigateUp();
        void onNavigateDown();
        void onSelect();
        void onBack();
        void onPauseToggle();
    }

    private MenuInputListener listener;
    private boolean axisLock = false;

    public InputsMenu(MenuInputListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean keyDown(int keycode) {
        // Detecta pausa con SPACE o P
        if (keycode == Input.Keys.SPACE || keycode == Input.Keys.P) {
            if (listener != null) listener.onPauseToggle();
            return true;
        }
        switch (keycode) {
            case Input.Keys.UP:
            case Input.Keys.LEFT:
                if (listener != null) listener.onNavigateUp();
                return true;
            case Input.Keys.DOWN:
            case Input.Keys.RIGHT:
                if (listener != null) listener.onNavigateDown();
                return true;
            case Input.Keys.ENTER:
            case Input.Keys.NUMPAD_ENTER:
                if (listener != null) listener.onSelect();
                return true;
            case Input.Keys.ESCAPE:
            case Input.Keys.BACKSPACE:
                if (listener != null) listener.onBack();
                return true;
        }
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        if (axisIndex == 1) { // Eje vertical
            if (Math.abs(value) < 0.2f) {
                axisLock = false;
                return false;
            }
            if (axisLock) return false;
            if (value > 0.5f) {
                if (listener != null) listener.onNavigateDown();
                axisLock = true;
                return true;
            } else if (value < -0.5f) {
                if (listener != null) listener.onNavigateUp();
                axisLock = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if (buttonIndex == BUTTON_START ) {
            if (listener != null) listener.onPauseToggle();
            return true;
        }
        if (buttonIndex == 0) {
            if (listener != null) listener.onSelect();
            return true;
        } else if (buttonIndex == 1) {
            if (listener != null) listener.onBack();
            return true;
        }
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {
        return false;
    }

    @Override
    public void connected(Controller controller) {
    }

    @Override
    public void disconnected(Controller controller) {
    }
}

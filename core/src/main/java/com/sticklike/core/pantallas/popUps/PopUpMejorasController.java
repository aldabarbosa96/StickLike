package com.sticklike.core.pantallas.popUps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.sticklike.core.gameplay.progreso.Mejora;
import com.sticklike.core.gameplay.sistemas.SistemaDeMejoras;
import com.sticklike.core.pantallas.juego.VentanaJuego1;

import java.util.List;

/**
 * Controlador del pop‑up de mejoras.  Gestiona teclado y gamepad, comunica al modelo y actualiza el HUD.
 */
public class PopUpMejorasController extends ControllerAdapter implements InputProcessor {

    private final SistemaDeMejoras modelo;
    private final VentanaJuego1 ventanaJuego1;
    private final PopUpMejoras view;

    private List<Mejora> opciones;
    private int selectedIndex;
    private int rerollCount = 1;
    private boolean axisLock = false;

    public PopUpMejorasController(SistemaDeMejoras modelo, VentanaJuego1 ventanaJuego1, PopUpMejoras view) {
        this.modelo = modelo;
        this.ventanaJuego1 = ventanaJuego1;
        this.view = view;
        view.setOnSelectListener(this::select);   // ratón o touch
    }

    public void show(List<Mejora> opciones) {
        this.opciones = opciones;
        this.selectedIndex = 0;

        // Construir la UI
        view.build(opciones, selectedIndex, rerollCount);

        // Pausar el juego y HUD
        ventanaJuego1.setPausado(true);
        ventanaJuego1.getMenuPause().bloquearInputs(true);
        ventanaJuego1.getRenderHUDComponents().pausarTemporizador();

        // Configurar inputs (stage + este controlador)
        InputMultiplexer im = new InputMultiplexer(this, view.getUiStage());
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(this);
    }

    public void hide() {
        Controllers.removeListener(this);
        Gdx.input.setInputProcessor(null);

        //view.animarSalida(view::clearPopUp); todo --> gestionar correctamente en un futuro (mejorar animación de salida)
        view.clearPopUp();

        ventanaJuego1.getMenuPause().bloquearInputs(false);
        ventanaJuego1.setPausado(false);
        ventanaJuego1.getRenderHUDComponents().reanudarTemporizador();
        ventanaJuego1.getSistemaDeNiveles().procesarNivelPendiente();
    }

    private void select(int index) {
        if (index == opciones.size()) { // reroll
            if (rerollCount > 0) {
                rerollCount--;
                opciones = modelo.generarOpcionesDeMejoraAleatorias(3);
                selectedIndex = 0;
                view.build(opciones, selectedIndex, rerollCount);
            }
            return;
        }

        // Aplicar mejora
        modelo.aplicarMejora(opciones.get(index));
        // Refrescar habilidades en el HUD
        ventanaJuego1.getRenderHUDComponents().setHabilidadesActivas(modelo.getHabilidadesActivas());

        hide();
    }

    private void moveSelection(int delta) {
        int max = opciones.size();  // último index = reroll
        selectedIndex = Math.max(0, Math.min(selectedIndex + delta, max));
        view.updateHighlight(selectedIndex);
    }

    /* -------------------------------------------------- */
    /*  Input: teclado                                    */
    /* -------------------------------------------------- */

    @Override
    public boolean keyDown(int code) {
        switch (code) {
            case Input.Keys.DOWN, Input.Keys.RIGHT -> {
                moveSelection(1);
                return true;
            }
            case Input.Keys.UP, Input.Keys.LEFT -> {
                moveSelection(-1);
                return true;
            }
            case Input.Keys.ENTER, Input.Keys.NUMPAD_ENTER -> {
                select(selectedIndex);
                return true;
            }
            case Input.Keys.NUM_1, Input.Keys.NUMPAD_1 -> {
                selectDirect(0);
                return true;
            }
            case Input.Keys.NUM_2, Input.Keys.NUMPAD_2 -> {
                selectDirect(1);
                return true;
            }
            case Input.Keys.NUM_3, Input.Keys.NUMPAD_3 -> {
                selectDirect(2);
                return true;
            }
            case Input.Keys.R -> {
                if (rerollCount > 0) {
                    selectedIndex = opciones.size();
                    view.updateHighlight(selectedIndex);
                    select(selectedIndex);
                }
                return true;
            }
        }
        return false;
    }

    private void selectDirect(int idx) {
        if (idx < opciones.size()) {
            selectedIndex = idx;
            view.updateHighlight(idx);
            select(idx);
        }
    }

    @Override
    public boolean keyUp(int k) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

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
    /*  Input: gamepad                                    */
    /* -------------------------------------------------- */

    @Override
    public boolean axisMoved(Controller c, int axis, float value) {
        if (axis == 1) {
            if (Math.abs(value) < 0.2f) {
                axisLock = false;
                return false;
            }
            if (axisLock) return false;
            if (value > 0.5f) moveSelection(1);
            if (value < -0.5f) moveSelection(-1);
            axisLock = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean buttonDown(Controller c, int b) {
        return switch (b) {
            case 0 -> {
                select(selectedIndex);
                yield true;
            }
            case 11 -> {
                moveSelection(-1);
                yield true;
            }
            case 12 -> {
                moveSelection(1);
                yield true;
            }
            default -> false;
        };
    }
}

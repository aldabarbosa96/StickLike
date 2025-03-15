package com.sticklike.core.pantallas.menus;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.sticklike.core.MainGame;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

public class MenuPrincipal extends ScreenAdapter {
    private MainGame game;
    private RenderMenuPrincipal renderMenu;
    private MenuInputHandler inputHandler;

    public MenuPrincipal(MainGame game) {
        this.game = game;
        renderMenu = new RenderMenuPrincipal();

        renderMenu.setMenuListener(new RenderMenuPrincipal.MenuListener() {
            @Override
            public void onSelectButton(int index) {
                ejecutarAccion(index);
            }
        });

        GestorDeAudio.getInstance().detenerMusica();
        inputHandler = new MenuInputHandler();
        InputMultiplexer im = new InputMultiplexer(renderMenu.getStage(), inputHandler);
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(inputHandler);
    }

    @Override
    public void render(float delta) {
        renderMenu.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        renderMenu.resize(width, height);
    }

    @Override
    public void dispose() {
        renderMenu.dispose();
        Controllers.removeListener(inputHandler);
    }

    private void ejecutarAccion(int index) {
        switch (index) {
            case 0: // Jugar
                GestorDeAudio.getInstance().detenerMusica();
                Controllers.removeListener(inputHandler);
                Gdx.input.setInputProcessor(null);
                game.ventanaJuego1 = new VentanaJuego1(game, VentanaJuego1.worldWidth, VentanaJuego1.worldHeight);
                game.setScreen(game.ventanaJuego1);
                break;
            case 1:  // Niveles
                break;
            case 2: // Personaje
                break;
            case 3: // Opciones
                Controllers.removeListener(inputHandler);
                Gdx.input.setInputProcessor(null);
                game.setScreen(new MenuOpciones(game));
                break;
            case 4: // Logros
                break;
            case 5: // Créditos
                GestorDeAudio.getInstance().detenerMusica();
                Controllers.removeListener(inputHandler);
                Gdx.input.setInputProcessor(null);
                game.setScreen(new Creditos(game));
                break;
            case 6: // Salir
                Controllers.removeListener(inputHandler);
                Gdx.input.setInputProcessor(null);
                Gdx.app.exit();
                break;
        }
    }

    // Manejo de entradas para navegación (teclado y controlador)
    private class MenuInputHandler extends InputAdapter implements ControllerListener {
        private boolean axisLock = false;

        private void onSelectButton(int index) {
            renderMenu.setSelectedIndex(index);
            ejecutarAccion(index);
        }

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.DOWN:
                case Input.Keys.RIGHT:
                    renderMenu.incrementSelectedIndex();
                    return true;
                case Input.Keys.UP:
                case Input.Keys.LEFT:
                    renderMenu.decrementSelectedIndex();
                    return true;
                case Input.Keys.ENTER:
                case Input.Keys.NUMPAD_ENTER:
                    onSelectButton(renderMenu.getSelectedIndex());
                    return true;
            }
            return false;
        }

        @Override
        public boolean axisMoved(Controller controller, int axisIndex, float value) {
            if (axisIndex == 1) {
                if (Math.abs(value) < 0.2f) {
                    axisLock = false;
                    return false;
                }
                if (axisLock) return false;
                if (value > 0.5f) {
                    renderMenu.incrementSelectedIndex();
                    axisLock = true;
                    return true;
                } else if (value < -0.5f) {
                    renderMenu.decrementSelectedIndex();
                    axisLock = true;
                    return true;
                }
            }
            return false;
        }

        @Override
        public void connected(Controller controller) {}

        @Override
        public void disconnected(Controller controller) {}

        @Override
        public boolean buttonDown(Controller controller, int buttonIndex) {
            if (buttonIndex == 0) {
                onSelectButton(renderMenu.getSelectedIndex());
                return true;
            }
            return false;
        }

        @Override public boolean buttonUp(Controller controller, int buttonIndex) { return false; }
        @Override public boolean keyUp(int keycode) { return false; }
        @Override public boolean keyTyped(char character) { return false; }
        @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
        @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
        @Override public boolean scrolled(float amountX, float amountY) { return false; }
    }
}

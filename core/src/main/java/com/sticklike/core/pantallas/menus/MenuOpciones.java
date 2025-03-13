package com.sticklike.core.pantallas.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.sticklike.core.MainGame;

public class MenuOpciones extends ScreenAdapter {
    private MainGame game;
    private RenderMenuOpciones renderMenuOpciones;
    private OpcionesInputHandler inputHandler;

    public MenuOpciones(MainGame game) {
        this.game = game;
        renderMenuOpciones = new RenderMenuOpciones();

        renderMenuOpciones.setMenuOpcionesListener(new RenderMenuOpciones.MenuOpcionesListener() {
            @Override
            public void onVolver() {
                volverAlMenuPrincipal();
            }
        });
    }

    @Override
    public void show() {
        inputHandler = new OpcionesInputHandler();
        InputMultiplexer im = new InputMultiplexer(renderMenuOpciones.getStage(), inputHandler);
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(inputHandler);
    }

    @Override
    public void render(float delta) {
        renderMenuOpciones.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        renderMenuOpciones.resize(width, height);
    }

    @Override
    public void dispose() {
        renderMenuOpciones.dispose();
        Controllers.removeListener(inputHandler);
    }

    private void volverAlMenuPrincipal() {
        Controllers.removeListener(inputHandler);
        Gdx.input.setInputProcessor(null);
        game.setScreen(new MenuPrincipal(game));
    }

    private class OpcionesInputHandler extends com.badlogic.gdx.InputAdapter
        implements com.badlogic.gdx.controllers.ControllerListener {

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case com.badlogic.gdx.Input.Keys.ESCAPE:
                case com.badlogic.gdx.Input.Keys.BACKSPACE:
                    volverAlMenuPrincipal();
                    return true;
            }
            return false;
        }

        @Override public void connected(com.badlogic.gdx.controllers.Controller controller) {}
        @Override public void disconnected(com.badlogic.gdx.controllers.Controller controller) {}
        @Override
        public boolean buttonDown(com.badlogic.gdx.controllers.Controller controller, int buttonIndex) {
            if (buttonIndex == 1) {
                volverAlMenuPrincipal();
                return true;
            }
            return false;
        }
        @Override public boolean buttonUp(com.badlogic.gdx.controllers.Controller controller, int buttonIndex) { return false; }
        @Override public boolean axisMoved(com.badlogic.gdx.controllers.Controller controller, int axisIndex, float value) { return false; }
    }
}

package com.sticklike.core.pantallas.menus.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.sticklike.core.MainGame;
import com.sticklike.core.pantallas.menus.InputsMenu;
import com.sticklike.core.pantallas.menus.renders.RenderMenuOpciones;

public class MenuOpciones extends ScreenAdapter {
    private MainGame game;
    private RenderMenuOpciones renderMenuOpciones;
    private InputsMenu inputsMenu;

    public MenuOpciones(MainGame game) {
        this.game = game;
        renderMenuOpciones = new RenderMenuOpciones();

        renderMenuOpciones.setMenuOpcionesListener(new RenderMenuOpciones.MenuOpcionesListener() {
            @Override
            public void onVolver() {
                volverAlMenuPrincipal();
            }
        });

        // Se utiliza InputsMenu para capturar el botón BACK (teclado o gamepad)
        inputsMenu = new InputsMenu(new InputsMenu.MenuInputListener() {
            @Override
            public void onNavigateUp() { }
            @Override
            public void onNavigateDown() { }
            @Override
            public void onSelect() { }
            @Override
            public void onBack() {
                volverAlMenuPrincipal();
            }
        });

        InputMultiplexer im = new InputMultiplexer(renderMenuOpciones.getStage(), inputsMenu);
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(inputsMenu);
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
        Controllers.removeListener(inputsMenu);
    }

    private void volverAlMenuPrincipal() {
        Controllers.removeListener(inputsMenu);
        Gdx.input.setInputProcessor(null);
        // Llamamos a la animación de salida y, al finalizar, cambiamos de pantalla
        renderMenuOpciones.animarSalida(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new MenuPrincipal(game));
            }
        });
    }
}

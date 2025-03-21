package com.sticklike.core.pantallas.menus.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controllers;
import com.sticklike.core.MainGame;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.pantallas.menus.InputsMenu;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.pantallas.menus.renders.RenderBaseMenuPrincipal;

public class MenuPrincipal extends ScreenAdapter {
    private MainGame game;
    private RenderBaseMenuPrincipal renderMenu;
    private InputsMenu inputsMenu;

    public MenuPrincipal(MainGame game) {
        this.game = game;
        renderMenu = new RenderBaseMenuPrincipal();
        // Configuramos el listener para el render
        renderMenu.setMenuListener(new RenderBaseMenuPrincipal.MenuListener() {
            @Override
            public void onSelectButton(int index) {
                ejecutarAccion(index);
            }
        });

        GestorDeAudio.getInstance().detenerMusica();

        // Instanciamos InputsMenu con su listener para gestionar la navegación
        inputsMenu = new InputsMenu(new InputsMenu.MenuInputListener() {
            @Override
            public void onNavigateUp() {
                renderMenu.decrementSelectedIndex();
            }

            @Override
            public void onNavigateDown() {
                renderMenu.incrementSelectedIndex();
            }

            @Override
            public void onSelect() {
                int index = renderMenu.getSelectedIndex();
                ejecutarAccion(index);
            }

            @Override
            public void onBack() {
            }

            @Override
            public void onPauseToggle() {

            }
        });

        InputMultiplexer im = new InputMultiplexer(renderMenu.getStage(), inputsMenu);
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(inputsMenu);
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
        Controllers.removeListener(inputsMenu);
    }

    private void ejecutarAccion(int index) {
        switch(index) {
            case 0: // Jugar
                GestorDeAudio.getInstance().detenerMusica();
                Controllers.removeListener(inputsMenu);
                Gdx.input.setInputProcessor(null);
                game.ventanaJuego1 = new VentanaJuego1(game, VentanaJuego1.worldWidth, VentanaJuego1.worldHeight);
                game.setScreen(game.ventanaJuego1);
                break;
            case 1: // Niveles
                Controllers.removeListener(inputsMenu);
                Gdx.input.setInputProcessor(null);
                game.setScreen(new MenuNiveles(game));
                break;
            case 2: // Personaje
                // Implementar acción de Personaje
                break;
            case 3: // Opciones
                Controllers.removeListener(inputsMenu);
                Gdx.input.setInputProcessor(null);
                game.setScreen(new MenuOpciones(game));
                break;
            case 4: // Logros
                // Implementar acción de Logros
                break;
            case 5: // Créditos
                GestorDeAudio.getInstance().detenerMusica();
                Controllers.removeListener(inputsMenu);
                Gdx.input.setInputProcessor(null);
                game.setScreen(new Creditos(game));
                break;
            case 6: // Salir
                Controllers.removeListener(inputsMenu);
                Gdx.input.setInputProcessor(null);
                Gdx.app.exit();
                break;
        }
    }
}

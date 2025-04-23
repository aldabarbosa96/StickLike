package com.sticklike.core.pantallas.menus.ventanas;

import com.badlogic.gdx.Application;
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

        renderMenu.setMenuListener(new RenderBaseMenuPrincipal.MenuListener() {
            @Override
            public void onSelectButton(int index) {
                ejecutarAccion(index);
            }
        });

        GestorDeAudio.getInstance().detenerMusica();

        // Se instancia InputsMenu y se le asigna su listener
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
                ejecutarAccion(renderMenu.getSelectedIndex());
            }

            @Override
            public void onBack() {
                // Acción para botón de volver, si es necesaria
            }

            @Override
            public void onPauseToggle() {
                // Acción para pausar/continuar, si es necesaria
            }
        });

        InputMultiplexer im = new InputMultiplexer(renderMenu.getStage(), inputsMenu);
        Gdx.input.setInputProcessor(im);

        // Solo se agrega el listener de controladores si no estamos en Android
        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            Controllers.addListener(inputsMenu);
        }
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
        // Removemos el listener solo si fue agregado
        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            Controllers.removeListener(inputsMenu);
        }
    }

    private void ejecutarAccion(int index) {
        switch(index) {
            case 0: // Jugar
                GestorDeAudio.getInstance().detenerMusica();
                if (Gdx.app.getType() != Application.ApplicationType.Android) {
                    Controllers.removeListener(inputsMenu);
                }
                Gdx.input.setInputProcessor(null);
                game.ventanaJuego1 = new VentanaJuego1(game, VentanaJuego1.worldWidth, VentanaJuego1.worldHeight);
                game.setScreen(game.ventanaJuego1);
                break;
            case 1: // Niveles
                if (Gdx.app.getType() != Application.ApplicationType.Android) {
                    Controllers.removeListener(inputsMenu);
                }
                Gdx.input.setInputProcessor(null);
                game.setScreen(new MenuNiveles(game));
                break;
            case 2: // Personaje
                if (Gdx.app.getType() != Application.ApplicationType.Android) {
                    Controllers.removeListener(inputsMenu);
                }
                Gdx.input.setInputProcessor(null);
                game.setScreen(new MenuPersonaje(game));
                break;
            case 3: // Opciones
                if (Gdx.app.getType() != Application.ApplicationType.Android) {
                    Controllers.removeListener(inputsMenu);
                }
                Gdx.input.setInputProcessor(null);
                game.setScreen(new MenuOpciones(game));
                break;
            case 4: // Logros
                // Implementar acción de Logros si es necesario
                break;
            case 5: // Créditos
                GestorDeAudio.getInstance().detenerMusica();
                if (Gdx.app.getType() != Application.ApplicationType.Android) {
                    Controllers.removeListener(inputsMenu);
                }
                Gdx.input.setInputProcessor(null);
                game.setScreen(new Creditos(game));
                break;
            case 6: // Salir
                if (Gdx.app.getType() != Application.ApplicationType.Android) {
                    Controllers.removeListener(inputsMenu);
                }
                Gdx.input.setInputProcessor(null);
                Gdx.app.exit();
                break;
        }
    }
}

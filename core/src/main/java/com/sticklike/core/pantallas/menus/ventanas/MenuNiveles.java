package com.sticklike.core.pantallas.menus.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.sticklike.core.MainGame;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.pantallas.menus.InputsMenu;
import com.sticklike.core.pantallas.menus.renders.RenderBaseMenuNiveles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

public class MenuNiveles extends ScreenAdapter {
    private MainGame game;
    private RenderBaseMenuNiveles renderMenu;
    private InputsMenu inputsMenu;

    public MenuNiveles(MainGame game) {
        this.game = game;
        renderMenu = new RenderBaseMenuNiveles();
        renderMenu.setMenuNivelesListener(new RenderBaseMenuNiveles.MenuNivelesListener() {
            @Override
            public void onSelectNivel1() {
                GestorDeAudio.getInstance().detenerMusica();
                renderMenu.animarSalida(new Runnable() {
                    @Override
                    public void run() {
                        game.ventanaJuego1 = new VentanaJuego1(game, VentanaJuego1.worldWidth, VentanaJuego1.worldHeight);
                        game.setScreen(game.ventanaJuego1);
                    }
                });
            }

            @Override
            public void onSelectNivel2() {
            }

            @Override
            public void onSelectNivel3() {
            }

            @Override
            public void onSelectNivel4() {
            }

            @Override
            public void onSelectNivel5() {
            }

            @Override
            public void onBack() {
                renderMenu.animarSalida(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MenuPrincipal(game));
                    }
                });
            }
        });

        // Configurar InputsMenu para gestionar teclado, ratón y mando
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
                renderMenu.activateSelectedButton();
            }

            @Override
            public void onBack() {
                renderMenu.animarSalida(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MenuPrincipal(game));
                    }
                });
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
}

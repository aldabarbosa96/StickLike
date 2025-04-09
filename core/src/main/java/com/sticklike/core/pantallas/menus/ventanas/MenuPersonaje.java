package com.sticklike.core.pantallas.menus.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controllers;
import com.sticklike.core.MainGame;
import com.sticklike.core.pantallas.menus.InputsMenu;
import com.sticklike.core.pantallas.menus.renders.RenderBaseMenuPersonaje;

public class MenuPersonaje extends ScreenAdapter {
    private MainGame game;
    private RenderBaseMenuPersonaje renderMenu;
    private InputsMenu inputsMenu;

    public MenuPersonaje(MainGame game) {
        this.game = game;
        renderMenu = new RenderBaseMenuPersonaje();

        inputsMenu = new InputsMenu(new InputsMenu.MenuInputListener() {
            @Override
            public void onNavigateUp() { }
            @Override
            public void onNavigateDown() { }
            @Override
            public void onSelect() { }
            @Override
            public void onBack() {
                game.setScreen(new MenuPrincipal(game));
            }
            @Override
            public void onPauseToggle() { }
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

package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.screens.GameScreen;
import com.sticklike.core.utils.AssetLoader;

public class MainGame extends Game {

    @Override
    public void create() {
        AssetLoader.load(); // Cargamos los assets al iniciar
        setScreen(new GameScreen()); // Asignamos la pantalla a visualizar
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
    }
}

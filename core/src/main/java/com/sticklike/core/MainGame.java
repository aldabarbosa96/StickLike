package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.sticklike.core.screens.GameScreen;
import com.sticklike.core.utils.AssetLoader;

public class MainGame extends Game {
    public GameScreen gameScreen;

    @Override
    public void create() {
        AssetLoader.load(); // Cargamos los assets al iniciar
        gameScreen = new GameScreen(this); // Pasamos MainGame a GameScreen
        setScreen(gameScreen); // Asignamos la pantalla a visualizar
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
        gameScreen.dispose();
    }
}

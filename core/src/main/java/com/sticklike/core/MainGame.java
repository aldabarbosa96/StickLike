package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.screens.GameScreen;

public class MainGame extends Game {
    public SpriteBatch spriteBatch;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Generamos un objeto para dibujar elementos en pantalla
        setScreen(new GameScreen()); // Asignamos la pantalla a visualizar
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        super.dispose();
    }
}

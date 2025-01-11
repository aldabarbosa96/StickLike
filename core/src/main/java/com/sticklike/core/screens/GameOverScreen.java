package com.sticklike.core.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOverScreen implements Screen {
    private final Game game;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private GlyphLayout layout;
    private OrthographicCamera camera;
    private FitViewport viewport;

    private static final float VIRTUAL_WIDTH = 1080;
    private static final float VIRTUAL_HEIGHT = 720;

    public GameOverScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();

        font.getData().setScale(2f);
        String gameOverText = "GAME OVER";
        layout.setText(font, gameOverText);
        float textWidth = layout.width;
        float textHeight = layout.height;

        float textX = (VIRTUAL_WIDTH - textWidth) / 2f;
        float textY = (VIRTUAL_HEIGHT + textHeight) / 2f;
        font.draw(spriteBatch, gameOverText, textX, textY);

        font.getData().setScale(1f);
        String optionsText = "Press R to Restart or Q to Quit";
        layout.setText(font, optionsText);

        textWidth = layout.width;
        textHeight = layout.height;

        textX = (VIRTUAL_WIDTH - textWidth) / 2f;
        textY -= 60;
        font.draw(spriteBatch, optionsText, textX, textY);

        spriteBatch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            game.setScreen(new GameScreen());
        } else if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
    }
}

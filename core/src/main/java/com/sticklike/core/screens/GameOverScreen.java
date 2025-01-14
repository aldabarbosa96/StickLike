package com.sticklike.core.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.MainGame;
import com.sticklike.core.utils.GameConfig;

public class GameOverScreen implements Screen {
    private final MainGame game;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private GlyphLayout layout;
    private OrthographicCamera camera;
    private FitViewport viewport;

    private static final float VIRTUAL_WIDTH = GameConfig.VIRTUAL_WIDTH;
    private static final float VIRTUAL_HEIGHT = GameConfig.VIRTUAL_HEIGHT;

    public GameOverScreen(MainGame game) {
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

        // Establecemos el InputProcessor para manejar las entradas
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.R) {
                    handleRestart();
                } else if (keycode == Input.Keys.Q) {
                    handleQuit();
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();

        font.getData().setScale(2f);
        String gameOverText = "G A M E  O V E R";
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
    }

    private void handleRestart() {
        game.gameScreen.dispose();
        game.gameScreen = new GameScreen(game); // Crear una nueva instancia de GameScreen
        game.setScreen(game.gameScreen);
    }

    private void handleQuit() {
        Gdx.app.exit();
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
    public void hide() {
        spriteBatch.dispose();
        font.dispose();

        // Limpiamos el InputProcessor al ocultar la pantalla
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
    }
}

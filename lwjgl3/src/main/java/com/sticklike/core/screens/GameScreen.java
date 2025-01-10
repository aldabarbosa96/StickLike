package com.sticklike.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.sticklike.core.entities.Enemy;
import com.sticklike.core.entities.Player;

public class GameScreen implements Screen {
    public static final int WORLD_WIDTH = 1080;
    public static final int WORLD_HEIGHT = 720;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private FillViewport viewport;
    private int cellSize = 38;
    private Player player;
    private Enemy enemy;

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();

        player = new Player();
        enemy = new Enemy(700, 550);


        player.setEnemyTarget(enemy);

        camera.position.set(
            player.getSprite().getX() + player.getSprite().getWidth() / 2,
            player.getSprite().getY() + player.getSprite().getHeight() / 2,
            0
        );
        camera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.updatePlayer(delta);

        camera.position.set(
            player.getSprite().getX() + player.getSprite().getWidth() / 2,
            player.getSprite().getY() + player.getSprite().getHeight() / 2,
            0
        );
        camera.update();

        // Configuramos las matrices de proyección
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line); //cuadrícula azul que representa el cuaderno
        shapeRenderer.setColor(0.64f, 0.80f, 0.86f, 1);



        float startX = camera.position.x - (WORLD_WIDTH / 2f);
        float endX = camera.position.x + (WORLD_WIDTH / 2f);
        float startY = camera.position.y - (WORLD_HEIGHT / 2f);
        float endY = camera.position.y + (WORLD_HEIGHT / 2f);

        for (float x = startX - (startX % cellSize); x <= endX; x += cellSize) {
            shapeRenderer.line(x, startY, x, endY);
        }
        for (float y = startY - (startY % cellSize); y <= endY; y += cellSize) {
            shapeRenderer.line(startX, y, endX, y);
        }

        shapeRenderer.end();

        spriteBatch.begin();
        enemy.render(spriteBatch);
        player.renderPlayer(spriteBatch);
        spriteBatch.end();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
    }
}

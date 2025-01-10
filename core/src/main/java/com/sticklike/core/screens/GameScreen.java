package com.sticklike.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.sticklike.core.entities.InGameText;
import com.sticklike.core.entities.Player;
import com.sticklike.core.managers.EnemyManager;

public class GameScreen implements Screen {
    public static final int WORLD_WIDTH = 1080;
    public static final int WORLD_HEIGHT = 720;

    private ShapeRenderer shapeRenderer; //gestiona la cuadrícula del cuaderno (mapa)
    private SpriteBatch spriteBatch; //para renderizar el player, enemies, etc
    private OrthographicCamera camera;
    private FillViewport viewport; //para encuadrar y mantener la proporción al redimensionar
    private int cellSize = 38;
    private Player player;
    private Array<InGameText> dmgText;
    private EnemyManager enemyManager;

    @Override
    public void show() {

        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        dmgText = new Array<>();
        camera = new OrthographicCamera();
        viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        player = new Player();
        enemyManager = new EnemyManager(player, 1.5f);

        // Asignamos el EnemyManager al Player.
        player.setEnemyManager(enemyManager);

        // Establece la posición inicial de la cámara centrada en el jugador.
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

        // Actualiza la lógica del jugador y del EnemyManager.
        player.updatePlayer(delta, dmgText);
        enemyManager.update(delta);


        // Actualizamos y renderizamos los textos flotantes.
        for (int i = dmgText.size - 1; i >= 0; i--) {
            InGameText floatingText = dmgText.get(i);
            floatingText.update(delta);
            if (floatingText.isExpired()) {
                dmgText.removeIndex(i);
            }
        }

        // Actualiza la posición de la cámara para que siga al jugador.
        camera.position.set(
            player.getSprite().getX() + player.getSprite().getWidth() / 2,
            player.getSprite().getY() + player.getSprite().getHeight() / 2,
            0
        );
        camera.update();

        // Configura las matrices de proyección para renderizar los elementos.
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.86f, 1);

        // Dibuja la cuadrícula.
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

        // Renderizamos sprites del jugador, enemigos y textos flotantes.
        spriteBatch.begin();
        enemyManager.render(spriteBatch);
        player.renderPlayer(spriteBatch);
        for (InGameText floatingText : dmgText) {floatingText.render(spriteBatch);}
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        player.dispose();
        for (InGameText floatingText : dmgText) {
            floatingText.dispose();
        }
        enemyManager.dispose();
    }
}

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
import com.sticklike.core.renderers.GridRenderer;
import com.sticklike.core.ui.HUD;

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
    private HUD hud;
    private GridRenderer gridRenderer;


    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        dmgText = new Array<>();
        camera = new OrthographicCamera();
        viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        player = new Player(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        enemyManager = new EnemyManager(player, 1.5f);
        hud = new HUD(player);
        gridRenderer = new GridRenderer(38);

        player.setEnemyManager(enemyManager);

        updateCameraPosition();
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

        updateCameraPosition();

        gridRenderer.render(camera);

        // Renderizamos todos los sprites (player, enemigos y texto flotante).
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        enemyManager.render(spriteBatch);
        player.renderPlayerAndProjectile(spriteBatch);
        for (InGameText floatingText : dmgText) { floatingText.render(spriteBatch); }
        spriteBatch.end();

        hud.renderHUD(spriteBatch);
    }


    public void updateCameraPosition(){
        camera.position.set(
            player.getSprite().getX() + player.getSprite().getWidth() / 2,
            player.getSprite().getY() + player.getSprite().getHeight() / 2,
            0
        );
        camera.update();
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

    public Player getPlayer() {
        return player;
    }
}

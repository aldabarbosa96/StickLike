package com.sticklike.core.screens;

import com.badlogic.gdx.Game;
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
    private static final float CAMERA_OFFSET_Y = -65f;

    private ShapeRenderer shapeRenderer; // Para renderizar la cuadrícula (mapa) y el HUD
    private SpriteBatch spriteBatch; // Para renderizar sprites (player, enemigos, etc)
    private OrthographicCamera camera;
    private FillViewport viewport;
    private Player player;
    private Array<InGameText> dmgText;
    private EnemyManager enemyManager;
    private HUD hud;
    private GridRenderer gridRenderer;

    @Override
    public void show() { //espero que el orden de instancia sea el adecuado en todos los casos...
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();

        float playerStartX = WORLD_WIDTH / 2f;
        float playerStartY = WORLD_HEIGHT / 2f + 125f;
        player = new Player(playerStartX, playerStartY);
        enemyManager = new EnemyManager(player, 1.5f);
        player.setEnemyManager(enemyManager);

        gridRenderer = new GridRenderer(38);
        hud = new HUD(player, shapeRenderer, spriteBatch);

        dmgText = new Array<>();

        updateCameraPosition();
    }

    @Override
    public void render(float delta) {
        if (player.isDead()) {
            renderGameOverScreen();
            return;
        }
        // Clear de pantalla y actualizado de entidades
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.updatePlayer(delta, dmgText);
        enemyManager.update(delta);
        updateFloatingTexts(delta);

        // Renderizado de la cuadrícula principal
        updateCameraPosition();
        gridRenderer.render(camera);

        // Renderizado de sprites
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        enemyManager.render(spriteBatch);
        player.renderPlayerAndProjectile(spriteBatch);
        renderFloatingTexts(spriteBatch);
        spriteBatch.end();

        hud.renderStaticHUD();
    }

    private void renderGameOverScreen() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(new GameOverScreen((Game) Gdx.app.getApplicationListener()));
    }

    private void updateCameraPosition() {
        camera.position.set(
            player.getSprite().getX() + player.getSprite().getWidth() / 2,
            player.getSprite().getY() + player.getSprite().getHeight() / 2 + CAMERA_OFFSET_Y,
            0
        );
        camera.update();
    }

    private void updateFloatingTexts(float delta) {
        for (int i = dmgText.size - 1; i >= 0; i--) {
            InGameText floatingText = dmgText.get(i);
            floatingText.update(delta);
            if (floatingText.isExpired()) {
                dmgText.removeIndex(i);
            }
        }
    }

    private void renderFloatingTexts(SpriteBatch batch) {
        for (InGameText floatingText : dmgText) {
            floatingText.render(batch);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hud.resize(width, height);
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

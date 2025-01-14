package com.sticklike.core.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.sticklike.core.MainGame;
import com.sticklike.core.entities.Enemy;
import com.sticklike.core.entities.InGameText;
import com.sticklike.core.entities.Player;
import com.sticklike.core.entities.XPobjects;
import com.sticklike.core.managers.EnemyManager;
import com.sticklike.core.managers.UpgradeManager;
import com.sticklike.core.renderers.GridRenderer;
import com.sticklike.core.systems.LevelingSystem;
import com.sticklike.core.ui.HUD;
import com.sticklike.core.utils.GameConfig;

public class GameScreen implements Screen {
    public static final int WORLD_WIDTH = (int) GameConfig.VIRTUAL_WIDTH;
    public static final int WORLD_HEIGHT = (int) GameConfig.VIRTUAL_HEIGHT;
    private static final float CAMERA_OFFSET_Y = GameConfig.CAMERA_OFFSET_Y;
    private ShapeRenderer shapeRenderer; // Para renderizar la cuadrícula (mapa) y el HUD
    private SpriteBatch spriteBatch; // Para renderizar sprites (player, enemigos, etc)
    private OrthographicCamera camera;
    private FillViewport viewport;
    private Player player;
    private Array<InGameText> dmgText;
    private EnemyManager enemyManager;
    private UpgradeManager upgradeManager;
    private HUD hud;
    private GridRenderer gridRenderer;
    private LevelingSystem levelingSystem;
    private Array<XPobjects> xPobjects = new Array<>();
    private Array<Enemy> enemiesToRemove = new Array<>();
    private boolean pausado = false;
    private MainGame game;

    // Constructor modificado para aceptar MainGame
    public GameScreen(MainGame game) {
        this.game = game;

        // Inicializamos los componentes aquí
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FillViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT, camera);
        viewport.apply();

        float playerStartX = WORLD_WIDTH / 2f;
        float playerStartY = WORLD_HEIGHT / 2f + 125f;
        player = new Player(playerStartX, playerStartY);

        upgradeManager = new UpgradeManager(player, game); // Instanciamos UpgradeManager primero
        levelingSystem = new LevelingSystem(player, upgradeManager); // Luego pasamos UpgradeManager a LevelingSystem (orden correcto sino da error)

        enemyManager = new EnemyManager(player, 1.5f, this);
        player.setEnemyManager(enemyManager);

        gridRenderer = new GridRenderer((int) GameConfig.GRID_CELL_SIZE);
        hud = new HUD(player, levelingSystem, shapeRenderer, spriteBatch);

        dmgText = new Array<>();

        updateCameraPosition();
    }

    @Override
    public void show() {
        // Al mostrar GameScreen, reanudamos el juego
        pausado = false;

        // Limpiamos el InputProcessor para que no quede apuntando a UpgradeScreen
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        if (pausado) return;

        if (player.isDead()) {
            renderGameOverScreen();
            return;
        }

        // Clear de pantalla y actualizado de entidades
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.updatePlayer(delta, dmgText);
        enemyManager.update(delta);

        // Generamos los objetos de xp al morir los enemigos
        for (Enemy enemy : enemyManager.getEnemies()) {
            if (enemy.isDead() && !enemy.hasDroppedXP()) {
                XPobjects object = enemy.dropExperiencia();
                if (object != null) {
                    System.out.println("XPobject generado en: " + object.getSprite().getX() + ", " + object.getSprite().getY());
                    xPobjects.add(object);
                }
                enemy.setProcesado(true);
                enemiesToRemove.add(enemy);
            }
        }

        for (int i = xPobjects.size - 1; i >= 0; i--) {
            XPobjects object = xPobjects.get(i);
            object.update(delta);

            if (object.overlapsWith(player.getSprite())) {
                object.collect();
                xPobjects.removeIndex(i);
                levelingSystem.addExperience(20f);
            }
        }

        updateFloatingTexts(delta);

        // Renderizado de la cuadrícula principal
        updateCameraPosition();
        gridRenderer.render(camera);

        // Renderizado de sprites
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        enemyManager.render(spriteBatch);
        player.renderPlayerAndProjectile(spriteBatch);

        for (XPobjects object : xPobjects) {
            object.render(spriteBatch);
        }

        renderFloatingTexts(spriteBatch);
        spriteBatch.end();

        hud.renderStaticHUD();
    }

    private void renderGameOverScreen() {
        game.setScreen(new GameOverScreen(game)); // Pasamos la referencia de MainGame
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

    public void addXPObject(XPobjects xpObject) {
        xPobjects.add(xpObject);
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
        player.dispose();
        for (InGameText floatingText : dmgText) {
            floatingText.dispose();
        }
        enemyManager.dispose();
    }
}

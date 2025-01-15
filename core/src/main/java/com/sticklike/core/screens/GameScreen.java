package com.sticklike.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
import com.sticklike.core.upgrades.Upgrade;
import com.sticklike.core.utils.GameConfig;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import javax.swing.*;
import java.util.List;

public class GameScreen implements Screen {

    public static final int WORLD_WIDTH = (int) GameConfig.VIRTUAL_WIDTH;
    public static final int WORLD_HEIGHT = (int) GameConfig.VIRTUAL_HEIGHT;
    private static final float CAMERA_OFFSET_Y = GameConfig.CAMERA_OFFSET_Y;

    private MainGame game;

    // Render básico
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private GridRenderer gridRenderer;

    // UI Scene2D (pop-up)
    private Stage uiStage;
    private Skin uiSkin;

    // Jugador y sistemas
    private Player player;
    private EnemyManager enemyManager;
    private UpgradeManager upgradeManager;
    private LevelingSystem levelingSystem;
    private HUD hud;

    // Arrays de entidades
    private Array<InGameText> dmgText;
    private Array<XPobjects> xPobjects;
    private Array<Enemy> enemiesToRemove;

    // Control de pausa
    private boolean paused = false;

    public GameScreen(MainGame game) {
        this.game = game;

        // Render base
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        viewport = new FillViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT, camera);
        viewport.apply();

        // Stage + Skin (para pop-up)
        uiStage = new Stage(new FillViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT));
        uiSkin = createUISkin(); // Creamos un Skin con fondo simple

        // Jugador
        float playerStartX = WORLD_WIDTH / 2f;
        float playerStartY = WORLD_HEIGHT / 2f + 125f;
        player = new Player(playerStartX, playerStartY);

        // Managers
        upgradeManager = new UpgradeManager(player, game);
        levelingSystem = new LevelingSystem(player, upgradeManager);
        enemyManager = new EnemyManager(player, 1.5f, this);
        player.setEnemyManager(enemyManager);

        // Grilla (mapa) y HUD
        gridRenderer = new GridRenderer((int) GameConfig.GRID_CELL_SIZE);
        hud = new HUD(player, levelingSystem, shapeRenderer, spriteBatch);

        dmgText = new Array<>();
        xPobjects = new Array<>();
        enemiesToRemove = new Array<>();

        updateCameraPosition();
    }

    private Skin createUISkin() { // Creamos pixmap para que haga de fondo del pop-up
        // todo -> crear textura personalizada en un futuro estilo post-it para el pop-up de upgrades
        Skin skin = new Skin();

        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.97f, 0.88f, 0.6f, 1f));
        pixmap.fill();
        Texture pixmapTexture = new Texture(pixmap);
        pixmap.dispose();

        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(pixmapTexture);

        Window.WindowStyle wStyle = new Window.WindowStyle(font, Color.BLACK, backgroundDrawable);
        skin.add("default-window", wStyle);

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = font;
        skin.add("default-button", tbs);

        return skin;
    }

    @Override
    public void show() {
        paused = false;
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        if (player.isDead()) {
            game.setScreen(new GameOverScreen(game));
            return;
        }

        // Si no está pausado, actualizamos lógica
        if (!paused) {
            updateLogic(delta);
        }

        // Limpiamos
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render del mapa
        updateCameraPosition();
        gridRenderer.render(camera);

        // Render de entidades
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        enemyManager.render(spriteBatch);
        player.renderPlayerAndProjectile(spriteBatch);

        for (XPobjects xp : xPobjects) {
            xp.render(spriteBatch);
        }
        for (InGameText txt : dmgText) {
            txt.render(spriteBatch);
        }
        spriteBatch.end();

        // Render del HUD
        hud.renderStaticHUD();

        // Render del Stage (pop-up) por encima
        uiStage.act(delta);
        uiStage.draw();
    }

    private void updateLogic(float delta) {
        player.updatePlayer(delta, paused, dmgText);
        enemyManager.update(delta);

        // Manejo de enemigos muertos => XP
        for (Enemy enemy : enemyManager.getEnemies()) {
            if (enemy.isDead() && !enemy.hasDroppedXP()) {
                XPobjects xp = enemy.dropExperiencia();
                if (xp != null) {
                    xPobjects.add(xp);
                }
                enemy.setProcesado(true);
                enemiesToRemove.add(enemy);
            }
        }
        for (Enemy e : enemiesToRemove) {
            enemyManager.getEnemies().removeValue(e, true);
        }
        enemiesToRemove.clear();

        // Recoger XP
        for (int i = xPobjects.size - 1; i >= 0; i--) {
            XPobjects xp = xPobjects.get(i);
            xp.update(delta);

            if (xp.overlapsWith(player.getSprite())) {
                xp.collect();
                xPobjects.removeIndex(i);
                levelingSystem.addExperience(20f);
            }
        }

        // Actualizar textos flotantes
        for (int i = dmgText.size - 1; i >= 0; i--) {
            InGameText floatingText = dmgText.get(i);
            floatingText.update(delta);
            if (floatingText.isExpired()) {
                dmgText.removeIndex(i);
            }
        }
    }

    private void updateCameraPosition() {
        camera.position.set(
            player.getSprite().getX() + player.getSprite().getWidth() / 2,
            player.getSprite().getY() + player.getSprite().getHeight() / 2 + CAMERA_OFFSET_Y,
            0
        );
        camera.update();
    }

    public void showUpgradePopup(final List<Upgrade> upgrades) {
        paused = true;

        // Creamos la ventana con estilo
        Window.WindowStyle wStyle = uiSkin.get("default-window", Window.WindowStyle.class);
        final Window upgradeWindow = new Window("\n\nU P G R A D E S", wStyle);
        upgradeWindow.getTitleLabel().setAlignment(Align.center);

        float w = 400;
        float h = 350;

        upgradeWindow.setSize(w, h);
        upgradeWindow.setPosition(
            (GameConfig.VIRTUAL_WIDTH - w) / 2f,
            (GameConfig.VIRTUAL_HEIGHT - h + 150f) / 2f
        );

        upgradeWindow.padTop(75f);
        upgradeWindow.setModal(true);
        upgradeWindow.setMovable(false);

        for (int i = 0; i < upgrades.size(); i++) {
            final int index = i;
            final Upgrade upgrade = upgrades.get(i);

            TextButton.TextButtonStyle tbs = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
            TextButton btn = new TextButton((i + 1) + ") " + upgrade.getName() + " --> " + upgrade.getDescription(), tbs);

            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.left);
            btn.getLabel().setColor(Color.BLACK);

            upgradeWindow.row().pad(0);
            upgradeWindow.add(btn).width(350).pad(10);
        }

        upgradeWindow.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.NUM_1) {
                    selectUpgrade(0, upgrades, upgradeWindow);
                    return true;
                } else if (keycode == Input.Keys.NUM_2) {
                    selectUpgrade(1, upgrades, upgradeWindow);
                    return true;
                } else if (keycode == Input.Keys.NUM_3) {
                    selectUpgrade(2, upgrades, upgradeWindow);
                    return true;
                }
                return false;
            }
        });

        uiStage.addActor(upgradeWindow);

        // Foco de teclado
        uiStage.setKeyboardFocus(upgradeWindow);

        // Stage procesa input (para que se detecten las teclas)
        InputMultiplexer im = new InputMultiplexer(uiStage);
        Gdx.input.setInputProcessor(im);
    }

    private void selectUpgrade(int index, List<Upgrade> upgrades, Window upgradeWindow) {
        if (index < 0 || index >= upgrades.size()) return;
        upgradeManager.applyUpgrade(upgrades.get(index));
        upgradeWindow.remove();
        paused = false;
        Gdx.input.setInputProcessor(null);
    }

    public void addXPObject(XPobjects xpObject) {
        xPobjects.add(xpObject);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiStage.getViewport().update(width, height, true);
        hud.resize(width, height);
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
        spriteBatch.dispose();
        shapeRenderer.dispose();
        gridRenderer.dispose();
        uiStage.dispose();
        uiSkin.dispose();

        if (player != null) {
            player.dispose();
        }
        if (enemyManager != null) {
            enemyManager.dispose();
        }

        for (InGameText ft : dmgText) {
            ft.dispose();
        }
        dmgText.clear();

        for (XPobjects xp : xPobjects) {
            xp.dispose();
        }
        xPobjects.clear();
    }
}

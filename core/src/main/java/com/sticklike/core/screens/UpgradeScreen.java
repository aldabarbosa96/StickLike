package com.sticklike.core.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sticklike.core.managers.UpgradeManager;
import com.sticklike.core.upgrades.Upgrade;
import com.sticklike.core.utils.GameConfig;

import java.util.List;

public class UpgradeScreen extends ScreenAdapter {
    private final UpgradeManager upgradeManager;
    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final BitmapFont font;
    private boolean upgradeSeleccionado;
    private List<Upgrade> upgradeOptions;

    public UpgradeScreen(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;

        // Inicializamos la cámara y el Viewport
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT);
        this.viewport = new FitViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT, camera);
        this.viewport.apply();

        // Inicializamos otros componentes
        this.spriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.upgradeOptions = upgradeManager.generateUpgradeOptions(3);
        this.upgradeSeleccionado = false;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.NUM_1) {
                    handleInput(1);
                } else if (keycode == Input.Keys.NUM_2) {
                    handleInput(2);
                } else if (keycode == Input.Keys.NUM_3) {
                    handleInput(3);
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        // Limpieza de pantalla con un color claro para confirmar que se renderiza algo
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.5f, 1); // Azul oscuro para distinguir
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();

        renderUpgradeOptions();
    }

    private void renderUpgradeOptions() {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        float startY = GameConfig.VIRTUAL_HEIGHT / 2f + 60f; // Comienza en el centro superior
        float stepY = 50f; // Espaciado entre las opciones

        for (int i = 0; i < upgradeOptions.size(); i++) {
            Upgrade upgrade = upgradeOptions.get(i);

            String optionText = (i + 1) + ". " + upgrade.getName() + " - " + upgrade.getDescription();

            float textX = GameConfig.VIRTUAL_WIDTH / 2f - 150f; // Centrado horizontalmente
            float textY = startY - (i * stepY);

            font.setColor(Color.WHITE);
            font.getData().setScale(1.2f);
            font.draw(spriteBatch, optionText, textX, textY);
        }

        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }

    public void handleInput(int selectedOption) {

        if (selectedOption < 1 || selectedOption > upgradeOptions.size()) {
            return;
        }

        Upgrade selectedUpgrade = upgradeOptions.get(selectedOption - 1);

        try {
            upgradeManager.applyUpgrade(selectedUpgrade);
            upgradeSeleccionado = true;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Cambiar de nuevo a la pantalla del juego
        try {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

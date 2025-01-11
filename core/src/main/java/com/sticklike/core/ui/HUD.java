package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.sticklike.core.entities.Player;
import com.sticklike.core.utils.AssetLoader;

public class HUD {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;
    private final Player player;
    private final Texture heartTexture;
    private final OrthographicCamera hudCamera;
    private final FillViewport hudViewport;
    private final BitmapFont font;
    private final GlyphLayout layout; // Para calcular el tamaño del texto

    private static final float VIRTUAL_WIDTH = 1080;
    private static final float VIRTUAL_HEIGHT = 720;

    public HUD(Player player, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        this.player = player;
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
        this.heartTexture = AssetLoader.life;

        this.font = new BitmapFont();
        this.layout = new GlyphLayout();

        // Configuramos la cámara y el viewport
        this.hudCamera = new OrthographicCamera();
        this.hudViewport = new FillViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, hudCamera);
        this.hudCamera.update();
    }

    public void renderStaticHUD() {
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);

        float hudHeight = 200f;
        float screenWidth = VIRTUAL_WIDTH;

        // Fondo del HUD
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.99f, 0.9735f, 0.863f, 1);
        shapeRenderer.rect(0, 0, screenWidth, hudHeight);

        // Línea divisoria superior
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(0, hudHeight, screenWidth, 1);

        shapeRenderer.end();

        renderGrid(hudHeight);
        renderHealthBar(hudHeight);
        renderHealthText(hudHeight);
        renderHeartIcon(hudHeight);
    }

    private void renderGrid(float hudHeight) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.86f, 1);

        float screenWidth = VIRTUAL_WIDTH;
        float cellSize = 38f;

        for (float y = 0; y <= hudHeight; y += cellSize) {
            shapeRenderer.line(0, y, screenWidth, y);
        }

        shapeRenderer.end();
    }

    private void renderHealthBar(float hudHeight) {
        float healthPercentage = player.getHealthPercentage();
        float barWidth = 200f;
        float barHeight = 20f;
        float barX = 90f;
        float barY = hudHeight - barHeight - 20;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Borde negro de la barra de salud
        shapeRenderer.setColor(0, 0, 0, 1f);
        shapeRenderer.rect(barX - 1.5f, barY - 1.5f, barWidth + 3f, barHeight + 3f);

        // Fondo de la barra de salud
        shapeRenderer.setColor(0.8f, 0.1f, 0.1f, 0.5f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Barra de salud actual
        shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 0.8f);
        shapeRenderer.rect(barX, barY, barWidth * healthPercentage, barHeight);

        shapeRenderer.end();
    }

    private void renderHealthText(float hudHeight) {
        float barX = 100f;
        float barWidth = 200f;
        float barY = hudHeight - 25;

        String healthText = (int) player.getHealth() + "/" + (int) player.getMaxHealth();

        layout.setText(font, healthText);
        float textWidth = layout.width;
        float textX = barX + (barWidth - textWidth) / 2;

        spriteBatch.begin();

        font.setColor(0, 0, 0, 1);
        float offset = 1f;

        // Dibujamos el texto desplazado en las cuatro direcciones
        font.draw(spriteBatch, healthText, textX - offset, barY + offset);
        font.draw(spriteBatch, healthText, textX + offset, barY + offset);
        font.draw(spriteBatch, healthText, textX - offset, barY - offset);
        font.draw(spriteBatch, healthText, textX + offset, barY - offset);

        font.setColor(1, 1, 1, 1);
        font.draw(spriteBatch, healthText, textX, barY);

        spriteBatch.end();
    }

    private void renderHeartIcon(float hudHeight) {
        float heartSize = 25f;
        float heartX = 50f;
        float heartY = hudHeight - heartSize - 17;

        spriteBatch.begin();
        spriteBatch.draw(heartTexture, heartX, heartY, heartSize, heartSize);
        spriteBatch.end();
    }

    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
    }

    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        heartTexture.dispose();
    }
}

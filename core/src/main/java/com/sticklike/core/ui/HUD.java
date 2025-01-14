package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.sticklike.core.entities.Player;
import com.sticklike.core.systems.LevelingSystem;
import com.sticklike.core.utils.AssetLoader;
import com.sticklike.core.utils.GameConfig;

public class HUD {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;
    private final Player player;
    private final LevelingSystem levelingSystem;
    private final Texture heartTexture, xpTexture;
    private final OrthographicCamera hudCamera;
    private final FillViewport hudViewport;
    private final BitmapFont font;
    private final GlyphLayout layout; // Sirve para calcular el tamaño del texto
    private static final float VIRTUAL_WIDTH = GameConfig.VIRTUAL_WIDTH;

    public HUD(Player player, LevelingSystem levelingSystem, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        this.player = player;
        this.shapeRenderer = shapeRenderer;
        this.levelingSystem = levelingSystem;
        this.spriteBatch = spriteBatch;
        this.heartTexture = AssetLoader.life;
        this.xpTexture = AssetLoader.xpIcon;

        this.font = new BitmapFont();
        this.layout = new GlyphLayout();

        // Configuramos la cámara y el viewport
        this.hudCamera = new OrthographicCamera();
        this.hudViewport = new FillViewport(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT, hudCamera);
        this.hudCamera.update();
    }

    public void renderStaticHUD() {
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);

        float hudHeight = 200f;
        renderBackground();
        renderDivider();
        renderGrid(hudHeight);
        renderHealthBar();
        renderXPBar();
        renderHealthText(hudHeight);
        renderHeartIcon();
        renderLvlText();
    }


    private void renderBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.99f, 0.9735f, 0.863f, 1);
        shapeRenderer.rect(0, 0, GameConfig.VIRTUAL_WIDTH, GameConfig.HUD_HEIGHT);
        shapeRenderer.end();
    }

    private void renderDivider() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(0, GameConfig.HUD_HEIGHT, GameConfig.VIRTUAL_WIDTH, 1);
        shapeRenderer.end();
    }


    private void renderGrid(float hudHeight) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.86f, 1);

        float screenWidth = VIRTUAL_WIDTH;
        float cellSize = GameConfig.GRID_CELL_SIZE;

        for (float y = 0; y <= hudHeight; y += cellSize) {
            shapeRenderer.line(0, y, screenWidth, y);
        }

        shapeRenderer.end();
    }

    private void renderLvlText() {
        String levelText = "LVL :  ";
        String levelNumber = String.valueOf(levelingSystem.getLevel());

        layout.setText(font, levelText + levelNumber);

        float textX = (VIRTUAL_WIDTH - layout.width) / 2;
        float textY = GameConfig.HUD_HEIGHT - 22f;

        spriteBatch.begin();

        // Texto Lvl
        font.getData().setScale(0.95f);
        font.setColor(0, 0, 0, 1); // Blanco
        float offset = 1f;
        font.draw(spriteBatch, levelText, textX - offset, textY + offset);
        font.draw(spriteBatch, levelText, textX + offset, textY + offset);
        font.draw(spriteBatch, levelText, textX - offset, textY - offset);
        font.draw(spriteBatch, levelText, textX + offset, textY - offset);

        font.setColor(1, 1, 1, 1);
        font.draw(spriteBatch, levelText, textX, textY);

        // Coordenadas para el número
        float levelTextWidth = new GlyphLayout(font, levelText).width;
        float levelNumberX = textX + levelTextWidth;
        float textYNumber = GameConfig.HUD_HEIGHT - 17.5f;

        // Texto número
        font.getData().setScale(1.4f);
        font.setColor(0, 0, 0, 1); // Blanco
        font.draw(spriteBatch, levelNumber, levelNumberX - offset, textYNumber + offset);
        font.draw(spriteBatch, levelNumber, levelNumberX + offset, textYNumber + offset);
        font.draw(spriteBatch, levelNumber, levelNumberX - offset, textYNumber - offset);
        font.draw(spriteBatch, levelNumber, levelNumberX + offset, textYNumber - offset);

        font.setColor(1, 1, 1, 1); // Azul
        font.draw(spriteBatch, levelNumber, levelNumberX, textYNumber);

        font.getData().setScale(1.4f);

        spriteBatch.end();
    }

    private void renderHealthBar() {
        float healthPercentage = player.getHealthPercentage();
        float barWidth = GameConfig.HUD_BAR_WIDTH;
        float barHeight = GameConfig.HUD_BAR_HEIGHT;
        float barX = GameConfig.HUD_BAR_X;
        float barY = GameConfig.HUD_HEIGHT - barHeight - GameConfig.HUD_BAR_Y_OFFSET;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Borde negro de la barra de salud
        shapeRenderer.setColor(0, 0, 0, 1f);
        shapeRenderer.rect(barX - 2f, barY - 2, barWidth + 4f, barHeight + 4f);

        // Fondo de la barra de salud
        shapeRenderer.setColor(0.8f, 0.1f, 0.1f, 0.5f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        // Barra de salud actual
        shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 0.8f);
        shapeRenderer.rect(barX, barY, barWidth * healthPercentage, barHeight);

        shapeRenderer.end();
    }

    private void renderHealthText(float hudHeight) {
        float barX = 75f;
        float barWidth = 180f;
        float barY = hudHeight - 24;

        String healthText = (int) player.getHealth() + " / " + (int) player.getMaxHealth();

        layout.setText(font, healthText);
        float textWidth = layout.width;
        float textX = barX + (barWidth - textWidth) / 2;

        spriteBatch.begin();

        font.setColor(0, 0, 0, 1);
        font.getData().setScale(1.2f, 1.1f);
        float offset = 1f;

        // Dibujamos el texto desplazado en las cuatro direcciones
        font.draw(spriteBatch, healthText, textX - offset, barY + offset);
        font.draw(spriteBatch, healthText, textX + offset, barY + offset);
        font.draw(spriteBatch, healthText, textX - offset, barY - offset);
        font.draw(spriteBatch, healthText, textX + offset, barY - offset);

        font.setColor(1, 1, 1, 1);
        font.draw(spriteBatch, healthText, textX, barY);
        font.getData().setScale(1.0f);

        spriteBatch.end();
    }

    private void renderHeartIcon() {
        float heartSize = GameConfig.HEART_SIZE;
        float heartX = GameConfig.HEART_X;
        float heartY = GameConfig.HUD_HEIGHT - heartSize - GameConfig.HEART_Y_OFFSET;

        spriteBatch.begin();
        spriteBatch.draw(heartTexture, heartX, heartY, heartSize, heartSize);
        spriteBatch.end();
    }

    private void renderXPBar() {
        float barWidth = GameConfig.HUD_BAR_WIDTH;
        float barHeight = GameConfig.HUD_BAR_HEIGHT;
        float barX = GameConfig.HUD_BAR_X;
        float barY = GameConfig.HUD_HEIGHT - barHeight - GameConfig.HUD_BAR_Y_OFFSET - 38;
        float experiencePercentage = levelingSystem.getCurrentExperience() / levelingSystem.getExperienceToNextLevel();

        renderXPBarBackground(barX, barY, barWidth, barHeight, experiencePercentage);
        renderXPBarText(barX, barY -2, barWidth, barHeight);
        renderXPBarIcon(barX, barY, barHeight);
    }

    private void renderXPBarBackground(float barX, float barY, float barWidth, float barHeight, float experiencePercentage) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0, 0, 0, 1f);
        shapeRenderer.rect(barX - 2f, barY - 2f, barWidth + 4f, barHeight + 4f);

        shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 0.5f);
        shapeRenderer.rect(barX, barY, barWidth, barHeight);

        shapeRenderer.setColor(0.1f, 0.6f, 0.9f, 0.8f);
        shapeRenderer.rect(barX, barY, barWidth * experiencePercentage, barHeight);

        shapeRenderer.end();
    }

    private void renderXPBarText(float barX, float barY, float barWidth, float barHeight) {
        String experienceText = (int) levelingSystem.getCurrentExperience() + "/" + (int) levelingSystem.getExperienceToNextLevel();
        layout.setText(font, experienceText);
        float textWidth = layout.width;
        float textX = barX + (barWidth - textWidth) / 2;
        float textY = barY + (barHeight + layout.height + 1) / 2;

        spriteBatch.begin();
        font.setColor(0, 0, 0, 1);
        font.getData().setScale(1.2f, 1.1f);

        float offset = 1f;
        font.draw(spriteBatch, experienceText, textX - offset, textY + offset);
        font.draw(spriteBatch, experienceText, textX + offset, textY + offset);
        font.draw(spriteBatch, experienceText, textX - offset, textY - offset);
        font.draw(spriteBatch, experienceText, textX + offset, textY - offset);

        font.setColor(1, 1, 1, 1);
        font.draw(spriteBatch, experienceText, textX, textY);

        font.getData().setScale(1.0f);
        spriteBatch.end();
    }

    private void renderXPBarIcon(float barX, float barY, float barHeight) {
        float iconSize = GameConfig.HEART_SIZE;
        float iconX = barX - iconSize - 5f;
        float iconY = barY - 5f;

        spriteBatch.begin();
        spriteBatch.draw(xpTexture, iconX, iconY, iconSize -10, iconSize);
        spriteBatch.end();
    }


    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
    }

    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        heartTexture.dispose();
        xpTexture.dispose();
    }
}

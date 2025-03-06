package com.sticklike.core.pantallas.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sticklike.core.ui.RenderHUDComponents;

/**
 * Clase encargada de dibujar el icono y el efecto "overlay" cuando se recoge un boost.
 */

public class BoostIconEffect {
    private Texture icon;
    private float duration;
    private float timeRemaining;
    private boolean active;
    private Color effectColor;
    private float scale;
    private float x;
    private float y;
    private float iconAlpha;
    private ShapeRenderer shapeRenderer;

    public BoostIconEffect() {
        active = false;
        scale = 1f;
        effectColor = new Color(1, 1, 1, 1);
        iconAlpha = 1.0f;
        shapeRenderer = new ShapeRenderer();
    }

    public void addTime(float additionalTime) {
        this.timeRemaining += additionalTime;
        this.duration += additionalTime;
    }
    public void activate(Texture icon, Color effectColor, float duration, float x, float y, float desiredSize) {
        this.icon = icon;
        this.effectColor = new Color(effectColor);
        this.duration = duration;
        this.timeRemaining = duration;
        this.active = true;
        this.x = x;
        this.y = y;
        this.scale = desiredSize / icon.getWidth();
        this.iconAlpha = 1.0f;
    }


    public void update(float delta, RenderHUDComponents renderHUDComponents) {
        if (!active) return;
        if (!renderHUDComponents.isPausadoTemporizador()) {
            timeRemaining -= delta;
            if (timeRemaining > 5) {
                // Mantener el ícono opaco
                iconAlpha = 1.0f;
            } else {
                // Últimos 5 segundos: parpadeo del ícono
                float blinkPeriod = 0.25f; // parpadea cada 0.25 segundos
                if (((int)((5 - timeRemaining) / blinkPeriod)) % 2 == 0) {
                    iconAlpha = 1.0f;
                } else {
                    iconAlpha = 0.0f;
                }
            }
            if (timeRemaining <= 0) {
                active = false;
            }
        }
    }


    public void render(SpriteBatch batch) {
        if (!active || icon == null) return;

        // Dibujamos el fondo (overlay) con ShapeRenderer
        // Finalizamos el batch actual para usar el ShapeRenderer
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Color bgColor = new Color(effectColor);
        bgColor.a = 0.075f;
        float hudHeight = 185; // Área del HUD que no queremos cubrir
        shapeRenderer.setColor(bgColor);
        shapeRenderer.rect(0, hudHeight, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - hudHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();

        // Dibujamos el ícono encima del fondo; usamos Color.WHITE con iconAlpha para preservar los colores originales de la textura.
        batch.setColor(1, 1, 1, iconAlpha);
        float width = icon.getWidth() * scale;
        float height = icon.getHeight() * scale;
        batch.draw(icon, x - width / 2f, y - height / 2f, width, height);
        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

    public boolean isActive() {
        return active;
    }
}

package com.sticklike.core.pantallas.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sticklike.core.ui.RenderHUDComponents;

/**
 * Clase encargada de dibujar el icono y un overlay degradado cuando se recoge un boost.
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

    // Permitimos extender la duración si se recogen boosts adicionales
    public void addTime(float additionalTime) {
        this.timeRemaining += additionalTime;
        this.duration += additionalTime;
    }

    public void activate(Texture icon, Color effectColor, float duration, float x, float y, float desiredSize) {
        this.icon = icon;
        // Copiamos el color para no modificar el original
        this.effectColor = new Color(effectColor);
        this.duration = duration;
        this.timeRemaining = duration;
        this.active = true;
        this.x = x;
        this.y = y;
        // Calculamos la escala en función del ancho original de la textura
        this.scale = desiredSize / icon.getWidth();
        this.iconAlpha = 1.0f;
    }

    public void update(float delta, RenderHUDComponents renderHUDComponents) {
        if (!active) return;

        if (!renderHUDComponents.isPausadoTemporizador()) {
            timeRemaining -= delta;

            // Si faltan más de 6 segundos, no parpadea
            if (timeRemaining > 6f) {
                iconAlpha = 1.0f;
            } else {
                float blinkPeriod = (timeRemaining > 2.5f) ? 0.25f : 0.1f;
                float blinkTime = 6f - timeRemaining;
                if (((int)(blinkTime / blinkPeriod)) % 2 == 0) {
                    iconAlpha = 1.0f;
                } else {
                    iconAlpha = 0.0f;
                }
            }

            // Si se acaba el tiempo desactivamos
            if (timeRemaining <= 0) {
                active = false;
            }
        }
    }


    public void render(SpriteBatch batch) {
        if (!active || icon == null) return;

        // Terminamos el batch para poder usar shapeRenderer
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float hudHeight = 185f;

        // Definimos dos colores para el degradado
        Color topColor = new Color(effectColor.r, effectColor.g, effectColor.b, 0.20f);
        Color bottomColor = new Color(effectColor.r, effectColor.g, effectColor.b, 0.00f);

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // Dibujamos dos triángulos que cubren toda el área por debajo del hudHeight
        // 1er triángulo
        shapeRenderer.triangle(0f, hudHeight, screenW, hudHeight, 0f, screenH, topColor, topColor, bottomColor);
        // 2do triángulo
        shapeRenderer.triangle(screenW, hudHeight, screenW, screenH, 0f, screenH, topColor, bottomColor, bottomColor);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Volvemos a comenzar el batch para dibujar el ícono
        batch.begin();

        // Dibujamos el ícono con el alpha calculado (parpadeo)
        batch.setColor(1, 1, 1, iconAlpha);
        float width = icon.getWidth() * scale;
        float height = icon.getHeight() * scale;
        batch.draw(icon, x - width / 2f, y - height / 2f, width, height);

        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        shapeRenderer.dispose();
        icon = null;
    }

    public boolean isActive() {
        return active;
    }
}

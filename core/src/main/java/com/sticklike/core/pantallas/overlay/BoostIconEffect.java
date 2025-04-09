package com.sticklike.core.pantallas.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.ui.RenderHUDComponents;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Clase encargada de dibujar el icono y un overlay degradado cuando se recoge un boost.
 */
public class BoostIconEffect {
    private Texture icon;
    private float duration;
    private float timeRemaining;
    private boolean active;
    private Color effectColor;
    private Color topColor;
    private Color bottomColor;
    private float scale;
    private float x;
    private float y;
    private float iconAlpha;
    private ShapeRenderer shapeRenderer;

    public BoostIconEffect() {
        active = false;
        scale = 0.75f;
        effectColor = new Color(1, 1, 1, 1);
        iconAlpha = 1.0f;
        shapeRenderer = new ShapeRenderer();
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

        // Inicializamos colores aquí para evitar repetidas creaciones de objetos en el render
        topColor = new Color(effectColor.r, effectColor.g, effectColor.b, 0.20f);
        bottomColor = new Color(effectColor.r, effectColor.g, effectColor.b, 0.00f);
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
                iconAlpha = (((int) (blinkTime / blinkPeriod)) % 2 == 0) ? 1.0f : 0.0f;


                if (timeRemaining <= 0) {
                    active = false;
                }
            }
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        if (!active || icon == null) return;

        // Terminamos el batch para poder usar shapeRenderer
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float screenWidth = Gdx.graphics.getWidth() ;
        float screenHeight = Gdx.graphics.getHeight();

        float overlayBottom = HUD_HEIGHT + HUD_BAR_Y_OFFSET;

        shapeRenderer.rect(0, overlayBottom, screenWidth, screenHeight - overlayBottom, bottomColor, bottomColor, topColor, topColor);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Volvemos a comenzar el batch para dibujar el ícono
        batch.begin();

        batch.setColor(1, 1, 1, iconAlpha);
        float width = icon.getWidth() * scale;
        float height = icon.getHeight() * scale;
        batch.draw(icon, x - width / 2f, y - height / 2f, width, height);
        batch.setColor(Color.WHITE);
    }

    // Permitimos extender la duración si se recogen boosts adicionales
    public void addTime(float additionalTime) {
        this.timeRemaining += additionalTime;
        this.duration += additionalTime;
    }
    public void updateDimensions(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
    }


    public void dispose() {
        shapeRenderer.dispose();
        //icon = null; todo --> (parece que sí ha resuelto el problema del icono null al reiniciar)
    }

    public boolean isActive() {
        return active;
    }
}

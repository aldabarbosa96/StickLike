package com.sticklike.core.pantallas.gameOver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoPowerUp;
import com.sticklike.core.pantallas.juego.VentanaJuego1;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Renderiza la pantalla de Game Over.
 * Muestra animaciones de texto y estadísticas de la partida al finalizar el juego.
 */

public class RenderVentanaGameOver {
    private float sceneTime = 0f;

    private final float FADE_IN_DURATION = 0.8f;
    private final float WAIT_CENTER_TIME = 0.4f;
    private final float MOVE_DURATION = 0.8f;
    private final float DELAY_STATS_TIME = 0.4f;

    private final float GAMEOVER_CENTER_Y = VIRTUAL_HEIGHT / 2f + 50f;
    private final float GAMEOVER_TOP_Y = VIRTUAL_HEIGHT - 125;

    public RenderVentanaGameOver() {
        this.sceneTime = 0f;
    }

    public void renderizarVentanaGameOver(FitViewport viewport, OrthographicCamera camera, SpriteBatch spriteBatch, GlyphLayout layout, BitmapFont font, VentanaJuego1 ventanaJuego1) {

        Gdx.gl.glClearColor(0.125f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();

        float delta = Gdx.graphics.getDeltaTime();
        sceneTime += delta;

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        dibujarTextoGameOver(layout, font, spriteBatch);
        dibujarRestoTextos(layout, font, spriteBatch, ventanaJuego1);

        spriteBatch.end();
    }

    private void dibujarTextoGameOver(GlyphLayout layout, BitmapFont font, SpriteBatch spriteBatch) {

        float gameOverAlpha = Math.min(1f, sceneTime / FADE_IN_DURATION);
        float gameOverY;
        float totalWaitTime = FADE_IN_DURATION + WAIT_CENTER_TIME;

        if (sceneTime < totalWaitTime) {
            gameOverY = GAMEOVER_CENTER_Y;
        } else if (sceneTime < totalWaitTime + MOVE_DURATION) {
            float elapsedMove = sceneTime - totalWaitTime;
            float t = elapsedMove / MOVE_DURATION;
            gameOverY = lerp(GAMEOVER_CENTER_Y, GAMEOVER_TOP_Y, t);
        } else {
            gameOverY = GAMEOVER_TOP_Y;
        }

        float originalScale = font.getData().scaleX;

        font.getData().setScale(4f);
        font.setColor(1, 0.25f, 0.35f, gameOverAlpha);

        String gameOverText = GAMEOVER;
        layout.setText(font, gameOverText);
        float textWidth = layout.width;
        float textX = (VIRTUAL_WIDTH - textWidth) / 2f;

        font.draw(spriteBatch, gameOverText, textX, gameOverY);

        font.getData().setScale(originalScale);
        font.setColor(1, 1, 1, 1f);
    }

    private void dibujarRestoTextos(GlyphLayout layout, BitmapFont font, SpriteBatch spriteBatch, VentanaJuego1 ventanaJuego1) {
        float totalWaitTime = FADE_IN_DURATION + WAIT_CENTER_TIME + MOVE_DURATION + DELAY_STATS_TIME;

        if (sceneTime < totalWaitTime) {
            return;
        }

        float elapsedStats = sceneTime - totalWaitTime;
        float statsAlpha = Math.min(1f, elapsedStats / 1f);

        font.setColor(1, 1, 1, statsAlpha);

        dibujarStatsVentanaGameOver(spriteBatch, layout, font, ventanaJuego1);
        dibujarTextoOpciones(layout, font, spriteBatch);

        font.setColor(1, 1, 1, 1f);
    }

    private void dibujarTextoOpciones(GlyphLayout layout, BitmapFont font, SpriteBatch spriteBatch) {
        float originalScale = font.getData().scaleX;

        font.getData().setScale(1f);

        String optionsText = GAMEOVER_TEXT;
        layout.setText(font, optionsText);

        float textWidth = layout.width;
        float textX = (VIRTUAL_WIDTH - textWidth) / 2f;
        float textY = (VIRTUAL_HEIGHT * 0.2f);

        font.draw(spriteBatch, optionsText, textX, textY);

        font.getData().setScale(originalScale);
    }

    private void dibujarStatsVentanaGameOver(SpriteBatch batch, GlyphLayout layout, BitmapFont font, VentanaJuego1 ventanaJuego1) {
        float originalScale = font.getData().scaleX;
        font.getData().setScale(1f);

        String[] lineas = {
            "Tiempo: " + ventanaJuego1.getRenderHUDComponents().formatearTiempo(ventanaJuego1.getRenderHUDComponents().getTiempoTranscurrido()),
            "Kills: " + ventanaJuego1.getControladorEnemigos().getKillCounter(),
            "Oro: " + ventanaJuego1.getJugador().getOroGanado(),
            "Trazos: " + ObjetoPowerUp.getContador()
        };

        float totalAltura = 0f;
        float espacioEntreLineas = 20f;

        // Usamos LinkedHashMap para almacenar la altura de cada línea y evitar cálculos repetidos
        Map<String, Float> lineHeights = new LinkedHashMap<>();

        for (String linea : lineas) {
            layout.setText(font, linea);
            float height = layout.height;
            lineHeights.put(linea, height);
            totalAltura += height + espacioEntreLineas;
        }
        totalAltura -= espacioEntreLineas;

        float startY = (VIRTUAL_HEIGHT / 2f) + 100;
        float y = startY;

        for (Map.Entry<String, Float> entry : lineHeights.entrySet()) {
            String linea = entry.getKey();
            float lineHeight = entry.getValue();

            layout.setText(font, linea);
            float lineWidth = layout.width;
            float x = (VIRTUAL_WIDTH - lineWidth) / 2f;

            font.draw(batch, linea, x, y);
            y -= (lineHeight + espacioEntreLineas);
        }

        font.getData().setScale(originalScale);
    }


    private float lerp(float start, float end, float alpha) {
        return start + (end - start) * alpha;
    }
}

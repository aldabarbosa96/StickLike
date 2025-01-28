package com.sticklike.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sticklike.core.pantallas.juego.VentanaJuego;

public class MenuPause { // todo --> habrá que convertir esta clase en un scene2D para poder implementar botones reales
    private float pauseWidth;
    private float pauseHeight;
    private float pauseSpacing;
    private float menuWidth;
    private float marginRight;
    private float marginTop;
    private boolean isPaused;
    private BitmapFont font;
    private VentanaJuego ventanaJuego;
    private boolean inputsBloqueados = false;
    private SpriteBatch spriteBatch;

    public MenuPause(float pauseWidth, float pauseHeight, float pauseSpacing, float menuWidth, float marginRight, float marginTop, VentanaJuego ventanaJuego) {
        this.pauseWidth = pauseWidth;
        this.pauseHeight = pauseHeight;
        this.pauseSpacing = pauseSpacing;
        this.menuWidth = menuWidth;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.isPaused = false;
        this.ventanaJuego = ventanaJuego;
        this.font = new BitmapFont();
        this.spriteBatch = new SpriteBatch();
    }

    /**
     * Renderiza los rectángulos de pausa en la esquina superior derecha con rebordes.
     *
     * @param shapeRenderer  ShapeRenderer para dibujar las figuras.
     * @param viewportWidth  Ancho de la ventana.
     * @param viewportHeight Alto de la ventana.
     */
    public void render(ShapeRenderer shapeRenderer, float viewportWidth, float viewportHeight) {
        // Habilitar blending para transparencias
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Coordenadas para el fondo del botón de pausa
        float pauseButtonX = viewportWidth - marginRight - menuWidth;
        float pauseButtonY = viewportHeight - marginTop - menuWidth - 25f;

        // Dibuja el fondo cuadrado del botón de pausa
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.65f));
        shapeRenderer.rect(pauseButtonX, pauseButtonY, menuWidth, menuWidth);

        // Coordenadas para los rectángulos de pausa dentro del fondo
        float pauseX = pauseButtonX + (menuWidth - (pauseWidth * 2 + pauseSpacing)) / 2;
        float pauseY = pauseButtonY + (menuWidth - pauseHeight) / 2;

        // Dibuja el reborde negro para los rectángulos de pausa
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.65f);
        shapeRenderer.rect(pauseX - 1.5f, pauseY - 1.5f, pauseWidth + 3f, pauseHeight + 3f);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing - 1.5f, pauseY - 1.5f, pauseWidth + 3f, pauseHeight + 3f);

        // Dibuja los rectángulos de pausa
        shapeRenderer.setColor(new Color(Color.WHITE));
        shapeRenderer.rect(pauseX, pauseY, pauseWidth, pauseHeight);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing, pauseY, pauseWidth, pauseHeight);

        shapeRenderer.end();

        // Deshabilitar blending después de dibujar
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Mostrar texto "PAUSA" si el juego está pausado
        if (isPaused) {
            spriteBatch.begin();
            font.getData().setScale(2.5f);
            String text = "P A U S A";

            // Calcula la posición del texto
            float textX = viewportWidth / 2 + 345f;
            float textY = viewportHeight / 2 + 450f;

            // Dibuja el reborde negro del texto
            font.setColor(Color.BLACK);
            font.draw(spriteBatch, text, textX - 1, textY);
            font.draw(spriteBatch, text, textX + 1, textY);
            font.draw(spriteBatch, text, textX, textY - 1);
            font.draw(spriteBatch, text, textX, textY + 1);
            font.draw(spriteBatch, text, textX - 1, textY - 1);
            font.draw(spriteBatch, text, textX + 1, textY - 1);
            font.draw(spriteBatch, text, textX - 1, textY + 1);
            font.draw(spriteBatch, text, textX + 1, textY + 1);

            // Dibuja el texto principal en blanco encima
            font.setColor(0.9f,0.9f,0.9f,1f);
            font.draw(spriteBatch, text, textX, textY);

            spriteBatch.end();
        }
    }


    public void handleInput() {
        if (inputsBloqueados) return;

        // Detección de teclado (tecla "P" o barra espaciadora)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePause();
        }
    }

    public void bloquearInputs(boolean bloquear) {
        inputsBloqueados = bloquear;
    }

    private void togglePause() {
        // Alternar el estado de pausa
        isPaused = !isPaused;
        ventanaJuego.setPausado(isPaused);
    }

    public boolean isPaused() {
        return isPaused;
    }
}

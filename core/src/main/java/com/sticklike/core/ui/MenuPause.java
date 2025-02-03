package com.sticklike.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sticklike.core.pantallas.juego.VentanaJuego;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Gestiona el dibujado en pantalla del botón y texto PAUSA, además de gestionar sus inputs
 * todo--> los inputs deberán moverse a una clase separada
 */
public class MenuPause extends ControllerAdapter {
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
    private Viewport viewport;

    public MenuPause(VentanaJuego ventanaJuego, Viewport viewport) {
        this.pauseWidth = 4;
        this.pauseHeight = 12;
        this.pauseSpacing = 4;
        this.menuWidth = 22;
        this.marginRight = 15;
        this.marginTop = 55;
        this.isPaused = false;
        this.ventanaJuego = ventanaJuego;
        this.font = new BitmapFont();
        this.spriteBatch = new SpriteBatch();
        this.viewport = viewport;

        // Nos registramos como listener global de mando para recibir cualquier input en esta clase
        Controllers.addListener(this);
    }

    public void render(ShapeRenderer shapeRenderer) {
        // Habilitar blending para transparencias
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Extra offset para subir el botón de pausa (por ejemplo, -50 unidades)
        float extraVerticalOffset = -50f;
        // Calcular la posición del botón usando las dimensiones actuales del viewport
        float pauseButtonX = viewport.getWorldWidth() - marginRight - menuWidth;
        float pauseButtonY = viewport.getWorldHeight() - marginTop - menuWidth - BUTTON_PAUSE_Y_CORRECTION - extraVerticalOffset;

        // --- Dibujar el botón de pausa ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Fondo del botón
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.65f));
        shapeRenderer.rect(pauseButtonX, pauseButtonY, menuWidth, menuWidth);
        // Coordenadas internas para los rectángulos del icono de pausa:
        float pauseX = pauseButtonX + (menuWidth - (pauseWidth * 2 + pauseSpacing)) / 2;
        float pauseY = pauseButtonY + (menuWidth - pauseHeight) / 2;
        // Dibujar el reborde de cada rectángulo
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.65f);
        shapeRenderer.rect(pauseX - BORDER_NEGATIVE, pauseY - BORDER_NEGATIVE, pauseWidth + BORDER_POSITIVE, pauseHeight + BORDER_POSITIVE);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing - BORDER_NEGATIVE, pauseY - BORDER_NEGATIVE, pauseWidth + BORDER_POSITIVE, pauseHeight + BORDER_POSITIVE);
        // Dibujar los rectángulos (rojos si está pausado, blancos en caso contrario)
        shapeRenderer.setColor(isPaused ? Color.RED : Color.WHITE);
        shapeRenderer.rect(pauseX, pauseY, pauseWidth, pauseHeight);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing, pauseY, pauseWidth, pauseHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // --- Dibujar el texto START (relativo al botón) ---
        spriteBatch.begin();
        font.setColor(isPaused ? Color.RED : Color.BLACK);
        font.getData().setScale(0.9f);
        float startTextX = pauseButtonX + START_TEXT_OFFSET_X;
        float startTextY = pauseButtonY + START_TEXT_OFFSET_Y;
        font.draw(spriteBatch, START, startTextX, startTextY);
        spriteBatch.end();

        // --- Dibujar el texto "P A U S A" centrado en pantalla ---
        if (isPaused) {
            spriteBatch.begin();
            font.getData().setScale(2.5f);
            GlyphLayout layoutPausa = new GlyphLayout(font, PAUSA);
            float pauseTextX = (viewport.getWorldWidth() - layoutPausa.width) / 2 + PAUSE_TEXT_OFFSET_X;
            float pauseTextY = (viewport.getWorldHeight() / 2) + PAUSE_TEXT_OFFSET_Y;

            // Dibujar sombra en 4 direcciones (puedes ajustar o agregar más si lo deseas)
            font.setColor(Color.BLACK);
            font.draw(spriteBatch, PAUSA, pauseTextX - BASIC_OFFSET, pauseTextY);
            font.draw(spriteBatch, PAUSA, pauseTextX + BASIC_OFFSET, pauseTextY);
            font.draw(spriteBatch, PAUSA, pauseTextX, pauseTextY - BASIC_OFFSET);
            font.draw(spriteBatch, PAUSA, pauseTextX, pauseTextY + BASIC_OFFSET);
            // Dibujar el texto principal
            font.setColor(new Color(0.9f, 0.9f, 0.9f, 1f));
            font.draw(spriteBatch, PAUSA, pauseTextX, pauseTextY);
            spriteBatch.end();
        }
    }


    public void handleInput() {
        if (inputsBloqueados) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            alternarPausa();
        }
    }

    public void bloquearInputs(boolean bloquear) {
        inputsBloqueados = bloquear;
    }

    private void alternarPausa() {
        isPaused = !isPaused;
        ventanaJuego.setPausado(isPaused);

        if (isPaused) {
            ventanaJuego.reproducirSonidoPausa();
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if (inputsBloqueados) return false;

        if (buttonIndex == BUTTON_START) {
            alternarPausa();
            return true;
        }
        return false;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void dispose() {
        Controllers.removeListener(this);
        spriteBatch.dispose();
        font.dispose();


    }
}

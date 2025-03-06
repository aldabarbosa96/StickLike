package com.sticklike.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sticklike.core.pantallas.juego.VentanaJuego1;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Gestiona el botón y el menú de pausa en la interfaz del juego.
 * Permite pausar y reanudar la partida mediante teclado o mando.
 */

public class Pausa extends ControllerAdapter { // todo --> implementar menú real próximamente
    private float pauseWidth;
    private float pauseHeight;
    private float pauseSpacing;
    private float menuWidth;
    private float marginRight;
    private float marginTop;
    private boolean isPaused;
    private BitmapFont font;
    private VentanaJuego1 ventanaJuego1;
    private boolean inputsBloqueados = false;
    private SpriteBatch spriteBatch;
    private OrthographicCamera hudCamera;
    private Viewport hudViewport;

    public Pausa(VentanaJuego1 ventanaJuego1) {
        this.pauseWidth = 4;
        this.pauseHeight = 12;
        this.pauseSpacing = 4;
        this.menuWidth = 22;
        this.marginRight = 20;
        this.marginTop = 55;
        this.isPaused = false;
        this.ventanaJuego1 = ventanaJuego1;
        this.font = new BitmapFont();
        this.spriteBatch = new SpriteBatch();

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        hudViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, hudCamera);
        hudViewport.apply();

        Controllers.addListener(this);
    }

    public void render(ShapeRenderer shapeRenderer) {
        // Usamos la cámara del HUD (no la del juego)
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);

        float extraVerticalOffset = -50f;
        float pauseButtonX = VIRTUAL_WIDTH - marginRight - menuWidth;
        float pauseButtonY = VIRTUAL_HEIGHT - marginTop - menuWidth - BUTTON_PAUSE_Y_CORRECTION - extraVerticalOffset;

        //DIBUJAR EL BOTÓN DE PAUSA
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.65f));
        shapeRenderer.rect(pauseButtonX, pauseButtonY, menuWidth, menuWidth);

        // Posición interna para el icono de pausa
        float pauseX = pauseButtonX + (menuWidth - (pauseWidth * 2 + pauseSpacing)) / 2;
        float pauseY = pauseButtonY + (menuWidth - pauseHeight) / 2;

        // Dibujar el reborde de cada rectángulo
        shapeRenderer.setColor(new Color(0.3f, 0.3f, 0.3f, 0.65f));
        shapeRenderer.rect(pauseX - BORDER_NEGATIVE, pauseY - BORDER_NEGATIVE, pauseWidth + BORDER_POSITIVE, pauseHeight + BORDER_POSITIVE);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing - BORDER_NEGATIVE, pauseY - BORDER_NEGATIVE, pauseWidth + BORDER_POSITIVE, pauseHeight + BORDER_POSITIVE);

        // Dibujar los rectángulos del icono de pausa
        shapeRenderer.setColor(isPaused ? Color.RED : Color.WHITE);
        shapeRenderer.rect(pauseX, pauseY, pauseWidth, pauseHeight);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing, pauseY, pauseWidth, pauseHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        //DIBUJAR EL TEXTO START
        spriteBatch.begin();
        font.setColor(isPaused ? Color.RED : Color.BLACK);
        font.getData().setScale(0.8f);
        GlyphLayout layoutStart = new GlyphLayout(font, START);
        float startTextX = pauseButtonX + (menuWidth - layoutStart.width) / 2;
        float startTextY = pauseButtonY - 10;

        font.draw(spriteBatch, START, startTextX, startTextY);
        spriteBatch.end();

        // DIBUJAR EL TEXTO "P A U S A"
        if (isPaused) {
            spriteBatch.begin();
            font.getData().setScale(2.5f);
            GlyphLayout layoutPausa = new GlyphLayout(font, PAUSA);
            float pauseTextX = (VIRTUAL_WIDTH - layoutPausa.width) / 2;
            float pauseTextY = (VIRTUAL_HEIGHT + layoutPausa.height) / 2 + 250f;

            // Dibujar sombras
            font.setColor(Color.WHITE);
            font.draw(spriteBatch, PAUSA, pauseTextX - BASIC_OFFSET, pauseTextY);
            font.draw(spriteBatch, PAUSA, pauseTextX + BASIC_OFFSET, pauseTextY);
            font.draw(spriteBatch, PAUSA, pauseTextX, pauseTextY - BASIC_OFFSET);
            font.draw(spriteBatch, PAUSA, pauseTextX, pauseTextY + BASIC_OFFSET);

            // Dibujar el texto principal
            font.setColor(Color.RED);
            font.draw(spriteBatch, PAUSA, pauseTextX, pauseTextY);
            spriteBatch.end();
        }
    }

    public void handleInput() {
        if (inputsBloqueados) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            alternarPausa();
        }
    }

    public void bloquearInputs(boolean bloquear) {
        inputsBloqueados = bloquear;
    }

    private void alternarPausa() {
        isPaused = !isPaused;
        ventanaJuego1.setPausado(isPaused);

        if (isPaused) {
            ventanaJuego1.reproducirSonidoPausa();
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


    // Usamos el viewport del HUD para actualizar el tamaño cuando hacemos resize
    public Viewport getViewport() {
        return hudViewport;
    }

    public void dispose() {
        Controllers.removeListener(this);
        spriteBatch.dispose();
        font.dispose();
    }
}

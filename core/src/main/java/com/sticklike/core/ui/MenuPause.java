package com.sticklike.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.sticklike.core.pantallas.juego.VentanaJuego;

public class MenuPause extends ControllerAdapter {  // todo --> corregir valores mágicos
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

    // Índice del botón "Start" en mando Xbox todo --> comprobar funcionamiento con mando PS
    private static final int BUTTON_START = 6;

    public MenuPause(
        float pauseWidth, float pauseHeight, float pauseSpacing,
        float menuWidth, float marginRight, float marginTop,
        VentanaJuego ventanaJuego
    ) {
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

        // Nos registramos como listener global de mando
        Controllers.addListener(this);
    }

    /**
     * Renderiza el botón de pausa y, si está activo, el texto de "PAUSA".
     */
    public void render(ShapeRenderer shapeRenderer, float viewportWidth, float viewportHeight) {
        // Habilitar blending para transparencias
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float pauseButtonX = viewportWidth - marginRight - menuWidth;
        float pauseButtonY = viewportHeight - marginTop - menuWidth - 25f;

        // Fondo cuadrado del botón
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.65f));
        shapeRenderer.rect(pauseButtonX, pauseButtonY, menuWidth, menuWidth);

        // Coordenadas para los rectángulos de pausa
        float pauseX = pauseButtonX + (menuWidth - (pauseWidth * 2 + pauseSpacing)) / 2;
        float pauseY = pauseButtonY + (menuWidth - pauseHeight) / 2;

        // Reborde
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.65f);
        shapeRenderer.rect(pauseX - 1.5f, pauseY - 1.5f, pauseWidth + 3f, pauseHeight + 3f);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing - 1.5f, pauseY - 1.5f, pauseWidth + 3f, pauseHeight + 3f);

        // **Rectángulos de pausa**: si está en pausa --> rojo, si no, blanco
        if (isPaused) {
            shapeRenderer.setColor(Color.BLACK);
        } else {
            shapeRenderer.setColor(Color.WHITE);
        }
        shapeRenderer.rect(pauseX, pauseY, pauseWidth, pauseHeight);
        shapeRenderer.rect(pauseX + pauseWidth + pauseSpacing, pauseY, pauseWidth, pauseHeight);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        spriteBatch.begin();

        // Ajusta el texto START debajo del icono pausa
        font.setColor(Color.BLACK);
        font.getData().setScale(0.9f);

        float startTextX = pauseX + 802.5f;
        float startTextY = pauseY + 295;
        font.draw(spriteBatch, "START", startTextX, startTextY);

        spriteBatch.end();

        // Mostrar texto "P A U S A" si está pausado
        if (isPaused) {
            spriteBatch.begin();
            font.getData().setScale(2.5f);

            String text = "P A U S A";
            float textX = viewportWidth / 2 + 345f;
            float textY = viewportHeight / 2 + 450f;

            // Sombra / Reborde negro
            font.setColor(Color.BLACK);
            font.draw(spriteBatch, text, textX - 1, textY);
            font.draw(spriteBatch, text, textX + 1, textY);
            font.draw(spriteBatch, text, textX, textY - 1);
            font.draw(spriteBatch, text, textX, textY + 1);

            // Texto principal en blanco
            font.setColor(0.9f, 0.9f, 0.9f, 1f);
            font.draw(spriteBatch, text, textX, textY);

            spriteBatch.end();
        }
    }


    /**
     * Lógica de entrada para pausar con teclado.
     */
    public void handleInput() {
        if (inputsBloqueados) return;

        // Barra espaciadora o tecla "P"
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePause();
        }
    }

    /**
     * Bloquea/desbloquea inputs (mando y teclado).
     */
    public void bloquearInputs(boolean bloquear) {
        inputsBloqueados = bloquear;
    }

    /**
     * Alterna el estado de pausa.
     */
    private void togglePause() {
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

        // Comprobamos si se ha pulsado el botón START (6)
        if (buttonIndex == BUTTON_START) {
            togglePause();
            return true;
        }
        return false;
    }


    public void dispose() {
        Controllers.removeListener(this);
        spriteBatch.dispose();
        font.dispose();

    }
}

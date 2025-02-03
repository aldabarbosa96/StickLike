package com.sticklike.core.pantallas.gameOver;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.MainGame;
import com.sticklike.core.pantallas.juego.VentanaJuego;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Pantalla ** GAME OVER ** que se muestra al terminar la partida (al morir)
 * Permite al usuario reiniciar el juego o salir
 */
public class VentanaGameOver implements Screen {
    private final MainGame game;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private GlyphLayout layout;
    private RenderVentanaGameOver renderVentanaGameOver;
    private OrthographicCamera camera;
    private FitViewport viewport;

    public VentanaGameOver(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.gestorDeAudio.detenerMusica();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();
        renderVentanaGameOver = new RenderVentanaGameOver();
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();

        inputsVentanaGameOver();

    }

    @Override
    public void render(float delta) {
        renderVentanaGameOver.renderizarVentanaGameOver(viewport, camera, spriteBatch, layout, font);
    }

    private void inputsVentanaGameOver() { // todo --> manejar en una clase dedicada (añadir además inputs de mando)
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.R) {
                    reiniciarJuego();
                } else if (keycode == Input.Keys.Q) {
                    cerrarJuego();
                }
                return true;
            }
        });
    }

    // En VentanaGameOver.java
    private void reiniciarJuego() {
        game.ventanaJuego.dispose();
        game.ventanaJuego = new VentanaJuego(game, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.ventanaJuego.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // <-- Añadir esta línea
        game.setScreen(game.ventanaJuego);
        game.gestorDeAudio.reproducirMusica();
    }

    private void cerrarJuego() {
        Gdx.app.exit();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() { // se llama cuando se oculta la pantalla, iberamos recursos para no malgastar memoria
        spriteBatch.dispose();
        font.dispose();

        // Limpiamos el InputProcessor al ocultar la pantalla
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
    }
}

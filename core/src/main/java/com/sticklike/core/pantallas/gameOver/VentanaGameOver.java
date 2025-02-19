package com.sticklike.core.pantallas.gameOver;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.MainGame;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoPolla;
import com.sticklike.core.pantallas.juego.VentanaJuego1;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Gestiona la pantalla de Game Over.
 * Permite reiniciar la partida o cerrar el juego mediante entradas del usuario.
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
        renderVentanaGameOver.renderizarVentanaGameOver(viewport, camera, spriteBatch, layout, font, game.getVentanaJuego1());
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
        EnemigoCulo.resetStats();
        EnemigoPolla.resetStats();
        game.ventanaJuego1.dispose();
        game.ventanaJuego1 = new VentanaJuego1(game, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.ventanaJuego1.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.setScreen(game.ventanaJuego1);
        game.gestorDeAudio.cambiarMusica("fondo2");
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
    public void hide() { // se llama cuando se oculta la pantalla, liberamos recursos para no malgastar memoria
        spriteBatch.dispose();
        font.dispose();

        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
    }
}

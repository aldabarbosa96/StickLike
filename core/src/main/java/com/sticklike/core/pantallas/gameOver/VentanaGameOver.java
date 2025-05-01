package com.sticklike.core.pantallas.gameOver;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.MainGame;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoExamen;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoPolla;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.pantallas.menus.ventanas.MenuPrincipal;
import com.sticklike.core.ui.Mensajes;
import com.sticklike.core.ui.MensajesData;


import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

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
    private ControladorProyectiles controladorProyectiles;

    public VentanaGameOver(MainGame game, ControladorProyectiles controladorProyectiles) {
        this.game = game;
        this.controladorProyectiles = controladorProyectiles;
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

    private void reiniciarJuego() {
        // 1) Reset de stats y proyectiles todo --> manejar reseteo de todas las entidades en un futuro
        EnemigoCulo.resetStats();
        EnemigoPolla.resetStats();
        EnemigoExamen.resetStats();
        controladorProyectiles.reset();

        // 2) Liberamos la instancia anterior de VentanaJuego1
        if (game.ventanaJuego1 != null) {
            game.ventanaJuego1.dispose();
        }

        // 3) Reiniciamos mensajes y datos
        Mensajes.reset();
        MensajesData.getInstance().reset();

        // 4) Creamos y asignamos la nueva pantalla de juego
        VentanaJuego1 nueva = new VentanaJuego1(game, VentanaJuego1.worldWidth, VentanaJuego1.worldHeight);
        nueva.resize(VentanaJuego1.worldWidth, VentanaJuego1.worldHeight);
        game.ventanaJuego1 = nueva;

        // 5) Mostramos la nueva pantalla
        game.setScreen(nueva);
    }

    private void cerrarJuego() {
        if (game.ventanaJuego1 != null) {
            game.ventanaJuego1.dispose();
            game.ventanaJuego1 = null;
        }
        game.setScreen(new MenuPrincipal(game));
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
        dispose();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();

    }
}

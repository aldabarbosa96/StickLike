package com.sticklike.core.pantallas;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.MainGame;
import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.utilidades.GestorConstantes;

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

    // Tamaño virtual de la ventana
    private static final float VIRTUAL_WIDTH = GestorConstantes.VIRTUAL_WIDTH;
    private static final float VIRTUAL_HEIGHT = GestorConstantes.VIRTUAL_HEIGHT;

    /**
     * @param game referencia a la clase principal {@link MainGame}, la cual gestiona el cambio de pantallas
     */
    public VentanaGameOver(MainGame game) {
        this.game = game;
    }

    /**
     * Llamado cuando se muestra esta pantalla por primera vez
     * Configura las variables de render y un {@link InputProcessor} para capturar teclas: R para reiniciar, Q para salir
     */
    @Override
    public void show() {
        game.controladorAudio.detenerMusica();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();
        renderVentanaGameOver = new RenderVentanaGameOver();
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();

        inputsVentanaGameOver();

    }

    /**
     * Método principal de renderizado. Dibuja el texto de Game Over y las instrucciones para reiniciar o salir
     *
     * @param delta tiempo transcurrido desde el último frame
     */
    @Override
    public void render(float delta) {
        renderVentanaGameOver.renderizarVentanaGameOver(viewport,camera,spriteBatch,layout,font);
    }


    private void inputsVentanaGameOver(){
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

    /**
     * Llamado al pulsar la tecla R. Reinicia el juego creando una nueva instancia
     * de la pantalla principal (VentanaJuego) y asignándola como pantalla actual
     */
    private void reiniciarJuego() {
        game.ventanaJuego.dispose();
        game.ventanaJuego = new VentanaJuego(game); // Crear una nueva instancia de GameScreen
        game.setScreen(game.ventanaJuego);
        game.controladorAudio.reproducirMusica();
    }

    /**
     * Llamado al pulsar la tecla Q. Cierra la aplicación
     */
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

    /**
     * Se llama cuando se oculta la pantalla. Liberamos recursos para no malgastar memoria
     */
    @Override
    public void hide() {
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

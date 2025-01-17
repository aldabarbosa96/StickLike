package com.sticklike.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sticklike.core.MainGame;
import com.sticklike.core.entities.Enemigo;
import com.sticklike.core.entities.TextoFlotante;
import com.sticklike.core.entities.Jugador;
import com.sticklike.core.entities.ObjetoXP;
import com.sticklike.core.managers.ControladorEnemigos;
import com.sticklike.core.managers.ControladorMejoras;
import com.sticklike.core.renderers.RenderizadoCuadriculaMapa;
import com.sticklike.core.systems.SistemaDeNiveles;
import com.sticklike.core.ui.HUD;
import com.sticklike.core.upgrades.Mejora;
import com.sticklike.core.utils.GestorConstantes;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.List;

/**
 * GameScreen es la pantalla principal del juego. Gestiona la lógica de:
 * <p>
 * El jugador (movimiento, acciones, colisiones)
 * Los enemigos y su spawn (a través de EnemyManager)
 * El HUD (vida, experiencia, nivel, etc.)
 * La cámara y renderizado de las entidades
 * La aparición de un pop-up para upgrades cuando el jugador sube de nivel
 * <p>
 * Implementa la interfaz {@link com.badlogic.gdx.Screen}, propia del framework
 */
public class VentanaJuego implements Screen {

    public static final int WORLD_WIDTH = (int) GestorConstantes.VIRTUAL_WIDTH;
    public static final int WORLD_HEIGHT = (int) GestorConstantes.VIRTUAL_HEIGHT;
    private static final float CAMERA_OFFSET_Y = GestorConstantes.CAMERA_OFFSET_Y;

    private MainGame game;

    // Render básico
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camara;
    private Viewport viewport;
    private RenderizadoCuadriculaMapa renderizadoCuadriculaMapa;

    // UI Scene2D (pop-up upgrades)
    private Stage uiStage;
    private Skin uiSkin;

    // Jugador y sistemas
    private Jugador jugador;
    private ControladorEnemigos controladorEnemigos;
    private ControladorMejoras controladorMejoras;
    private SistemaDeNiveles sistemaDeNiveles;
    private HUD hud;

    // Arrays de entidades
    private Array<TextoFlotante> textosDanyo;
    private Array<ObjetoXP> objetosXP;
    private Array<Enemigo> enemigosAEliminar;

    // Control de pausa
    private boolean pausado = false;

    /**
     * Inicializa todos los elementos necesarios para manejar el juego, como la cámara,
     * el jugador, los managers de enemigos y de upgrades, y el HUD
     *
     * @param game referencia a la clase principal {@link MainGame}, donde se maneja el
     *             {@code setScreen} y el ciclo de vida general
     */
    public VentanaJuego(MainGame game) {
        this.game = game;

        // Render base
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camara = new OrthographicCamera();
        viewport = new FillViewport(GestorConstantes.VIRTUAL_WIDTH, GestorConstantes.VIRTUAL_HEIGHT, camara);
        viewport.apply();

        // Stage + Skin (para pop-up)
        uiStage = new Stage(new FillViewport(GestorConstantes.VIRTUAL_WIDTH, GestorConstantes.VIRTUAL_HEIGHT));
        uiSkin = crearAspectoUI(); // Creamos un Skin con fondo simple todo --> *

        // Jugador
        float playerStartX = WORLD_WIDTH / 2f;
        float playerStartY = WORLD_HEIGHT / 2f + 125f;
        jugador = new Jugador(playerStartX, playerStartY);

        // Managers
        controladorMejoras = new ControladorMejoras(jugador, game);
        sistemaDeNiveles = new SistemaDeNiveles(jugador, controladorMejoras);
        controladorEnemigos = new ControladorEnemigos(jugador, 1.5f, this);
        jugador.estableceControladorEnemigos(controladorEnemigos);

        // Grid (mapa) y HUD
        renderizadoCuadriculaMapa = new RenderizadoCuadriculaMapa((int) GestorConstantes.GRID_CELL_SIZE);
        hud = new HUD(jugador, sistemaDeNiveles, shapeRenderer, spriteBatch);

        // Listas
        textosDanyo = new Array<>();
        objetosXP = new Array<>();
        enemigosAEliminar = new Array<>();

        actualizarPosCamara();
    }

    /**
     * Crea un {@link Skin} con un fondo simple (un Pixmap de color) par mostrar la ventana de upgrades
     *
     * @return un Skin con estilo definido para la ventana y botones
     */
    private Skin crearAspectoUI() {
        // todo -> * crear textura personalizada en un futuro estilo post-it para el pop-up de upgrades
        Skin skin = new Skin();

        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        // Se crea un Pixmap de 1x1 con color amarillo/ocre suave
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.97f, 0.88f, 0.6f, 1f));
        pixmap.fill();
        Texture pixmapTexture = new Texture(pixmap);
        pixmap.dispose();

        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(pixmapTexture);

        Window.WindowStyle wStyle = new Window.WindowStyle(font, Color.BLACK, backgroundDrawable);
        skin.add("default-window", wStyle);

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = font;
        skin.add("default-button", tbs);

        return skin;
    }

    /**
     * Método de {@link Screen} que se llama al mostrar esta pantalla
     */
    @Override
    public void show() {
        pausado = false;
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Método principal de renderizado. Actualiza la lógica (si no está pausado),
     * limpia la pantalla, dibuja el mapa, entidades, HUD y
     * renderiza la UI del pop-up (si procede)
     *
     * @param delta tiempo transcurrido desde el último frame
     */
    @Override
    public void render(float delta) {
        if (jugador.estaMuerto()) {
            // Si el jugador muere, pasamos a la pantalla de GameOver
            game.setScreen(new VentanaGameOver(game));
            return;
        }

        // Si no está pausado, actualizamos lógica
        if (!pausado) {
            actualizarLogica(delta);
        }

        // Limpiamos
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render del mapa (grid)
        actualizarPosCamara();
        renderizadoCuadriculaMapa.renderizarLineasCuadricula(camara);

        // Render de entidades
        spriteBatch.setProjectionMatrix(camara.combined);
        spriteBatch.begin();
        controladorEnemigos.renderizarEnemigos(spriteBatch);
        jugador.renderizarJugadorYProyectil(spriteBatch);

        for (ObjetoXP xp : objetosXP) {
            xp.renderizarObjetoXP(spriteBatch);
        }
        for (TextoFlotante txt : textosDanyo) {
            txt.renderizarTextoFlotante(spriteBatch);
        }
        spriteBatch.end();

        // Render del HUD
        hud.renderizarHUD();

        // La UI del pop-up (Stage) se dibuja encima
        uiStage.act(delta);
        uiStage.draw();
    }

    /**
     * Actualiza la lógica principal: jugador, enemigos, XP, textos flotantes.
     * Se llama en cada frame, salvo cuando el juego está pausado.
     *
     * @param delta tiempo transcurrido desde el último frame
     */
    private void actualizarLogica(float delta) {
        jugador.actualizarJugador(delta, pausado, textosDanyo);
        controladorEnemigos.actualizarSpawnEnemigos(delta);

        // Manejo de enemigos muertos => suelta objetoXP
        for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
            if (enemigo.estaMuerto() && !enemigo.haSoltadoXP()) {
                ObjetoXP xp = enemigo.sueltaObjetoXP();
                if (xp != null) {
                    objetosXP.add(xp);
                }
                enemigo.setProcesado(true);
                enemigosAEliminar.add(enemigo);
            }
        }
        for (Enemigo e : enemigosAEliminar) {
            controladorEnemigos.getEnemigos().removeValue(e, true);
        }
        enemigosAEliminar.clear();

        // Recoger XP
        for (int i = objetosXP.size - 1; i >= 0; i--) {
            ObjetoXP xp = objetosXP.get(i);
            xp.actualizarObjetoXP(delta); // todo --> implementar si se requiere que se desplace el objetoXP (no hace nada por ahora)

            if (xp.colisionaConOtroSprite(jugador.getSprite())) {
                xp.recolectar();
                objetosXP.removeIndex(i);
                sistemaDeNiveles.agregarXP(20f);
            }
        }

        // Actualizar textos flotantes
        for (int i = textosDanyo.size - 1; i >= 0; i--) {
            TextoFlotante floatingText = textosDanyo.get(i);
            floatingText.actualizarTextoFlotante(delta);
            if (floatingText.haDesaparecido()) {
                textosDanyo.removeIndex(i);
            }
        }
    }

    /**
     * Ajusta la cámara para que siga al jugador, aplicando un offset vertical
     * para que quede ligeramente más arriba y encage mejor con el HUD
     */
    private void actualizarPosCamara() {
        camara.position.set(jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2,
            jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2 + CAMERA_OFFSET_Y, 0);

        camara.update();
    }

    /**
     * Muestra un pop-up con las mejoras disponibles (Upgrades)
     * Pausa la lógica del juego y escucha las teclas 1,2,3 para escoger
     *
     * @param mejoras lista de upgrades a mostrar
     */
    public void mostrarPopUpDeMejoras(final List<Mejora> mejoras) {
        pausado = true;

        // Creamos la ventana con estilo default
        Window.WindowStyle wStyle = uiSkin.get("default-window", Window.WindowStyle.class);
        final Window upgradeWindow = new Window("\n\n\nU P G R A D E S", wStyle);
        upgradeWindow.getTitleLabel().setAlignment(Align.center);

        float w = 400;
        float h = 350;

        upgradeWindow.setSize(w, h);
        upgradeWindow.setPosition((GestorConstantes.VIRTUAL_WIDTH - w) / 2f, (GestorConstantes.VIRTUAL_HEIGHT - h + 150f) / 2f);

        upgradeWindow.padTop(75f);
        upgradeWindow.setModal(true);
        upgradeWindow.setMovable(false);

        // Añadimos un botón por cada Upgrade
        for (int i = 0; i < mejoras.size(); i++) {
            final int index = i;
            final Mejora mejora = mejoras.get(i);

            TextButton.TextButtonStyle tbs = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
            TextButton btn = new TextButton((i + 1) + ") " + mejora.getNombreMejora() + " ==> " + mejora.getDescripcionMejora(), tbs);

            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.left);
            btn.getLabel().setColor(Color.BLACK);

            upgradeWindow.row().pad(0);
            upgradeWindow.add(btn).width(350).pad(10);
        }

        // Listener de teclado para 1,2,3
        upgradeWindow.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.NUM_1) {
                    seleccionarMejora(0, mejoras, upgradeWindow);
                    return true;
                } else if (keycode == Input.Keys.NUM_2) {
                    seleccionarMejora(1, mejoras, upgradeWindow);
                    return true;
                } else if (keycode == Input.Keys.NUM_3) {
                    seleccionarMejora(2, mejoras, upgradeWindow);
                    return true;
                }
                return false;
            }
        });

        uiStage.addActor(upgradeWindow);

        // Foco del teclado en el pop-up
        uiStage.setKeyboardFocus(upgradeWindow);

        // Permite que Stage reciba las entradas del input
        InputMultiplexer inputMultiplexer = new InputMultiplexer(uiStage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    /**
     * Aplica la mejora elegida y cierra el pop-up. Reanuda la lógica.
     *
     * @param index         índice de la mejora seleccionada
     * @param mejoras      lista de todas las mejoras mostradas
     * @param upgradeWindow la ventana que se cierra tras la selección
     */
    private void seleccionarMejora(int index, List<Mejora> mejoras, Window upgradeWindow) {
        if (index < 0 || index >= mejoras.size()) return;
        controladorMejoras.aplicarMejora(mejoras.get(index));
        upgradeWindow.remove();
        pausado = false;
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Añade un {@link ObjetoXP} al array de objetosXP
     *
     * @param xpObject objeto de XP a añadir
     */
    public void addXPObject(ObjetoXP xpObject) {
        objetosXP.add(xpObject);
    }

    /**
     * Método de {@link Screen} llamado al redimensionar la ventana. Actualiza el viewport y el HUD
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiStage.getViewport().update(width, height, true);
        hud.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }


    /**
     * Libera los recursos usados por esta pantalla, incluidos los batch, shapes, Stage, texturas del HUD y entidades.
     */
    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        renderizadoCuadriculaMapa.dispose();
        uiStage.dispose();
        uiSkin.dispose();

        if (jugador != null) {
            jugador.dispose();
        }
        if (controladorEnemigos != null) {
            controladorEnemigos.dispose();
        }

        for (TextoFlotante ft : textosDanyo) {
            ft.dispose();
        }
        textosDanyo.clear();

        for (ObjetoXP xp : objetosXP) {
            xp.dispose();
        }
        objetosXP.clear();
    }
}

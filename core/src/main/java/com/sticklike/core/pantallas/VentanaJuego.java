package com.sticklike.core.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueCalcetin;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaquePiedra;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.entidades.jugador.*;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.gameplay.managers.ControladorEnemigos;
import com.sticklike.core.gameplay.sistemas.SistemaDeMejoras;
import com.sticklike.core.gameplay.managers.ControladorProyectiles;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;
import com.sticklike.core.ui.HUD;
import com.sticklike.core.gameplay.mejoras.Mejora;
import com.sticklike.core.utilidades.GestorConstantes;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.List;

/**
 * GameScreen (VentanaJuego) es la pantalla principal del juego.
 * Gestiona la lógica de:
 * <ul>
 *   <li>El jugador (movimiento, acciones, colisiones)</li>
 *   <li>Los enemigos y su spawn (a través de EnemyManager)</li>
 *   <li>El HUD (vida, experiencia, nivel, etc.)</li>
 *   <li>La cámara y renderizado de las entidades</li>
 *   <li>La aparición de un pop-up para upgrades cuando el jugador sube de nivel</li>
 * </ul>
 * <p>
 * Implementa la interfaz {@link com.badlogic.gdx.Screen} propia de libGDX.
 */
public class VentanaJuego implements Screen {

    public static final int WORLD_WIDTH = (int) GestorConstantes.VIRTUAL_WIDTH;
    public static final int WORLD_HEIGHT = (int) GestorConstantes.VIRTUAL_HEIGHT;
    private static final float CAMERA_OFFSET_Y = GestorConstantes.CAMERA_OFFSET_Y;

    // Referencia al MainGame
    private MainGame game;

    // Render básico
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camara;
    private Viewport viewport;
    private RenderVentanaJuego renderVentanaJuego;

    // UI Scene2D (pop-up upgrades)
    private Stage uiStage;
    private Skin uiSkin;

    // Jugador y sistemas
    private Jugador jugador;
    private ControladorEnemigos controladorEnemigos;
    private SistemaDeMejoras sistemaDeMejoras;
    private SistemaDeNiveles sistemaDeNiveles;
    private HUD hud;
    private ColisionesJugador colisionesJugador;
    private MovimientoJugador movimientoJugador;
    private AtaquePiedra ataquePiedra;
    private AtaqueCalcetin ataqueCalcetin;
    private ControladorProyectiles controladorProyectiles;
    private AnimacionesJugador animacionesJugador;
    private ControladorAudio controladorAudio;

    // Arrays de entidades
    private Array<TextoFlotante> textosDanyo;
    private Array<ObjetosXP> objetosXP;
    private Array<Enemigo> enemigosAEliminar;

    // Control de pausa
    private boolean pausado = false;

    /**
     * Construye la ventana principal del juego, inicializando la cámara,
     * el jugador (con su controlador de inputs), los sistemas, el HUD, etc.
     *
     * @param game referencia a {@link MainGame}, que maneja el ciclo de vida
     */
    public VentanaJuego(MainGame game) {
        this.game = game;

        inicializarRenderYCamara();
        inicializarJugador();
        inicializarSistemasYControladores();
        inicializarCuadriculaYHUD();
        inicializarListas();
        inicializarPopUp();

        // Ajustamos posición de la cámara siguiendo al jugador
        actualizarPosCamara();
    }

    private void inicializarRenderYCamara() {
        // Render base
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camara = new OrthographicCamera();
        viewport = new FillViewport(GestorConstantes.VIRTUAL_WIDTH, GestorConstantes.VIRTUAL_HEIGHT, camara);
        viewport.apply();
    }

    private void inicializarJugador() {
        // Instanciamos el controlador de input y su lógica relacionada
        InputsJugador inputJugador = new InputsJugador();
        colisionesJugador = new ColisionesJugador();
        controladorAudio = game.controladorAudio;
        movimientoJugador = new MovimientoJugador();
        ataquePiedra = new AtaquePiedra(1f);
        ataqueCalcetin = new AtaqueCalcetin(2.5f);
        controladorProyectiles = new ControladorProyectiles();
        animacionesJugador = new AnimacionesJugador();

        // Creamos al jugador en el centro aproximado del mapa, pasando el controlador de inputs
        float playerStartX = WORLD_WIDTH / 2f;
        float playerStartY = WORLD_HEIGHT / 2f + 125f;
        jugador = new Jugador(playerStartX, playerStartY, inputJugador, colisionesJugador, movimientoJugador, ataquePiedra,ataqueCalcetin, controladorProyectiles);
    }

    private void inicializarSistemasYControladores() {
        // Managers / sistemas de mejoras y enemigos
        sistemaDeMejoras = new SistemaDeMejoras(jugador, game);
        sistemaDeNiveles = new SistemaDeNiveles(jugador, sistemaDeMejoras);
        controladorEnemigos = new ControladorEnemigos(jugador, 1f, this);
        jugador.estableceControladorEnemigos(controladorEnemigos);
    }

    private void inicializarCuadriculaYHUD() {
        // Renderizado de la cuadrícula (mapa) y el HUD
        renderVentanaJuego = new RenderVentanaJuego((int) GestorConstantes.GRID_CELL_SIZE);
        hud = new HUD(jugador, sistemaDeNiveles, shapeRenderer, spriteBatch);
    }

    private void inicializarListas() {
        // Listas de texto daño, objetos XP y enemigos a eliminar
        textosDanyo = new Array<>();
        objetosXP = new Array<>();
        enemigosAEliminar = new Array<>();
    }

    private void inicializarPopUp() {
        // Stage + Skin (para pop-up)
        uiStage = new Stage(new FillViewport(GestorConstantes.VIRTUAL_WIDTH, GestorConstantes.VIRTUAL_HEIGHT));
        uiSkin = crearAspectoUI();
    }

    /**
     * Crea un {@link Skin} con un fondo simple (un Pixmap de color)
     * para mostrar la ventana de upgrades.
     *
     * @return un Skin con estilo definido para la ventana y botones
     */
    private Skin crearAspectoUI() {
        Skin skin = new Skin();

        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        // Pixmap de 1x1 con color amarillo/ocre suave
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

    @Override
    public void show() {
        pausado = false;
        // Eliminamos cualquier InputProcessor previo
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        // Si el jugador muere, pasamos a la pantalla de GameOver
        if (jugador.estaVivo()) {
            game.setScreen(new VentanaGameOver(game));
            return;
        }

        // Actualizamos lógica solo si no está en pausa todo --> gestionar el pausado y reanudado de la música de otra manera
        if (!pausado) {
            actualizarLogica(delta, controladorAudio);
            controladorAudio.reproducirMusica();

        } else controladorAudio.pausarMusica();

        // Renderizado de los componentes principales de la ventana
        renderVentanaJuego.renderizarVentana(delta, this, jugador, objetosXP, controladorEnemigos, textosDanyo, hud, spriteBatch, camara);

        // Stage de la UI (pop-up de upgrades)
        uiStage.act(delta);
        uiStage.draw();
    }

    private void actualizarLogica(float delta, ControladorAudio controladorAudio) {
        jugador.actualizarLogicaDelJugador(delta, pausado, textosDanyo, controladorAudio);
        controladorEnemigos.actualizarSpawnEnemigos(delta);

        gestionarEnemigos();
        gestionarRecogidaXP(delta);
        gestionarTextoFlotante(delta);

    }

    private void gestionarEnemigos() {
        // Manejo de enemigos muertos => suelta objeto XP
        for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
            if (enemigo.estaMuerto() && !enemigo.haSoltadoXP()) {
                ObjetosXP xp = enemigo.sueltaObjetoXP();
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
    }

    private void gestionarRecogidaXP(float delta) {
        // Recoger XP
        for (int i = objetosXP.size - 1; i >= 0; i--) {
            ObjetosXP xp = objetosXP.get(i);
            //xp.actualizar(jugador,delta); // para cuando haya que animar el objeto (efecto al recoger)

            if (xp.colisionaConOtroSprite(jugador.getSprite())) {
                xp.recolectar();
                objetosXP.removeIndex(i);
                sistemaDeNiveles.agregarXP(15f + (float) (Math.random() * (25.5f - 15.75f)));
            }
        }
    }

    private void gestionarTextoFlotante(float delta) {
        // Textos flotantes (daño)
        for (int i = textosDanyo.size - 1; i >= 0; i--) {
            TextoFlotante floatingText = textosDanyo.get(i);
            floatingText.actualizarTextoFlotante(delta);
            if (floatingText.haDesaparecido()) {
                textosDanyo.removeIndex(i);
            }
        }
    }

    /**
     * Ajusta la cámara para que siga al jugador, con offset vertical.
     */
    public void actualizarPosCamara() {
        camara.position.set(
            jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f,
            jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f + CAMERA_OFFSET_Y,
            0);

        camara.update();
    }

    /**
     * Muestra un pop-up de upgrades, pausando el juego hasta que se elija una.
     */
    public void mostrarPopUpDeMejoras(final List<Mejora> mejoras) {
        pausado = true;

        Window.WindowStyle wStyle = uiSkin.get("default-window", Window.WindowStyle.class);
        final Window upgradeWindow = new Window("\n\n\nU P G R A D E S\n------------------------", wStyle);
        upgradeWindow.getTitleLabel().setAlignment(Align.center);

        float w = 400;
        float h = 350;
        upgradeWindow.setSize(w, h);
        upgradeWindow.setPosition(
            (GestorConstantes.VIRTUAL_WIDTH - w) / 2f,
            (GestorConstantes.VIRTUAL_HEIGHT - h + 150f) / 2f
        );

        upgradeWindow.padTop(75f);
        upgradeWindow.setModal(true);
        upgradeWindow.setMovable(false);

        // Añadimos botones por cada mejora
        for (int i = 0; i < mejoras.size(); i++) {
            final int index = i;
            final Mejora mejora = mejoras.get(i);

            TextButton.TextButtonStyle tbs = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
            TextButton btn = new TextButton(
                (i + 1) + ") " + mejora.getNombreMejora() + " ==> " + mejora.getDescripcionMejora(),
                tbs
            );

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

        // Foco al pop-up
        uiStage.setKeyboardFocus(upgradeWindow);

        // Stage recibe el input
        InputMultiplexer im = new InputMultiplexer(uiStage);
        Gdx.input.setInputProcessor(im);

    }

    private void seleccionarMejora(int index, List<Mejora> mejoras, Window upgradeWindow) {
        if (index < 0 || index >= mejoras.size()) return;
        sistemaDeMejoras.aplicarMejora(mejoras.get(index));
        upgradeWindow.remove();
        pausado = false;
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Añade un ObjetoXP al array de objetosXP.
     */
    public void addXPObject(ObjetosXP xpObject) {
        objetosXP.add(xpObject);
    }

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
     * Libera los recursos usados por esta pantalla.
     */
    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        renderVentanaJuego.dispose();
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

        for (ObjetosXP xp : objetosXP) {
            xp.dispose();
        }
        objetosXP.clear();
    }

    public OrthographicCamera getOrtographicCamera() {
        return camara;
    }

    public boolean isPausado() {
        return pausado;
    }
}

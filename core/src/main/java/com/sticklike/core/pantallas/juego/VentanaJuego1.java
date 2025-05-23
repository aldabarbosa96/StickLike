package com.sticklike.core.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.sticklike.core.MainGame;
import com.sticklike.core.entidades.mobiliario.tragaperras.FlechaTragaperras;
import com.sticklike.core.entidades.renderizado.particulas.ParticleManager;
import com.sticklike.core.pantallas.popUps.PopUpTragaperras;
import com.sticklike.core.pantallas.popUps.TragaperrasInputProcessor;
import com.sticklike.core.entidades.mobiliario.tragaperras.TragaperrasLogic;
import com.sticklike.core.entidades.objetos.recolectables.*;
import com.sticklike.core.entidades.objetos.recolectables.Boost;
import com.sticklike.core.pantallas.overlay.BoostIconEffectManager;
import com.sticklike.core.pantallas.popUps.PopUpMejorasInputProcessor;
import com.sticklike.core.ui.*;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.entidades.objetos.armas.comportamiento._00AtaquePiedra;
import com.sticklike.core.gameplay.sistemas.SistemaDeEventos;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.entidades.jugador.*;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;
import com.sticklike.core.gameplay.sistemas.SistemaDeMejoras;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;
import com.sticklike.core.pantallas.popUps.PopUpMejoras;
import com.sticklike.core.pantallas.gameOver.VentanaGameOver;
import com.sticklike.core.gameplay.progreso.Mejora;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.List;

/**
 * Representa la pantalla principal del juego donde se lleva a cabo la jugabilidad.
 * Gestiona la actualización de la lógica del jugador, enemigos, proyectiles, experiencia, mejoras y eventos.
 * Encapsula el renderizado de los elementos en pantalla mediante la clase RenderVentanaJuego.
 */

public class VentanaJuego1 implements Screen {
    public static final int worldWidth = (int) VIRTUAL_WIDTH;
    public static final int worldHeight = (int) VIRTUAL_HEIGHT;
    private float cameraOffsetY = CAMERA_OFFSET_Y;
    private MainGame game;

    // Render básico
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private static OrthographicCamera camara;
    private ExtendViewport viewport;
    private RenderVentanaJuego1 renderVentanaJuego1;
    private RenderHUDComponents renderHUDComponents;

    // Jugador y sistemas
    private Jugador jugador;
    private ControladorEnemigos controladorEnemigos;
    private SistemaDeMejoras sistemaDeMejoras;
    private SistemaDeNiveles sistemaDeNiveles;
    private SistemaDeEventos sistemaDeEventos;
    private HUD hud;
    private ColisionesJugador colisionesJugador;
    private MovimientoJugador movimientoJugador;
    private _00AtaquePiedra a00AtaquePiedra;
    private ControladorProyectiles controladorProyectiles;
    private GestorDeAudio gestorDeAudio;
    private PopUpMejoras popUpMejoras;
    private PopUpMejorasInputProcessor popUpMejorasInputProcessor;
    private PopUpTragaperras popupTraga;
    private TragaperrasInputProcessor popupTragaInput;
    private Pausa pausa;
    private Boost boostActivo;
    private FlechaTragaperras flechaTragaperras;

    // Arrays de entidades
    private Array<TextoFlotante> textosDanyo;
    private Array<ObjetosXP> objetosXP;

    private int currentScreenWidth;
    private int currentScreenHeight;
    private boolean pausado = false;
    private boolean musicChanged = false;

    private float zoomLevel = 0.92f;

    public VentanaJuego1(MainGame game, int screenWidth, int screenHeight) {
        this.game = game;
        this.currentScreenWidth = screenWidth;
        this.currentScreenHeight = screenHeight;

        Mensajes.reset();
        MensajesData.getInstance().reset();

        inicializarRenderYCamara();
        inicializarJugador();
        inicializarSistemasYControladores();
        inicializarCuadriculaYHUD();
        inicializarListas();
        spawnObjetoPowerUp();

        // Ajustar la posición de la cámara
        actualizarPosCamara();
        Mensajes.getInstance().addMessage("StickMan", "Ah shit! Here we go again...", jugador.getSprite().getX(), jugador.getSprite().getY() - 10);

        ParticleManager.get().clear();
    }

    private void inicializarRenderYCamara() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camara = new OrthographicCamera();
        viewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camara);
        viewport.apply();
    }

    private void inicializarJugador() {
        InputsJugador inputJugador = new InputsJugador();
        colisionesJugador = new ColisionesJugador();
        gestorDeAudio = game.gestorDeAudio;
        movimientoJugador = new MovimientoJugador();
        a00AtaquePiedra = new _00AtaquePiedra(INTERVALO_DISPARO);
        controladorProyectiles = new ControladorProyectiles();

        float playerStartX = worldWidth / 2f - CAMERA_JUGADOR_OFFSET_X;
        float playerStartY = worldHeight / 2f + CAMERA_JUGADOR_OFFSET_Y;
        StatsJugador baseStats = game.getStatsJugador();
        if (baseStats == null) {
            // primer arranque: valores por defecto
            baseStats = new StatsJugador(VEL_MOV_JUGADOR, VIDA_JUGADOR, VIDAMAX_JUGADOR, RANGO_ATAQUE, DANYO, INTERVALO_DISPARO, VEL_ATAQUE_JUGADOR, NUM_PROYECTILES_INICIALES, RESISTENCIA, CRITICO, REGENERACION_VIDA, PODER_JUGADOR);
            game.setStatsJugador(baseStats);
        }
        // clon de stats para la sesión de juego
        StatsJugador sessionStats = new StatsJugador(baseStats);
        jugador = new Jugador(playerStartX, playerStartY, inputJugador, colisionesJugador, movimientoJugador, a00AtaquePiedra, controladorProyectiles, sessionStats);
    }

    private void inicializarSistemasYControladores() {
        sistemaDeMejoras = new SistemaDeMejoras(jugador, game);
        popUpMejoras = new PopUpMejoras();
        popUpMejorasInputProcessor = new PopUpMejorasInputProcessor(sistemaDeMejoras, this, popUpMejoras);
        sistemaDeNiveles = new SistemaDeNiveles(jugador, sistemaDeMejoras, popUpMejoras);
        controladorEnemigos = new ControladorEnemigos(jugador, INTERVALO_SPAWN, this);
        jugador.estableceControladorEnemigos(controladorEnemigos);
        popupTraga = new PopUpTragaperras(jugador);
        popupTragaInput = new TragaperrasInputProcessor(this, popupTraga);

    }

    private void inicializarCuadriculaYHUD() {
        renderVentanaJuego1 = new RenderVentanaJuego1((int) GRID_CELL_SIZE, jugador, spriteBatch, camara);
        hud = new HUD(jugador, sistemaDeNiveles, shapeRenderer, spriteBatch);
        this.renderHUDComponents = hud.getRenderHUDComponents();
        sistemaDeEventos = new SistemaDeEventos(renderHUDComponents, controladorEnemigos, sistemaDeNiveles);
        pausa = new Pausa(this);
        flechaTragaperras = new FlechaTragaperras(camara, viewport, controladorEnemigos.getTragaperras(), HUD_HEIGHT - HUD_BAR_Y_OFFSET);
    }

    private void inicializarListas() {
        textosDanyo = new Array<>();
        objetosXP = new Array<>();
    }

    @Override
    public void show() {
        pausado = false;
        Gdx.input.setInputProcessor(null);

    }

    @Override
    public void render(float delta) {
        // 1) Si seguimos cargando recursos, dibujamos la pantalla de carga
        if (!renderVentanaJuego1.isLoadingComplete()) {
            gestorDeAudio.pausarMusica();
            renderVentanaJuego1.renderizarVentana(delta, this, jugador, objetosXP, controladorEnemigos, textosDanyo, hud);
            return;
        }

        // 2) Gestionar la pausa y la entrada de usuario
        pausa.handleInput();
        if (jugador.estaMuerto()) {
            game.setScreen(new VentanaGameOver(game, controladorProyectiles, jugador));
            return;
        }

        // 3) Lógica de actualización del juego solo si no está en pausa
        if (!pausado && !pausa.isPaused()) {
            if (!musicChanged) {
                gestorDeAudio.cambiarMusica("fondo2");
                musicChanged = true;
            }
            actualizarLogica(delta, gestorDeAudio);
        } else {
            gestorDeAudio.pausarMusica();
        }

        actualizarPosCamara();
        camara.zoom = zoomLevel;
        camara.update();
        spriteBatch.setProjectionMatrix(camara.combined);

        // 4) Renderizar el mundo y el HUD
        renderVentanaJuego1.renderizarVentana(delta, this, jugador, objetosXP, controladorEnemigos, textosDanyo, hud);

        // 5) Dibujar overlay de pausa y menú si hace falta
        pausa.render(shapeRenderer);

        // 6) Ajustar el SpriteBatch al HUD (coordenadas de pantalla)
        OrthographicCamera hudCam = (OrthographicCamera) pausa.getRenderPausa().getHudViewport().getCamera();
        spriteBatch.setProjectionMatrix(hudCam.combined);

        // 7) Actualizar y dibujar el efecto de boost
        BoostIconEffectManager.getInstance().update(delta, renderHUDComponents);
        spriteBatch.begin();
        BoostIconEffectManager.getInstance().render(spriteBatch);
        spriteBatch.end();

        // 8) Dibujar cualquier pop-up de tragaperras
        Stage slotStage = popupTraga.getUiStage();
        if (slotStage.getActors().size > 0) {
            slotStage.act(delta);
            slotStage.draw();
        }

        // 9) Dibujar cualquier pop-up de mejoras
        Stage stage = popUpMejoras.getUiStage();
        if (stage.getActors().size > 0) {
            stage.act(delta);
            stage.draw();
        }
    }


    private void actualizarLogica(float delta, GestorDeAudio gestorDeAudio) {
        jugador.actualizarLogicaDelJugador(delta, pausado, textosDanyo, gestorDeAudio);
        sistemaDeEventos.actualizar();
        controladorEnemigos.actualizarSpawnEnemigos(delta);

        ParticleManager.get().update(delta);
        actualizarRecogidaObjetos(delta);
        actualizarTextoFlotante(delta);
    }

    private void actualizarRecogidaObjetos(float delta) {
        // 1) Primero, actualizamos cada objeto
        for (int i = objetosXP.size - 1; i >= 0; i--) {
            ObjetosXP xp = objetosXP.get(i);
            xp.actualizarObjetoXP(delta, jugador, gestorDeAudio);

            // 2) Si colisionamos con cualquier objeto:
            if (xp.colisionaConOtroSprite(jugador.getSprite())) {
                if (xp instanceof Boost nuevoBoost) {
                    if (!nuevoBoost.isCollected() && !nuevoBoost.isActivo()) {
                        if (boostActivo != null && boostActivo.isActivo()) {
                            boostActivo.revertirBoost(jugador);
                            objetosXP.removeValue(boostActivo, true);
                        }
                        boostActivo = nuevoBoost;
                        nuevoBoost.aplicarBoost(jugador, gestorDeAudio);
                        xp.recolectar(gestorDeAudio);
                    }
                } else {
                    xp.aplicarEfecto(jugador, gestorDeAudio, this);
                    xp.recolectar(gestorDeAudio);
                    objetosXP.removeIndex(i);
                }
            }
        }

        // 3) Segunda pasada: eliminamos boosts que ya fueron recogidos y que expiraron
        for (int i = objetosXP.size - 1; i >= 0; i--) {
            ObjetosXP xp = objetosXP.get(i);
            if (xp instanceof Boost boost) {
                if (boost.isCollected() && !boost.isActivo()) {
                    if (boost == boostActivo) {
                        boostActivo = null;
                    }
                    objetosXP.removeIndex(i);
                }
            }
        }
    }

    private void actualizarTextoFlotante(float delta) {
        for (int i = textosDanyo.size - 1; i >= 0; i--) {
            TextoFlotante floatingText = textosDanyo.get(i);
            floatingText.actualizarTextoFlotante(delta);
            if (floatingText.haDesaparecido()) {
                textosDanyo.removeIndex(i);
            }
        }
    }

    public void actualizarPosCamara() {
        float halfW = camara.viewportWidth / 2f;
        float halfH = camara.viewportHeight / 2f;

        float camX = MathUtils.clamp(jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f, MAP_MIN_X + halfW, MAP_MAX_X - halfW);

        float camY = MathUtils.clamp(jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f + CAMERA_OFFSET_Y, MAP_MIN_Y + halfH, MAP_MAX_Y - halfH);

        camara.position.set(camX, camY, 0);
    }

    public void mostrarPopUpDeMejoras(final List<Mejora> mejoras) {
        popUpMejorasInputProcessor.show(mejoras);
        setPausado(true);
        reproducirSonidoUpgrade();
    }

    public void addXPObject(ObjetosXP xpObject) {
        objetosXP.add(xpObject);
    }

    public void spawnObjetoPowerUp() {
        int cantidadPowerUps = 3;
        Array<ObjetoPowerUp> powerUps = ObjetoPowerUp.crearPowerUps(cantidadPowerUps, MAP_MIN_X_DROP, MAP_MAX_X_DROP, MAP_MIN_Y_DROP, MAP_MAX_Y_DROP);

        for (ObjetoPowerUp powerUp : powerUps) {
            addXPObject(powerUp);
        }
    }

    public void mostrarPopUpTragaperras(TragaperrasLogic logic) {
        popupTragaInput.show(logic);
        reproducirSonidoUpgrade();         // todo --> cambiar el sonido
    }

    @Override
    public void resize(int width, int height) {
        currentScreenWidth = width;
        currentScreenHeight = height;
        viewport.update(width, height, true);
        popUpMejoras.getUiStage().getViewport().update(width, height, true);
        hud.resize(width, height);
        pausa.getRenderPausa().getHudViewport().update(width, height, true);
        controladorEnemigos.setVentanaRedimensionada(true);
        BoostIconEffectManager.getInstance().getEffect().updateDimensions(camara);
        popupTraga.getUiStage().getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        ParticleManager.get().clear();
    }

    @Override
    public void dispose() {
        if (spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
        renderVentanaJuego1.dispose();
        popUpMejoras.dispose();
        popupTraga.dispose();

        if (pausa != null) {
            pausa.dispose();
            pausa = null;
        }

        if (jugador != null) {
            jugador.dispose();
            jugador = null;
        }
        if (controladorEnemigos != null) {
            controladorEnemigos.dispose();
            controladorEnemigos = null;
        }

        textosDanyo.clear();

        for (ObjetosXP xp : objetosXP) {
            xp.dispose();
        }
        objetosXP.clear();
        sistemaDeEventos.dispose();
        BoostIconEffectManager.getInstance().dispose();
    }

    public OrthographicCamera getOrtographicCamera() {
        return camara;
    }

    public void setPausado(boolean pausa) {
        this.pausado = pausa;

        if (pausa) {
            gestorDeAudio.pausarMusica();
            renderHUDComponents.pausarTemporizador();
        } else {
            gestorDeAudio.reproducirMusica();
            renderHUDComponents.reanudarTemporizador();
        }
    }

    public SistemaDeNiveles getSistemaDeNiveles() {
        return sistemaDeNiveles;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Pausa getMenuPause() {
        return pausa;
    }

    public RenderHUDComponents getRenderHUDComponents() {
        return renderHUDComponents;
    }

    public void reproducirSonidoPausa() {
        gestorDeAudio.reproducirEfecto("pausa", AUDIO_PAUSA);
    }

    public void reproducirSonidoUpgrade() {
        gestorDeAudio.reproducirEfecto("upgrade", AUDIO_UPGRADE);
    }

    public ControladorEnemigos getControladorEnemigos() {
        return controladorEnemigos;
    }

    public ExtendViewport getViewport() {
        return viewport;
    }

    public MainGame getGame() {
        return game;
    }

    public HUD getHud() {
        return hud;
    }

    public static OrthographicCamera getCamara() {
        return camara;
    }

    public FlechaTragaperras getFlechaTragaperras() {
        return flechaTragaperras;
    }

    public RenderVentanaJuego1 getRenderVentanaJuego1() {
        return renderVentanaJuego1;
    }
    public Array<ObjetosXP> getObjetosXP() {
        return objetosXP;
    }
}

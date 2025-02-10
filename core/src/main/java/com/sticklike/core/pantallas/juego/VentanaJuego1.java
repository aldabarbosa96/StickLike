package com.sticklike.core.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.MainGame;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoPowerUp;
import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaquePiedra;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoOro;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.gameplay.sistemas.SistemaDeEventos;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.entidades.jugador.*;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;
import com.sticklike.core.gameplay.sistemas.SistemaDeMejoras;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;
import com.sticklike.core.pantallas.popUps.PopUpMejoras;
import com.sticklike.core.pantallas.gameOver.VentanaGameOver;
import com.sticklike.core.ui.HUD;
import com.sticklike.core.gameplay.progreso.Mejora;
import com.sticklike.core.ui.MenuPause;
import com.sticklike.core.ui.RenderHUDComponents;
import static com.sticklike.core.utilidades.GestorConstantes.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.List;

public class VentanaJuego1 implements Screen {
    public static final int worldWidth = (int) VIRTUAL_WIDTH;
    public static final int worldHeight = (int) VIRTUAL_HEIGHT;
    private float cameraOffsetY = CAMERA_OFFSET_Y;
    private MainGame game;

    // Render básico
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camara;
    private FitViewport viewport;
    private RenderVentanaJuego renderVentanaJuego;
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
    private AtaquePiedra ataquePiedra;
    private ControladorProyectiles controladorProyectiles;
    private GestorDeAudio gestorDeAudio;
    private PopUpMejoras popUpMejoras;
    private MenuPause menuPause;

    // Arrays de entidades
    private Array<TextoFlotante> textosDanyo;
    private Array<ObjetosXP> objetosXP;
    private Array<Enemigo> enemigosAEliminar;

    private int currentScreenWidth;
    private int currentScreenHeight;

    private boolean pausado = false;

    public VentanaJuego1(MainGame game, int screenWidth, int screenHeight) {
        this.game = game;
        this.currentScreenWidth = screenWidth;
        this.currentScreenHeight = screenHeight;

        inicializarRenderYCamara();
        inicializarJugador();
        inicializarSistemasYControladores();
        inicializarCuadriculaYHUD();
        inicializarListas();
        spawnObjetoPowerUp();

        // Ajustar la posición de la cámara
        actualizarPosCamara();
    }

    private void inicializarRenderYCamara() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camara = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camara);
        viewport.apply();
    }

    private void inicializarJugador() {
        InputsJugador inputJugador = new InputsJugador();
        colisionesJugador = new ColisionesJugador();
        gestorDeAudio = game.gestorDeAudio;
        movimientoJugador = new MovimientoJugador();
        ataquePiedra = new AtaquePiedra(INTERVALO_DISPARO);
        controladorProyectiles = new ControladorProyectiles();

        float playerStartX = worldWidth / 2f;
        float playerStartY = worldHeight / 2f + CAMERA_JUGADOR_OFFSET_Y;
        jugador = new Jugador(playerStartX, playerStartY, inputJugador, colisionesJugador,
            movimientoJugador, ataquePiedra, controladorProyectiles);
    }

    private void inicializarSistemasYControladores() {
        sistemaDeMejoras = new SistemaDeMejoras(jugador, game);
        this.popUpMejoras = new PopUpMejoras(sistemaDeMejoras, this);
        sistemaDeNiveles = new SistemaDeNiveles(jugador, sistemaDeMejoras);

        controladorEnemigos = new ControladorEnemigos(jugador, INTERVALO_SPAWN, this);
        jugador.estableceControladorEnemigos(controladorEnemigos);
    }

    private void inicializarCuadriculaYHUD() {
        renderVentanaJuego = new RenderVentanaJuego((int) GRID_CELL_SIZE);
        hud = new HUD(jugador, sistemaDeNiveles, shapeRenderer, spriteBatch);
        this.renderHUDComponents = hud.getRenderHUDComponents();
        sistemaDeEventos = new SistemaDeEventos(renderHUDComponents, controladorEnemigos, sistemaDeNiveles);

        menuPause = new MenuPause( this);
    }

    private void inicializarListas() {
        textosDanyo = new Array<>();
        objetosXP = new Array<>();
        enemigosAEliminar = new Array<>();
    }

    @Override
    public void show() {
        pausado = false;
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        menuPause.handleInput();

        if (jugador.estaVivo()) {  // booleano invertido, no olvidar!
            game.setScreen(new VentanaGameOver(game));
            return;
        }

        if (!pausado && !menuPause.isPaused()) {
            actualizarLogica(delta, gestorDeAudio);
            reproducirMusica();
        } else {
            pausarMusica();
        }

        renderVentanaJuego.renderizarVentana(delta, this, jugador, objetosXP, controladorEnemigos, textosDanyo, hud, spriteBatch, camara);
        menuPause.render(shapeRenderer);

        popUpMejoras.getUiStage().act(delta);
        popUpMejoras.getUiStage().draw();
    }

    private void reproducirMusica() {
        gestorDeAudio.reproducirMusica();
    }

    private void pausarMusica() {
        gestorDeAudio.pausarMusica();
    }

    private void actualizarLogica(float delta, GestorDeAudio gestorDeAudio) {
        jugador.actualizarLogicaDelJugador(delta, pausado, textosDanyo, gestorDeAudio);
        controladorEnemigos.actualizarSpawnEnemigos(delta);

        actualizarRecogidaXP(delta);
        actualizarTextoFlotante(delta);
        sistemaDeEventos.actualizar();
    }



    private void actualizarRecogidaXP(float delta) {
        for (int i = objetosXP.size - 1; i >= 0; i--) {
            ObjetosXP xp = objetosXP.get(i);
            xp.actualizarObjetoXP(delta, jugador, gestorDeAudio);

            if (xp.colisionaConOtroSprite(jugador.getSprite())) {
                xp.recolectar(gestorDeAudio);
                objetosXP.removeIndex(i);

                if (xp instanceof ObjetoXp objetoXp) {
                    float xpOtorgada;
                    if (objetoXp.isEsXPGorda()) {
                        xpOtorgada = 50f + (float) (Math.random() * 50f);  // 50 a 100
                    } else {
                        xpOtorgada = 10f + (float) (Math.random() * 15f);  // 10 a 25
                    }
                    sistemaDeNiveles.agregarXP(xpOtorgada);

                } else if (xp instanceof ObjetoVida) {
                    float vidaExtra = 6f + (float) (Math.random() * 10f); // 6 a 16
                    float nuevaVida = jugador.getVidaJugador() + vidaExtra;
                    if (nuevaVida > jugador.getMaxVidaJugador()) {
                        nuevaVida = jugador.getMaxVidaJugador();
                    }
                    jugador.setVidaJugador(nuevaVida);
                    Gdx.app.log("Recolección", "ObjetoVida recolectado. Vida extra otorgada: " + vidaExtra);

                } else if (xp instanceof ObjetoOro) {
                    jugador.setOroGanado(jugador.getOroGanado() + 1);
                    Gdx.app.log("Recolección", "ObjetoOro recolectado. Oro extra otorgado: " + 1
                        + ". Total oro: " + jugador.getOroGanado());
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
        camara.position.set(
            jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f,
            jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f + cameraOffsetY,
            0
        );
        camara.update();
    }

    public void mostrarPopUpDeMejoras(final List<Mejora> mejoras) {
        popUpMejoras.mostrarPopUpMejoras(mejoras);
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


    @Override
    public void resize(int width, int height) {
        currentScreenWidth = width;
        currentScreenHeight = height;
        viewport.update(width, height, true);
        popUpMejoras.getUiStage().getViewport().update(width, height, true);
        hud.resize(width, height);
        menuPause.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        renderVentanaJuego.dispose();
        popUpMejoras.getUiStage().dispose();
        popUpMejoras.getUiSkin().dispose();
        menuPause.dispose();

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
        sistemaDeEventos.dispose();
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

    public MenuPause getMenuPause() {
        return menuPause;
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
}

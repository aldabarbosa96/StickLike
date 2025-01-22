package com.sticklike.core.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sticklike.core.MainGame;
import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueCalcetin;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaquePiedra;
import com.sticklike.core.entidades.objetos.objetosxp.ObjetoVida;
import com.sticklike.core.entidades.objetos.objetosxp.ObjetoXpCaca;
import com.sticklike.core.gameplay.sistemas.SistemaDeEventos;
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
import com.sticklike.core.ui.RenderHUDComponents;
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
    private AtaqueCalcetin ataqueCalcetin;
    private ControladorProyectiles controladorProyectiles;
    private ControladorAudio controladorAudio;
    private PopUpMejoras popUpMejoras;

    // Arrays de entidades
    private Array<TextoFlotante> textosDanyo;
    private Array<ObjetosXP> objetosXP;
    private Array<Enemigo> enemigosAEliminar;

    // Control de pausa
    private boolean pausado = false;
    private boolean efectoSonidoPopUpReproducido = false;


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
        ataquePiedra = new AtaquePiedra(GestorConstantes.PLAYER_SHOOT_INTERVAL);
        ataqueCalcetin = new AtaqueCalcetin(GestorConstantes.ATAQUE_CALCETIN_INTERVALO);
        controladorProyectiles = new ControladorProyectiles();

        // Creamos al jugador en el centro aproximado del mapa, pasando el controlador de inputs
        float playerStartX = WORLD_WIDTH / 2f;
        float playerStartY = WORLD_HEIGHT / 2f + 125f;
        jugador = new Jugador(playerStartX, playerStartY, inputJugador, colisionesJugador, movimientoJugador, ataquePiedra, ataqueCalcetin, controladorProyectiles);
    }

    private void inicializarSistemasYControladores() {
        // Primero inicializa los sistemas base
        sistemaDeMejoras = new SistemaDeMejoras(jugador, game);
        this.popUpMejoras = new PopUpMejoras(sistemaDeMejoras, this);
        sistemaDeNiveles = new SistemaDeNiveles(jugador, sistemaDeMejoras);

        // Luego los controladores dependientes
        controladorEnemigos = new ControladorEnemigos(jugador, GestorConstantes.INTERVALO_SPAWN, this);
        jugador.estableceControladorEnemigos(controladorEnemigos);
    }


    private void inicializarCuadriculaYHUD() {
        // Renderizado de la cuadrícula (mapa) y el HUD
        renderVentanaJuego = new RenderVentanaJuego((int) GestorConstantes.GRID_CELL_SIZE);
        hud = new HUD(jugador, sistemaDeNiveles, shapeRenderer, spriteBatch);
        this.renderHUDComponents = hud.getRenderHUDComponents();
        sistemaDeEventos = new SistemaDeEventos(renderHUDComponents,controladorEnemigos);

    }

    private void inicializarListas() {
        // Listas de texto daño, objetos XP y enemigos a eliminar
        textosDanyo = new Array<>();
        objetosXP = new Array<>();
        enemigosAEliminar = new Array<>();
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
        if (jugador.estaVivo()) { // este boleano está invertido todo --> mejorar en un futuro para mayor claridad
            game.setScreen(new VentanaGameOver(game));
            return;
        }
        if (!pausado) {
            actualizarLogica(delta, controladorAudio);
            reproducirMusica();

        } else pausarMusica();

        // Renderizado de los componentes principales de la ventana
        renderVentanaJuego.renderizarVentana(delta, this, jugador, objetosXP, controladorEnemigos, textosDanyo, hud, spriteBatch, camara);

        // Stage de la UI (pop-up de upgrades)
        popUpMejoras.getUiStage().act(delta);
        popUpMejoras.getUiStage().draw();
    }
    private void reproducirMusica(){
        controladorAudio.reproducirMusica();
        efectoSonidoPopUpReproducido = false;
    }
    private void pausarMusica(){ // genera además un efecto de sonido "wow"
        controladorAudio.pausarMusica();
        if (!efectoSonidoPopUpReproducido) {
            controladorAudio.reproducirEfecto6();
            efectoSonidoPopUpReproducido = true;
        }
    }

    private void actualizarLogica(float delta, ControladorAudio controladorAudio) {
        jugador.actualizarLogicaDelJugador(delta, pausado, textosDanyo, controladorAudio);
        controladorEnemigos.actualizarSpawnEnemigos(delta);
        actualizarEnemigos();
        actualizarRecogidaXP(delta);
        actualizarTextoFlotante(delta);
        sistemaDeEventos.actualizar();


    }

    private void actualizarEnemigos() {
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

    private void actualizarRecogidaXP(float delta) {
        // Recoger XP
        for (int i = objetosXP.size - 1; i >= 0; i--) {
            ObjetosXP xp = objetosXP.get(i);
            xp.actualizarObjetoXP(delta, jugador, controladorAudio); // Efecto de recogida

            if (xp.colisionaConOtroSprite(jugador.getSprite())) {
                xp.recolectar(controladorAudio);
                objetosXP.removeIndex(i);
                if (xp instanceof ObjetoXpCaca) {
                    sistemaDeNiveles.agregarXP(15f + (float) (Math.random() * (25.5f - 15.75f)));
                } else if (xp instanceof ObjetoVida) {
                    jugador.setVidaJugador(jugador.getVidaJugador() + (float) Math.random() * (15 - 5) + 5);
                    if (jugador.getVidaJugador() >= jugador.getMaxVidaJugador()) {
                        jugador.setVidaJugador(jugador.getMaxVidaJugador());
                    }
                }
            }
        }
    }

    private void actualizarTextoFlotante(float delta) {
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
        popUpMejoras.mostrarPopUpMejoras(mejoras);
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
        popUpMejoras.getUiStage().getViewport().update(width, height, true);
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
        popUpMejoras.getUiStage().dispose();
        popUpMejoras.getUiSkin().dispose();

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

    public void setPausado(boolean pausa) {
        this.pausado = pausa;
    }

    public RenderHUDComponents getRenderHUDComponents() {
        return renderHUDComponents;
    }
}

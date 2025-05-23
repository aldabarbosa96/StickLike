package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.sticklike.core.entidades.jugador.StatsJugador;
import com.sticklike.core.entidades.objetos.texto.FontManager;
import com.sticklike.core.entidades.renderizado.particulas.ParticleManager;
import com.sticklike.core.pantallas.menus.ventanas.MenuPrincipal;
import com.sticklike.core.utilidades.DebugStats;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;
import com.badlogic.gdx.graphics.profiling.GLProfiler;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.VIRTUAL_HEIGHT;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.VIRTUAL_WIDTH;

/**
 * MainGame es la clase principal del juego desarrollado con libGDX
 * Extiende de {@link Game}, para poder administrar diversas pantallas simultáneamente.
 * Se encarga de la carga de recursos, la inicialización del menú principal y la gestión del ciclo de vida del juego.
 */
public class MainGame extends Game {
    public VentanaJuego1 ventanaJuego1;
    public GestorDeAudio gestorDeAudio;
    private SpriteBatch batch;
    private DebugStats debugStats;
    private StatsJugador statsJugador;
    private ExtendViewport mainViewport;
    private static GLProfiler glProfiler;

    @Override
    public void create() {
        glProfiler = new GLProfiler(Gdx.graphics);
        glProfiler.enable();

        // Cargamos todos los recursos al iniciar
        GestorDeAssets.cargarRecursos();
        FontManager.initFonts();
        ParticleManager.get().loadAllParticles();
        gestorDeAudio = GestorDeAudio.getInstance();
        batch = new SpriteBatch();
        debugStats = new DebugStats();
        mainViewport = new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        setScreen(new MenuPrincipal(this));
    }

    @Override
    public void render() {
        super.render();
        debugStats.update();
        mainViewport.apply();
        batch.setProjectionMatrix(mainViewport.getCamera().combined);
        batch.begin();
        debugStats.render(batch, mainViewport.getWorldHeight());
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        FontManager.disposeFonts();
        GestorDeAssets.dispose();
        if (ventanaJuego1 != null) {
            ventanaJuego1.dispose();
            ventanaJuego1 = null;
        }
        gestorDeAudio.dispose();
        batch.dispose();
        debugStats.dispose();
    }

    @Override
    public void resize(int width, int height) {
        mainViewport.update(width, height, true);
        super.resize(width, height);
    }

    public VentanaJuego1 getVentanaJuego1() {
        return ventanaJuego1;
    }

    public static GLProfiler getGlProfiler() {
        return glProfiler;
    }

    public StatsJugador getStatsJugador() {
        return statsJugador;
    }
    public void setStatsJugador(StatsJugador statsJugador) {
        this.statsJugador = statsJugador;
    }

}

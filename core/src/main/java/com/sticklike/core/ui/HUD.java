package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Clase que encapsula el renderizado del HUD del juego. Se centralizan las llamadas a begin()/end() de los renderizadores.
 */
public class HUD {
    private RenderHUDComponents renderHUDComponents;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;
    private final OrthographicCamera hudCamara;
    private final StretchViewport hudViewport;
    private static final float desplazamientoVertHUD = DESPLAZAMIENTO_VERTICAL_HUD;

    public HUD(Jugador jugador, SistemaDeNiveles sistemaDeNiveles, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        this.renderHUDComponents = new RenderHUDComponents(shapeRenderer, spriteBatch, jugador, sistemaDeNiveles);
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
        this.hudCamara = new OrthographicCamera();
        this.hudViewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT - 30, hudCamara);
        this.hudViewport.apply();
        this.hudCamara.update();
        renderHUDComponents.crearSlots();
    }

    public void renderizarHUD(float delta) {
        // Actualizamos viewport y proyectores
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudCamara.combined);
        shapeRenderer.setProjectionMatrix(hudCamara.combined);
        float hudHeight = HUD_HEIGHT + desplazamientoVertHUD;

        // 1. Dibujar el fondo
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderHUDComponents.renderizarFondoHUD();
        shapeRenderer.end();

        // 2. Dibujar las líneas horizontales
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        renderHUDComponents.renderizarLineasHorizontalesCuadricula(hudHeight);
        shapeRenderer.end();

        // 3. Dibujar el resto de los elementos HUD
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderHUDComponents.renderizarMarcoHUD();
        renderHUDComponents.renderizarLineaVerticalCuadricula(hudHeight);
        renderHUDComponents.renderizarBarraXPFondo();
        shapeRenderer.end();

        // Dibujos con SpriteBatch
        spriteBatch.begin();
        renderHUDComponents.renderizarTextoNivelPlayer();
        renderHUDComponents.renderizarTemporizador(delta);
        renderHUDComponents.renderizarBarraXPInfo();
        renderHUDComponents.renderizarStatsJugadorBloque1();
        renderHUDComponents.renderizarStatsJugadorBloque2();
        renderHUDComponents.dibujarAtaqueBasico(manager.get(ARMA_PIEDRA, Texture.class));
        renderHUDComponents.renderizarMarcosMejoras();
        spriteBatch.end();

        MensajesChat.getInstance().update();

        renderHUDComponents.getChatlogStage().act(delta);
        renderHUDComponents.getChatlogStage().draw();
        renderHUDComponents.getHudStage().act(delta);
        renderHUDComponents.getHudStage().draw();
    }


    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
    }

    public void dispose() {
        renderHUDComponents.dispose();
        shapeRenderer.dispose();
        spriteBatch.dispose();
    }

    public RenderHUDComponents getRenderHUDComponents() {
        return renderHUDComponents;
    }
}

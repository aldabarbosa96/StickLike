package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

/**
 * Clase que encapsula el renderizado del HUD del juego. Se centralizan las llamadas a begin()/end() de los renderizadores.
 */
public class HUD {
    private RenderHUDComponents renderHUDComponents;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;
    private final OrthographicCamera hudCamara;
    private final FillViewport hudViewport;
    private static final float desplazamientoVertHUD = DESPLAZAMIENTO_VERTICAL_HUD;

    public HUD(Jugador jugador, SistemaDeNiveles sistemaDeNiveles, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        this.renderHUDComponents = new RenderHUDComponents(shapeRenderer, spriteBatch, jugador, sistemaDeNiveles);
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
        this.hudCamara = new OrthographicCamera();
        this.hudViewport = new FillViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, hudCamara);
        this.hudCamara.update();
        renderHUDComponents.crearSlots();
    }

    public void renderizarHUD(float delta) {
        // Actualizamos viewport y proyectores
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudCamara.combined);
        shapeRenderer.setProjectionMatrix(hudCamara.combined);
        float hudHeight = HUD_HEIGHT + desplazamientoVertHUD;

        // Dibujos con ShapeRenderer (tipo Filled)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderHUDComponents.renderizarFondoHUD();
        renderHUDComponents.renderizarMarcoHUD();
        renderHUDComponents.renderizarLineaVerticalCuadricula(hudHeight);
        renderHUDComponents.renderizarBarraXPFondo();
        shapeRenderer.end();

        // Dibujos con ShapeRenderer (tipo Line)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        renderHUDComponents.renderizarLineasHorizontalesCuadricula(hudHeight);
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

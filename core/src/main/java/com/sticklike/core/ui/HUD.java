package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;
import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Clase responsable de dibujar la información de interfaz del jugador en pantalla (vida, experiencia, nivel, etc.)
 * El renderizado de los elementos del HUD se encapsula a través de RenderHUDComponents
 */
public class HUD {
    private RenderHUDComponents renderHUDComponents;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;
    private final OrthographicCamera hudCamara;
    private final FillViewport hudViewport;
    private static final float desplazamientoVertHUD = DESPLAZAMIENTO_VERTICAL_HUD;

    public HUD(Jugador jugador, SistemaDeNiveles sistemaDeNiveles, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        this.renderHUDComponents = new RenderHUDComponents(shapeRenderer, spriteBatch, jugador, sistemaDeNiveles); // Clase encargada de renderizar los componentes del HUD
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
        this.hudCamara = new OrthographicCamera();
        this.hudViewport = new FillViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, hudCamara);
        this.hudCamara.update();

    }

     //todo -- > falta implementar elementos en el HUD (iconos stats player, mejoras obtenidas...)
    public void renderizarHUD(float delta) {
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudCamara.combined);
        shapeRenderer.setProjectionMatrix(hudCamara.combined);

        float hudHeight = HUD_HEIGHT + desplazamientoVertHUD;
        renderHUDComponents.renderizarFondoHUD();
        renderHUDComponents.renderizarLineasHorizontalesCuadricula(hudHeight);
        renderHUDComponents.renderizarMarcoHUD();
        renderHUDComponents.renderizarBarraXP();
        renderHUDComponents.renderizarTextoNivelPlayer();
        renderHUDComponents.renderizarTemporizador(delta);
        renderHUDComponents.renderizarStatsJugador();
        renderHUDComponents.renderizarMasStatsJugador();
    }

    public void resize(int width, int height) { // ajusta el viewport al redimensionar la ventana
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

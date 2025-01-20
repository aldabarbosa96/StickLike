package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;
import com.sticklike.core.utilidades.GestorConstantes;

/**
 * Clase responsable de dibujar la información de interfaz del jugador en pantalla (vida, experiencia, nivel, etc.)
 * <p>
 * El renderizado de los elementos del HUD se encapsula a través de RenderHUDComponents
 */
public class HUD {
    private RenderHUDComponents renderHUDComponents;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;
    private final OrthographicCamera hudCamara;
    private final FillViewport hudViewport;
    private static final float DESPLAZAMIENTO_VERTICAL_HUD = GestorConstantes.DESPLAZAMIENTO_VERTICAL_HUD;


    /**
     * @param jugador          referencia al jugador, para consultar su vida
     * @param sistemaDeNiveles referencia al sistema que maneja la XP y el nivel del jugador
     * @param shapeRenderer    usado para dibujar rectángulos y líneas (barras, grids)
     * @param spriteBatch      usado para renderizar texturas e iconos
     */
    public HUD(Jugador jugador, SistemaDeNiveles sistemaDeNiveles, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        this.renderHUDComponents = new RenderHUDComponents(shapeRenderer, spriteBatch, jugador, sistemaDeNiveles); // Clase encargada de renderizar los componentes del HUD
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;

        // Configuramos la cámara y el viewport
        this.hudCamara = new OrthographicCamera();
        this.hudViewport = new FillViewport(GestorConstantes.VIRTUAL_WIDTH, GestorConstantes.VIRTUAL_HEIGHT, hudCamara);
        this.hudCamara.update();
    }

    /**
     * Dibuja el HUD estático en la parte inferior de la pantalla:
     * fondo, barras de salud y XP, iconos, texto de nivel, etc.
     * todo -- > falta implementar elementos en el HUD (stats player, mejoras obtenidas...)
     */
    public void renderizarHUD(float delta) {
        hudViewport.apply();
        spriteBatch.setProjectionMatrix(hudCamara.combined);
        shapeRenderer.setProjectionMatrix(hudCamara.combined);

        float hudHeight = 200f + DESPLAZAMIENTO_VERTICAL_HUD;
        renderHUDComponents.renderizarFondoHUD();
        renderHUDComponents.renderizarLineasHorizontalesCuadricula(hudHeight);
        renderHUDComponents.renderizarMarcoHUD();
        renderHUDComponents.renderizarBarraDeSalud();
        renderHUDComponents.renderizarBarraXP();
        renderHUDComponents.renderizarTextoSalud(hudHeight);
        renderHUDComponents.renderizarIconoVidaJugador();
        renderHUDComponents.renderizarTextoNivelPlayer();
        renderHUDComponents.renderizarTemporizador(delta);
    }


    /**
     * Ajusta el viewport del HUD al redimensionar la ventana
     *
     * @param width,height nuevo ancho, alto
     */
    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
    }
    public void dispose() {
        renderHUDComponents.dispose();
        shapeRenderer.dispose();
        spriteBatch.dispose();
    }

}

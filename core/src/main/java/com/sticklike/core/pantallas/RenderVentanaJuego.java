package com.sticklike.core.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.interfaces.ObjetosXP;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.gameplay.managers.ControladorEnemigos;
import com.sticklike.core.ui.HUD;

/**
 * Se encarga de dibujar la ventana principal del juego (mapa) y sus elementos
 */
public class RenderVentanaJuego {
    private ShapeRenderer shapeRenderer;
    private final int tamanyoCeldas;

    /**
     * Inicializa un ShpareRenderer para renderizar las líneas
     * @param tamanyoCeldas tamaño de las celdas de la cuadrícula
     */
    public RenderVentanaJuego(int tamanyoCeldas) {
        this.shapeRenderer = new ShapeRenderer();
        this.tamanyoCeldas = tamanyoCeldas;
    }
    public void renderizarVentana(float delta, VentanaJuego ventanaJuego, Jugador jugador, Array<ObjetosXP> objetosXP,
                                  ControladorEnemigos controladorEnemigos, Array<TextoFlotante> textosDanyo, HUD hud, SpriteBatch spriteBatch, OrthographicCamera camara) {
        // Limpiamos la pantalla
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render del mapa (cuadrícula)
        ventanaJuego.actualizarPosCamara();
        this.renderizarLineasCuadricula(camara);

        // Dibujo de entidades
        spriteBatch.setProjectionMatrix(camara.combined);
        spriteBatch.begin();

        jugador.aplicarRenderizadoAlJugador(spriteBatch);
        jugador.getControladorProyectiles().renderizarProyectiles(spriteBatch);

        for (ObjetosXP xp : objetosXP) {
            xp.renderizarObjetoXP(spriteBatch);
        }

        controladorEnemigos.renderizarEnemigos(spriteBatch);

        for (TextoFlotante txt : textosDanyo) {
            txt.renderizarTextoFlotante(spriteBatch);
        }

        spriteBatch.end();

        // Encima renderizamos el HUD
        hud.renderizarHUD();
    }

    public void renderizarLineasCuadricula(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.86f, 1);

        // Calcula los límites visibles de la cámara.
        float startX = camera.position.x - camera.viewportWidth / 2;
        float endX = camera.position.x + camera.viewportWidth / 2;
        float startY = camera.position.y - camera.viewportHeight / 2;
        float endY = camera.position.y + camera.viewportHeight / 2;

        // Dibuja las líneas de la cuadrícula.
        for (float x = startX - (startX % tamanyoCeldas); x <= endX; x += tamanyoCeldas) {
            shapeRenderer.line(x, startY, x, endY);
        }
        for (float y = startY - (startY % tamanyoCeldas); y <= endY; y += tamanyoCeldas) {
            shapeRenderer.line(startX, y, endX, y);
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}

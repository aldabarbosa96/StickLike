package com.sticklike.core.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Se encarga de dibujar la cuadrícula que conforma el mapa (cuaderno)
 */
public class RenderizadoCuadriculaMapa {
    private final ShapeRenderer shapeRenderer;
    private final int tamanyoCeldas;

    /**
     * Inicializa un ShpareRenderer para renderizar las líneas
     * @param tamanyoCeldas tamaño de las celdas de la cuadrícula
     */
    public RenderizadoCuadriculaMapa(int tamanyoCeldas) {
        this.shapeRenderer = new ShapeRenderer();
        this.tamanyoCeldas = tamanyoCeldas;
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

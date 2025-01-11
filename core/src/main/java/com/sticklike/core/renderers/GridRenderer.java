package com.sticklike.core.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GridRenderer {
    private ShapeRenderer shapeRenderer;
    private int cellSize;

    public GridRenderer(int cellSize) {
        this.shapeRenderer = new ShapeRenderer();
        this.cellSize = cellSize;
    }

    public void render(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.86f, 1);

        // Calcula los límites visibles de la cámara.
        float startX = camera.position.x - camera.viewportWidth / 2;
        float endX = camera.position.x + camera.viewportWidth / 2;
        float startY = camera.position.y - camera.viewportHeight / 2;
        float endY = camera.position.y + camera.viewportHeight / 2;

        // Dibuja las líneas de la cuadrícula.
        for (float x = startX - (startX % cellSize); x <= endX; x += cellSize) {
            shapeRenderer.line(x, startY, x, endY);
        }
        for (float y = startY - (startY % cellSize); y <= endY; y += cellSize) {
            shapeRenderer.line(startX, y, endX, y);
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}

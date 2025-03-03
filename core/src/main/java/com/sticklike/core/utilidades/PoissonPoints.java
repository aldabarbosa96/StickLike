package com.sticklike.core.utilidades;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PoissonPoints {
    // Instancia única de la clase (Singleton)
    private static final PoissonPoints INSTANCE = new PoissonPoints();

    // Constructor privado para evitar instanciaciones
    private PoissonPoints() {}

    public static PoissonPoints getInstance() {
        return INSTANCE;
    }

    /**
     * Implementación del algoritmo de Poisson Disk Sampling para generar puntos en una región rectangular.
     *
     * @param minX límite mínimo en X
     * @param minY límite mínimo en Y
     * @param maxX límite máximo en X
     * @param maxY límite máximo en Y
     * @param r distancia mínima entre puntos
     * @param k número máximo de intentos por punto activo
     * @return Array de puntos generados
     */
    public Array<Vector2> generatePoissonPoints(float minX, float minY, float maxX, float maxY, float r, int k) {
        Array<Vector2> points = new Array<>();
        Array<Vector2> activeList = new Array<>();

        float width = maxX - minX;
        float height = maxY - minY;
        float cellSize = r / (float) Math.sqrt(2);
        int gridCols = (int) Math.ceil(width / cellSize);
        int gridRows = (int) Math.ceil(height / cellSize);
        Vector2[][] grid = new Vector2[gridCols][gridRows];

        // Elegir un punto inicial aleatorio
        float initialX = MathUtils.random(minX, maxX);
        float initialY = MathUtils.random(minY, maxY);
        Vector2 initial = new Vector2(initialX, initialY);
        points.add(initial);
        activeList.add(initial);
        int gridX = (int) ((initial.x - minX) / cellSize);
        int gridY = (int) ((initial.y - minY) / cellSize);
        grid[gridX][gridY] = initial;

        while (activeList.size > 0) {
            int index = MathUtils.random(activeList.size - 1);
            Vector2 point = activeList.get(index);
            boolean found = false;
            for (int i = 0; i < k; i++) {
                float angle = MathUtils.random(0, MathUtils.PI2);
                float distance = MathUtils.random(r, 2 * r);
                float newX = point.x + distance * MathUtils.cos(angle);
                float newY = point.y + distance * MathUtils.sin(angle);
                if (newX < minX || newX >= maxX || newY < minY || newY >= maxY) continue;
                Vector2 newPoint = new Vector2(newX, newY);
                int newGridX = (int) ((newX - minX) / cellSize);
                int newGridY = (int) ((newY - minY) / cellSize);
                boolean ok = true;
                // Comprobar vecinos en el grid
                for (int ix = Math.max(0, newGridX - 2); ix <= Math.min(gridCols - 1, newGridX + 2); ix++) {
                    for (int iy = Math.max(0, newGridY - 2); iy <= Math.min(gridRows - 1, newGridY + 2); iy++) {
                        if (grid[ix][iy] != null) {
                            if (newPoint.dst2(grid[ix][iy]) < r * r) {
                                ok = false;
                                break;
                            }
                        }
                    }
                    if (!ok) break;
                }
                if (ok) {
                    points.add(newPoint);
                    activeList.add(newPoint);
                    grid[newGridX][newGridY] = newPoint;
                    found = true;
                }
            }
            if (!found) {
                activeList.removeIndex(index);
            }
        }
        return points;
    }
}

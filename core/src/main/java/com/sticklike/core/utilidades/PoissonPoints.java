package com.sticklike.core.utilidades;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PoissonPoints {
    // Instancia única de la clase (Singleton)
    private static final PoissonPoints INSTANCE = new PoissonPoints();

    // Constructor privado para evitar instanciaciones
    private PoissonPoints() {
    }

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
     * @param r    distancia mínima entre puntos
     * @param k    número máximo de intentos por punto activo
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

        // Punto inicial
        float initialX = MathUtils.random(minX, maxX);
        float initialY = MathUtils.random(minY, maxY);
        Vector2 initial = new Vector2(initialX, initialY);
        points.add(initial);
        activeList.add(initial);
        int gx = (int) ((initialX - minX) / cellSize);
        int gy = (int) ((initialY - minY) / cellSize);
        grid[gx][gy] = initial;

        float r2 = r * r;  // distancia mínima al cuadrado

        while (activeList.size > 0) {
            int index = MathUtils.random(activeList.size - 1);
            Vector2 point = activeList.get(index);
            boolean found = false;

            for (int i = 0; i < k; i++) {
                float angle = MathUtils.random(0, MathUtils.PI2);
                float distance = MathUtils.random(r, 2 * r);
                float newX = point.x + MathUtils.cos(angle) * distance;
                float newY = point.y + MathUtils.sin(angle) * distance;

                if (newX < minX || newX >= maxX || newY < minY || newY >= maxY) continue;

                int newGX = (int) ((newX - minX) / cellSize);
                int newGY = (int) ((newY - minY) / cellSize);

                boolean ok = true;
                // comprobamos vecinos dentro de radio
                for (int ix = Math.max(0, newGX - 2); ix <= Math.min(gridCols - 1, newGX + 2) && ok; ix++) {
                    for (int iy = Math.max(0, newGY - 2); iy <= Math.min(gridRows - 1, newGY + 2); iy++) {
                        Vector2 neighbor = grid[ix][iy];
                        if (neighbor != null) {
                            // usamos dst2 evitando crear vectores temporales
                            if ((newX - neighbor.x) * (newX - neighbor.x) + (newY - neighbor.y) * (newY - neighbor.y) < r2) {
                                ok = false;
                                break;
                            }
                        }
                    }
                }

                if (ok) {
                    // Solo aquí creamos el Vector2 porque vale para points y activeList
                    Vector2 newPoint = new Vector2(newX, newY);
                    points.add(newPoint);
                    activeList.add(newPoint);
                    grid[newGX][newGY] = newPoint;
                    found = true;
                    break;
                }
            }

            if (!found) {
                activeList.removeIndex(index);
            }
        }

        return points;
    }

}

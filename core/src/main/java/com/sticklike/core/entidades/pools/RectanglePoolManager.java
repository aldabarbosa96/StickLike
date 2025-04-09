package com.sticklike.core.entidades.pools;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

/**
 * Clase que gestiona las pools de objetos Rectangle (colisiones jugador y enemigos)
 */
public class RectanglePoolManager {

    // Clase interna que extiende Pool para crear Rectangles
    private static class RectanglePool extends Pool<Rectangle> {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    }

    // Instancia del pool
    private static final RectanglePool pool = new RectanglePool();

    public static Rectangle obtenerRectangulo(float x, float y, float width, float height) {
        Rectangle rect = pool.obtain();
        rect.set(x, y, width, height);
        return rect;
    }

    public static void liberarRectangulo(Rectangle rect) {
        pool.free(rect);
    }
}

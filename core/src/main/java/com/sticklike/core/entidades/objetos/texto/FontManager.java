package com.sticklike.core.entidades.objetos.texto;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Gestiona las fuentes utilizadas en el juego. Inicializa y libera la fuente principal para los textos de daño.
 */

public class FontManager {

    // Fuente principal para los textos de daño
    public static BitmapFont damageFont;

    public static void initFonts() {
        damageFont = new BitmapFont();

        damageFont.getData().setScale(1.0f);
    }

    public static void disposeFonts() {
        if (damageFont != null) {
            damageFont.dispose();
        }
    }
}

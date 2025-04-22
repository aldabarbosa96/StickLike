package com.sticklike.core.entidades.objetos.texto;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Gestiona las fuentes utilizadas en el juego. Inicializa y libera la fuente principal para los textos de daño.
 */

public class FontManager {
    public static BitmapFont damageFont;
    public static BitmapFont menuFont;
    public static BitmapFont hudFont;
    public static BitmapFont pauseFont;
    public static BitmapFont debugFont;

    public static void initFonts() {
        damageFont = new BitmapFont();
        damageFont.getData().setScale(1f);
        menuFont = new BitmapFont();
        menuFont.getData().setScale(1.2f);
        hudFont = new BitmapFont();
        pauseFont = new BitmapFont();
        debugFont = new BitmapFont();
    }

    public static void disposeFonts() {
        if (damageFont != null) {
            damageFont.dispose();
        }
    }

    public static BitmapFont getDamageFont() {
        return damageFont;
    }

    public static BitmapFont getMenuFont() {
        return menuFont;
    }

    public static BitmapFont getHudFont() {
        return hudFont;
    }

    public static BitmapFont getPauseFont() {
        return pauseFont;
    }

    public static BitmapFont getDebugFont() {
        return debugFont;
    }
}

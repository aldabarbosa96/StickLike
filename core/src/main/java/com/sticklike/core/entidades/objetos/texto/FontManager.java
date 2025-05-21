package com.sticklike.core.entidades.objetos.texto;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.VIRTUAL_WIDTH;

/**
 * Gestiona las fuentes utilizadas en el juego. Inicializa y libera la fuente principal para los textos de daño.
 */

public class FontManager {
    public static BitmapFont damageFont;
    public static BitmapFont menuFont;
    public static BitmapFont menuPJFont;
    public static BitmapFont menuTitleFont;
    public static BitmapFont hudFont;
    public static BitmapFont hudBLUEFont;
    public static BitmapFont hudBLACKFont;
    public static BitmapFont pauseFont;
    public static BitmapFont debugFont;
    public static BitmapFont popUpFont;
    public static BitmapFont plusFont;
    public static BitmapFont loadingFont;
    public static BitmapFont slotFont;
    public static BitmapFont messageFont;

    private static float scale;
    private static final Color DARKER_WHITE = new Color(0.95f, 0.95f, 0.95f, 1);

    public static void initFonts() {

        scale = Gdx.graphics.getWidth() / VIRTUAL_WIDTH;
        FreeTypeFontGenerator dmgFont = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/font1.ttf"));
        FreeTypeFontGenerator mainFont = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/fontPmaker.ttf"));
        FreeTypeFontGenerator slotsFont = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/fontPresStart2.ttf"));
        FreeTypeFontGenerator messagesFont = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/fontIndieFlower.ttf"));

        // Texto mensajes
        FreeTypeFontGenerator.FreeTypeFontParameter messagesParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        messagesParam.size = Math.round(10 * scale);
        messagesParam.color = Color.WHITE;
        messageFont = mainFont.generateFont(messagesParam);


        // texto daño
        FreeTypeFontGenerator.FreeTypeFontParameter damageParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        damageParam.size = Math.round(10 * scale);
        damageParam.color = Color.WHITE;
        damageParam.borderWidth = 3;
        damageParam.borderColor = Color.BLACK;
        damageFont = dmgFont.generateFont(damageParam);

        // texto menús
        FreeTypeFontGenerator.FreeTypeFontParameter menuParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        menuParam.size = Math.round(16 * scale);
        menuParam.color = Color.BLUE;
        menuFont = mainFont.generateFont(menuParam);

        FreeTypeFontGenerator.FreeTypeFontParameter menuPJParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        menuPJParam.size = Math.round(13 * scale);
        menuPJParam.color = Color.WHITE;
        menuPJFont = mainFont.generateFont(menuPJParam);

        // texto títulos menús
        FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParam.size = Math.round(75 * scale);
        titleParam.color = DARKER_WHITE;
        titleParam.borderWidth = 7.5f;
        titleParam.borderColor = Color.BLUE;
        titleParam.padTop = 8;
        titleParam.padBottom = 8;
        titleParam.padLeft = 8;
        titleParam.padRight = 8;
        menuTitleFont = mainFont.generateFont(titleParam);

        // texto hud
        FreeTypeFontGenerator.FreeTypeFontParameter hudParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        hudParam.size = Math.round(12 * scale);
        hudParam.color = Color.WHITE;
        hudFont = mainFont.generateFont(hudParam);

        // texto hudAzul
        FreeTypeFontGenerator.FreeTypeFontParameter hudBlueParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        hudBlueParam.size = Math.round(25 * scale);
        hudBlueParam.color = Color.WHITE;
        hudBlueParam.borderColor = Color.BLUE;
        hudBlueParam.borderWidth = 2.5f;
        hudBLUEFont = mainFont.generateFont(hudBlueParam);

        // texto hud negro
        FreeTypeFontGenerator.FreeTypeFontParameter hudBlackParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        hudBlackParam.size = Math.round(15 * scale);
        hudBlackParam.color = Color.WHITE;
        hudBlackParam.borderColor = Color.BLACK;
        hudBlackParam.borderWidth = 2.5f;
        hudBLACKFont = mainFont.generateFont(hudBlackParam);

        // texto poup
        FreeTypeFontGenerator.FreeTypeFontParameter popUpParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        popUpParam.size = Math.round(14 * scale);
        popUpParam.color = Color.WHITE;
        popUpFont = mainFont.generateFont(popUpParam);

        // texto "+"
        FreeTypeFontGenerator.FreeTypeFontParameter plusParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        plusParam.size = Math.round(18 * scale);
        plusParam.color = Color.GREEN;
        plusParam.borderColor = Color.BLACK;
        plusParam.borderWidth = 2.5f;
        plusFont = mainFont.generateFont(plusParam);

        // texto tragaperras
        FreeTypeFontGenerator.FreeTypeFontParameter slotParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        slotParam.size = Math.round(12 * scale);
        slotParam.color = Color.WHITE;
        slotParam.borderColor = Color.RED;
        slotParam.borderWidth = 3f;
        slotFont = slotsFont.generateFont(slotParam);

        // texto loading
        FreeTypeFontGenerator.FreeTypeFontParameter loadingParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        loadingParam.size = Math.round(28 * scale);
        loadingParam.color = Color.BLUE;
        loadingFont = mainFont.generateFont(loadingParam);

        /* texto pausa (sin uso real)
        FreeTypeFontGenerator.FreeTypeFontParameter pauseParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pauseParam.size = 28;
        pauseParam.color = Color.WHITE;
        pauseParam.borderWidth = 2;
        pauseFont = dmgFont.generateFont(pauseParam);*/

        // texto debug
        debugFont = new BitmapFont();

        dmgFont.dispose();
        mainFont.dispose();
        slotsFont.dispose();
        messagesFont.dispose();
    }

    public static void disposeFonts() {

        if (damageFont != null) {
            damageFont.dispose();
            damageFont = null;
        }
        if (menuFont != null) {
            menuFont.dispose();
            menuFont = null;
        }
        if (menuPJFont != null) {
            menuPJFont.dispose();
            menuPJFont = null;
        }
        if (menuTitleFont != null) {
            menuTitleFont.dispose();
            menuTitleFont = null;
        }
        if (hudFont != null) {
            hudFont.dispose();
            hudFont = null;
        }
        if (hudBLUEFont != null) {
            hudBLUEFont.dispose();
            hudBLUEFont = null;
        }
        if (hudBLACKFont != null) {
            hudBLACKFont.dispose();
            hudBLACKFont = null;
        }
        if (popUpFont != null) {
            popUpFont.dispose();
            popUpFont = null;
        }
        if (plusFont != null) {
            plusFont.dispose();
            plusFont = null;
        }
        if (loadingFont != null) {
            loadingFont.dispose();
            loadingFont = null;
        }
        if (slotFont != null) {
            slotFont.dispose();
            slotFont = null;
        }
        if (pauseFont != null) {
            pauseFont.dispose();
            pauseFont = null;
        }
        if (debugFont != null) {
            debugFont.dispose();
            debugFont = null;
        }
        if (messageFont != null) {
            messageFont.dispose();
            messageFont = null;
        }
    }

    public static BitmapFont getDamageFont() {
        return damageFont;
    }

    public static BitmapFont getMenuFont() {
        return menuFont;
    }

    public static BitmapFont getMenuTitleFont() {
        return menuTitleFont;
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

    public static BitmapFont getHudBLUEFont() {
        return hudBLUEFont;
    }

    public static BitmapFont getHudBLACKFont() {
        return hudBLACKFont;
    }

    public static BitmapFont getPopUpFont() {
        return popUpFont;
    }

    public static BitmapFont getLoadingFont() {
        return loadingFont;
    }

    public static float getScale() {
        return scale;
    }

    public static BitmapFont getMenuPJFont() {
        return menuPJFont;
    }

    public static BitmapFont getPlusFont() {
        return plusFont;
    }

    public static BitmapFont getSlotFont() {
        return slotFont;
    }

    public static BitmapFont getMessageFont() {
        return messageFont;
    }
}

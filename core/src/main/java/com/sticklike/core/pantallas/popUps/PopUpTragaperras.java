package com.sticklike.core.pantallas.popUps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.entidades.mobiliario.tragaperras.TragaperrasLogic;
import com.sticklike.core.entidades.objetos.texto.FontManager;
import com.sticklike.core.pantallas.menus.renders.RenderBaseMenus;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Pop-up que simula una ruleta vertical en cada carrete.
 */
public class PopUpTragaperras extends RenderBaseMenus {
    private static final float EXTRA_BORDE = 12.5f;
    private static final float REEL_WIDTH = 120f;
    private static final float REEL_HEIGHT = 225f;
    private static final float SYMBOL_SCALE = 0.5f;
    private static final float SPIN_BASE_DURATION = 2.5f;
    private static final int SPIN_LOOPS = 6;
    private static final float REEL_MARGIN = 23f;

    private static final String[] SYMBOL_KEYS = {RECOLECTABLE_CACA_DORADA, SKATE2, EXAMEN, ALARMA, IMAN, DISKETE, MECHERO, JACKPOT, BORRADOR};
    private final Stage uiStage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
    private Skin uiSkin;
    private Window ventana;
    private Table reelsTable;
    private Table buttonsTable;
    private Group popupGroup;

    // Fondo animado
    private FondoAnimadoPopUp fondoAnimadoPopUp;

    // Cache de drawables de símbolos
    private final Map<String, Drawable> symbolDrawables = new HashMap<>();

    // ScrollPanes (carretes)
    private final List<ScrollPane> reelPanes = new ArrayList<>();

    private boolean initialized = false;
    private boolean abierto = false;
    private TragaperrasLogic logic;
    private Runnable onExit = () -> {
    };

    public PopUpTragaperras() {
        // Lazy init en build()
    }

    public void setOnExitListener(Runnable listener) {
        this.onExit = listener != null ? listener : () -> {
        };
    }


    private void initUI() {
        if (initialized) return;

        uiSkin = crearAspectoUI();

        // Cachear drawables de símbolos
        for (String key : SYMBOL_KEYS) {
            Texture t = manager.get(key, Texture.class);
            symbolDrawables.put(key, new TextureRegionDrawable(new TextureRegion(t)));
        }

        // Ventana principal
        ventana = new Window("<<< ¡JUEGA Y GANA! >>>", uiSkin.get("default-window", Window.WindowStyle.class));
        ventana.setSize(POPUP_WIDTH, POPUP_HEIGHT);
        float titleH = ventana.getTitleLabel().getPrefHeight() * ventana.getTitleLabel().getFontScaleX();
        ventana.getTitleTable().padTop(titleH + 50);
        ventana.getTitleLabel().setAlignment(Align.center);
        ventana.getTitleLabel().setFontScale(1.25f);
        ventana.setModal(true);
        ventana.setMovable(false);
        ventana.defaults().pad(10);

        // Tabla de carretes (vacía, rellenada en build)
        reelsTable = new Table();
        reelsTable.defaults().minHeight(REEL_HEIGHT).expandX().fillX().space(4f);

        // Tabla de botones
        buttonsTable = new Table();
        buttonsTable.defaults().uniformX().fillX();

        // Botón “Jugar 2× caca”
        TextureRegionDrawable cacaDrawable = new TextureRegionDrawable(new TextureRegion(manager.get(RECOLECTABLE_CACA_DORADA, Texture.class)));
        TextButton play2x = new TextButton("Jugar 2x", uiSkin.get("play-button", TextButton.TextButtonStyle.class));
        play2x.add(new Image(cacaDrawable)).size(25, 25).padLeft(10f).padRight(5f);
        play2x.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                logic.spin();
                animateReels(logic.getCurrentResult());
                GestorDeAudio.getInstance().reproducirEfecto("tragaperras", 0.5f);
            }
        });
        play2x.pad(8);

        // Botón “Jugar 1× power-up”
        TextureRegionDrawable powerUpDrawable = new TextureRegionDrawable(new TextureRegion(manager.get(RECOLECTABLE_POWER_UP, Texture.class)));
        TextButton play1x = new TextButton("Jugar 1x", uiSkin.get("play-button", TextButton.TextButtonStyle.class));
        play1x.add(new Image(powerUpDrawable)).size(10, 25).padLeft(10f).padRight(5f);
        play1x.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                logic.spin();
                animateReels(logic.getCurrentResult());
                GestorDeAudio.getInstance().reproducirEfecto("tragaperras", 0.5f);
            }
        });
        play1x.pad(8);

        // Botón “Salir”
        TextButton exitBtn = new TextButton("Salir", uiSkin.get("exit-button", TextButton.TextButtonStyle.class));
        exitBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                onExit.run();
            }
        });
        exitBtn.pad(8);

        buttonsTable.add(play2x).padRight(8);
        buttonsTable.add(play1x).padRight(8);
        buttonsTable.add(exitBtn).padLeft(8);

        // Grupo con borde animado y ventana
        popupGroup = new Group();
        float totalW = POPUP_WIDTH + EXTRA_BORDE * 2;
        float totalH = POPUP_HEIGHT + EXTRA_BORDE * 2;
        popupGroup.setSize(totalW, totalH);
        float posX = (VIRTUAL_WIDTH - POPUP_WIDTH) / 2f - EXTRA_BORDE;
        float posY = (VIRTUAL_HEIGHT - POPUP_HEIGHT + POPUP_POSITION_CORRECTION) / 2f - EXTRA_BORDE;
        popupGroup.setPosition(posX, posY);

        Image border = new Image(crearSombraConBorde(Color.WHITE, 10, Color.WHITE, 2));
        border.setSize(totalW, totalH);
        border.addAction(Actions.forever(Actions.sequence(Actions.color(Color.RED, 1f), Actions.color(Color.GREEN, 0.25f))));
        popupGroup.addActor(border);

        ventana.setPosition(EXTRA_BORDE, EXTRA_BORDE);
        popupGroup.addActor(ventana);
        uiStage.addActor(popupGroup);

        initialized = true;
    }

    public void build(TragaperrasLogic logic) {
        this.logic = logic;
        initUI();

        // Fondo animado detrás del popup
        if (fondoAnimadoPopUp != null) {
            fondoAnimadoPopUp.clearParticles();
            fondoAnimadoPopUp.remove();
        }
        fondoAnimadoPopUp = new FondoAnimadoPopUp(15, 30, manager.get(RECOLECTABLE_CACA_DORADA, Texture.class));
        uiStage.getRoot().addActorAt(0, fondoAnimadoPopUp);
        abierto = true;
        reelPanes.clear();
        reelsTable.clearChildren();

        // Rellenar carretes según resultado actual
        List<Integer> current = logic.getCurrentResult();
        float symbolSize = Math.min(REEL_WIDTH, REEL_HEIGHT) * SYMBOL_SCALE;
        int passes = SPIN_LOOPS + 2;

        for (int i = 0; i < current.size(); i++) {
            Table content = new Table();
            for (int pass = 0; pass < passes; pass++) {
                for (String key : SYMBOL_KEYS) {
                    Image img = new Image(symbolDrawables.get(key));
                    img.setScaling(Scaling.fit);
                    content.add(img).size(symbolSize, symbolSize).row();
                }
            }

            ScrollPane pane = new ScrollPane(content, uiSkin.get("default", ScrollPaneStyle.class));
            pane.setOverscroll(false, false);
            pane.setClamp(false);
            pane.setFlickScroll(false);
            pane.setScrollingDisabled(true, false);
            pane.layout();
            pane.setScrollY(content.getHeight() / 2f - symbolSize / 2f);

            Table slotCell = new Table();
            slotCell.setBackground(uiSkin.getDrawable("reel-bg"));
            slotCell.add(pane).width(REEL_WIDTH).height(REEL_HEIGHT - 2 * REEL_MARGIN).padTop(REEL_MARGIN).padBottom(REEL_MARGIN).center();

            reelsTable.add(slotCell).uniform();
            reelPanes.add(pane);
        }

        // Reconstruir contenido de la ventana
        ventana.clearChildren();
        ventana.add(reelsTable).expand().fill().padTop(ventana.getTitleTable().getPadTop()).row();
        ventana.add(buttonsTable).fillX().padTop(10).padBottom(20);

        uiStage.setKeyboardFocus(ventana);
    }

    private void animateReels(List<Integer> result) {
        float symbolSize = Math.min(REEL_WIDTH, REEL_HEIGHT) * SYMBOL_SCALE;
        float loopHeight = SYMBOL_KEYS.length * symbolSize;

        for (int i = 0; i < reelPanes.size(); i++) {
            ScrollPane pane = reelPanes.get(i);
            pane.clearActions();
            pane.setScrollY(0);

            int sym = MathUtils.clamp(result.get(i), 0, SYMBOL_KEYS.length - 1);
            float delay = i * 0.25f;
            float duration = SPIN_BASE_DURATION + i * 0.15f;
            float endY = SPIN_LOOPS * loopHeight + sym * symbolSize;

            pane.addAction(Actions.sequence(Actions.delay(delay), new ScrollToAction(pane, endY, duration)));
        }
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public boolean isAbierto() {
        return abierto;
    }

    public void clear() {
        if (fondoAnimadoPopUp != null) {
            fondoAnimadoPopUp.clearParticles();
            fondoAnimadoPopUp.remove();
        }
        uiStage.clear();
        abierto = false;
        initialized = false;
    }

    @Override
    public void animarSalida(Runnable callback) {
        clear();
        if (callback != null) callback.run();
    }

    // ───────────────────────── SKIN ─────────────────────────

    private Skin crearAspectoUI() {
        Skin skin = new Skin();

        // Fuentes
        BitmapFont font = FontManager.getHudFont();
        BitmapFont bigFont = FontManager.getMenuFont();
        bigFont.getData().setScale(1.5f);
        skin.add("default-font", font);
        skin.add("big-font", bigFont);

        // LabelStyle por defecto
        Label.LabelStyle lblStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", lblStyle, Label.LabelStyle.class);

        // WindowStyle
        Texture windowBg = crearTexturaUnicolor(0.97f, 0.88f, 0.6f, 1f);
        skin.add("windowBg", windowBg, Texture.class);
        Window.WindowStyle winStyle = new Window.WindowStyle(font, Color.BLUE, new TextureRegionDrawable(new TextureRegion(windowBg)));
        skin.add("default-window", winStyle, Window.WindowStyle.class);

        // ScrollPaneStyle sin scrollbars visibles
        ScrollPaneStyle spStyle = new ScrollPaneStyle();
        skin.add("default", spStyle, ScrollPaneStyle.class);

        // TextButtonStyles
        Texture greenUp = crearTexturaUnicolor(0.2f, 0.8f, 0.2f, 1f);
        Texture greenDown = crearTexturaUnicolor(0.1f, 0.4f, 0.1f, 1f);
        TextButton.TextButtonStyle playStyle = new TextButton.TextButtonStyle();
        playStyle.font = bigFont;
        playStyle.up = new TextureRegionDrawable(new TextureRegion(greenUp));
        playStyle.down = new TextureRegionDrawable(new TextureRegion(greenDown));
        playStyle.fontColor = Color.WHITE;
        skin.add("play-button", playStyle, TextButton.TextButtonStyle.class);

        Texture redUp = crearTexturaUnicolor(0.8f, 0.2f, 0.2f, 1f);
        Texture redDown = crearTexturaUnicolor(0.5f, 0.1f, 0.1f, 1f);
        TextButton.TextButtonStyle exitStyle = new TextButton.TextButtonStyle();
        exitStyle.font = bigFont;
        exitStyle.up = new TextureRegionDrawable(new TextureRegion(redUp));
        exitStyle.down = new TextureRegionDrawable(new TextureRegion(redDown));
        exitStyle.fontColor = Color.WHITE;
        skin.add("exit-button", exitStyle, TextButton.TextButtonStyle.class);

        // Fondo de slots
        Texture slotTex = manager.get(SLOT, Texture.class);
        skin.add("reel-bg", new TextureRegionDrawable(new TextureRegion(slotTex)), Drawable.class);

        return skin;
    }

    private static Texture crearTexturaUnicolor(float r, float g, float b, float a) {
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(r, g, b, a);
        p.fill();
        Texture tex = new Texture(p);
        p.dispose();
        return tex;
    }

    /**
     * Acción que interpola scrollY de un ScrollPane.
     */
    private static class ScrollToAction extends Action {
        private final ScrollPane pane;
        private final float startY, endY, dur;
        private float elapsed;
        private final Interpolation interp = Interpolation.fastSlow;

        ScrollToAction(ScrollPane pane, float toY, float duration) {
            this.pane = pane;
            this.startY = pane.getScrollY();
            this.endY = toY;
            this.dur = duration;
        }

        @Override
        public boolean act(float delta) {
            elapsed = Math.min(elapsed + delta, dur);
            float a = interp.apply(elapsed / dur);
            pane.setScrollY(startY + (endY - startY) * a);
            return elapsed >= dur;
        }
    }
}

package com.sticklike.core.pantallas.popUps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.entidades.objetos.texto.FontManager;
import com.sticklike.core.gameplay.progreso.Mejora;
import com.sticklike.core.pantallas.menus.renders.RenderBaseMenus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Vista del pop‑up de mejoras.
 */
public class PopUpMejoras extends RenderBaseMenus {
    private static final float EXTRA_BORDE = 12.5f;
    private Stage uiStage;
    private Skin uiSkin;
    private Window upgradeWindow;
    private Group popupGroup;
    private final List<TextButton> improvementButtons = new ArrayList<>();
    private int selectedIndex = 0;
    private boolean popUpAbierto = false;
    private FondoAnimadoPopUp fondoAnimadoPopUp;
    private static final Color COLOR_LABEL = new Color(0f, 0.5f, 0.25f, 1);
    private static final Color COLOR_PIXMAP = new Color(0.97f, 0.88f, 0.6f, 1);
    private static final Color COLOR_PIXMAP_GLOW = new Color(0.9f, 0.9f, 0.9f, 0.5f);
    private static final Color COLOR_GREEN_SELECTED = new Color(0f, 0.5f, 0.25f, 1);
    private BitmapFont font;
    private BitmapFont titleFont;
    private float invScale = 1f / FontManager.getScale();

    // Datos mostrados actualmente
    private List<Mejora> currentMejoras = List.of();
    private int rerollCount = 1;

    // Callback hacia el controlador
    private IntConsumer onSelectListener;

    public PopUpMejoras() {
        this.font = FontManager.getPopUpFont();
        this.titleFont = FontManager.getMenuTitleFont();
        font.getData().setScale(invScale);
        uiStage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = crearAspectoUI();
    }

    public void build(List<Mejora> mejoras, int selectedIndex, int rerollCount) {
        this.currentMejoras = mejoras;
        this.selectedIndex = selectedIndex;
        this.rerollCount = rerollCount;

        if (!popUpAbierto) {
            fondoAnimadoPopUp = new FondoAnimadoPopUp(30, 40, manager.get(RECOLECTABLE_XP, Texture.class), manager.get(RECOLECTABLE_XP2, Texture.class), manager.get(RECOLECTABLE_XP3, Texture.class));
            uiStage.addActor(fondoAnimadoPopUp);
            popupGroup = new Group();
            uiStage.addActor(popupGroup);
            popUpAbierto = true;
        } else {
            popupGroup.clearChildren();
        }

        crearVentana();
        updateButtonHighlight();
    }

    public void updateHighlight(int newIndex) {
        this.selectedIndex = newIndex;
        updateButtonHighlight();
    }

    public void clearPopUp() {
        if (!popUpAbierto) return;
        fondoAnimadoPopUp.clearParticles();
        uiStage.clear();
        popupGroup.remove();
        fondoAnimadoPopUp.remove();
        popUpAbierto = false;
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }

    public boolean isPopUpAbierto() {
        return popUpAbierto;
    }

    public void setOnSelectListener(IntConsumer listener) {
        this.onSelectListener = listener;
    }

    private void crearVentana() {
        uiStage.setKeyboardFocus(null);

        Window.WindowStyle wStyle = uiSkin.get("default-window", Window.WindowStyle.class);
        upgradeWindow = new Window(TITULO_POPUP, wStyle);
        upgradeWindow.getTitleLabel().setAlignment(Align.center);
        upgradeWindow.getTitleLabel().setFontScale(1.25f * invScale);
        upgradeWindow.getTitleTable().padTop(50);

        float w = POPUP_WIDTH;
        float h = POPUP_HEIGHT;
        upgradeWindow.setSize(w, h);
        upgradeWindow.padTop(POPUP_HEADER_PADDING);
        upgradeWindow.setModal(true);
        upgradeWindow.setMovable(false);

        improvementButtons.clear();
        llenarOpciones(currentMejoras);

        // Borde + ventana en un group para la sombra
        float totalWidth = w + EXTRA_BORDE * 2;
        float totalHeight = h + EXTRA_BORDE * 2;
        popupGroup.setSize(totalWidth, totalHeight);
        float posX = (VIRTUAL_WIDTH - w) / 2f - EXTRA_BORDE;
        float posY = (VIRTUAL_HEIGHT - h + POPUP_POSITION_CORRECTION) / 2f - EXTRA_BORDE;
        popupGroup.setPosition(posX, posY);

        Image borderImage = new Image(super.crearSombraConBorde(Color.WHITE, 10, Color.WHITE, 2));
        borderImage.setSize(totalWidth, totalHeight);
        borderImage.addAction(Actions.forever(Actions.sequence(Actions.color(Color.PURPLE, 0.5f), Actions.color(Color.BLUE, 0.5f), Actions.color(Color.MAGENTA, 0.5f))));
        popupGroup.addActor(borderImage);

        upgradeWindow.setPosition(EXTRA_BORDE, EXTRA_BORDE);
        popupGroup.addActor(upgradeWindow);

        uiStage.setKeyboardFocus(upgradeWindow);
    }

    private void llenarOpciones(List<Mejora> mejoras) {
        upgradeWindow.clearChildren();
        improvementButtons.clear();

        for (int i = 0; i < mejoras.size(); i++) {
            if (i >= POPUP_BUTTON_LABELS.length) break;
            final int index = i;
            final Mejora mejora = mejoras.get(i);

            // 1) Botón de texto con nombre y descripción
            TextButton.TextButtonStyle tbs = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
            TextButton btn = new TextButton(mejora.getNombreMejora() + POPUP_FOOTER + mejora.getDescripcionMejora() + POPUP_FOOTER2, tbs);
            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.center);

            // 2) Fila contenedora
            Table rowTable = new Table();
            rowTable.defaults().center();

            float iconsizePlus = 35;
            float iconSize = 32;
            if (mejora.getNombreMejora().contains("DILDO") || mejora.getNombreMejora().contains("LLUVIA")) {
                iconSize = iconsizePlus;
            }
            float horizontalPadding = 12f;
            float innerPadding = 4f;

            // 2a) Etiqueta HAB / stat en celda cuadrada con padding al borde
            String estiloLabel = mejora.getTipoMejora().equals("HAB") ? "hab" : "stat";
            Label labelTipo = new Label(mejora.getTipoMejora(), uiSkin, estiloLabel);
            rowTable.add(labelTipo).size(iconSize, iconSize).padLeft(horizontalPadding).padRight(innerPadding).center();

            // 2b) Botón con texto ocupa el espacio central, con paddings internos
            rowTable.add(btn).expandX().fillX().center().padLeft(innerPadding).padRight(innerPadding);

            // 3) ICONO + ETIQUETA “NEW” con tamaño fijo
            float tagWidth = 30, tagHeight = 20f;
            Group iconGroup = new Group();
            iconGroup.setSize(iconSize, iconSize);
            if (mejora.getIcono() != null) {
                Image iconImage = new Image(mejora.getIcono());
                iconImage.setSize(iconSize, iconSize);
                iconImage.setPosition(0, 0);
                iconGroup.addActor(iconImage);

                if (mejora.getIdHabilidad() != null) {
                    String idHab = mejora.getIdHabilidad();
                    if (idHab.contains("_")) {
                        float scale = 1.25f;
                        float extra = 2f;
                        float extra2 = 5f;

                        BitmapFont plusFont = FontManager.getPlusFont();
                        Label.LabelStyle plusStyle = new Label.LabelStyle(plusFont, Color.GREEN);

                        Label plus = new Label("+", plusStyle);
                        plus.setFontScale(invScale * scale);

                        float px = iconSize - plus.getPrefWidth() / 2f + extra;
                        float py = iconSize - plus.getPrefHeight() / 2f - extra2;
                        plus.setPosition(px, py);

                        iconGroup.addActor(plus);
                    } else {
                        Texture newTex = manager.get(NEW, Texture.class);
                        Image newTag = new Image(newTex);
                        newTag.setSize(tagWidth, tagHeight);
                        newTag.setPosition(-18f, iconSize - tagHeight + 5f);
                        iconGroup.addActor(newTag);
                    }
                }
            }
            Container<Group> iconContainer = new Container<>(iconGroup);

            // 3c) Celda cuadrada para el icono con padding al borde
            rowTable.add(iconContainer).size(iconSize, iconSize).padLeft(innerPadding).padRight(horizontalPadding).center();

            // 4) Añadir la fila al upgradeWindow
            upgradeWindow.row();
            upgradeWindow.add(rowTable).expandX().fillX().center().pad(BUTTON_PADDING);
            improvementButtons.add(btn);

            // 5) Listener de selección
            btn.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent e, float x, float y, int p, int b) {
                    if (onSelectListener != null) onSelectListener.accept(index);
                    return true;
                }
            });
        }

        //  Botón de Reroll
        Table rerollTable = new Table();
        Texture diceTexture = manager.get(DADOS, Texture.class);
        TextureRegionDrawable diceDrawable = new TextureRegionDrawable(new TextureRegion(diceTexture));
        Image diceImage = new Image(diceDrawable);
        float scale = 0.45f;
        diceImage.setSize(diceDrawable.getMinWidth() * scale, diceDrawable.getMinHeight() * scale);
        diceImage.setScaling(Scaling.stretch);
        Container<Image> diceContainer = new Container<>(diceImage);
        diceContainer.size(diceImage.getWidth(), diceImage.getHeight());

        Label rerollLabel = new Label("x" + rerollCount, uiSkin);
        rerollLabel.setColor(Color.BLUE);

        rerollTable.add(diceContainer).padLeft(2).size(diceImage.getWidth(), diceImage.getHeight());
        rerollTable.add(rerollLabel).padLeft(5).padRight(2);

        TextButton rerollButton = new TextButton("", uiSkin.get("default-button", TextButton.TextButtonStyle.class));
        rerollButton.add(rerollTable);
        improvementButtons.add(rerollButton);

        final int rerollIndex = mejoras.size();
        rerollButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int p, int b) {
                if (onSelectListener != null) onSelectListener.accept(rerollIndex);
                return true;
            }
        });

        upgradeWindow.row().padTop(35);
        upgradeWindow.add(rerollButton).center();
    }

    private void updateButtonHighlight() {
        TextButton.TextButtonStyle defaultStyle = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle selectedBlueStyle = uiSkin.get("selected-button", TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle selectedGreenStyle = uiSkin.get("selected-button-green", TextButton.TextButtonStyle.class);

        for (int i = 0; i < improvementButtons.size(); i++) {
            TextButton btn = improvementButtons.get(i);
            if (i < currentMejoras.size()) {
                Mejora mej = currentMejoras.get(i);
                if (i == selectedIndex) {
                    btn.setStyle(mej.getTipoMejora().equals("HAB") ? selectedBlueStyle : selectedGreenStyle);
                } else {
                    btn.setStyle(defaultStyle);
                }
            } else { // reroll
                btn.setStyle(i == selectedIndex ? selectedBlueStyle : defaultStyle);
            }
        }
    }

    private Skin crearAspectoUI() {
        Skin skin = new Skin();
        BitmapFont copy = new BitmapFont(font.getData(), font.getRegion(), false);
        skin.add("default-font", copy);

        Label.LabelStyle defaultLabel = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", defaultLabel);

        BitmapFont small = new BitmapFont();
        small.getData().setScale(0.75f);

        skin.add("hab", new Label.LabelStyle(small, Color.BLUE));
        skin.add("stat", new Label.LabelStyle(small, COLOR_LABEL));

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(COLOR_PIXMAP);
        p.fill();
        Texture bg = new Texture(p);
        p.dispose();
        skin.add("windowBg", bg, Texture.class);
        TextureRegionDrawable bgDraw = new TextureRegionDrawable(new TextureRegion(bg));
        skin.add("default-window", new Window.WindowStyle(copy, Color.BLUE, bgDraw));

        TextButton.TextButtonStyle defBtn = new TextButton.TextButtonStyle();
        defBtn.font = copy;
        defBtn.fontColor = Color.BLACK;
        skin.add("default-button", defBtn);

        Pixmap hiPx = new Pixmap(8, 12, Pixmap.Format.RGBA8888);
        hiPx.setColor(COLOR_PIXMAP_GLOW);
        hiPx.fill();
        Texture hiTx = new Texture(hiPx);
        hiPx.dispose();
        NinePatchDrawable hiD = new NinePatchDrawable(new NinePatch(hiTx, 3, 3, 3, 3));
        hiD.setMinHeight(50);

        TextButton.TextButtonStyle selBlue = new TextButton.TextButtonStyle();
        selBlue.font = copy;
        selBlue.up = hiD;
        selBlue.fontColor = Color.BLUE;
        skin.add("selected-button", selBlue);

        TextButton.TextButtonStyle selGreen = new TextButton.TextButtonStyle();
        selGreen.font = copy;
        selGreen.up = hiD;
        selGreen.fontColor = COLOR_GREEN_SELECTED;
        skin.add("selected-button-green", selGreen);

        return skin;
    }

    public void dispose() {
        if (uiSkin != null) {
            uiSkin.dispose();
            uiSkin = null;
        }
        if (uiStage != null) {
            uiStage.dispose();
            uiStage = null;
        }
    }

    @Override
    public void animarSalida(Runnable callback) { // todo --> usar en un futuro
        // 1‑ Animamos la ventana + borde (popupGroup) con el helper heredado
        /*animarSalida(popupGroup, () -> {
            // 2‑ Cuando termine, desvanecemos también el fondo animado
            fondoAnimadoPopUp.addAction(Actions.sequence(Actions.fadeOut(0.25f), Actions.run(() -> {
                popupGroup.remove();
                fondoAnimadoPopUp.remove();
                fondoAnimadoPopUp.clearParticles();
                popUpAbierto = false;
                if (callback != null) callback.run();
            })));
        });*/
    }
}

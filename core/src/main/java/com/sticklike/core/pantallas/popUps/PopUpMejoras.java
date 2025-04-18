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
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
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

    // Datos mostrados actualmente
    private List<Mejora> currentMejoras = List.of();
    private int rerollCount = 1;

    // Callback hacia el controlador
    private IntConsumer onSelectListener;

    public PopUpMejoras() {
        uiStage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = crearAspectoUI();
    }

    public void build(List<Mejora> mejoras, int selectedIndex, int rerollCount) {
        this.currentMejoras = mejoras;
        this.selectedIndex = selectedIndex;
        this.rerollCount = rerollCount;

        if (!popUpAbierto) {
            fondoAnimadoPopUp = new FondoAnimadoPopUp();
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
        upgradeWindow = new Window(POPUP_HEADER, wStyle);
        upgradeWindow.getTitleLabel().setAlignment(Align.center);
        upgradeWindow.getTitleLabel().setFontScale(1.25f);
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

        Image borderImage = new Image(crearSombraConBorde(Color.DARK_GRAY, 10, Color.BLUE, 2));
        borderImage.setSize(totalWidth, totalHeight);
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

            TextButton.TextButtonStyle tbs = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
            TextButton btn = new TextButton(mejora.getNombreMejora() + POPUP_FOOTER + mejora.getDescripcionMejora() + POPUP_FOOTER2, tbs);
            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.center);

            Table rowTable = new Table();
            rowTable.defaults().center();

            // Etiqueta HAB / stat
            String estiloLabel = mejora.getTipoMejora().equals("HAB") ? "hab" : "stat";
            Label labelTipo = new Label(mejora.getTipoMejora(), uiSkin, estiloLabel);
            rowTable.add(labelTipo).width(25).center().padLeft(10).padRight(-10f);

            rowTable.add(btn).expandX().fillX().center();

            // Icono
            if (mejora.getIcono() != null) {
                Image iconImage = new Image(mejora.getIcono());
                iconImage.setScale(1.25f);
                Container<Image> iconContainer = new Container<>(iconImage);
                rowTable.add(iconContainer).width(25).center().padRight(10f).padLeft(-10f).height(25);
            } else {
                rowTable.add().width(25);
            }

            upgradeWindow.row();
            upgradeWindow.add(rowTable).expandX().fillX().center().pad(BUTTON_PADDING);
            improvementButtons.add(btn);

            btn.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent e, float x, float y, int p, int b) {
                    if (onSelectListener != null) onSelectListener.accept(index);
                    return true;
                }
            });
        }

        // Botón REROLL
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
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        Label.LabelStyle defaultLabel = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", defaultLabel);

        BitmapFont small = new BitmapFont();
        small.getData().setScale(0.75f);

        skin.add("hab", new Label.LabelStyle(small, Color.BLUE));
        skin.add("stat", new Label.LabelStyle(small, new Color(0f, 0.5f, 0.25f, 1)));

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(new Color(0.97f, 0.88f, 0.6f, 1));
        p.fill();
        Texture bg = new Texture(p);
        p.dispose();
        skin.add("windowBg", bg, Texture.class);
        TextureRegionDrawable bgDraw = new TextureRegionDrawable(new TextureRegion(bg));
        skin.add("default-window", new Window.WindowStyle(font, Color.BLUE, bgDraw));

        TextButton.TextButtonStyle defBtn = new TextButton.TextButtonStyle();
        defBtn.font = font;
        defBtn.fontColor = Color.BLACK;
        skin.add("default-button", defBtn);

        Pixmap hiPx = new Pixmap(8, 12, Pixmap.Format.RGBA8888);
        hiPx.setColor(new Color(0.9f, 0.9f, 0.9f, 0.5f));
        hiPx.fill();
        Texture hiTx = new Texture(hiPx);
        hiPx.dispose();
        NinePatchDrawable hiD = new NinePatchDrawable(new NinePatch(hiTx, 3, 3, 3, 3));
        hiD.setMinHeight(50);

        TextButton.TextButtonStyle selBlue = new TextButton.TextButtonStyle();
        selBlue.font = font;
        selBlue.up = hiD;
        selBlue.fontColor = Color.BLUE;
        skin.add("selected-button", selBlue);

        TextButton.TextButtonStyle selGreen = new TextButton.TextButtonStyle();
        selGreen.font = font;
        selGreen.up = hiD;
        selGreen.fontColor = new Color(0f, 0.5f, 0.25f, 1);
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
        animarSalida(popupGroup, () -> {
            // 2‑ Cuando termine, desvanecemos también el fondo animado
            fondoAnimadoPopUp.addAction(Actions.sequence(
                Actions.fadeOut(0.25f),
                Actions.run(() -> {
                    popupGroup.remove();
                    fondoAnimadoPopUp.remove();
                    popUpAbierto = false;
                    if (callback != null) callback.run();
                })
            ));
        });
    }
}

package com.sticklike.core.entidades.mobiliario.tragaperras;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.pantallas.menus.renders.RenderBaseMenus;

import java.util.ArrayList;
import java.util.List;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Pop-up que aparece al romper la tragaperras.
 * Pausa la partida y permite girar los carretes con ENTER / botón A.
 */
public class PopUpTragaperras extends RenderBaseMenus {
    private Stage uiStage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
    private Skin uiSkin = crearAspectoUI();
    private Window ventana;
    private final List<Label> reelLabels = new ArrayList<>();
    private TragaperrasLogic logic;
    private boolean abierto = false;

    public void build(TragaperrasLogic logic) {
        this.logic = logic;

        if (!abierto) {
            uiStage.clear();
            abierto = true;
        } else {
            uiStage.clear();
        }

        ventana = new Window("¡Gira y gana!", uiSkin.get("default-window", Window.WindowStyle.class));
        ventana.setSize(POPUP_WIDTH, POPUP_HEIGHT);
        ventana.setModal(true);
        ventana.setMovable(false);

        // Carretes
        Table reelsTable = new Table();
        reelLabels.clear();
        for (int i = 0; i < logic.getCurrentResult().size(); i++) {
            Label lbl = new Label("?", uiSkin);
            reelLabels.add(lbl);
            reelsTable.add(lbl).pad(10);
        }

        // Botón "Girar"
        TextButton spinBtn = new TextButton("Girar", uiSkin.get("default-button", TextButton.TextButtonStyle.class));
        spinBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                logic.spin();
            }
        });

        ventana.add(reelsTable).expandX().center().row();
        ventana.add(spinBtn).padTop(25f);

        // Borde + sombra (idéntico al pop-up de mejoras)
        float bord = 12.5f;
        Group popupGroup = new Group();
        float totalW = POPUP_WIDTH + bord * 2, totalH = POPUP_HEIGHT + bord * 2;
        popupGroup.setSize(totalW, totalH);
        popupGroup.setPosition((VIRTUAL_WIDTH - totalW) / 2f, (VIRTUAL_HEIGHT - totalH) / 2f);

        Image sombra = new Image(crearSombraConBorde(Color.DARK_GRAY, 10, Color.BLUE, 2));
        sombra.setSize(totalW, totalH);
        popupGroup.addActor(sombra);

        ventana.setPosition(bord, bord);
        popupGroup.addActor(ventana);

        uiStage.addActor(popupGroup);

        // Escuchar al modelo para refrescar resultados
        logic.addListener(this::updateUI);
    }

    private void updateUI(List<Integer> resultado) {
        for (int i = 0; i < resultado.size() && i < reelLabels.size(); i++) {
            reelLabels.get(i).setText(String.valueOf(resultado.get(i)));
        }
    }

    /* -------------------------------------------------- */
    /*  Acceso / limpieza                                 */
    /* -------------------------------------------------- */

    public Stage getUiStage() {
        return uiStage;
    }

    public boolean isAbierto() {
        return abierto;
    }

    public void clear() {
        uiStage.clear();
        abierto = false;
    }

    public void dispose() {
        if (uiSkin != null) uiSkin.dispose();
        if (uiStage != null) uiStage.dispose();
    }

    /* -------------------------------------------------- */
    /*  Helpers de aspecto (copiados de PopUpMejoras)     */
    /* -------------------------------------------------- */

    private Skin crearAspectoUI() {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);
        skin.add("default", new Label.LabelStyle(font, Color.WHITE));

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(0.97f, 0.88f, 0.6f, 1);
        p.fill();
        Texture bg = new Texture(p);
        p.dispose();
        skin.add("windowBg", bg, Texture.class);
        skin.add("default-window", new Window.WindowStyle(font, Color.BLUE, new TextureRegionDrawable(new TextureRegion(bg))));

        TextButton.TextButtonStyle btn = new TextButton.TextButtonStyle();
        btn.font = font;
        btn.fontColor = Color.BLACK;
        skin.add("default-button", btn);

        return skin;
    }

    @Override
    public void animarSalida(Runnable callback) {

    }
}

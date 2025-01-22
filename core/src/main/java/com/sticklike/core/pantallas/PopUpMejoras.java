package com.sticklike.core.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.sticklike.core.gameplay.mejoras.Mejora;
import com.sticklike.core.gameplay.sistemas.SistemaDeMejoras;
import com.sticklike.core.utilidades.GestorConstantes;

import java.util.List;

public class PopUpMejoras {
    // UI Scene2D (pop-up upgrades)
    private Stage uiStage;
    private Skin uiSkin;
    private boolean pausado;
    private SistemaDeMejoras sistemaDeMejoras;
    private VentanaJuego ventanaJuego;

    public PopUpMejoras(SistemaDeMejoras sistemaDeMejoras, VentanaJuego ventanaJuego) {
        this.ventanaJuego = ventanaJuego;
        uiStage = new Stage(new FillViewport(GestorConstantes.VIRTUAL_WIDTH, GestorConstantes.VIRTUAL_HEIGHT));
        uiSkin = crearAspectoUI();
        this.pausado = false;
        this.sistemaDeMejoras = sistemaDeMejoras;
    }

    /**
     * Crea un {@link Skin} con un fondo simple (un Pixmap de color) para mostrar la ventana de upgrades.
     *
     * @return un Skin con estilo definido para la ventana y botones
     */
    public Skin crearAspectoUI() {
        Skin skin = new Skin();

        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        // Pixmap de 1x1 con color amarillo/ocre suave
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.97f, 0.88f, 0.6f, 1f));
        pixmap.fill();
        Texture pixmapTexture = new Texture(pixmap);
        pixmap.dispose();

        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(pixmapTexture);

        Window.WindowStyle wStyle = new Window.WindowStyle(font, Color.BLUE, backgroundDrawable);
        skin.add("default-window", wStyle);

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = font;
        skin.add("default-button", tbs);

        return skin;
    }

    public void mostrarPopUpMejoras(final List<Mejora> mejoras) {
        ventanaJuego.setPausado(true);
        ventanaJuego.getRenderHUDComponents().pausarTemporizador();

        Window.WindowStyle wStyle = uiSkin.get("default-window", Window.WindowStyle.class);
        final Window upgradeWindow = new Window(GestorConstantes.POPUP_HEADER, wStyle);
        upgradeWindow.getTitleLabel().setAlignment(Align.center);

        float w = GestorConstantes.POPUP_WIDTH;
        float h = GestorConstantes.POPUP_HEIGHT;
        upgradeWindow.setSize(w, h);
        upgradeWindow.setPosition((GestorConstantes.VIRTUAL_WIDTH - w)  / 2f, (GestorConstantes.VIRTUAL_HEIGHT - h + 150f) / 2f);
        upgradeWindow.padTop(75f);
        upgradeWindow.setModal(true);
        upgradeWindow.setMovable(false);

        // Añadimos botones por cada mejora
        for (int i = 0; i < mejoras.size(); i++) {
            final int index = i;
            final Mejora mejora = mejoras.get(i);

            TextButton.TextButtonStyle tbs = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
            TextButton btn = new TextButton((i + 1) + ") " + mejora.getNombreMejora() + GestorConstantes.POPUP_FOOTER + mejora.getDescripcionMejora()
                + GestorConstantes.POPUP_FOOTER, tbs);

            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.center);
            btn.getLabel().setColor(Color.BLACK);

            upgradeWindow.row().pad(0);
            upgradeWindow.add(btn).width(350).pad(7);
        }

        // Listener de teclado para 1,2,3
        upgradeWindow.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.NUM_1) {
                    seleccionarMejora(0, mejoras, upgradeWindow);
                    return true;
                } else if (keycode == Input.Keys.NUM_2) {
                    seleccionarMejora(1, mejoras, upgradeWindow);
                    return true;
                } else if (keycode == Input.Keys.NUM_3) {
                    seleccionarMejora(2, mejoras, upgradeWindow);
                    return true;
                }
                return false;
            }
        });

        uiStage.addActor(upgradeWindow);

        // Foco al pop-up
        uiStage.setKeyboardFocus(upgradeWindow);

        // Stage recibe el input
        InputMultiplexer im = new InputMultiplexer(uiStage);
        Gdx.input.setInputProcessor(im);

    }

    private void seleccionarMejora(int index, List<Mejora> mejoras, Window upgradeWindow) {
        if (index < 0 || index >= mejoras.size()) return;
        sistemaDeMejoras.aplicarMejora(mejoras.get(index));
        upgradeWindow.remove();
        ventanaJuego.setPausado(false);
        ventanaJuego.getRenderHUDComponents().reanudarTemporizador();
        Gdx.input.setInputProcessor(null);
    }


    public Stage getUiStage() {
        return uiStage;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }
}

package com.sticklike.core.pantallas.popUps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
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
import com.sticklike.core.gameplay.progreso.Mejora;
import com.sticklike.core.gameplay.sistemas.SistemaDeMejoras;
import com.sticklike.core.pantallas.juego.VentanaJuego;
import static com.sticklike.core.utilidades.GestorConstantes.*;

import java.util.List;

public class PopUpMejoras {
    private Stage uiStage;
    private Skin uiSkin;
    private SistemaDeMejoras sistemaDeMejoras;
    private VentanaJuego ventanaJuego;
    private Window upgradeWindow; // Referencia a la ventana emergente

    public PopUpMejoras(SistemaDeMejoras sistemaDeMejoras, VentanaJuego ventanaJuego) {
        this.ventanaJuego = ventanaJuego;
        uiStage = new Stage(new FillViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = crearAspectoUI();
        this.sistemaDeMejoras = sistemaDeMejoras;

        // Agregamos listener global para que los botones del mando siempre funcionen
        Controllers.addListener(new MandoListener());
    }

    /**
     * Crea un {@link Skin} con un fondo simple para mostrar la ventana de mejoras.
     */
    public Skin crearAspectoUI() {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        // Pixmap de 1x1 con color amarillo/ocre simulando post-it
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
        ventanaJuego.getMenuPause().bloquearInputs(true);
        ventanaJuego.getRenderHUDComponents().pausarTemporizador();

        Window.WindowStyle wStyle = uiSkin.get("default-window", Window.WindowStyle.class);
        upgradeWindow = new Window(POPUP_HEADER, wStyle);
        upgradeWindow.getTitleLabel().setAlignment(Align.center);

        float w = POPUP_WIDTH;
        float h = POPUP_HEIGHT;
        upgradeWindow.setSize(w, h);
        upgradeWindow.setPosition((VIRTUAL_WIDTH - w) / 2f, (VIRTUAL_HEIGHT - h + 150f) / 2f);
        upgradeWindow.padTop(75f);
        upgradeWindow.setModal(true);
        upgradeWindow.setMovable(false);

        // Añadimos botones para cada mejora
        for (int i = 0; i < mejoras.size(); i++) {
            final int index = i;
            final Mejora mejora = mejoras.get(i);

            TextButton.TextButtonStyle tbs = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
            TextButton btn = new TextButton((i + 1) + ") " + mejora.getNombreMejora() + POPUP_FOOTER + mejora.getDescripcionMejora() + POPUP_FOOTER, tbs);

            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.center);
            btn.getLabel().setColor(Color.BLACK);

            upgradeWindow.row().pad(0);
            upgradeWindow.add(btn).width(350).pad(7);

            // Listener para botones (ratón y teclado)
            btn.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    seleccionarMejora(index, mejoras);
                    return true;
                }
            });
        }

        // Listener de teclado para seleccionar mejoras
        upgradeWindow.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.NUM_1:
                    case Input.Keys.NUMPAD_1:
                        seleccionarMejora(0, mejoras);
                        return true;
                    case Input.Keys.NUM_2:
                    case Input.Keys.NUMPAD_2:
                        seleccionarMejora(1, mejoras);
                        return true;
                    case Input.Keys.NUM_3:
                    case Input.Keys.NUMPAD_3:
                        seleccionarMejora(2, mejoras);
                        return true;
                }
                return false;
            }
        });

        uiStage.addActor(upgradeWindow);
        uiStage.setKeyboardFocus(upgradeWindow);
        InputMultiplexer im = new InputMultiplexer(uiStage);
        Gdx.input.setInputProcessor(im);
    }

    private void seleccionarMejora(int index, List<Mejora> mejoras) {
        if (index < 0 || index >= mejoras.size()) return;
        sistemaDeMejoras.aplicarMejora(mejoras.get(index));
        upgradeWindow.remove();
        ventanaJuego.setPausado(false);
        ventanaJuego.getRenderHUDComponents().reanudarTemporizador();
        ventanaJuego.getMenuPause().bloquearInputs(false);
        Gdx.input.setInputProcessor(null);
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }

    /**
     * Listener del mando para seleccionar mejoras con botones X, Y, B en Xbox y Cuadrado, Triángulo, Círculo en PlayStation.
     */
    private class MandoListener extends ControllerAdapter {
        @Override
        public boolean buttonDown(Controller controller, int buttonIndex) {
            if (upgradeWindow == null) return false;

            //System.out.println("Botón presionado: " + buttonIndex);

            switch (buttonIndex) {
                case 2: // X (Xbox) / Cuadrado (PS)
                    seleccionarMejora(0, sistemaDeMejoras.getMejorasMostradas());
                    return true;
                case 3: // Y (Xbox) / Triángulo (PS)
                    seleccionarMejora(1, sistemaDeMejoras.getMejorasMostradas());
                    return true;
                case 1: // B (Xbox) / Círculo (PS)
                    seleccionarMejora(2, sistemaDeMejoras.getMejorasMostradas());
                    return true;
            }
            return false;
        }
    }
}

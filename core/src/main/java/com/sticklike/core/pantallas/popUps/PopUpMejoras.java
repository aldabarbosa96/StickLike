package com.sticklike.core.pantallas.popUps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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

/**
 * Clase que gestiona la creación y dibujado del Pop-Up de mejoras y maneja sus inputs
 * todo --> separar lógica de inputs en una clase a parte
 */
public class PopUpMejoras {
    private Stage uiStage;
    private Skin uiSkin;
    private SistemaDeMejoras sistemaDeMejoras;
    private VentanaJuego ventanaJuego;
    private Window upgradeWindow;
    private GameInputHandler inputHandler;

    public PopUpMejoras(SistemaDeMejoras sistemaDeMejoras, VentanaJuego ventanaJuego) {
        this.ventanaJuego = ventanaJuego;
        uiStage = new Stage(new FillViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = crearAspectoUI();
        this.sistemaDeMejoras = sistemaDeMejoras;
        this.inputHandler = new GameInputHandler();
    }

    public Skin crearAspectoUI() {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        // Pixmap de 1x1 amarillo
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

        // **Aquí agregas un estilo por defecto para Label**
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.BLACK; // color del texto de los Labels

        // Lo registras con el nombre "default" en el Skin
        skin.add("default", labelStyle, Label.LabelStyle.class);

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
        upgradeWindow.setPosition((VIRTUAL_WIDTH - w) / 2f, (VIRTUAL_HEIGHT - h + POPUP_POSITION_CORRECTION) / 2f);
        upgradeWindow.padTop(POPUP_HEADER_PADDING);
        upgradeWindow.setModal(true);
        upgradeWindow.setMovable(false);

        // array de etiquetas para hasta 4 mejoras
        String[] botonLabels = POPUP_BUTTON_LABELS;

        // Recorremos la lista de mejoras y creamos filas en la tabla
        for (int i = 0; i < mejoras.size(); i++) {
            if (i >= botonLabels.length) break; // Evitamos out of bounds si hay más de 4

            final int index = i;
            final Mejora mejora = mejoras.get(i);

            // Crea el Label para el mando
            Label labelBoton = new Label(botonLabels[i] + ")", uiSkin);
            labelBoton.setColor(Color.BLACK);

            // Crea el TextButton con la descripción de la mejora
            TextButton.TextButtonStyle tbs = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
            TextButton btn = new TextButton(mejora.getNombreMejora() + POPUP_FOOTER + mejora.getDescripcionMejora() + POPUP_FOOTER, tbs);
            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.center);
            btn.getLabel().setColor(Color.BLACK);

            // Añade una nueva fila en la tabla y mete primero el label, luego el botón
            upgradeWindow.row().pad(POPUP_ROW_PADDING); // pad representa el espaciado vertical entre filas
            upgradeWindow.add(labelBoton).width(LABEL_WIDTH).left(); // ancho fijo para la col del label
            upgradeWindow.add(btn).width(BUTTON_WIDTH).pad(BUTTON_PADDING);             // ancho para el botón

            // Listener para la selección con click/touch
            btn.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    onSelectMejora(index, mejoras);
                    return true;
                }
            });
        }

        uiStage.addActor(upgradeWindow);
        uiStage.setKeyboardFocus(upgradeWindow);

        // con InputMultiplexer aseguramos que inputHandler reciba los eventos de teclado antes que uiStage
        InputMultiplexer im = new InputMultiplexer(inputHandler, uiStage);
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(inputHandler);
    }

    private void onSelectMejora(int index, List<Mejora> mejoras) {
        if (index < 0 || index >= mejoras.size()) return;
        sistemaDeMejoras.aplicarMejora(mejoras.get(index));
        upgradeWindow.remove();
        ventanaJuego.setPausado(false);
        ventanaJuego.getRenderHUDComponents().reanudarTemporizador();
        ventanaJuego.getMenuPause().bloquearInputs(false);

        // **Aquí se desactiva el input handler para que no quede escuchando**
        Gdx.input.setInputProcessor(null);
        Controllers.removeListener(inputHandler);
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }

    private class GameInputHandler extends ControllerAdapter implements InputProcessor { // todo --> mover a una clase dedicada en un futuro
        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.NUM_1:
                case Input.Keys.NUMPAD_1:
                    onSelectMejora(0, sistemaDeMejoras.getMejorasMostradas());
                    return true;
                case Input.Keys.NUM_2:
                case Input.Keys.NUMPAD_2:
                    onSelectMejora(1, sistemaDeMejoras.getMejorasMostradas());
                    return true;
                case Input.Keys.NUM_3:
                case Input.Keys.NUMPAD_3:
                    onSelectMejora(2, sistemaDeMejoras.getMejorasMostradas());
                    return true;
            }
            return false; // Importante: si no lo manejamos, devolver false
        }

        @Override
        public boolean buttonDown(Controller controller, int buttonIndex) {
            switch (buttonIndex) {
                case 2: // X
                    onSelectMejora(0, sistemaDeMejoras.getMejorasMostradas());
                    return true;
                case 3: // Y
                    onSelectMejora(1, sistemaDeMejoras.getMejorasMostradas());
                    return true;
                case 1: // B
                    onSelectMejora(2, sistemaDeMejoras.getMejorasMostradas());
                    return true;
                case 0: // A
                    onSelectMejora(3, sistemaDeMejoras.getMejorasMostradas());
                    return true;
            }
            return false;
        }

        @Override public boolean keyUp(int keycode) { return false; }
        @Override public boolean keyTyped(char character) { return false; }
        @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
        }
        @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
        @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
        @Override public boolean scrolled(float amountX, float amountY) { return false; }
    }
}

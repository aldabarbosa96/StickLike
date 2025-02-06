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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.gameplay.progreso.Mejora;
import com.sticklike.core.gameplay.sistemas.SistemaDeMejoras;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import static com.sticklike.core.utilidades.GestorConstantes.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona la creación y dibujado del Pop-Up de mejoras y maneja sus inputs.
 */
public class PopUpMejoras {
    private Stage uiStage;
    private Skin uiSkin;
    private SistemaDeMejoras sistemaDeMejoras;
    private VentanaJuego1 ventanaJuego1;
    private Window upgradeWindow;
    private GameInputHandler inputHandler;

    private List<TextButton> improvementButtons;
    // Índice de selección actual
    private int selectedIndex = 0;

    public PopUpMejoras(SistemaDeMejoras sistemaDeMejoras, VentanaJuego1 ventanaJuego1) {
        this.ventanaJuego1 = ventanaJuego1;
        uiStage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = crearAspectoUI();
        this.sistemaDeMejoras = sistemaDeMejoras;
        this.inputHandler = new GameInputHandler();
        improvementButtons = new ArrayList<>();
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

        return skin;
    }

    public void mostrarPopUpMejoras(final List<Mejora> mejoras) {
        ventanaJuego1.setPausado(true);
        ventanaJuego1.getMenuPause().bloquearInputs(true);
        ventanaJuego1.getRenderHUDComponents().pausarTemporizador();

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

        improvementButtons.clear();

        // Recorremos la lista de mejoras y creamos filas en la tabla.
        // Ahora se añade solo el botón.
        for (int i = 0; i < mejoras.size(); i++) {
            if (i >= POPUP_BUTTON_LABELS.length) break; // Evitamos out of bounds si hay más de 4

            final int index = i;
            final Mejora mejora = mejoras.get(i);

            // Crea el TextButton con la descripción de la mejora
            TextButton.TextButtonStyle tbs = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
            TextButton btn = new TextButton(mejora.getNombreMejora() + POPUP_FOOTER + mejora.getDescripcionMejora() + POPUP_FOOTER, tbs);
            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.center);
            btn.getLabel().setColor(Color.BLACK);

            // Añadir el botón a la lista para luego poder navegar entre ellos
            improvementButtons.add(btn);

            // Añade una nueva fila en la tabla y agrega únicamente el botón.
            upgradeWindow.row().pad(POPUP_ROW_PADDING);
            upgradeWindow.add(btn).width(BUTTON_WIDTH).pad(BUTTON_PADDING);

            // También seguimos añadiendo el listener para touch
            btn.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    onSelectMejora(index, mejoras);
                    return true;
                }
            });
        }

        // Inicialmente, selecciona la primera mejora (si existe)
        if (!improvementButtons.isEmpty()) {
            selectedIndex = 0;
            updateButtonHighlight();
        }

        uiStage.addActor(upgradeWindow);
        uiStage.setKeyboardFocus(upgradeWindow);

        // Con InputMultiplexer aseguramos que inputHandler reciba los eventos de teclado antes que uiStage
        InputMultiplexer im = new InputMultiplexer(inputHandler, uiStage);
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(inputHandler);
    }

    private void updateButtonHighlight() {
        for (int i = 0; i < improvementButtons.size(); i++) {
            TextButton btn = improvementButtons.get(i);
            // Si el botón está seleccionado, se cambia el color del label; de lo contrario, se deja en negro.
            if (i == selectedIndex) {
                btn.getLabel().setColor(Color.BLUE);
            } else {
                btn.getLabel().setColor(Color.DARK_GRAY);
            }
        }
    }

    private void onSelectMejora(int index, List<Mejora> mejoras) {
        if (index < 0 || index >= mejoras.size()) return;
        sistemaDeMejoras.aplicarMejora(mejoras.get(index));
        ventanaJuego1.getRenderHUDComponents().setHabilidadesActivas(sistemaDeMejoras.getHabilidadesActivas());
        upgradeWindow.remove();
        ventanaJuego1.setPausado(false);
        ventanaJuego1.getRenderHUDComponents().reanudarTemporizador();
        ventanaJuego1.getMenuPause().bloquearInputs(false);

        // Desactivamos el input handler para que no siga escuchando
        Gdx.input.setInputProcessor(null);
        Controllers.removeListener(inputHandler);
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }

    private class GameInputHandler extends ControllerAdapter implements InputProcessor {
        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                // Navegación vertical (para teclado)
                case Input.Keys.DOWN:
                case Input.Keys.RIGHT:
                    if (selectedIndex < improvementButtons.size() - 1) {
                        selectedIndex++;
                        updateButtonHighlight();
                    }
                    return true;
                case Input.Keys.UP:
                case Input.Keys.LEFT:
                    if (selectedIndex > 0) {
                        selectedIndex--;
                        updateButtonHighlight();
                    }
                    return true;
                // Confirmar selección con Enter o la tecla A
                case Input.Keys.ENTER:
                case Input.Keys.NUMPAD_ENTER:
                    onSelectMejora(selectedIndex, sistemaDeMejoras.getMejorasMostradas());
                    return true;
                // Selección directa
                case Input.Keys.NUM_1:
                case Input.Keys.NUMPAD_1:
                    selectedIndex = 0;
                    updateButtonHighlight();
                    onSelectMejora(0, sistemaDeMejoras.getMejorasMostradas());
                    return true;
                case Input.Keys.NUM_2:
                case Input.Keys.NUMPAD_2:
                    if (improvementButtons.size() > 1) {
                        selectedIndex = 1;
                        updateButtonHighlight();
                        onSelectMejora(1, sistemaDeMejoras.getMejorasMostradas());
                    }
                    return true;
                case Input.Keys.NUM_3:
                case Input.Keys.NUMPAD_3:
                    if (improvementButtons.size() > 2) {
                        selectedIndex = 2;
                        updateButtonHighlight();
                        onSelectMejora(2, sistemaDeMejoras.getMejorasMostradas());
                    }
                    return true;
            }
            return false;
        }

        private boolean axisLock = false; // variable para bloquear movimientos continuos

        @Override
        public boolean axisMoved(Controller controller, int axisIndex, float value) {
            if (axisIndex == 1) {
                // Si el joystick está en una posición neutral, liberamos el lock
                if (Math.abs(value) < 0.2f) {
                    axisLock = false;
                    return false;
                }
                // Si ya se ha registrado un movimiento y no ha vuelto al centro, ignoramos
                if (axisLock) {
                    return false;
                }
                // Si se inclina hacia abajo (valor positivo) y aún hay opciones hacia abajo
                if (value > 0.5f && selectedIndex < improvementButtons.size() - 1) {
                    selectedIndex++;
                    updateButtonHighlight();
                    axisLock = true;
                    return true;
                }
                // Si se inclina hacia arriba (valor negativo) y aún hay opciones hacia arriba
                else if (value < -0.5f && selectedIndex > 0) {
                    selectedIndex--;
                    updateButtonHighlight();
                    axisLock = true;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean buttonDown(Controller controller, int buttonIndex) {
            // Ejemplo: Para el nuevo Xbox, asumamos que:
            // - Botón 11 = D-pad Up
            // - Botón 12 = D-pad Down
            // (Estos valores son comunes en Windows, pero verifica en tu entorno)
            switch (buttonIndex) {
                case 0: // A (confirmar)
                    onSelectMejora(selectedIndex, sistemaDeMejoras.getMejorasMostradas());
                    return true;
                case 11: // D-pad Up
                    if (selectedIndex > 0) {
                        selectedIndex--;
                        updateButtonHighlight();
                    }
                    return true;
                case 12: // D-pad Down
                    if (selectedIndex < improvementButtons.size() - 1) {
                        selectedIndex++;
                        updateButtonHighlight();
                    }
                    return true;
            }
            return false;
        }


        @Override public boolean keyUp(int keycode) { return false; }
        @Override public boolean keyTyped(char character) { return false; }
        @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
        @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
        @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
        @Override public boolean scrolled(float amountX, float amountY) { return false; }
    }
}

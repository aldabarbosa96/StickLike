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
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.gameplay.progreso.Mejora;
import com.sticklike.core.gameplay.sistemas.SistemaDeMejoras;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un pop-up que muestra mejoras disponibles para el jugador.
 * Permite al usuario seleccionar una mejora usando teclado, ratón o mando.
 */
public class PopUpMejoras {
    private Stage uiStage;
    private Skin uiSkin;
    private SistemaDeMejoras sistemaDeMejoras;
    private VentanaJuego1 ventanaJuego1;
    private Window upgradeWindow;
    private GameInputHandler inputHandler;
    private List<TextButton> improvementButtons;
    private int selectedIndex = 0;
    private boolean popUpAbierto = false;
    private FondoAnimadoPopUp fondoAnimadoPopUp;


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
        // Registramos la fuente en el Skin para que también se libere al hacer skin.dispose()
        skin.add("default-font", font);

        // 1) Creamos Pixmap y su textura para el fondo de la ventana
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.97f, 0.88f, 0.6f, 1f));
        pixmap.fill();
        Texture pixmapTexture = new Texture(pixmap);
        pixmap.dispose();

        skin.add("windowBackgroundTexture", pixmapTexture, Texture.class);
        // Usamos la textura registrada para crear el Drawable
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(skin.getRegion("windowBackgroundTexture"));

        // 2) Creamos el estilo de la ventana
        Window.WindowStyle wStyle = new Window.WindowStyle(font, Color.BLUE, backgroundDrawable);
        // Registramos el estilo en el Skin
        skin.add("default-window", wStyle);

        // 3) Estilo de botón por defecto
        TextButton.TextButtonStyle defaultButtonStyle = new TextButton.TextButtonStyle();
        defaultButtonStyle.font = font;
        defaultButtonStyle.fontColor = Color.BLACK;
        skin.add("default-button", defaultButtonStyle);

        // 4) Creamos Pixmap y textura para el estado "seleccionado"
        Pixmap highlightPixmap = new Pixmap(8, 12, Pixmap.Format.RGBA8888);
        highlightPixmap.setColor(new Color(0.9f, 0.9f, 0.9f, 0.5f));
        highlightPixmap.fill();
        Texture highlightTexture = new Texture(highlightPixmap);
        highlightPixmap.dispose();

        skin.add("highlightTexture", highlightTexture, Texture.class);
        // Creamos el NinePatch a partir de la textura registrada
        NinePatch highlightNinePatch =
            new NinePatch(skin.get("highlightTexture", Texture.class), 3, 3, 3, 3);
        NinePatchDrawable highlightDrawable = new NinePatchDrawable(highlightNinePatch);
        highlightDrawable.setMinHeight(50);

        // 5) Estilo para botón seleccionado (azul)
        TextButton.TextButtonStyle selectedButtonStyle = new TextButton.TextButtonStyle();
        selectedButtonStyle.font = font;
        selectedButtonStyle.up = highlightDrawable;
        selectedButtonStyle.fontColor = Color.BLUE;
        skin.add("selected-button", selectedButtonStyle);

        // 6) Estilo para botón seleccionado (verde) cuando no hay icono
        TextButton.TextButtonStyle selectedButtonGreenStyle = new TextButton.TextButtonStyle();
        selectedButtonGreenStyle.font = font;
        selectedButtonGreenStyle.up = highlightDrawable;
        selectedButtonGreenStyle.fontColor = new Color(0f, 0.5f, 0.25f, 1);
        skin.add("selected-button-green", selectedButtonGreenStyle);

        return skin;
    }


    public void mostrarPopUpMejoras(final List<Mejora> mejoras) {
        this.fondoAnimadoPopUp = new FondoAnimadoPopUp();
        ventanaJuego1.setPausado(true);
        ventanaJuego1.getMenuPause().bloquearInputs(true);
        ventanaJuego1.getRenderHUDComponents().pausarTemporizador();
        popUpAbierto = true;
        uiStage.addActor(fondoAnimadoPopUp);

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
        upgradeWindow.getTitleTable().padTop(15);

        improvementButtons.clear();

        for (int i = 0; i < mejoras.size(); i++) {
            if (i >= POPUP_BUTTON_LABELS.length) break;

            final int index = i;
            final Mejora mejora = mejoras.get(i);

            TextButton.TextButtonStyle tbs = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
            TextButton btn = new TextButton(mejora.getNombreMejora() + POPUP_FOOTER + mejora.getDescripcionMejora() + POPUP_FOOTER2, tbs);
            // Asociamos la mejora al botón para usarlo en la lógica de resaltado
            btn.setUserObject(mejora);
            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.center);

            Table rowTable = new Table();
            rowTable.pad(POPUP_ROW_PADDING + 5f);
            rowTable.defaults().center();

            rowTable.add().width(25).padLeft(8).padRight(-5f); // Espaciado a la izquierda
            rowTable.add(btn).expandX().fillX().center(); // Centrar el botón

            if (mejora.getIcono() != null) {
                Image iconImage = new Image(mejora.getIcono());
                Container<Image> iconContainer = new Container<>(iconImage);
                rowTable.add(iconContainer).width(25).center().padRight(10f).padLeft(-5f).height(25); // Icono a la derecha
            } else {
                rowTable.add().width(25); // Espacio adicional para centrar si no tiene icono la mejora
            }

            upgradeWindow.row();
            upgradeWindow.add(rowTable).expandX().fillX().center().pad(BUTTON_PADDING);

            improvementButtons.add(btn);

            btn.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    onSelectMejora(index, mejoras);
                    return true;
                }
            });
        }

        if (!improvementButtons.isEmpty()) {
            selectedIndex = 0;
            updateButtonHighlight();
        }

        uiStage.addActor(upgradeWindow);
        uiStage.setKeyboardFocus(upgradeWindow);

        Controllers.removeListener(ventanaJuego1.getMenuPause().getInputsMenu());
        InputMultiplexer im = new InputMultiplexer(inputHandler, uiStage);
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(inputHandler);
    }

    private void updateButtonHighlight() {
        TextButton.TextButtonStyle defaultStyle = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle selectedStyle = uiSkin.get("selected-button", TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle selectedGreenStyle = uiSkin.get("selected-button-green", TextButton.TextButtonStyle.class);

        for (int i = 0; i < improvementButtons.size(); i++) {
            TextButton btn = improvementButtons.get(i);
            // Recuperamos la mejora asociada
            Mejora mejora = (Mejora) btn.getUserObject();
            if (i == selectedIndex) {
                if (mejora.getIcono() != null) {
                    btn.setStyle(selectedStyle);
                } else {
                    btn.setStyle(selectedGreenStyle);
                }
            } else {
                btn.setStyle(defaultStyle);
            }
        }
    }

    private void onSelectMejora(int index, List<Mejora> mejoras) {
        if (index < 0 || index >= mejoras.size()) return;
        Controllers.addListener(ventanaJuego1.getMenuPause().getInputsMenu());
        sistemaDeMejoras.aplicarMejora(mejoras.get(index));
        ventanaJuego1.getRenderHUDComponents().setHabilidadesActivas(sistemaDeMejoras.getHabilidadesActivas());
        upgradeWindow.remove();
        fondoAnimadoPopUp.remove();
        ventanaJuego1.setPausado(false);
        ventanaJuego1.getRenderHUDComponents().reanudarTemporizador();
        ventanaJuego1.getMenuPause().bloquearInputs(false);
        popUpAbierto = false;

        // Desactivamos el input handler
        Gdx.input.setInputProcessor(null);
        Controllers.removeListener(inputHandler);

        ventanaJuego1.getSistemaDeNiveles().procesarNivelPendiente();
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

    /**
     * Clase interna para gestionar los inputs (teclado/ratón/mando) del pop-up.
     */
    private class GameInputHandler extends ControllerAdapter implements InputProcessor {
        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
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
                case Input.Keys.ENTER:
                case Input.Keys.NUMPAD_ENTER:
                    onSelectMejora(selectedIndex, sistemaDeMejoras.getMejorasMostradas());
                    return true;
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

        private boolean axisLock = false;

        @Override
        public boolean axisMoved(Controller controller, int axisIndex, float value) {
            if (axisIndex == 1) {
                if (Math.abs(value) < 0.2f) {
                    axisLock = false;
                    return false;
                }
                if (axisLock) return false;
                if (value > 0.5f && selectedIndex < improvementButtons.size() - 1) {
                    selectedIndex++;
                    updateButtonHighlight();
                    axisLock = true;
                    return true;
                } else if (value < -0.5f && selectedIndex > 0) {
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
            switch (buttonIndex) {
                case 0:
                    onSelectMejora(selectedIndex, sistemaDeMejoras.getMejorasMostradas());
                    return true;
                case 11:
                    if (selectedIndex > 0) {
                        selectedIndex--;
                        updateButtonHighlight();
                    }
                    return true;
                case 12:
                    if (selectedIndex < improvementButtons.size() - 1) {
                        selectedIndex++;
                        updateButtonHighlight();
                    }
                    return true;
            }
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            return false;
        }
    }
}

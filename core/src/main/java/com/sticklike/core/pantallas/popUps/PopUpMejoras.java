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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import com.sticklike.core.gameplay.sistemas.SistemaDeMejoras;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.pantallas.menus.renders.RenderBaseMenus;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un pop-up que muestra las mejoras disponibles para el jugador.
 * Se muestran 3 opciones (filas) y, debajo, se añade una fila extra con un botón de reroll.
 */
public class PopUpMejoras extends RenderBaseMenus {
    private static final float EXTRA_BORDE = 12.5f;
    private Stage uiStage;
    private Skin uiSkin;
    private SistemaDeMejoras sistemaDeMejoras;
    private VentanaJuego1 ventanaJuego1;
    private Window upgradeWindow;
    private Group popupGroup;
    private GameInputHandler inputHandler;
    private List<TextButton> improvementButtons;
    private int selectedIndex = 0;
    private boolean popUpAbierto = false;
    private FondoAnimadoPopUp fondoAnimadoPopUp;

    // Variables para el reroll
    private List<Mejora> currentMejoras;
    private int rerollCount = 1;

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

        // Registro de estilo predeterminado para los Label
        Label.LabelStyle defaultLabelStyle = new Label.LabelStyle();
        defaultLabelStyle.font = font;
        defaultLabelStyle.fontColor = Color.WHITE;
        skin.add("default", defaultLabelStyle);

        // Fuente pequeña
        BitmapFont smallFont = new BitmapFont();
        smallFont.getData().setScale(0.75f);

        Label.LabelStyle habLabelStyle = new Label.LabelStyle();
        habLabelStyle.font = smallFont;
        habLabelStyle.fontColor = Color.BLUE;
        skin.add("hab", habLabelStyle);

        Label.LabelStyle statLabelStyle = new Label.LabelStyle();
        statLabelStyle.font = smallFont;
        statLabelStyle.fontColor = new Color(0f, 0.5f, 0.25f, 1);
        skin.add("stat", statLabelStyle);

        // Fondo de la ventana
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.97f, 0.88f, 0.6f, 1f));
        pixmap.fill();
        Texture pixmapTexture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("windowBackgroundTexture", pixmapTexture, Texture.class);

        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(skin.getRegion("windowBackgroundTexture"));

        // Estilo de la ventana
        Window.WindowStyle wStyle = new Window.WindowStyle(font, Color.BLUE, backgroundDrawable);
        skin.add("default-window", wStyle);

        // Botón por defecto
        TextButton.TextButtonStyle defaultButtonStyle = new TextButton.TextButtonStyle();
        defaultButtonStyle.font = font;
        defaultButtonStyle.fontColor = Color.BLACK;
        skin.add("default-button", defaultButtonStyle);

        // Textura para botón resaltado
        Pixmap highlightPixmap = new Pixmap(8, 12, Pixmap.Format.RGBA8888);
        highlightPixmap.setColor(new Color(0.9f, 0.9f, 0.9f, 0.5f));
        highlightPixmap.fill();
        Texture highlightTexture = new Texture(highlightPixmap);
        highlightPixmap.dispose();
        skin.add("highlightTexture", highlightTexture, Texture.class);

        NinePatch highlightNinePatch = new NinePatch(skin.get("highlightTexture", Texture.class), 3, 3, 3, 3);
        NinePatchDrawable highlightDrawable = new NinePatchDrawable(highlightNinePatch);
        highlightDrawable.setMinHeight(50);

        // Botón seleccionado (azul)
        TextButton.TextButtonStyle selectedButtonStyle = new TextButton.TextButtonStyle();
        selectedButtonStyle.font = font;
        selectedButtonStyle.up = highlightDrawable;
        selectedButtonStyle.fontColor = Color.BLUE;
        skin.add("selected-button", selectedButtonStyle);

        // Botón seleccionado (verde)
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

        // Creamos la Window
        Window.WindowStyle wStyle = uiSkin.get("default-window", Window.WindowStyle.class);
        upgradeWindow = new Window(POPUP_HEADER, wStyle);
        upgradeWindow.getTitleLabel().setAlignment(Align.center);
        upgradeWindow.getTitleLabel().setFontScale(1.25f);
       upgradeWindow.getTitleTable().padTop(75);

        float w = POPUP_WIDTH;
        float h = POPUP_HEIGHT;
        upgradeWindow.setSize(w, h);
        upgradeWindow.padTop(POPUP_HEADER_PADDING);
        upgradeWindow.setModal(true);
        upgradeWindow.setMovable(false);

        improvementButtons.clear();
        llenarOpciones(mejoras);

        // Grupo para contener la ventana con borde extra
        popupGroup = new Group();
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

        uiStage.addActor(popupGroup);
        uiStage.setKeyboardFocus(upgradeWindow);

        // Configuramos los inputs
        Controllers.removeListener(ventanaJuego1.getMenuPause().getInputsMenu());
        InputMultiplexer im = new InputMultiplexer(inputHandler, uiStage);
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(inputHandler);

        // Guardamos las mejoras actualmente mostradas
        currentMejoras = mejoras;
    }

    private void llenarOpciones(List<Mejora> mejoras) {
        upgradeWindow.clearChildren();
        improvementButtons.clear();

        // Se crean las filas para cada mejora
        for (int i = 0; i < mejoras.size(); i++) {
            if (i >= POPUP_BUTTON_LABELS.length) break;
            final int index = i;
            final Mejora mejora = mejoras.get(i);

            TextButton.TextButtonStyle tbs = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
            TextButton btn = new TextButton(mejora.getNombreMejora() + POPUP_FOOTER + mejora.getDescripcionMejora() + POPUP_FOOTER2, tbs);
            btn.setUserObject(mejora);
            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.center);

            Table rowTable = new Table();
            rowTable.defaults().center();

            // Se añade la etiqueta de tipo (por ejemplo, "HAB" o "stat")
            String estiloLabel = mejora.getTipoMejora().equals("HAB") ? "hab" : "stat";
            Label labelTipo = new Label(mejora.getTipoMejora(), uiSkin, estiloLabel);
            rowTable.add(labelTipo).width(25).center().padLeft(10).padRight(-10f);

            rowTable.add(btn).expandX().fillX().center();

            // Se añade el icono si está disponible
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
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    onSelectMejora(index, currentMejoras);
                    return true;
                }
            });
        }

        Table rerollTable = new Table();
        Texture diceTexture = manager.get(DADOS, Texture.class);
        TextureRegionDrawable diceDrawable = new TextureRegionDrawable(new TextureRegion(diceTexture));
        Image diceImage = new Image(diceDrawable);

        float scale = 0.45f;
        float diceWidth = diceDrawable.getMinWidth() * scale;
        float diceHeight = diceDrawable.getMinHeight() * scale;
        diceImage.setSize(diceWidth, diceHeight);
        diceImage.setScaling(Scaling.stretch);

        Container<Image> diceContainer = new Container<>(diceImage);
        diceContainer.size(diceWidth, diceHeight);

        Label rerollLabel = new Label("x" + rerollCount, uiSkin);
        rerollLabel.setColor(Color.BLUE);

        rerollTable.add(diceContainer).padLeft(2).size(diceWidth, diceHeight);
        rerollTable.add(rerollLabel).padLeft(5).padRight(2);

        TextButton.TextButtonStyle defaultButtonStyle = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
        TextButton rerollButton = new TextButton("", defaultButtonStyle);
        rerollButton.add(rerollTable);


        rerollButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (rerollCount > 0) {
                    // El botón de reroll se identifica por tener índice igual a currentMejoras.size()
                    onSelectMejora(currentMejoras.size(), currentMejoras);
                }
                return true;
            }
        });

        upgradeWindow.row().padTop(25);
        upgradeWindow.add(rerollButton).center();
        improvementButtons.add(rerollButton);
    }

    private void updateButtonHighlight() {
        TextButton.TextButtonStyle defaultStyle = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle selectedStyle = uiSkin.get("selected-button", TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle selectedGreenStyle = uiSkin.get("selected-button-green", TextButton.TextButtonStyle.class);

        for (int i = 0; i < improvementButtons.size(); i++) {
            TextButton btn = improvementButtons.get(i);
            if (i < currentMejoras.size()) {
                Mejora mejora = currentMejoras.get(i);
                if (i == selectedIndex) {
                    if (mejora.getIcono() != null && mejora.getTipoMejora().equals("HAB")) btn.setStyle(selectedStyle);
                    else btn.setStyle(selectedGreenStyle);
                } else {
                    btn.setStyle(defaultStyle);
                }
            } else { // Botón de reroll
                if (i == selectedIndex) btn.setStyle(selectedStyle);
                else btn.setStyle(defaultStyle);
            }
        }
    }

    private void onSelectMejora(int index, List<Mejora> mejoras) {
        if (index == mejoras.size()) {
            if (rerollCount > 0) {
                rerollMejoras();
            }
            return;
        }
        // Caso normal: se aplica la mejora seleccionada
        Controllers.addListener(ventanaJuego1.getMenuPause().getInputsMenu());
        sistemaDeMejoras.aplicarMejora(mejoras.get(index));
        ventanaJuego1.getRenderHUDComponents().setHabilidadesActivas(sistemaDeMejoras.getHabilidadesActivas());

        popupGroup.remove();
        fondoAnimadoPopUp.remove();

        ventanaJuego1.setPausado(false);
        ventanaJuego1.getRenderHUDComponents().reanudarTemporizador();
        ventanaJuego1.getMenuPause().bloquearInputs(false);
        popUpAbierto = false;

        Gdx.input.setInputProcessor(null);
        Controllers.removeListener(inputHandler);

        ventanaJuego1.getSistemaDeNiveles().procesarNivelPendiente();
    }

    private void rerollMejoras() {
        if (rerollCount <= 0) return;
        rerollCount--;
        List<Mejora> nuevasMejoras = sistemaDeMejoras.generarOpcionesDeMejoraAleatorias(3);
        currentMejoras = nuevasMejoras;
        upgradeWindow.clearChildren();
        llenarOpciones(nuevasMejoras);
        selectedIndex = 0;
        updateButtonHighlight();
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

    @Override
    public void animarSalida(Runnable callback) {
        // Implementa la animación de salida si es necesario
    }

    public void dispose() {
        if (uiSkin != null) uiSkin.dispose();
        if (uiStage != null) uiStage.dispose();
    }

    /**
     * Clase interna para gestionar los inputs (teclado, ratón y mando) del pop-up.
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
                    onSelectMejora(selectedIndex, currentMejoras);
                    return true;
                case Input.Keys.NUM_1:
                case Input.Keys.NUMPAD_1:
                    selectedIndex = 0;
                    updateButtonHighlight();
                    onSelectMejora(0, currentMejoras);
                    return true;
                case Input.Keys.NUM_2:
                case Input.Keys.NUMPAD_2:
                    if (currentMejoras.size() > 1) {
                        selectedIndex = 1;
                        updateButtonHighlight();
                        onSelectMejora(1, currentMejoras);
                    }
                    return true;
                case Input.Keys.NUM_3:
                case Input.Keys.NUMPAD_3:
                    if (currentMejoras.size() > 2) {
                        selectedIndex = 2;
                        updateButtonHighlight();
                        onSelectMejora(2, currentMejoras);
                    }
                    return true;
                case Input.Keys.R:
                    if (rerollCount > 0) {
                        selectedIndex = improvementButtons.size() - 1;
                        updateButtonHighlight();
                        onSelectMejora(selectedIndex, currentMejoras);
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
                    onSelectMejora(selectedIndex, currentMejoras);
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

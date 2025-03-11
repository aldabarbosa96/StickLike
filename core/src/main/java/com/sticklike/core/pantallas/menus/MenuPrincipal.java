package com.sticklike.core.pantallas.menus;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.MainGame;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.ArrayList;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class MenuPrincipal extends ScreenAdapter {
    private MainGame game;
    private Stage stage;
    private Skin uiSkin;
    private TextButton btnJugar, btnNiveles, btnPersonaje, btnOpciones, btnCreditos, btnSalir;
    private ArrayList<TextButton> menuButtons;
    private int selectedIndex = 0;
    private MenuInputHandler inputHandler;

    public MenuPrincipal(MainGame game) {
        this.game = game;
        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = crearAspectoUI();

        GestorDeAudio.getInstance().detenerMusica();
        // Crear el título con reborde azul
        Actor titleActor = createTitleWithOutline("STICK-LIKE");

        // Ajustar alpha inicial a 0 para ver el fade-in
        titleActor.getColor().a = 0;

        // Tabla para el título (arriba)
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();
        titleTable.add(titleActor).padTop(75).padBottom(50).center();
        titleActor.addAction(Actions.sequence(Actions.delay(0.25f),
            Actions.fadeIn(0.5f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    GestorDeAudio.getInstance().cambiarMusica("fondoMenu");
                }
            })));
        stage.addActor(titleTable);

        // Crear los botones con su estilo por defecto
        btnJugar = createMenuButton(1, "Jugar", "default-button");
        btnNiveles = createMenuButton(2, "Niveles", "default-button");
        btnPersonaje = createMenuButton(3, "Personaje", "default-button");
        btnOpciones = createMenuButton(4, "Opciones", "default-button");
        btnCreditos = createMenuButton(5, "Créditos", "default-button");
        btnSalir = createMenuButton(6, "Salir", "default-button");

        // Tabla interna para los botones (en columna)
        Table buttonTable = new Table();
        buttonTable.center();
        buttonTable.add(btnJugar).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnNiveles).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnPersonaje).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnOpciones).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnCreditos).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnSalir).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);

        // Fondo tipo hoja (sin líneas)
        Pixmap bgPixmap = new Pixmap(200, 40, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0.985f, 0.91f, 0.7f, 1.0f));
        bgPixmap.fill();
        Texture bgTexture = new Texture(bgPixmap);
        bgPixmap.dispose();
        TiledDrawable tiledDrawable = new TiledDrawable(new TextureRegion(bgTexture));

        // Contenedor interno con el fondo original
        Container<Table> innerContainer = new Container<>(buttonTable);
        innerContainer.setBackground(tiledDrawable);
        innerContainer.pad(20);
        innerContainer.pack();

        Pixmap borderPixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(0, 0, 0, 0); // transparente
        borderPixmap.fill();
        borderPixmap.setColor(Color.BLUE);
        borderPixmap.drawRectangle(0, 0, 12, 12);
        Texture borderTex = new Texture(borderPixmap);
        borderPixmap.dispose();

        // Creamos un NinePatch con 4px de "stretch" en cada lado
        NinePatch borderPatch = new NinePatch(borderTex, 4, 4, 4, 4);
        NinePatchDrawable borderDrawable = new NinePatchDrawable(borderPatch);

        // Contenedor externo con borde
        Container<Container<Table>> buttonContainer = new Container<>(innerContainer);
        buttonContainer.setBackground(borderDrawable);
        buttonContainer.pack();

        // Posicionar inicialmente el contenedor fuera de pantalla (abajo)
        buttonContainer.setPosition((VIRTUAL_WIDTH - buttonContainer.getWidth()) / 2, -buttonContainer.getHeight());
        stage.addActor(buttonContainer);

        // Animación de entrada para el contenedor de botones
        buttonContainer.addAction(Actions.sequence(Actions.delay(0.75f), Actions.moveTo((VIRTUAL_WIDTH - buttonContainer.getWidth()) / 2, (VIRTUAL_HEIGHT - buttonContainer.getHeight()) / 2, 0.25f), Actions.fadeIn(0.5f)));

        // Listener para el botón "Jugar"
        btnJugar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onSelectButton(0);
            }
        });
        // Listener para el botón "Salir"
        btnSalir.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onSelectButton(5);
            }
        });

        // Crear la lista de botones para poder recorrerlos
        menuButtons = new ArrayList<>();
        menuButtons.add(btnJugar);
        menuButtons.add(btnNiveles);
        menuButtons.add(btnPersonaje);
        menuButtons.add(btnOpciones);
        menuButtons.add(btnCreditos);
        menuButtons.add(btnSalir);

        addHoverEffect();
        updateButtonHighlight();

        // Configurar InputMultiplexer para teclado, mando y stage
        inputHandler = new MenuInputHandler();
        InputMultiplexer im = new InputMultiplexer(inputHandler, stage);
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(inputHandler);
    }


    private Actor createTitleWithOutline(String text) {
        Stack stack = new Stack();

        // Estilo principal para el título (blanco)
        Label.LabelStyle mainStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE);
        Label mainLabel = new Label(text, mainStyle);
        mainLabel.setFontScale(2.25f);

        // Estilo para el borde (azul)
        Label.LabelStyle outlineStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.BLUE);
        outlineStyle.font.getData().setScale(2.25f);

        // Grupo para las copias con offsets (borde)
        Group outlineGroup = new Group();
        float offset = 2f; // Desplazamiento para simular el borde
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Label outlineLabel = new Label(text, outlineStyle);
                outlineLabel.setFontScale(2.25f);
                outlineLabel.setPosition(dx * offset, dy * offset);
                outlineGroup.addActor(outlineLabel);
            }
        }

        stack.add(outlineGroup);
        stack.add(mainLabel);

        return stack;
    }

    private TextButton createMenuButton(int number, String text, String styleName) {
        TextButton button = new TextButton("", uiSkin, styleName);
        Table contentTable = new Table();
        contentTable.defaults().center().pad(0);

        // Celda izquierda: número
        Label numberLabel = new Label(String.format("%2d.", number), uiSkin);
        numberLabel.setAlignment(Align.left);
        numberLabel.setFontScale(1.2f);
        contentTable.add(numberLabel).width(30);

        // Celda central: texto
        Label textLabel = new Label(text, uiSkin);
        textLabel.setAlignment(Align.center);
        textLabel.setFontScale(1.2f);
        contentTable.add(textLabel).expandX().fillX();

        // Celda derecha: dummy para compensar
        Label dummyLabel = new Label("", uiSkin);
        contentTable.add(dummyLabel).width(30);

        button.clearChildren();
        button.add(contentTable).expand().fill();

        // Guardar ambos labels en un objeto auxiliar para poder modificar su estilo al actualizar
        button.setUserObject(new ButtonLabels(numberLabel, textLabel));
        return button;
    }

    private void addHoverEffect() {
        for (int i = 0; i < menuButtons.size(); i++) {
            final TextButton btn = menuButtons.get(i);
            btn.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    int idx = menuButtons.indexOf(btn);
                    if (idx != selectedIndex) {
                        btn.setStyle(uiSkin.get("hover-button", TextButton.TextButtonStyle.class));
                    }
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    int idx = menuButtons.indexOf(btn);
                    if (idx != selectedIndex) {
                        btn.setStyle(uiSkin.get("default-button", TextButton.TextButtonStyle.class));
                    }
                }
            });
        }
    }

    private void updateButtonHighlight() {
        TextButton.TextButtonStyle defaultStyle = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle selectedStyle = uiSkin.get("selected-button", TextButton.TextButtonStyle.class);

        for (int i = 0; i < menuButtons.size(); i++) {
            TextButton button = menuButtons.get(i);
            ButtonLabels labels = (ButtonLabels) button.getUserObject();
            if (i == selectedIndex) {
                button.setStyle(selectedStyle);
                labels.text.setStyle(new Label.LabelStyle(labels.text.getStyle().font, selectedStyle.fontColor));
                labels.number.setStyle(new Label.LabelStyle(labels.number.getStyle().font, selectedStyle.fontColor));
            } else {
                button.setStyle(defaultStyle);
                labels.text.setStyle(new Label.LabelStyle(labels.text.getStyle().font, defaultStyle.fontColor));
                labels.number.setStyle(new Label.LabelStyle(labels.number.getStyle().font, defaultStyle.fontColor));
            }
        }
    }

    private void onSelectButton(int index) {
        switch (index) {
            case 0: // Jugar
                GestorDeAudio.getInstance().detenerMusica();
                Controllers.removeListener(inputHandler);
                Gdx.input.setInputProcessor(null);
                game.ventanaJuego1 = new VentanaJuego1(game, VentanaJuego1.worldWidth, VentanaJuego1.worldHeight);
                game.setScreen(game.ventanaJuego1);
                break;
            case 1:  // Niveles
                break;
            case 2: // Personaje
                break;
            case 3: // Opciones
                break;
            case 4: // Créditos
                break;
            case 5: // Salir
                Controllers.removeListener(inputHandler);
                Gdx.input.setInputProcessor(null);
                Gdx.app.exit();
                break;
        }
    }

    private Skin crearAspectoUI() {
        Skin skin = new Skin();

        // Fuente por defecto
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        // LabelStyle por defecto
        Label.LabelStyle defaultLabelStyle = new Label.LabelStyle(font, Color.GRAY);
        skin.add("default", defaultLabelStyle);

        // 1) Pixmap para fondo de los botones por defecto
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.97f, 0.88f, 0.6f, 1f));
        pixmap.fill();
        Texture pixmapTexture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("buttonBackground", pixmapTexture, Texture.class);

        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(skin.getRegion("buttonBackground"));

        // Estilo por defecto del botón
        TextButton.TextButtonStyle defaultButtonStyle = new TextButton.TextButtonStyle();
        defaultButtonStyle.font = font;
        defaultButtonStyle.up = backgroundDrawable;
        defaultButtonStyle.fontColor = new Color(0.3f, 0.3f, 0.3f, 1);
        skin.add("default-button", defaultButtonStyle);

        // 2) Estilo hover-button
        Pixmap hoverPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        hoverPixmap.setColor(new Color(1, 1, 1, 0.3f));
        hoverPixmap.fill();
        Texture hoverTexture = new Texture(hoverPixmap);
        hoverPixmap.dispose();
        skin.add("hoverBackground", hoverTexture, Texture.class);

        TextButton.TextButtonStyle hoverButtonStyle = new TextButton.TextButtonStyle();
        hoverButtonStyle.font = font;
        hoverButtonStyle.up = new TextureRegionDrawable(skin.getRegion("hoverBackground"));
        hoverButtonStyle.fontColor = Color.DARK_GRAY;
        skin.add("hover-button", hoverButtonStyle);

        // 3) Borde luminoso para el botón seleccionado
        Pixmap glowPixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        glowPixmap.setColor(new Color(1f, 1f, 1f, 0.8f));
        glowPixmap.fill();
        Texture glowTexture = new Texture(glowPixmap);
        glowPixmap.dispose();
        skin.add("glowTexture", glowTexture, Texture.class);

        // Crear un NinePatch para el glow
        NinePatch glowNinePatch = new NinePatch(skin.get("glowTexture", Texture.class), 5, 5, 5, 5);
        NinePatchDrawable glowDrawable = new NinePatchDrawable(glowNinePatch);

        // Estilo para el botón seleccionado
        TextButton.TextButtonStyle selectedButtonStyle = new TextButton.TextButtonStyle();
        selectedButtonStyle.font = font;
        selectedButtonStyle.up = glowDrawable;
        selectedButtonStyle.fontColor = Color.BLUE;
        skin.add("selected-button", selectedButtonStyle);

        return skin;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.125f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        uiSkin.dispose();
        Controllers.removeListener(inputHandler);
    }

    private class MenuInputHandler extends ControllerAdapter implements InputProcessor {
        private boolean axisLock = false;

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.DOWN:
                case Input.Keys.RIGHT:
                    if (selectedIndex < menuButtons.size() - 1) {
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
                    onSelectButton(selectedIndex);
                    return true;
            }
            return false;
        }

        @Override
        public boolean axisMoved(Controller controller, int axisIndex, float value) {
            if (axisIndex == 1) {
                if (Math.abs(value) < 0.2f) {
                    axisLock = false;
                    return false;
                }
                if (axisLock) return false;
                if (value > 0.5f && selectedIndex < menuButtons.size() - 1) {
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
            if (buttonIndex == 0) {
                onSelectButton(selectedIndex);
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

    private class ButtonLabels {
        public Label number;
        public Label text;

        public ButtonLabels(Label number, Label text) {
            this.number = number;
            this.text = text;
        }
    }
}

package com.sticklike.core.pantallas.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.ArrayList;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class RenderMenuPrincipal {
    private Stage stage;
    private Skin uiSkin;
    private Actor titleActor;
    private Container<?> buttonContainer;
    private ArrayList<TextButton> menuButtons;
    private int selectedIndex = 0;
    private ShapeRenderer shapeRenderer;

    // Interfaz callback para notificar la selección del botón
    public interface MenuListener {
        void onSelectButton(int index);
    }
    private MenuListener menuListener;

    public void setMenuListener(MenuListener listener) {
        this.menuListener = listener;
    }

    public RenderMenuPrincipal() {
        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = crearAspectoUI();
        menuButtons = new ArrayList<>();
        shapeRenderer = new ShapeRenderer();
        crearElementosUI();
    }

    private void crearElementosUI() {
        // Crear y configurar el título con borde
        titleActor = createTitleWithOutline("STICK-LIKE");
        titleActor.getColor().a = 0;
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();
        titleTable.add(titleActor).padTop(75).padBottom(50).center();
        stage.addActor(titleTable);
        titleActor.addAction(Actions.sequence(Actions.delay(0.25f), Actions.fadeIn(0.25f), Actions.run(new Runnable() {
            @Override
            public void run() {
                GestorDeAudio.getInstance().cambiarMusica("fondoMenu2");
            }
        })));

        TextButton btnJugar = createMenuButton(1, "Jugar", "default-button");
        TextButton btnNiveles = createMenuButton(2, "Niveles", "default-button");
        TextButton btnPersonaje = createMenuButton(3, "Personaje", "default-button");
        TextButton btnOpciones = createMenuButton(4, "Opciones", "default-button");
        TextButton btnCreditos = createMenuButton(5, "Créditos", "default-button");
        TextButton btnSalir = createMenuButton(6, "Salir", "default-button");

        // Agregamos ClickListeners para ejecutar la acción (además de actualizar la selección)
        btnJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(0);
                if(menuListener != null) {
                    menuListener.onSelectButton(0);
                }
            }
        });
        btnNiveles.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(1);
                if(menuListener != null) {
                    menuListener.onSelectButton(1);
                }
            }
        });
        btnPersonaje.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(2);
                if(menuListener != null) {
                    menuListener.onSelectButton(2);
                }
            }
        });
        btnOpciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(3);
                if(menuListener != null) {
                    menuListener.onSelectButton(3);
                }
            }
        });
        btnCreditos.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(4);
                if(menuListener != null) {
                    menuListener.onSelectButton(4);
                }
            }
        });
        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(5);
                if(menuListener != null) {
                    menuListener.onSelectButton(5);
                }
            }
        });

        // Agregar botones a la lista para controlar la navegación
        menuButtons.add(btnJugar);
        menuButtons.add(btnNiveles);
        menuButtons.add(btnPersonaje);
        menuButtons.add(btnOpciones);
        menuButtons.add(btnCreditos);
        menuButtons.add(btnSalir);

        // Crear la tabla que contendrá los botones
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

        // Crear un fondo tipo hoja para el contenedor de botones
        Pixmap bgPixmap = new Pixmap(200, 40, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0.985f, 0.91f, 0.7f, 1.0f));
        bgPixmap.fill();
        Texture bgTexture = new Texture(bgPixmap);
        bgPixmap.dispose();
        TiledDrawable tiledDrawable = new TiledDrawable(new TextureRegion(bgTexture));

        Container<Table> innerContainer = new Container<>(buttonTable);
        innerContainer.setBackground(tiledDrawable);
        innerContainer.pad(20);
        innerContainer.pack();

        // Crear un borde azul usando NinePatch para el contenedor
        Pixmap borderPixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(0, 0, 0, 0);
        borderPixmap.fill();
        borderPixmap.setColor(Color.BLUE);
        borderPixmap.drawRectangle(0, 0, 12, 12);
        Texture borderTex = new Texture(borderPixmap);
        borderPixmap.dispose();
        NinePatch borderPatch = new NinePatch(borderTex, 1, 1, 1, 1);
        NinePatchDrawable borderDrawable = new NinePatchDrawable(borderPatch);

        buttonContainer = new Container<>(innerContainer);
        buttonContainer.setBackground(borderDrawable);
        buttonContainer.pack();

        // Posicionar el contenedor fuera de pantalla y aplicar animación de entrada
        buttonContainer.setPosition((VIRTUAL_WIDTH - buttonContainer.getWidth()) / 2, -buttonContainer.getHeight());
        stage.addActor(buttonContainer);
        buttonContainer.addAction(Actions.sequence(
            Actions.delay(0.75f),
            Actions.moveTo((VIRTUAL_WIDTH - buttonContainer.getWidth()) / 2, (VIRTUAL_HEIGHT - buttonContainer.getHeight()) / 2, 0.25f),
            Actions.fadeIn(0.5f)
        ));

        // Agregar label de versión en la parte inferior
        Label versionLabel = new Label("v1.10.8-dev", uiSkin);
        versionLabel.setFontScale(0.95f);
        versionLabel.setColor(Color.DARK_GRAY);
        Table versionTable = new Table();
        versionTable.setFillParent(true);
        versionTable.bottom().right().padRight(22.5f).padBottom(17.5f);
        versionTable.add(versionLabel);
        stage.addActor(versionTable);

        addHoverEffect();
        updateButtonHighlight();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < menuButtons.size()) {
            selectedIndex = index;
            updateButtonHighlight();
        }
    }

    public void incrementSelectedIndex() {
        if (selectedIndex < menuButtons.size() - 1) {
            selectedIndex++;
            updateButtonHighlight();
        }
    }

    public void decrementSelectedIndex() {
        if (selectedIndex > 0) {
            selectedIndex--;
            updateButtonHighlight();
        }
    }

    public void render(float delta) {
        // 1) Limpiar la pantalla con un fondo gris
        Gdx.gl.glClearColor(0.89f, 0.89f, 0.89f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2) Dibujar la cuadrícula azul
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.9f, 1f);
        float cellSize = 75;
        float startX = stage.getCamera().position.x - stage.getViewport().getWorldWidth() / 2;
        float endX = stage.getCamera().position.x + stage.getViewport().getWorldWidth() / 2;
        float startY = stage.getCamera().position.y - stage.getViewport().getWorldHeight() / 2;
        float endY = stage.getCamera().position.y + stage.getViewport().getWorldHeight() / 2;

        // Líneas verticales
        for (float x = startX - (startX % cellSize); x <= endX; x += cellSize) {
            shapeRenderer.line(x, startY, x, endY);
        }
        // Líneas horizontales
        for (float y = startY - (startY % cellSize); y <= endY; y += cellSize) {
            shapeRenderer.line(startX, y, endX, y);
        }
        shapeRenderer.end();

        // 3) Actualizar y dibujar la escena (Stage)
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        uiSkin.dispose();
        shapeRenderer.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    private Actor createTitleWithOutline(String text) {
        Stack stack = new Stack();
        Label.LabelStyle mainStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE);
        Label mainLabel = new Label(text, mainStyle);
        mainLabel.setFontScale(2.25f);

        Label.LabelStyle outlineStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.BLUE);
        outlineStyle.font.getData().setScale(2.25f);

        Group outlineGroup = new Group();
        float offset = 2f;
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

        Label numberLabel = new Label(String.format("%2d.", number), uiSkin);
        numberLabel.setAlignment(Align.left);
        numberLabel.setFontScale(1.2f);
        contentTable.add(numberLabel).width(30);

        Label textLabel = new Label(text, uiSkin);
        textLabel.setAlignment(Align.center);
        textLabel.setFontScale(1.2f);
        contentTable.add(textLabel).expandX().fillX();

        Label dummyLabel = new Label("", uiSkin);
        contentTable.add(dummyLabel).width(30);

        button.clearChildren();
        button.add(contentTable).expand().fill();

        // Guardar ambos labels para facilitar la actualización de estilos
        button.setUserObject(new ButtonLabels(numberLabel, textLabel));
        return button;
    }

    private void addHoverEffect() {
        for (final TextButton btn : menuButtons) {
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

    private Skin crearAspectoUI() {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        Label.LabelStyle defaultLabelStyle = new Label.LabelStyle(font, Color.GRAY);
        skin.add("default", defaultLabelStyle);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.97f, 0.88f, 0.6f, 1f));
        pixmap.fill();
        Texture pixmapTexture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("buttonBackground", pixmapTexture, Texture.class);

        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(skin.getRegion("buttonBackground"));

        TextButton.TextButtonStyle defaultButtonStyle = new TextButton.TextButtonStyle();
        defaultButtonStyle.font = font;
        defaultButtonStyle.up = backgroundDrawable;
        defaultButtonStyle.fontColor = new Color(0.3f, 0.3f, 0.3f, 1);
        skin.add("default-button", defaultButtonStyle);

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

        Pixmap glowPixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        glowPixmap.setColor(new Color(1f, 1f, 1f, 0.8f));
        glowPixmap.fill();
        Texture glowTexture = new Texture(glowPixmap);
        glowPixmap.dispose();
        skin.add("glowTexture", glowTexture, Texture.class);

        NinePatch glowNinePatch = new NinePatch(skin.get("glowTexture", Texture.class), 5, 5, 5, 5);
        NinePatchDrawable glowDrawable = new NinePatchDrawable(glowNinePatch);

        TextButton.TextButtonStyle selectedButtonStyle = new TextButton.TextButtonStyle();
        selectedButtonStyle.font = font;
        selectedButtonStyle.up = glowDrawable;
        selectedButtonStyle.fontColor = Color.BLUE;
        skin.add("selected-button", selectedButtonStyle);

        return skin;
    }

    // Clase auxiliar para almacenar los labels del botón
    private class ButtonLabels {
        public Label number;
        public Label text;

        public ButtonLabels(Label number, Label text) {
            this.number = number;
            this.text = text;
        }
    }
}

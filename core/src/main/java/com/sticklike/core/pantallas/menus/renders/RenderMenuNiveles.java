package com.sticklike.core.pantallas.menus.renders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

import java.util.ArrayList;

public class RenderMenuNiveles {
    private Stage stage;
    private Skin uiSkin;
    private Actor titleActor;
    private TextButton btnNivel1;
    private TextButton btnNivel2;
    private TextButton btnNivel3;
    private TextButton btnVolver;
    private ShapeRenderer shapeRenderer;
    private Container<?> buttonContainer;

    // Lista de botones y gestión de selección, similar al menú principal
    private ArrayList<TextButton> nivelButtons;
    private int selectedIndex = 0;

    // Interfaz callback para notificar la acción de cada botón
    public interface MenuNivelesListener {
        void onSelectNivel1();
        void onSelectNivel2();
        void onSelectNivel3();
        void onBack();
    }
    private MenuNivelesListener listener;

    public void setMenuNivelesListener(MenuNivelesListener listener) {
        this.listener = listener;
    }

    public RenderMenuNiveles() {
        stage = new Stage(new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = crearAspectoUI();
        shapeRenderer = new ShapeRenderer();
        crearElementosUI();
    }

    private void crearElementosUI() {
        // Crear el título con contorno (como en el menú principal)
        titleActor = createTitleWithOutline("NIVELES");
        titleActor.getColor().a = 0;
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();
        titleTable.add(titleActor).padTop(75).padBottom(50).center();
        stage.addActor(titleTable);
        titleActor.addAction(Actions.sequence(Actions.delay(0.25f), Actions.fadeIn(0.25f)));

        // Crear botones para cada nivel y para "Volver"
        btnNivel1 = createMenuButton(1, "Nivel 1", "default-button");
        btnNivel2 = createMenuButton(2, "Nivel 2", "default-button");
        btnNivel3 = createMenuButton(3, "Nivel 3", "default-button");
        btnVolver = createMenuButton(4, "Volver", "default-button");

        // Inicializar la lista y agregar cada botón
        nivelButtons = new ArrayList<>();
        nivelButtons.add(btnNivel1);
        nivelButtons.add(btnNivel2);
        nivelButtons.add(btnNivel3);
        nivelButtons.add(btnVolver);

        // Asignar listeners de click a cada botón
        btnNivel1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = 0;
                updateButtonHighlight();
                if (listener != null) listener.onSelectNivel1();
            }
        });
        btnNivel2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = 1;
                updateButtonHighlight();
                if (listener != null) listener.onSelectNivel2();
            }
        });
        btnNivel3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = 2;
                updateButtonHighlight();
                if (listener != null) listener.onSelectNivel3();
            }
        });
        btnVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = 3;
                updateButtonHighlight();
                if (listener != null) listener.onBack();
            }
        });

        // Efecto hover (mismo comportamiento que en el menú principal)
        addHoverEffect();

        // Organizar botones en una tabla
        Table buttonTable = new Table();
        buttonTable.add(btnNivel1).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnNivel2).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnNivel3).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnVolver).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);

        // Crear fondo tipo "papel" para el contenedor
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

        // Crear un borde azul usando NinePatch (como en el menú principal)
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

        // Posicionar el contenedor y aplicar animación de entrada:
        // Inicia fuera de la pantalla (abajo) y se desplaza hasta su posición final con fadeIn.
        buttonContainer.setPosition((VIRTUAL_WIDTH - buttonContainer.getWidth()) / 2,
            -buttonContainer.getHeight());
        stage.addActor(buttonContainer);
        buttonContainer.addAction(Actions.sequence(
            Actions.delay(0.75f),
            Actions.parallel(
                Actions.moveTo((VIRTUAL_WIDTH - buttonContainer.getWidth()) / 2,
                    (VIRTUAL_HEIGHT - buttonContainer.getHeight()) / 2f, 0.25f),
                Actions.fadeIn(0.5f)
            )
        ));

        // Asegurarse de que el primer botón aparezca seleccionado
        updateButtonHighlight();
    }

    // Método para crear un botón con estilo similar al menú principal
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

        // Guardar labels para actualizar estilos posteriormente
        button.setUserObject(new ButtonLabels(numberLabel, textLabel));
        return button;
    }

    // Agrega efecto hover a cada botón
    private void addHoverEffect() {
        for (final TextButton btn : nivelButtons) {
            btn.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    int idx = nivelButtons.indexOf(btn);
                    if (idx != selectedIndex) {
                        btn.setStyle(uiSkin.get("hover-button", TextButton.TextButtonStyle.class));
                    }
                }
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    int idx = nivelButtons.indexOf(btn);
                    if (idx != selectedIndex) {
                        btn.setStyle(uiSkin.get("default-button", TextButton.TextButtonStyle.class));
                    }
                }
            });
        }
    }

    // Actualiza la apariencia de los botones según la selección
    private void updateButtonHighlight() {
        TextButton.TextButtonStyle defaultStyle = uiSkin.get("default-button", TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle selectedStyle = uiSkin.get("selected-button", TextButton.TextButtonStyle.class);

        for (int i = 0; i < nivelButtons.size(); i++) {
            TextButton button = nivelButtons.get(i);
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

    // Métodos públicos para navegación con teclado/mando

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < nivelButtons.size()) {
            selectedIndex = index;
            updateButtonHighlight();
        }
    }

    public void incrementSelectedIndex() {
        if (selectedIndex < nivelButtons.size() - 1) {
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

    // Activa el botón actualmente seleccionado
    public void activateSelectedButton() {
        switch(selectedIndex) {
            case 0:
                if (listener != null) listener.onSelectNivel1();
                break;
            case 1:
                if (listener != null) listener.onSelectNivel2();
                break;
            case 2:
                if (listener != null) listener.onSelectNivel3();
                break;
            case 3:
                if (listener != null) listener.onBack();
                break;
        }
    }

    // Método para animar la salida (efecto de slide y fade out) de la ventana.
    // Se ejecuta el callback al finalizar la animación.
    public void animateExit(Runnable callback) {
        float finalX = buttonContainer.getX();
        float finalY = -buttonContainer.getHeight();
        buttonContainer.addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveTo(finalX, finalY, 0.25f),
                Actions.fadeOut(0.25f)
            ),
            Actions.run(callback)
        ));
    }

    // Métodos de utilidad para el título y fondos
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

    // Crea el aspecto visual (skin) de la UI
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

    public void render(float delta) {
        Gdx.gl.glClearColor(0.89f, 0.89f, 0.89f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.9f, 1f);
        float cellSize = 75;
        float startX = stage.getCamera().position.x - stage.getViewport().getWorldWidth() / 2;
        float endX = stage.getCamera().position.x + stage.getViewport().getWorldWidth() / 2;
        float startY = stage.getCamera().position.y - stage.getViewport().getWorldHeight() / 2;
        float endY = stage.getCamera().position.y + stage.getViewport().getWorldHeight() / 2;
        for (float x = startX - (startX % cellSize); x <= endX; x += cellSize) {
            shapeRenderer.line(x, startY, x, endY);
        }
        for (float y = startY - (startY % cellSize); y <= endY; y += cellSize) {
            shapeRenderer.line(startX, y, endX, y);
        }
        shapeRenderer.end();

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

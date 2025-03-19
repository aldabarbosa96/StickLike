package com.sticklike.core.pantallas.menus.renders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.ArrayList;

public class RenderMenuPrincipal extends RenderMenus {
    private Actor titleActor;
    private Container<?> buttonContainer;
    private ArrayList<TextButton> menuButtons;
    private int selectedIndex = 0;

    public interface MenuListener {
        void onSelectButton(int index);
    }

    private MenuListener menuListener;

    public void setMenuListener(MenuListener listener) {
        this.menuListener = listener;
    }

    public RenderMenuPrincipal() {
        super();
        menuButtons = new ArrayList<>();
        crearElementosUI();
    }

    private void crearElementosUI() {
        titleActor = tituloConReborde("STICK-LIKE", 2.25f);
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

        // Crear botones del menú principal
        TextButton btnJugar = createMenuButton(1, "Jugar", "default-button");
        TextButton btnNiveles = createMenuButton(2, "Niveles", "default-button");
        TextButton btnPersonaje = createMenuButton(3, "Personaje", "default-button");
        TextButton btnOpciones = createMenuButton(4, "Opciones", "default-button");
        TextButton btnLogros = createMenuButton(5, "Logros", "default-button");
        TextButton btnCreditos = createMenuButton(6, "Créditos", "default-button");
        TextButton btnSalir = createMenuButton(7, "Salir", "default-button");

        // Agregar ClickListeners para manejar la selección y notificar al listener
        btnJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(0);
                if (menuListener != null) menuListener.onSelectButton(0);
            }
        });
        btnNiveles.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(1);
                if (menuListener != null) menuListener.onSelectButton(1);
            }
        });
        btnPersonaje.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(2);
                if (menuListener != null) menuListener.onSelectButton(2);
            }
        });
        btnOpciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(3);
                if (menuListener != null) menuListener.onSelectButton(3);
            }
        });
        btnLogros.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(4);
                if (menuListener != null) menuListener.onSelectButton(4);
            }
        });
        btnCreditos.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(5);
                if (menuListener != null) menuListener.onSelectButton(5);
            }
        });
        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedIndex(6);
                if (menuListener != null) menuListener.onSelectButton(6);
            }
        });

        // Agregar botones a la lista para la navegación
        menuButtons.add(btnJugar);
        menuButtons.add(btnNiveles);
        menuButtons.add(btnPersonaje);
        menuButtons.add(btnOpciones);
        menuButtons.add(btnLogros);
        menuButtons.add(btnCreditos);
        menuButtons.add(btnSalir);

        // Organizar los botones en una tabla
        Table buttonTable = new Table();
        buttonTable.add(btnJugar).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnNiveles).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnPersonaje).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnOpciones).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnLogros).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnCreditos).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnSalir).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);

        // Utilizar métodos comunes de la clase base para crear fondo "papel" y borde azul
        Container<Table> innerContainer = new Container<>(buttonTable);
        innerContainer.setBackground(papelFondo());
        innerContainer.pad(20);
        innerContainer.pack();

        buttonContainer = new Container<>(innerContainer);
        buttonContainer.setBackground(bordeAzul());
        buttonContainer.pack();

        // Animar la entrada del contenedor (se posiciona inicialmente fuera de la pantalla y se desplaza a la posición final)
        buttonContainer.setPosition((VIRTUAL_WIDTH - buttonContainer.getWidth()) / 2, -buttonContainer.getHeight());
        stage.addActor(buttonContainer);
        buttonContainer.addAction(Actions.sequence(Actions.delay(0.75f), Actions.parallel(Actions.moveTo((VIRTUAL_WIDTH - buttonContainer.getWidth()) / 2, (VIRTUAL_HEIGHT - buttonContainer.getHeight()) / 2.5f, 0.25f), Actions.fadeIn(0.5f))));

        addHoverEffect();
        updateButtonHighlight();
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

    // Aplica efecto hover a cada botón del menú
    private void addHoverEffect() {
        for (final TextButton btn : menuButtons) {
            btn.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    int idx = menuButtons.indexOf(btn);
                    if (idx != selectedIndex) {
                        btn.setStyle(uiSkin.get("hover-button", TextButtonStyle.class));
                    }
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    int idx = menuButtons.indexOf(btn);
                    if (idx != selectedIndex) {
                        btn.setStyle(uiSkin.get("default-button", TextButtonStyle.class));
                    }
                }
            });
        }
    }

    // Actualiza la apariencia de los botones según cuál esté seleccionado
    private void updateButtonHighlight() {
        TextButton.TextButtonStyle defaultStyle = uiSkin.get("default-button", TextButtonStyle.class);
        TextButton.TextButtonStyle selectedStyle = uiSkin.get("selected-button", TextButtonStyle.class);

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

    @Override
    protected Skin crearSkinBasico() {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        Label.LabelStyle defaultLabelStyle = new Label.LabelStyle(font, Color.BLACK);
        skin.add("default", defaultLabelStyle, Label.LabelStyle.class);

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
        skin.add("default-button", defaultButtonStyle, TextButton.TextButtonStyle.class);

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
        skin.add("hover-button", hoverButtonStyle, TextButton.TextButtonStyle.class);

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
        skin.add("selected-button", selectedButtonStyle, TextButton.TextButtonStyle.class);

        return skin;
    }

    @Override
    public void animarSalida(Runnable callback) {

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

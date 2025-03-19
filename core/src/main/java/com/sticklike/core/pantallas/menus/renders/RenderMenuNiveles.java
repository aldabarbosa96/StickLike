package com.sticklike.core.pantallas.menus.renders;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

import java.util.ArrayList;

public class RenderMenuNiveles extends RenderMenus {
    private Actor titleActor;
    private TextButton btnNivel1;
    private TextButton btnNivel2;
    private TextButton btnNivel3;
    private TextButton btnVolver;
    private Container<?> buttonContainer;
    private ArrayList<TextButton> nivelButtons;
    private int selectedIndex = 0;

    // Interfaz callback para notificar las acciones
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
        super();
        crearElementosUI();
    }

    private void crearElementosUI() {
        titleActor = tituloConReborde("NIVELES", 2.25f);
        titleActor.getColor().a = 0;
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();
        titleTable.add(titleActor).padTop(75).padBottom(50).center();
        stage.addActor(titleTable);
        titleActor.addAction(Actions.sequence(Actions.delay(0.25f), Actions.fadeIn(0.25f)));

        // Crear botones para cada nivel y "Volver"
        btnNivel1 = createMenuButton(1, "Nivel 1", "default-button");
        btnNivel2 = createMenuButton(2, "Nivel 2", "default-button");
        btnNivel3 = createMenuButton(3, "Nivel 3", "default-button");
        btnVolver = createMenuButton(4, "Volver", "default-button");

        // Inicializar la lista de botones
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

        // Agregar efecto hover a los botones
        addHoverEffect();

        // Organizar los botones en una tabla
        Table buttonTable = new Table();
        buttonTable.add(btnNivel1).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnNivel2).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnNivel3).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnVolver).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);

        // Crear el fondo estilo "papel" y el borde azul usando los métodos comunes
        Container<Table> innerContainer = new Container<>(buttonTable);
        innerContainer.setBackground(papelFondo());
        innerContainer.pad(20);
        innerContainer.pack();

        buttonContainer = new Container<>(innerContainer);
        buttonContainer.setBackground(bordeAzul());
        buttonContainer.pack();

        // Posicionar el contenedor fuera de la pantalla (abajo) y animar su entrada
        animarEntrada(buttonContainer);

        // Asegurar que se resalte el primer botón
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

        // Guardar labels para facilitar la actualización de estilos
        button.setUserObject(new ButtonLabels(numberLabel, textLabel));
        return button;
    }

    // Aplica efecto hover a cada botón
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

    // Actualiza la apariencia de los botones según cuál está seleccionado
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
        switch (selectedIndex) {
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

    public void animarSalida(final Runnable callback) {
        super.animarSalida(buttonContainer, callback);
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

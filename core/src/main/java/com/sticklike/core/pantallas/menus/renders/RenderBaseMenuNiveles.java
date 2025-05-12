package com.sticklike.core.pantallas.menus.renders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

import java.util.ArrayList;

public class RenderBaseMenuNiveles extends RenderBaseMenus {
    private Actor titleActor;
    private TextButton btnNivel1, btnNivel2, btnNivel3, btnNivel4, btnNivel5;
    private TextButton btnVolver;
    private Container<Container<Table>> buttonContainer;
    private ArrayList<TextButton> nivelButtons;
    private int selectedIndex = 0;

    public interface MenuNivelesListener {
        void onSelectNivel1();

        void onSelectNivel2();

        void onSelectNivel3();

        void onSelectNivel4();

        void onSelectNivel5();

        void onBack();
    }

    private MenuNivelesListener listener;

    public void setMenuNivelesListener(MenuNivelesListener listener) {
        this.listener = listener;
    }

    public RenderBaseMenuNiveles() {
        super();
        crearElementosUI();
    }

    private void crearElementosUI() {
        Label titleActor = new Label("NIVELES", uiSkin, "title");
        titleActor.getColor().a = 0;

        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();
        titleTable.add(titleActor).padTop(75).padBottom(50).center();
        stage.addActor(titleTable);

        // animación de fade-in igual que antes
        titleActor.addAction(Actions.sequence(Actions.delay(0.25f), Actions.fadeIn(0.25f)));

        // Crear botones para cada nivel y "Volver"
        btnNivel1 = crearBotonesNumerados(1, "<<  Cuaderno  >>", "default-button");
        btnNivel2 = crearBotonesNumerados(2, "<<  Pizarra  >>", "default-button");
        btnNivel3 = crearBotonesNumerados(3, "<<  Ordenador  >>", "default-button");
        btnNivel4 = crearBotonesNumerados(4, "<<  ???  >>", "default-button");
        btnNivel5 = crearBotonesNumerados(5, "<<  ???  >>", "default-button");
        btnVolver = crearBotonesNumerados(6, "Volver", "default-button");

        // Inicializar la lista de botones
        nivelButtons = new ArrayList<>();
        nivelButtons.add(btnNivel1);
        nivelButtons.add(btnNivel2);
        nivelButtons.add(btnNivel3);
        nivelButtons.add(btnNivel4);
        nivelButtons.add(btnNivel5);
        nivelButtons.add(btnVolver);

        // Asignar listeners de click a cada botón
        btnNivel1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = 0;
                actualizarBotonResaltado(nivelButtons, selectedIndex);
                if (listener != null) listener.onSelectNivel1();
            }
        });
        btnNivel2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = 1;
                actualizarBotonResaltado(nivelButtons, selectedIndex);
                if (listener != null) listener.onSelectNivel2();
            }
        });
        btnNivel3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = 2;
                actualizarBotonResaltado(nivelButtons, selectedIndex);
                if (listener != null) listener.onSelectNivel3();
            }
        });
        btnNivel4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = 3;
                actualizarBotonResaltado(nivelButtons, selectedIndex);
                if (listener != null) listener.onSelectNivel3();
            }
        });
        btnNivel5.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = 4;
                actualizarBotonResaltado(nivelButtons, selectedIndex);
                if (listener != null) listener.onSelectNivel3();
            }
        });
        btnVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = 5;
                actualizarBotonResaltado(nivelButtons, selectedIndex);
                if (listener != null) listener.onBack();
            }
        });

        efectoHover(nivelButtons, () -> selectedIndex);

        Table buttonTable = new Table();
        buttonTable.add(btnNivel1).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnNivel2).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnNivel3).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnNivel4).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnNivel5).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);
        buttonTable.row();
        buttonTable.add(btnVolver).pad(12).center().width(VIRTUAL_WIDTH / 4).height(45f);

        Container<Table> innerContainer = new Container<>(buttonTable);
        innerContainer.setBackground(papelFondo());
        innerContainer.pad(20);
        innerContainer.pack();

        buttonContainer = new Container<>(innerContainer);
        buttonContainer.setBackground(crearSombraConBorde(Color.DARK_GRAY, 10, Color.BLUE, 2));
        buttonContainer.pack();
        animarEntrada(buttonContainer, 2.25f);
        actualizarBotonResaltado(nivelButtons, selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < nivelButtons.size()) {
            selectedIndex = index;
            actualizarBotonResaltado(nivelButtons, selectedIndex);
        }
    }

    public void incrementSelectedIndex() {
        if (selectedIndex < nivelButtons.size() - 1) {
            selectedIndex++;
            actualizarBotonResaltado(nivelButtons, selectedIndex);
        }
    }

    public void decrementSelectedIndex() {
        if (selectedIndex > 0) {
            selectedIndex--;
            actualizarBotonResaltado(nivelButtons, selectedIndex);
        }
    }

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
                if (listener != null) listener.onSelectNivel4();
                break;
            case 4:
                if (listener != null) listener.onSelectNivel5();
                break;
            case 5:
                if (listener != null) listener.onBack();
                break;
        }
    }

    public void animarSalida(final Runnable callback) {
        super.animarSalida(buttonContainer, callback);
    }
}

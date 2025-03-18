package com.sticklike.core.pantallas.menus.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sticklike.core.MainGame;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class Creditos extends ScreenAdapter {
    private Stage stage;
    private Skin skin;
    private Label creditsLabel;

    public Creditos(final MainGame game) {
        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        String texto = "CREDITOS\n\n\n" +
            "Desarrollo:\n\n" +
            "David Barbosa Olayo\nDisenyo, programacion, creacion de assets, composicion y edicion de audio.\n\n" +
            "Colaboraciones:\n\n" +
            "Carlos Barbosa Molina \n Creacion de assets y contribucion en audio.\n\n" +
            "Albert Barbosa Olayo \n Contribucion en audio.\n\n" +
            "Aleix Albors Munyoz \n Contribucion en assets.\n\n" +
            "Agradecimientos:\n\n" +
            "A mi familia y amigos por su apoyo y contribucion en este proyecto.\n\n" +
            "Tecnologias utilizadas\n\n" +
            "    Lenguaje: Java\n" +
            "    Framework: LibGDX\n" +
            "    Imagen: PixelStudio\n" +
            "    Audio: Audacity\n\n\n\n" +
            "© 2025 SitckLike. Todos los derechos reservados." + "\n".repeat(40) +
            "--15.";

        creditsLabel = new Label(texto, skin);
        creditsLabel.setAlignment(Align.center);
        creditsLabel.setWrap(true);

        creditsLabel.setWidth(VIRTUAL_WIDTH * 0.8f);
        creditsLabel.invalidate();
        creditsLabel.layout();
        float labelHeight = creditsLabel.getPrefHeight();

        // Posición inicial centrado horizontalmente y justo debajo de la pantalla
        creditsLabel.setPosition((VIRTUAL_WIDTH - creditsLabel.getWidth()) / 2, -labelHeight);
        stage.addActor(creditsLabel);

        // Calculamos el recorrido total, desde su posición inicial hasta que salga por arriba de la pantalla
        float recorrido = VIRTUAL_HEIGHT + labelHeight;
        float duracion = 30f;

        creditsLabel.addAction(Actions.sequence(
            Actions.moveBy(0, recorrido, duracion),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MenuPrincipal(game));
                }
            })
        ));

        // Listener para capturar cualquier input y cambiar a MenuPrincipal
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                game.setScreen(new MenuPrincipal(game));
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MenuPrincipal(game));
                return true;
            }
        });

        Gdx.input.setInputProcessor(stage);
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
        skin.dispose();
    }
}

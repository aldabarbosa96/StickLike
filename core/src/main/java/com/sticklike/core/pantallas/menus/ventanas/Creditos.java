package com.sticklike.core.pantallas.menus.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();

        skin.add("default-font", font);
        skin.add("default", new Label.LabelStyle(font, Color.WHITE), Label.LabelStyle.class);

        String creditos = """
            CRÉDITOS

            ------------------------------------------

            Una producción de StickLike
            © 2025 - Todos los derechos reservados

            ------------------------------------------

            DIRECCIÓN, DISEÑO Y DESARROLLO
            - David Barbosa Olayo


            PROGRAMACIÓN
            - David Barbosa Olayo


            DISEÑO Y CREACIÓN DE ASSETS VISUALES
            - David Barbosa Olayo
            - Carlos Barbosa Molina (asistencia en diseño VFX)
            - Aleix Albors Munyoz (asistencia en diseño VFX)


            SONIDO Y AMBIENTACIÓN
            - David Barbosa Olayo
            - Albert Barbosa Olayo (asistencia en diseño SFX )
            - Carlos Barbosa Molina (asistencia en diseño SFX)



            ARTE Y SOPORTE ARTÍSTICO
            - Carlos Barbosa Molina


            AGRADECIMIENTOS ESPECIALES
            A mi familia y amigos, por su apoyo constante y por creer
            en este proyecto desde el primer día.

            ------------------------------------------

            TECNOLOGÍAS Y HERRAMIENTAS UTILIZADAS
            - Lenguaje de programación: Java
            - Motor gráfico: LibGDX
            - Editor de gráficos: Pixel Studio
            - Editor de audio: Audacity

            ------------------------------------------
            Este proyecto fue creado con pasión durante noches
            de café y aprendizaje constante. Gracias por ser
            parte de esta experiencia y darle vida a este sueño.

            Tu apoyo significa todo para un desarrollador independiente.

            ¡GRACIAS POR JUGAR! ;)
            """.repeat(1) + "\n".repeat(40) + "--15.";


        creditsLabel = new Label(creditos, skin);
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
        float duracion = 60f;

        creditsLabel.addAction(Actions.sequence(Actions.moveBy(0, recorrido, duracion), Actions.run(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new MenuPrincipal(game));
            }
        })));

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

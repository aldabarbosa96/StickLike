package com.sticklike.core.pantallas.menus.ventanas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controllers;
import com.sticklike.core.MainGame;
import com.sticklike.core.entidades.jugador.StatsJugador;
import com.sticklike.core.pantallas.menus.InputsMenu;
import com.sticklike.core.pantallas.menus.renders.RenderBaseMenuPersonaje;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.PODER_JUGADOR;

public class MenuPersonaje extends ScreenAdapter {
    private MainGame game;
    private RenderBaseMenuPersonaje renderMenu;
    private InputsMenu inputsMenu;
    private StatsJugador statsJugador;

    public MenuPersonaje(MainGame game) {
        this.game = game;
        this.statsJugador = new StatsJugador(VEL_MOV_JUGADOR, VIDA_JUGADOR, VIDAMAX_JUGADOR, RANGO_ATAQUE, DANYO, INTERVALO_DISPARO, VEL_ATAQUE_JUGADOR, NUM_PROYECTILES_INICIALES, RESISTENCIA, CRITICO, REGENERACION_VIDA, PODER_JUGADOR);
        renderMenu = new RenderBaseMenuPersonaje(statsJugador);

        renderMenu.setMenuPersonajeListener(new RenderBaseMenuPersonaje.MenuPersonajeListener() {
            @Override
            public void onVolver() {
                Controllers.removeListener(inputsMenu);
                Gdx.input.setInputProcessor(null);
                game.setStatsJugador(statsJugador);
                // Opcional: animar salida si lo deseas
                renderMenu.animarSalida(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MenuPrincipal(game));
                    }
                });
            }
        });

        inputsMenu = new InputsMenu(new InputsMenu.MenuInputListener() {
            @Override
            public void onNavigateUp() { }
            @Override
            public void onNavigateDown() { }
            @Override
            public void onSelect() { }
            @Override
            public void onBack() {
                game.setScreen(new MenuPrincipal(game));
            }
            @Override
            public void onPauseToggle() { }
        });

        InputMultiplexer im = new InputMultiplexer(renderMenu.getStage(), inputsMenu);
        Gdx.input.setInputProcessor(im);
        Controllers.addListener(inputsMenu);
    }

    @Override
    public void render(float delta) {
        renderMenu.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        renderMenu.resize(width, height);
    }

    @Override
    public void dispose() {
        renderMenu.dispose();
        Controllers.removeListener(inputsMenu);
    }
}

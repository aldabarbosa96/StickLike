package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.sticklike.core.screens.VentanaJuego;
import com.sticklike.core.utils.GestorDeAssets;

public class MainGame extends Game {
    public VentanaJuego ventanaJuego;

    @Override
    public void create() {
        GestorDeAssets.cargarRecursos(); // Cargamos los assets al iniciar
        ventanaJuego = new VentanaJuego(this); // Pasamos MainGame a GameScreen
        setScreen(ventanaJuego); // Asignamos la pantalla a visualizar
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        GestorDeAssets.dispose();
        ventanaJuego.dispose();
    }
}

package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.pantallas.juego.VentanaJuego;
import com.sticklike.core.utilidades.GestorDeAssets;

/**
 * MainGame es la clase principal del juego con libGDX
 * Extiende de {@link Game}, para poder administrar diversas pantallas simultáneamente y se encarga de cargar los assets e
 * inicializar la ventana principal del juego
 */
public class MainGame extends Game {
    public VentanaJuego ventanaJuego;
    public GestorDeAudio gestorDeAudio;

    @Override
    public void create() {
        GestorDeAssets.cargarRecursos(); // Cargamos los assets al iniciar
        gestorDeAudio = new GestorDeAudio();
        gestorDeAudio.reproducirMusica();
        ventanaJuego = new VentanaJuego(this,VentanaJuego.worldWidth,VentanaJuego.worldHeight); // Pasamos MainGame a GameScreen
        setScreen(ventanaJuego);
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
        gestorDeAudio.dispose();
    }
}

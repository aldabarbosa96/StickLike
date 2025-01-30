package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.pantallas.juego.VentanaJuego;
import com.sticklike.core.utilidades.GestorDeAssets;

/**
 * MainGame es la clase principal del juego con libGDX
 * Extiende de {@link Game}, para poder administrar diversas pantallas simultáneamente y se encarga de cargar los assets e
 * inicializar la ventana principal del juego
 */
public class MainGame extends Game {
    public VentanaJuego ventanaJuego;
    public ControladorAudio controladorAudio;

    @Override
    public void create() {
        GestorDeAssets.cargarRecursos(); // Cargamos los assets al iniciar
        controladorAudio = new ControladorAudio();
        controladorAudio.reproducirMusica();
        ventanaJuego = new VentanaJuego(this); // Pasamos MainGame a GameScreen
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
        controladorAudio.dispose();
    }
}

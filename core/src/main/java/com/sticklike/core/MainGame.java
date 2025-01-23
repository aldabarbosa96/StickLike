package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.pantallas.juego.VentanaJuego;
import com.sticklike.core.utilidades.GestorDeAssets;

/**
 * MainGame es la clase principal del juego con libGDX
 * Extiende de {@link Game}, de modo que puede administrar múltiples pantallas (Screens)
 * <p>
 * Carga los recursos (GestoDeAssets.cargarRecursos()) y crea una instancia de la pantalla principal (VentanaJuego), asignándola como pantalla activa
 */
public class MainGame extends Game {
    public VentanaJuego ventanaJuego;
    public ControladorAudio controladorAudio;

    /**
     * Llamado al cargar el juego
     * Carga los assets, inicializa el controlador de audio y establece la pantalla de juego
     */
    @Override
    public void create() {
        GestorDeAssets.cargarRecursos(); // Cargamos los assets al iniciar
        controladorAudio = new ControladorAudio();
        controladorAudio.reproducirMusica();
        ventanaJuego = new VentanaJuego(this); // Pasamos MainGame a GameScreen
        setScreen(ventanaJuego); // Asignamos la pantalla a visualizar
    }

    /**
     * Método principal de render, delega en {@link Game#render()} para actualizar y dibujar la pantalla activa
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * Método llamado al cerrar la aplicación
     * Libera todos los recursos utilizados
     */
    @Override
    public void dispose() {
        super.dispose();
        GestorDeAssets.dispose();
        ventanaJuego.dispose();
        controladorAudio.dispose();
    }
}

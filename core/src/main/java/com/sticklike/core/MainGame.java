package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.sticklike.core.pantallas.VentanaJuego;
import com.sticklike.core.utilidades.GestorDeAssets;

/**
 * MainGame es la clase principal de la aplicación con libGDX
 * Extiende de {@link Game}, de modo que puede administrar múltiples pantallas (Screens)
 * <p>
 * Carga los recursos (GestoDeAssets.cargarRecursos()) y crea una instancia de la pantalla principal (VentanaJuego), asignándola como pantalla activa
 */
public class MainGame extends Game {
    public VentanaJuego ventanaJuego;

    /**
     * Se llama al iniciar la aplicación. Carga los assets e instancia la pantalla principal
     */
    @Override
    public void create() {
        GestorDeAssets.cargarRecursos(); // Cargamos los assets al iniciar
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

    @Override
    public void dispose() {
        super.dispose();
        GestorDeAssets.dispose();
        ventanaJuego.dispose();
    }
}

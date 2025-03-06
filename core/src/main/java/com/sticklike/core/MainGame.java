package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.sticklike.core.entidades.objetos.texto.FontManager;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

/**
 * MainGame es la clase principal del juego desarrollado con libGDX
 * Extiende de {@link Game}, para poder administrar diversas pantallas simultáneamente.
 * Se encarga de la carga de recursos, la inicialización de la ventana principal y la gestión del ciclo de vida del juego.
 */
public class MainGame extends Game {
    public VentanaJuego1 ventanaJuego1;
    public GestorDeAudio gestorDeAudio;

    @Override
    public void create() {
        // Cargamos todos los recursos al iniciar
        GestorDeAssets.cargarRecursos();
        FontManager.initFonts();
        gestorDeAudio = GestorDeAudio.getInstance();
        ventanaJuego1 = new VentanaJuego1(this, VentanaJuego1.worldWidth, VentanaJuego1.worldHeight); // Pasamos MainGame a la ventana principal (en un futuro el menú del juego)
        setScreen(ventanaJuego1);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        FontManager.disposeFonts();
        GestorDeAssets.dispose();
        ventanaJuego1.dispose();
        gestorDeAudio.dispose();
    }

    public VentanaJuego1 getVentanaJuego1() {
        return ventanaJuego1;
    }
}

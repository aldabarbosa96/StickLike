package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.sticklike.core.entidades.objetos.texto.FontManager;
import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.GestorDeAssets;

/**
 * MainGame es la clase principal del juego con libGDX
 * Extiende de {@link Game}, para poder administrar diversas pantallas simultáneamente y se encarga de cargar los assets e
 * inicializar la ventana principal del juego
 */
public class MainGame extends Game {
    public VentanaJuego1 ventanaJuego1;
    public GestorDeAudio gestorDeAudio;

    @Override
    public void create() {
        GestorDeAssets.cargarRecursos(); // Cargamos los assets al iniciar
        FontManager.initFonts();
        gestorDeAudio = GestorDeAudio.getInstance();
        gestorDeAudio.reproducirMusica();
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

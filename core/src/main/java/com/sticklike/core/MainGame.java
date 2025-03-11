package com.sticklike.core;

import com.badlogic.gdx.Game;
import com.sticklike.core.entidades.objetos.texto.FontManager;
import com.sticklike.core.pantallas.menus.MenuPrincipal;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

/**
 * MainGame es la clase principal del juego desarrollado con libGDX
 * Extiende de {@link Game}, para poder administrar diversas pantallas simultáneamente.
 * Se encarga de la carga de recursos, la inicialización del menú principal y la gestión del ciclo de vida del juego.
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
        setScreen(new MenuPrincipal(this));
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
        if (ventanaJuego1 != null) {
            ventanaJuego1.dispose();
        }
        gestorDeAudio.dispose();
    }

    public VentanaJuego1 getVentanaJuego1() {
        return ventanaJuego1;
    }
}

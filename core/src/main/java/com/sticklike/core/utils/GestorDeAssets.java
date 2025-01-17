package com.sticklike.core.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

/**
 * Clase encargada de cargar y liberar los recursos gráficos usados en el juego (texturas y animaciones)
 * Facilita una única fuente para acceder a estos assets, evitando recargas repetidas y facilitando su gestión y liberación
 */
public class GestorDeAssets {

    // Texturas individuales
    public static Texture stickman, enemigoCulo, corazonVida, armaPiedra, iconoXP, recolectableCaca;

    // Diccionario para animaciones, indexadas por string
    public static HashMap<String, Animation<TextureRegion>> animations;

    public static void cargarRecursos() {
        cargarTexturas();
        cargarAnimaciones();
    }

    /**
     * Crea y almacena animaciones en el HashMap animations
     * Cada animación se genera a partir de una carpeta base, un número de frames y una duración por frame
     */
    public static void cargarAnimaciones() {
        animations = new HashMap<>();

        animations.put("idle", crearAnimacion("actions/movement/stickman_idle", 3, 0.15f));
        animations.put("moveRight", crearAnimacion("actions/movement/stickman_movementD", 5, 0.09f));
        animations.put("moveLeft", crearAnimacion("actions/movement/stickman_movementI", 5, 0.09f));
    }

    public static void cargarTexturas() {
        stickman = new Texture("player/01stickman.png");
        enemigoCulo = new Texture("enemies/01culo.png");
        corazonVida = new Texture("hud/life.png");
        armaPiedra = new Texture("weapons/01piedra.png");
        iconoXP = new Texture("hud/xp.png");
        recolectableCaca = new Texture("drops/caca.png");
    }


    /**
     * Crea una animación a partir de un conjunto de archivos con path base,
     * leyendo en número de contadorFrames imágenes y asignándoles un duracionContador
     *
     * @param ruta             ruta base de los archivos de animación (ej.: "actions/movement/stickman_idle")
     * @param contadorFrames   número de frames (imágenes) a cargar
     * @param duracionContador duración de cada frame en segundos
     * @return la animación construida con esas imágenes
     */
    private static Animation<TextureRegion> crearAnimacion(String ruta, int contadorFrames, float duracionContador) {
        TextureRegion[] frames = new TextureRegion[contadorFrames];
        for (int i = 0; i < contadorFrames; i++) {
            frames[i] = new TextureRegion(new Texture(ruta + "0" + i + ".png"));
        }
        return new Animation<>(duracionContador, frames);
    }

    /**
     * Libera todos los recursos gráficos que haya cargado (texturas y animaciones)
     * Debe llamarse al cerrar el juego para evitar pérdidas de memoria, en MainGame.dispose()
     */
    public static void dispose() {
        if (stickman != null) stickman.dispose();
        if (enemigoCulo != null) enemigoCulo.dispose();
        if (corazonVida != null) corazonVida.dispose();

        if (armaPiedra != null) {
            armaPiedra.dispose();
            armaPiedra = null;
        }
        if (iconoXP != null) iconoXP.dispose();
        if (recolectableCaca != null) recolectableCaca.dispose();

        for (Animation<TextureRegion> animation : animations.values()) {
            for (TextureRegion frame : animation.getKeyFrames()) {
                frame.getTexture().dispose();
            }
        }
    }

}

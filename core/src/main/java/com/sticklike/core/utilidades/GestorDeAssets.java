package com.sticklike.core.utilidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * Clase encargada de cargar y liberar los recursos gráficos usados en el juego (texturas y animaciones)
 * Facilita una única fuente para acceder a estos assets, evitando recargas repetidas y facilitando su gestión y liberación
 */
public class GestorDeAssets {
    // Texturas individuales
    public static Texture stickman, enemigoCulo,enemigoCuloOjo,enemigoRegla,enemigoReglaCruzada,enemigoReglaCasiEsvastica, enemigoPolla,
        enemigoPolla2, enemigoPolla3, enemigoPolla4, enemigoPolla5, enemigoPolla6,enemigoPolla7, corazonVida, armaPiedra,armaCalcetin, iconoXP,
        recolectableCaca, recolectableVida, recolectableLefa;
    //Texturas por lotes
    public static Array<Texture> borrones; // Lista de texturas de borrón

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

        animations.put("idle", crearAnimacion("acciones/movement/stickman_idle", 3, 0.15f));
        animations.put("moveRight", crearAnimacion("acciones/movement/stickman_movementD", 5, 0.09f));
        animations.put("moveLeft", crearAnimacion("acciones/movement/stickman_movementI", 5, 0.09f));
    }

    public static void cargarTexturas() {
        stickman = new Texture("jugador/01stickman.png");
        enemigoCulo = new Texture("enemigos/01culo.png");
        enemigoCuloOjo = new Texture("enemigos/04culoOjo.png");
        //enemigoRegla = new Texture("enemigos/02regla.png");
        enemigoReglaCruzada = new Texture("enemigos/03reglaCruzada.png");
        //enemigoReglaCasiEsvastica = new Texture("enemigos/06reglaEsvastica.png");
        enemigoPolla = new Texture("enemigos/05polla.png");
        enemigoPolla2 = new Texture("enemigos/05polla2.png");
        enemigoPolla3 = new Texture("enemigos/05polla3.png");
        enemigoPolla4 = new Texture("enemigos/05polla4.png");
        enemigoPolla5 = new Texture("enemigos/05polla5.png");
        enemigoPolla6 = new Texture("enemigos/05polla6.png");
        enemigoPolla7 = new Texture("enemigos/05polla7.png");
        corazonVida = new Texture("hud/life.png");
        armaPiedra = new Texture("armas/01piedra.png");
        armaCalcetin = new Texture("armas/02calcetin.png");
        iconoXP = new Texture("hud/xp.png");
        recolectableCaca = new Texture("drops/caca.png");
        recolectableVida = new Texture("hud/life.png");
        recolectableLefa = new Texture("drops/lefa.png");


        borrones = new Array<>();

        borrones.add(new Texture("fondo/borronPolla.png"));
        borrones.add(new Texture("fondo/borronPolla2.png"));
        borrones.add(new Texture("fondo/borronPezpolla.png"));
        borrones.add(new Texture("fondo/borronTetasText.png"));
        borrones.add(new Texture("fondo/borronAnalText.png"));
        borrones.add(new Texture("fondo/borronTontoQuienLoLea.png"));
        borrones.add(new Texture("fondo/borronTetas.png"));
        borrones.add(new Texture("fondo/borronAmorText.png"));
        borrones.add(new Texture("fondo/borronLOL.png"));
        borrones.add(new Texture("fondo/borronTeleranya.png"));
        borrones.add(new Texture("fondo/borronFollar.png"));
        borrones.add(new Texture("fondo/borronPizza2.png"));
        borrones.add(new Texture("fondo/borronELBERTO.png"));
        borrones.add(new Texture("fondo/borronColgado.png"));
        borrones.add(new Texture("fondo/borronSmile.png"));

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
     * Debe llamarse al cerrar el juego para evitar pérdidas de memoria, en {@link com.sticklike.core.MainGame#dispose()}
     */
    public static void dispose() {
        if (stickman != null) stickman.dispose();
        if (enemigoCulo != null) enemigoCulo.dispose();
        if (enemigoRegla != null) enemigoRegla.dispose();
        if (corazonVida != null) corazonVida.dispose();
        if (armaPiedra != null) armaPiedra.dispose();
        if (armaCalcetin != null) armaCalcetin.dispose();
        if (iconoXP != null) iconoXP.dispose();
        if (recolectableCaca != null) recolectableCaca.dispose();
        if (borrones != null) {
            for (Texture t : borrones) {
                if (t != null) t.dispose();
            }
        }
        for (Animation<TextureRegion> animation : animations.values()) {
            for (TextureRegion frame : animation.getKeyFrames()) {
                frame.getTexture().dispose();
            }
        }
    }

}

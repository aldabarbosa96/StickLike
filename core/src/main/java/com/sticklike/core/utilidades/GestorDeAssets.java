package com.sticklike.core.utilidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * Clase encargada de cargar y liberar los recursos gráficos usados en el juego (texturas y animaciones)
 */
public class GestorDeAssets {
    public static Texture stickman, enemigoCulo, enemigoCuloOjo, enemigoRegla, enemigoReglaCruzada, enemigoReglaCasiEsvastica,
        enemigoPolla, corazonVida, armaPiedra, armaCalcetin, iconoXP, recolectableXP, recolectableXP2, recolectableVida, iconoVelMov, iconoPoder,
        iconoFuerza, iconoResistencia, iconoProyectiles, iconoRegeneracion;
    public static Array<Texture> borrones;
    public static HashMap<String, Animation<TextureRegion>> animations;

    public static void cargarRecursos() {
        cargarTexturas();
        cargarAnimaciones();
    }

    public static void cargarAnimaciones() { // todo --> gestionar animaciones enemigos en un futuro

        animations = new HashMap<>();

        animations.put("idle", crearAnimacion("acciones/movement/stickman_idle", 3, 0.15f));
        animations.put("moveRight", crearAnimacion("acciones/movement/stickman_movementD", 5, 0.09f));
        animations.put("moveLeft", crearAnimacion("acciones/movement/stickman_movementI", 5, 0.09f));
    }

    public static void cargarTexturas() {
        // texturas
        stickman = new Texture("jugador/01stickman.png");
        enemigoCulo = new Texture("enemigos/01culo.png");
        enemigoCuloOjo = new Texture("enemigos/04culoOjo.png");
        enemigoReglaCruzada = new Texture("enemigos/03reglaCruzada.png");
        enemigoPolla = new Texture("enemigos/05polla2.png");
        corazonVida = new Texture("hud/life.png");
        armaPiedra = new Texture("armas/01piedra.png");
        armaCalcetin = new Texture("armas/02calcetin.png");
        iconoXP = new Texture("hud/xp.png");
        recolectableXP = new Texture("drops/xp1.png");
        recolectableXP2 = new Texture("drops/xp2.png");
        recolectableVida = new Texture("hud/life.png");
        iconoVelMov = new Texture("hud/vel_mov.png");
        iconoFuerza = new Texture("hud/fuerza.png");
        iconoPoder = new Texture("hud/poder_ataque.png");
        iconoResistencia = new Texture("hud/resistencia.png");
        iconoProyectiles = new Texture("hud/proyectiles.png");
        iconoRegeneracion = new Texture("hud/regeneracion_vida.png");

        // borrones del mapa
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

    private static Animation<TextureRegion> crearAnimacion(String ruta, int contadorFrames, float duracionContador) {
        TextureRegion[] frames = new TextureRegion[contadorFrames];
        for (int i = 0; i < contadorFrames; i++) {
            frames[i] = new TextureRegion(new Texture(ruta + "0" + i + ".png"));
        }
        return new Animation<>(duracionContador, frames);
    }

    public static void dispose() {
        if (stickman != null) stickman.dispose();
        if (enemigoCulo != null) enemigoCulo.dispose();
        if (enemigoRegla != null) enemigoRegla.dispose();
        if (corazonVida != null) corazonVida.dispose();
        if (armaPiedra != null) armaPiedra.dispose();
        if (armaCalcetin != null) armaCalcetin.dispose();
        if (iconoXP != null) iconoXP.dispose();
        if (recolectableXP != null) recolectableXP.dispose();
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

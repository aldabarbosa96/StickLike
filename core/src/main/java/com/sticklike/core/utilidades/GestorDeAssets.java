package com.sticklike.core.utilidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * Clase encargada de cargar y liberar los recursos gráficos usados en el juego (texturas y animaciones).
 */
public class GestorDeAssets {
    public static Texture stickman, enemigoCulo, enemigoCuloOjo,enemigoCuloOjoCerrado, enemigoRegla, enemigoReglaCruzada, enemigoPolla, enemigoExamen, enemigoExamen2,
        corazonVida, armaPiedra, armaCalcetin, armaTazos, armaNubePedo, iconoXP, recolectableXP, recolectableXP2, recolectableVida, recolectableCacaDorada,
        recolectablePowerUp, iconoVelMov, iconoPoder, iconoFuerza, iconoResistencia, iconoProyectiles, iconoRegeneracion, iconoRango, iconoVelAt,
        iconoCritico, iconoVida,iconoCalaveraKills,texturaMarco, bossPolla, bossPollaBocaCerrada, loadingTexture, damageCuloTexture, damagePollaTexture,
    damageReglaTexture, damageExamenTexture, damageBossPollaTexture;
    public static Array<Texture> borrones;
    public static HashMap<String, Animation<TextureRegion>> animations;

    public static void cargarRecursos() {
        cargarTexturas();
        cargarAnimaciones();
    }

    public static void cargarAnimaciones() { // todo --> gestionar animaciones enemigos en un futuro

        animations = new HashMap<>();

        animations.put("idle", crearAnimacion("acciones/movement/stickman_idle", 3, 0.125f));
        animations.put("moveRight", crearAnimacion("acciones/movement/stickman_movementD", 5, 0.05f));
        animations.put("moveLeft", crearAnimacion("acciones/movement/stickman_movementI", 5, 0.05f));
    }

    public static void cargarTexturas() {
        // texturas
        stickman = new Texture("jugador/01stickman.png");
        enemigoCulo = new Texture("enemigos/01culo.png");
        enemigoCuloOjo = new Texture("enemigos/04culoOjo.png");
        enemigoCuloOjoCerrado = new Texture("enemigos/04culoOjoCerrado.png");
        enemigoReglaCruzada = new Texture("enemigos/03reglaCruzada.png");
        enemigoPolla = new Texture("enemigos/05polla2.png");
        enemigoExamen = new Texture("enemigos/examen.png");
        enemigoExamen2 = new Texture("enemigos/examen2.png");
        corazonVida = new Texture("hud/life.png");
        armaPiedra = new Texture("armas/01piedra.png");
        armaCalcetin = new Texture("armas/02calcetin.png");
        armaTazos = new Texture("armas/04tetazo.png");
        armaNubePedo = new Texture("armas/03nubePedo.png");
        //iconoXP = new Texture("hud/xp.png");
        recolectableXP = new Texture("drops/01xp1.png");
        recolectableXP2 = new Texture("drops/01xp2.png");
        recolectableVida = new Texture("hud/life.png");
        recolectableCacaDorada = new Texture("drops/02cacaDorada.png");
        recolectablePowerUp = new Texture("drops/03powerUp.png");
        iconoVelMov = new Texture("hud/vel_mov.png");
        iconoFuerza = new Texture("hud/fuerza.png");
        iconoPoder = new Texture("hud/poder_ataque.png");
        iconoResistencia = new Texture("hud/resistencia2.png");
        iconoProyectiles = new Texture("hud/proyectiles.png");
        iconoRegeneracion = new Texture("hud/regeneracion_vida.png");
        iconoRango = new Texture("hud/rango_disparo.png");
        iconoVelAt = new Texture("hud/vel_ataque.png");
        iconoCritico = new Texture("hud/critico.png");
        iconoVida = new Texture("hud/vida.png");
        iconoCalaveraKills = new Texture("hud/calavera.png");
        texturaMarco = new Texture("hud/marco2.png");
        bossPolla = new Texture("enemigos/bossPolla.png");
        bossPollaBocaCerrada = new Texture("enemigos/bossPollaCerrada.png");
        loadingTexture = new Texture("fondo/fondoLoading.png");
        damageCuloTexture = new Texture("enemigos/damageSprites/01culoD.png");
        damagePollaTexture = new Texture("enemigos/damageSprites/05polla2D.png");
        damageReglaTexture = new Texture("enemigos/damageSprites/03reglaCruzadaD.png");
        damageExamenTexture = new Texture("enemigos/damageSprites/06examenD.png");
        damageBossPollaTexture = new Texture("enemigos/damageSprites/bossPollaD.png");

        // borrones del mapa
        borrones = new Array<>();
        borrones.add(new Texture("fondo/borrones/borronPolla.png"));
        borrones.add(new Texture("fondo/borrones/borronPolla2.png"));
        borrones.add(new Texture("fondo/borrones/borronPezpolla.png"));
        borrones.add(new Texture("fondo/borrones/borronTetasText.png"));
        borrones.add(new Texture("fondo/borrones/borronAnalText.png"));
        borrones.add(new Texture("fondo/borrones/borronTontoQuienLoLea.png"));
        borrones.add(new Texture("fondo/borrones/borronTetas.png"));
        borrones.add(new Texture("fondo/borrones/borronAmorText.png"));
        borrones.add(new Texture("fondo/borrones/borronLOL.png"));
        borrones.add(new Texture("fondo/borrones/borronTeleranya.png"));
        borrones.add(new Texture("fondo/borrones/borronFollar.png"));
        borrones.add(new Texture("fondo/borrones/borronPizza2.png"));
        borrones.add(new Texture("fondo/borrones/borronELBERTO.png"));
        borrones.add(new Texture("fondo/borrones/borronColgado.png"));
        borrones.add(new Texture("fondo/borrones/borronSmile.png"));
        borrones.add(new Texture("fondo/borrones/tote.png"));

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

package com.sticklike.core.utilidades.gestores;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * Clase encargada de cargar y liberar los recursos gráficos usados en el juego (texturas y animaciones)
 * usando el AssetManager de LibGDX para una gestión centralizada y asíncrona.
 */
public class GestorDeAssets {
    // Instancia global del AssetManager
    public static final AssetManager manager = new AssetManager();
    public static HashMap<String, Animation<TextureRegion>> animations;
    public static Array<Texture> borrones;
    // Enemigos/Proyectiles
    public static final String STICKMAN = "jugador/01stickman.png";
    public static final String ENEMIGO_CULO = "enemigos/01culo.png";
    public static final String ENEMIGO_CULO_OJO = "enemigos/04culoOjo.png";
    public static final String ENEMIGO_CULO_OJO_CERRADO = "enemigos/04culoOjoCerrado.png";
    public static final String ENEMIGO_REGLA_CRUZADA = "enemigos/03reglaCruzada.png";
    public static final String ENEMIGO_POLLA = "enemigos/05polla2.png";
    public static final String ENEMIGO_EXAMEN = "enemigos/examen.png";
    public static final String ENEMIGO_EXAMEN2 = "enemigos/examen2.png";
    public static final String ENEMIGO_VATER = "enemigos/water.png";
    public static final String ENEMIGO_VATER2 = "enemigos/water2.png";
    public static final String CORAZON_VIDA = "hud/life.png";
    public static final String ARMA_PIEDRA = "armas/01piedra.png";
    public static final String ARMA_CALCETIN = "armas/02calcetin.png";
    public static final String ARMA_TAZOS = "armas/04tetazo.png";
    public static final String ARMA_NUBE_PEDO = "armas/03nubePedo.png";
    public static final String ARMA_NUBE_PEDO_HUD = "armas/03nubePedoHUD.png";
    public static final String ARMA_PAPELCULO = "armas/05papelCulo.png";
    // Drops y HUD
    public static final String RECOLECTABLE_XP = "drops/01xp1.png";
    public static final String RECOLECTABLE_XP2 = "drops/01xp2.png";
    public static final String RECOLECTABLE_VIDA = "hud/life.png";
    public static final String RECOLECTABLE_CACA_DORADA = "drops/02cacaDorada.png";
    public static final String RECOLECTABLE_POWER_UP = "drops/03powerUp.png";
    public static final String DESTRUCTIBLE = "destructibles/hamburguesa.png";
    public static final String DESTRUCTIBLE_DMG = "destructibles/hamburguesaD.png";
    public static final String ICONO_VEL_MOV = "hud/vel_mov.png";
    public static final String ICONO_FUERZA = "hud/fuerza.png";
    public static final String ICONO_PODER = "hud/poder_ataque.png";
    public static final String ICONO_RESISTENCIA = "hud/resistencia2.png";
    public static final String ICONO_PROYECTILES = "hud/proyectiles.png";
    public static final String ICONO_REGENERACION = "hud/regeneracion_vida.png";
    public static final String ICONO_RANGO = "hud/rango_disparo.png";
    public static final String ICONO_VEL_ATAQUE = "hud/vel_ataque.png";
    public static final String ICONO_CRITICO = "hud/critico.png";
    public static final String ICONO_VIDA = "hud/vida.png";
    public static final String ICONO_CALEAVELA_KILLS = "hud/calavera.png";
    public static final String TEXTURA_MARCO = "hud/marco2.png";

    // Boss y loading
    public static final String BOSS_POLLA = "enemigos/bossPolla.png";
    public static final String BOSS_POLLA_BOCACERRADA = "enemigos/bossPollaCerrada.png";
    public static final String LOADING_TEXTURE = "fondo/fondoLoading.png";

    // Damage sprites
    public static final String DAMAGE_CULO_TEXTURE = "enemigos/damageSprites/01culoD.png";
    public static final String DAMAGE_POLLA_TEXTURE = "enemigos/damageSprites/05polla2D.png";
    public static final String DAMAGE_REGLA_TEXTURE = "enemigos/damageSprites/03reglaCruzadaD.png";
    public static final String DAMAGE_EXAMEN_TEXTURE = "enemigos/damageSprites/06examenD.png";
    public static final String DAMAGE_VATER_TEXTURE = "enemigos/damageSprites/07waterD.png";
    public static final String DAMAGE_BOSS_POLLA_TEXTURE = "enemigos/damageSprites/bossPollaD.png";

    public static void cargarRecursos() {
        cargarTexturas();
        manager.finishLoading();
        cargarAnimaciones();
        rellenarBorrones();
    }

    public static void cargarTexturas() {
        // Cargar texturas con el AssetManager
        manager.load(STICKMAN, Texture.class);
        manager.load(ENEMIGO_CULO, Texture.class);
        manager.load(ENEMIGO_CULO_OJO, Texture.class);
        manager.load(ENEMIGO_CULO_OJO_CERRADO, Texture.class);
        manager.load(ENEMIGO_REGLA_CRUZADA, Texture.class);
        manager.load(ENEMIGO_POLLA, Texture.class);
        manager.load(ENEMIGO_EXAMEN, Texture.class);
        manager.load(ENEMIGO_EXAMEN2, Texture.class);
        manager.load(ENEMIGO_VATER,Texture.class);
        manager.load(ENEMIGO_VATER2,Texture.class);
        manager.load(CORAZON_VIDA, Texture.class);
        manager.load(ARMA_PIEDRA, Texture.class);
        manager.load(ARMA_CALCETIN, Texture.class);
        manager.load(ARMA_TAZOS, Texture.class);
        manager.load(ARMA_NUBE_PEDO, Texture.class);
        manager.load(ARMA_NUBE_PEDO_HUD, Texture.class);
        manager.load(ARMA_PAPELCULO, Texture.class);

        manager.load("armas/05papelCuloImpacto00.png", Texture.class);
        manager.load("armas/05papelCuloImpacto01.png", Texture.class);
        manager.load("armas/05papelCuloImpacto02.png", Texture.class);

        manager.load(RECOLECTABLE_XP, Texture.class);
        manager.load(RECOLECTABLE_XP2, Texture.class);
        manager.load(RECOLECTABLE_VIDA, Texture.class);
        manager.load(RECOLECTABLE_CACA_DORADA, Texture.class);
        manager.load(RECOLECTABLE_POWER_UP, Texture.class);
        manager.load(DESTRUCTIBLE, Texture.class);
        manager.load(DESTRUCTIBLE_DMG, Texture.class);

        manager.load(ICONO_VEL_MOV, Texture.class);
        manager.load(ICONO_FUERZA, Texture.class);
        manager.load(ICONO_PODER, Texture.class);
        manager.load(ICONO_RESISTENCIA, Texture.class);
        manager.load(ICONO_PROYECTILES, Texture.class);
        manager.load(ICONO_REGENERACION, Texture.class);
        manager.load(ICONO_RANGO, Texture.class);
        manager.load(ICONO_VEL_ATAQUE, Texture.class);
        manager.load(ICONO_CRITICO, Texture.class);
        manager.load(ICONO_VIDA, Texture.class);
        manager.load(ICONO_CALEAVELA_KILLS, Texture.class);
        manager.load(TEXTURA_MARCO, Texture.class);

        manager.load(BOSS_POLLA, Texture.class);
        manager.load(BOSS_POLLA_BOCACERRADA, Texture.class);
        manager.load(LOADING_TEXTURE, Texture.class);

        manager.load(DAMAGE_CULO_TEXTURE, Texture.class);
        manager.load(DAMAGE_POLLA_TEXTURE, Texture.class);
        manager.load(DAMAGE_REGLA_TEXTURE, Texture.class);
        manager.load(DAMAGE_EXAMEN_TEXTURE, Texture.class);
        manager.load(DAMAGE_VATER_TEXTURE, Texture.class);
        manager.load(DAMAGE_BOSS_POLLA_TEXTURE, Texture.class);

        manager.load("acciones/movement/stickman_idle00.png", Texture.class);
        manager.load("acciones/movement/stickman_idle01.png", Texture.class);
        manager.load("acciones/movement/stickman_idle02.png", Texture.class);

        manager.load("acciones/movement/stickman_movementD00.png", Texture.class);
        manager.load("acciones/movement/stickman_movementD01.png", Texture.class);
        manager.load("acciones/movement/stickman_movementD02.png", Texture.class);
        manager.load("acciones/movement/stickman_movementD03.png", Texture.class);
        manager.load("acciones/movement/stickman_movementD04.png", Texture.class);

        manager.load("acciones/movement/stickman_movementI00.png", Texture.class);
        manager.load("acciones/movement/stickman_movementI01.png", Texture.class);
        manager.load("acciones/movement/stickman_movementI02.png", Texture.class);
        manager.load("acciones/movement/stickman_movementI03.png", Texture.class);
        manager.load("acciones/movement/stickman_movementI04.png", Texture.class);

        // Cargar borrones
        borrones = new Array<>();
        manager.load("fondo/borrones/borronPolla.png", Texture.class);
        manager.load("fondo/borrones/borronPolla2.png", Texture.class);
        manager.load("fondo/borrones/borronPezpolla.png", Texture.class);
        manager.load("fondo/borrones/borronTetasText.png", Texture.class);
        manager.load("fondo/borrones/borronAnalText.png", Texture.class);
        manager.load("fondo/borrones/borronTontoQuienLoLea.png", Texture.class);
        manager.load("fondo/borrones/borronTetas.png", Texture.class);
        manager.load("fondo/borrones/borronAmorText.png", Texture.class);
        manager.load("fondo/borrones/borronLOL.png", Texture.class);
        manager.load("fondo/borrones/borronTeleranya.png", Texture.class);
        manager.load("fondo/borrones/borronFollar.png", Texture.class);
        manager.load("fondo/borrones/borronPizza2.png", Texture.class);
        manager.load("fondo/borrones/borronELBERTO.png", Texture.class);
        manager.load("fondo/borrones/borronColgado.png", Texture.class);
        manager.load("fondo/borrones/borronSmile.png", Texture.class);
        manager.load("fondo/borrones/tote.png", Texture.class);


    }

    public static void cargarAnimaciones() {
        animations = new HashMap<>();
        animations.put("idle", crearAnimacion("acciones/movement/stickman_idle", 3, 0.125f));
        animations.put("moveRight", crearAnimacion("acciones/movement/stickman_movementD", 5, 0.05f));
        animations.put("moveLeft", crearAnimacion("acciones/movement/stickman_movementI", 5, 0.05f));
        animations.put("papelCuloImpacto", crearAnimacion("armas/05papelCuloImpacto", 3, 0.05f));
    }

    private static Animation<TextureRegion> crearAnimacion(String ruta, int contadorFrames, float duracionContador) {
        TextureRegion[] frames = new TextureRegion[contadorFrames];
        for (int i = 0; i < contadorFrames; i++) {
            // Se construye la ruta para cada frame
            String framePath = ruta + "0" + i + ".png";
            Texture frameTexture = manager.get(framePath, Texture.class);
            frames[i] = new TextureRegion(frameTexture);
        }
        return new Animation<>(duracionContador, frames);
    }
    public static void rellenarBorrones() {
        borrones = new Array<>();
        borrones.add(manager.get("fondo/borrones/borronPolla.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronPolla2.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronPezpolla.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronTetasText.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronAnalText.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronTontoQuienLoLea.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronTetas.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronAmorText.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronLOL.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronTeleranya.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronFollar.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronPizza2.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronELBERTO.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronColgado.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/borronSmile.png", Texture.class));
        borrones.add(manager.get("fondo/borrones/tote.png", Texture.class));
    }

    public static void dispose() {
        manager.dispose();
    }
}

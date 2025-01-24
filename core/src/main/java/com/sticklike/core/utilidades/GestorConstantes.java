package com.sticklike.core.utilidades;

/**
 * Clase para manejar de forma cómoda las constantes del juego
 */
public class GestorConstantes { // todo -> si escala demasiado habrá que gestionarlo con clases internas
    // Resolución virtual
    public static final float VIRTUAL_WIDTH = 1080f;
    public static final float VIRTUAL_HEIGHT = 720f;

    // Configuración de la cámara
    public static final float CAMERA_OFFSET_Y = -65f;

    // Configuración del HUD
    public static final float HUD_HEIGHT = 200f;
    public static final float GRID_CELL_SIZE = 38f;
    public static final float HUD_BAR_WIDTH = 200f;
    public static final float HUD_BAR_HEIGHT = 15f;
    public static final float HUD_BAR_X = 70f;
    public static final float HUD_BAR_Y_OFFSET = 20f;
    public static final float HEART_SIZE = 22f;
    public static final float HEART_X = 40f;
    public static final float HEART_Y_OFFSET = 10f;
    public static final float DESPLAZAMIENTO_VERTICAL_HUD = -30f; // Mueve el HUD completo 30 píxeles hacia abajo
    public static final float POPUP_WIDTH = 400;
    public static final float POPUP_HEIGHT = 350;
    public static final String POPUP_HEADER = "\n\n<< < U P G R A D E S > >> ";
    public static final String POPUP_FOOTER = "   \n";


    // Configuración del jugador
    public static final float VEL_MOV_JUGADOR = 125.0f;
    public static final float DANYO = 25f;
    public static final float VEL_ATAQUE_JUGADOR = 0.74f;
    public static final float VIDA_JUGADOR = 50f;
    public static final float VIDAMAX_JUGADOR = 50f;
    public static final float RANGO_ATAQUE_JUGADOR = 175.0f;
    public static final float INTERVALO_DISPARO = 1.35f;
    public static final float INTERVALO_MIN_DISPARO = 0.1f;
    public static final float ATAQUE_CALCETIN_INTERVALO = 2.25f;
    public static final int NUM_PROYECTILES_INICIALES = 2;
    public static final float WIDTH_JUGADOR = 13f;
    public static final float HEIGHT_JUGADOR = 38f;

    // Configuración de los enemigos
    public static final float VIDA_ENEMIGOCULO = 30f;
    public static final float COOLDOWN_ENEMIGOCULO = 1f;
    public static final float VIDA_ENEMIGOREGLA = 90f;
    public static final float COOLDOWN_ENEMIGOREGLA = 1.5f;
    public static final float TEMPORIZADOR_DANYO = 0f;
    public static final float ENEMY_MAX_PAUSE = 0.75f;
    public static final float ENEMY_MIN_PAUSE = 0.25f;
    public static final float ENEMY_MIN_MOVE_DURATION = 1.0f;
    public static final float ENEMY_MAX_MOVE_DURATION = 10.0f;
    public static final float BORDER_SPAWN_MARGIN = 200f;
    public static final float INTERVALO_SPAWN = 0.6f;
    public static final float MAX_OFFSET = 100f;
    public static final float AJUSTE_OFFSET_X = 65f;
    public static final float AJUSTE_OFFSET_Y = 50f;
    public static final float TIEMPO_CARGA_REGLA = 0.75f;

    // Animaciones enemigos
    public static final float TIEMPO_PARPADEO_RESTANTE = 0f;
    public static final float DURACION_PARPADEO_ENEMIGO = 0.15f;
    public static final float TIEMPO_FADE_RESTANTE = 0f;
    public static final float TIEMPO_FADE_TOTAL = 0.25f;
    public static final float ALPHA_ACTUAL = 1f;
    public static final float VELOCIDAD_EMPUJE = 0f;
    public static final float TEMPORIZADOR_EMPUJE = 0f;
    public static final float DURACION_EMPUJE = 0.15f;
    public static final float TEMPORIZADOR_ANIMACION_MOV = 0f;


    // Configuración del proyectil
    public static final float PROJECTILE_PIEDRA_SPEED = 250f;
    public static final float PROJECTILE_CALCETIN_SPEED = 185f;
    public static final int VEL_ROTACION_CALCETIN = 1080;
    public static final float SPEED_MULT = 1.8f;
    public static final float PIEDRA_SIZE = 8f;
    public static final float CALCETIN_W_SIZE = 20f;
    public static final float CALCETIN_H_SIZE = 24f;
    public static final float PROJECTILE_BASE_DAMAGE = 8f; //gestionar en un futuro
    public static final int NUM_CALCETINES_INICIALES = 4;
    public static final float ANGULO_SEPARACION = 90f;
    public static final float ANGULO_INCIAL = 45f;
    public static final float TEMPORIZADOR_DISPARO = 0f;
    public static final float MAX_DISTANCIA = 350f;
    public static final float DISTANCIA_RECORRIDA = 0f;
    public static final float DANYO_BASE_CALCETIN = 13 + (float) Math.random() * 11;
    public static final float DANYO_BASE_PIEDRA = 21 + (float) Math.random() * 10;
    public static final float EMPUJE_BASE_CALCETIN = 100f;
    public static final float EMPUJE_BASE_PIEDRA = 75f;

    // Configuración TextoFlotante
    public static final float DESPLAZAMIENTOY_TEXTO = 12f;
    public static final float DURACION_TEXTO = 0.4f;

    // Controlador AUDIO

    public static final float AUDIO_CALCETIN = 0.9f;
    public static final float AUDIO_PIEDRA = 0.7f;
}

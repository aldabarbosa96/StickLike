package com.sticklike.core.utilidades;

import javax.swing.plaf.PanelUI;

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


    // Configuración del jugador
    public static final float PLAYER_SPEED = 125.0f;
    public static final float DANYO = 25f;
    public static final float PLAYER_ATTACK_SPEED = 0.83f;
    public static final float PLAYER_HEALTH = 50f;
    public static final float PLAYER_MAX_HEALTH = 50f;
    public static final float PLAYER_ATTACK_RANGE = 175.0f;
    public static final float PLAYER_SHOOT_INTERVAL = 1.2f;
    public static final float ATAQUE_CALCETIN_INTERVALO = 2.5f;

    // Configuración de los enemigos
    public static final float ENEMY_MAX_PAUSE = 0.75f;
    public static final float ENEMY_MIN_PAUSE = 0.25f;
    public static final float ENEMY_MIN_MOVE_DURATION = 2.5f;
    public static final float ENEMY_MAX_MOVE_DURATION = 5.0f;
    public static final float BORDER_SPAWN_MARGIN = 200f;
    public static final float INTERVALO_SPAWN = 0.7f;

    // Configuración del proyectil
    public static final float PROJECTILE_SPEED = 150f;
    public static final float PROJECTILE_SIZE = 8f;
    public static final float PROJECTILE_BASE_DAMAGE = 8f; //gestionar en un futuro


}

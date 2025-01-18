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
    public static final float HUD_BAR_HEIGHT = 20f;
    public static final float HUD_BAR_X = 70f;
    public static final float HUD_BAR_Y_OFFSET = 20f;
    public static final float HEART_SIZE = 30f;
    public static final float HEART_X = 30f;
    public static final float HEART_Y_OFFSET = 12f;

    // Configuración del jugador
    public static final float PLAYER_SPEED = 125f;
    public static final float PLAYER_HEALTH = 50f;
    public static final float PLAYER_MAX_HEALTH = 50f;
    public static final float PLAYER_ATTACK_RANGE = 190f;
    public static final float PLAYER_SHOOT_INTERVAL = 1.1f;

    // Configuración de los enemigos
    public static final float ENEMY_MAX_PAUSE = 0.75f;
    public static final float ENEMY_MIN_PAUSE = 0.25f;
    public static final float ENEMY_MIN_MOVE_DURATION = 1.5f;
    public static final float ENEMY_MAX_MOVE_DURATION = 3.0f;
    public static final float BORDER_SPAWN_MARGIN = 100f;

    // Configuración del proyectil
    public static final float PROJECTILE_SPEED = 175f;
    public static final float PROJECTILE_SIZE = 8f;
    public static final float PROJECTILE_BASE_DAMAGE = 8f; //gestionar en un futuro
}

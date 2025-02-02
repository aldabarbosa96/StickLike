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
    public static final float CAMERA_JUGADOR_OFFSET_Y = 125f;

    // Configuración del HUD
    public static final float HUD_HEIGHT = 200f;
    public static final float GRID_CELL_SIZE = 38f;
    public static final float GRID_CELL_SIZE_CORRECTION = 20f;
    public static final float HUD_BAR_WIDTH = 200f;
    public static final float HUD_BAR_HEIGHT = 12.5f;
    public static final float HUD_BAR_X = 435f;
    public static final float HUD_BAR_Y_OFFSET = 10f;
    public static final float HEART_SIZE = 20f;
    public static final float HEART_X = 410f;
    public static final float HEART_Y_OFFSET = 2.5f;
    public static final float DESPLAZAMIENTO_VERTICAL_HUD = -30f;
    public static final float TEXT_X_CORRECTION = 10f;
    public static final float TEXT_Y_CORRECTION = 13f;
    public static final float NUMBER_Y_CORRECTION = 8.5f;
    public static final float XPBAR_Y_CORRECTION = 25f;
    public static final float BASIC_OFFSET = 1f;
    public static final float UNDER_OFFSET = 0.8f;
    public static final float GROSOR_MARCO = 0.9f;
    public static final float GROSOR_SOMBRA = 0.7f;
    public static final float BORDER_NEGATIVE = 1.5f;
    public static final float BORDER_POSITIVE = 3f;
    public static final float BORDER_CORRECTION = 2f;
    public static final float XPTEXT_Y_CORRECTION = 1.75f;
    public static final float STATS_X_CORRECTION = 960f;
    public static final float STATS_X_CORRECTION2 = 780;
    public static final float STATS_Y_CORRECTION = 45f;
    public static final float ESPACIADO = 18f;
    public static final float ANCHO_DESC1 = 75f;
    public static final float ANCHO_DESC2 = 75f;
    public static final float STATS_ICON_SIZE = 16f;
    public static final float ICON_Y_CORRECTION = 2f;
    public static final float ESPACIADO_LATERAL = 22.5f;

    // Pop_up
    public static final float POPUP_WIDTH = 400f;
    public static final float POPUP_HEIGHT = 350f;
    public static final float POPUP_HEADER_PADDING = 75f;
    public static final float POPUP_ROW_PADDING = 6f;
    public static final float LABEL_WIDTH = 6f;
    public static final float BUTTON_WIDTH = 315f;
    public static final float BUTTON_PADDING = 6f;
    public static final float POPUP_POSITION_CORRECTION = 150f;
    public static final String[] POPUP_BUTTON_LABELS = {"X","Y","B","A"};

    // Timer
    public static final float TIMER_Y_POS = 290f;
    public static final float TIMER_SCALE = 0.7f;
    public static final float TIMER_SECONDS = 60f;
    public static final float TIMER_Y_CORRECTION = 10f;

    // Texto HUD
    public static final String TEXTO_LVL = "LVL:  ";
    public static final String POPUP_HEADER = "\n\n<< < M E J O R A S > >> ";
    public static final String POPUP_FOOTER = "   \n";
    public static final String VEL_MOV = "Velocidad";
    public static final String RANGO = "Rango";
    public static final String VEL_ATAQUE = "Vel. Ataque";
    public static final String FUERZA = "Fuerza";
    public static final String NUM_PROYECTILES = "Munición";
    public static final String VIDA_MAX = "Vida Máx.";
    public static final String REG_VIDA = "Reg. Vida";
    public static final String PODER = "Poder";
    public static final String RESIST = "Resistencia";
    public static final String CRITIC = "Crítico";
    public static final String START = "START";

    // Texto GameOver
    public static final String GAMEOVER = "G A M E  O V E R";
    public static final String GAMEOVER_TEXT = "Pulsa R para Restart o Q para Quit";
    public static final float GAMEOVER_TEXT_Y = 60f;

    // ManuPausa
    public static final int BUTTON_START = 6; // todo --> comprobar funcionamiento con mando PS
    public static final float BUTTON_PAUSE_Y_CORRECTION = 25f;
    public static final float START_TEXT_X = 802.5f;
    public static final float START_TEXT_Y = 295f;
    public static final float PAUSE_TEXT_X = 345;
    public static final float PAUSE_TEXT_Y = 450;

    // Configuración del jugador
    public static final float VEL_MOV_JUGADOR = 120.0f;
    public static final float DANYO = 25f;
    public static final float VEL_ATAQUE_JUGADOR = 0.74f;
    public static final int VIDA_JUGADOR = 50;
    public static final int VIDAMAX_JUGADOR = 50;
    public static final float RANGO_ATAQUE = 175.0f;
    public static final float INTERVALO_DISPARO = 1.35f;
    public static final float INTERVALO_MIN_DISPARO = 0.1f;
    public static final float ATAQUE_CALCETIN_INTERVALO = 2.25f;
    public static final int NUM_PROYECTILES_INICIALES = 2;
    public static final float RESISTENCIA = 0.06f;
    public static final float CRITICO = 0.03f;
    public static final float REGENERACION_VIDA = 0.01f;
    public static final float WIDTH_JUGADOR = 11f;
    public static final float HEIGHT_JUGADOR = 33.5f;
    public static final float PARPADEO_JUGADOR = 0.2f;

    // Configuración de los enemigos
    public static final float VIDA_ENEMIGOCULO = 30f;
    public static final float DANYO_CULO = 2f;
    public static final float COOLDOWN_ENEMIGOCULO = 1f;
    public static final float VEL_BASE_CULO = 50f;
    public static final float VIDA_ENEMIGOREGLA = 75f;
    public static final float DANYO_REGLA = 3.5f;
    public static final float COOLDOWN_ENEMIGOREGLA = 1.5f;
    public static final float VEL_BASE_POLLA = 65f;
    public static final float VIDA_ENEMIGO_POLLA = 8f;
    public static final float DANYO_POLLA = 1.25f;
    public static final float MULT_VELOCIDAD_CULO = 0.85f;
    public static final float MULT_VELOCIDAD_REGLA = 3.8f;
    public static final float MULT_VELOCIDAD_POLLA = 1.5f;
    public static final float TEMPORIZADOR_DANYO = 0f;
    public static final float ENEMY_MAX_PAUSE = 0.75f;
    public static final float ENEMY_MIN_PAUSE = 0.25f;
    public static final float ENEMY_MIN_MOVE_DURATION = 1.0f;
    public static final float ENEMY_MAX_MOVE_DURATION = 10.0f;
    public static final float BORDER_SPAWN_MARGIN = 200f;
    public static final float INTERVALO_SPAWN = 0.5f;
    public static final float MAX_OFFSET = 100f;
    public static final float AJUSTE_OFFSET_X = 65f;
    public static final float AJUSTE_OFFSET_Y = 50f;
    public static final float TIEMPO_CARGA_REGLA = 0.75f;
    public static final float CORRECCION_SPAWN = 50f;
    public static final String[] TIPOS_ENEMIGOS = {"POLLA", "CULO","CULO","CULO","CULO","CULO"};
    public static final String[] TIPOS_ENEMIGOS2 = {"POLLA", "CULO","CULO","CULO","CULO","CULO","REGLA"};

    // Sombras enemigos
    public static final float SHADOW_WIDTH_CULO = 0.9f;
    public static final float SHADOW_HEIGHT_CULO = 0.3f;
    public static final float SHADOW_OFFSET = 2f;

    // Animaciones enemigos
    public static final float TIEMPO_PARPADEO_RESTANTE = 0f;
    public static final float DURACION_PARPADEO_ENEMIGO = 0.15f;
    public static final float DURACION_FADE_ENEMIGO = 0.2f;
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
    public static final float PIEDRA_SIZE = 6f;
    public static final float CALCETIN_W_SIZE = 18f;
    public static final float CALCETIN_H_SIZE = 22f;
    public static final float PROJECTILE_BASE_DAMAGE = 8f; //todo --> gestionar en un futuro
    public static final float TEMPORIZADOR_DISPARO = 0f;
    public static final float MAX_DISTANCIA = 350f;
    public static final float DISTANCIA_RECORRIDA = 0f;
    public static final float EMPUJE_BASE_CALCETIN = 100f;
    public static final float EMPUJE_BASE_PIEDRA = 75f;
    public static final float MULT_DANYO = 1f;
    public static final float TAZO_SIZE = 16f;
    public static final float DURACION_NUBE_PEDO = 8f;
    public static final float RADIO_TAZOS = 52.5f;
    public static final float RADIO_TAZOS_JUGADOR = 45f;
    public static final float VEL_ROTACION = 360f;
    public static final float INTERVALO_TAZOS = 0.1f;
    public static final float DANYO_TAZOS = 1.5f;
    public static final int NUM_TAZOS = 1;
    public static final float INTERVALO_DANYO_TAZOS = 0.5f;
    public static final float DANYO_PEDO = 1f;

    // Configuración TextoFlotante
    public static final float DESPLAZAMIENTOY_TEXTO = 5f;
    public static final float DESPLAZAMIENTOX_TEXTO = 5f;
    public static final float DESPLAZAMIENTOY_TEXTO2 = 5f;
    public static final float DURACION_TEXTO = 0.3f;
    public static final float TEXTO_WIDTH = 0.6f;
    public static final float TEXTO_HEIGHT = 1.4f;

    // Configuración recolectables
    public static final float DISTANCIA_ACTIVACION = 75f;
    public static final float VEL_ATRACCION = 275f;
    public static final float OBJETO_VIDA_WIDTH= 12f;
    public static final float OBJETO_VIDA_HEIGHT= 12f;
    public static final float OBJETO1_XP_WIDTH = 10f;
    public static final float OBJETO1_XP_HEIGHT = 10.5f;
    public static final float OBJETO_ORO_WIDTH = 10f;
    public static final float OBJETO_ORO_HEIGHT = 10f;

    // Controlador Evento
    public static final int LVL_EVENTO1 = 3;
    public static final int LVL_EVENTO2 = 5;
    public static final int LVL_EVENTO3 = 7;
    public static final int LVL_EVENTO4 = 10;
    public static final float EVENTO1_SPAWN_RATE = 0.25f;
    public static final float EVENTO1_SPEED_MULT = 1.15f;
    public static final float EVENTO2_SPAWN_RATE = 0.15f;
    public static final float EVENTO2_SPEED_MULT = 1.35f;
    public static final String[] LISTA_POLLAS = {"POLLA"};
    public static final String[] LISTA_BOSSPOLLA = {"BOSSPOLLA"};
    public static final float EVENTO_POLLAS_SPAWN_RATE = 0.1f;

    // Configuración borrones
    public static final float MIN_DIST_SAME_TEXTURE = 1000f;
    public static final int MAX_ATTEMPTS = 5;
    public static final int CANTIDAD_BORRONES = 1750; // todo --> dibujar más borrones

    public static final float MAP_MIN_X = -10000;
    public static final float MAP_MAX_X =  10000;
    public static final float MAP_MIN_Y = -10000;
    public static final float MAP_MAX_Y =  10000;

    // Controlador AUDIO
    public static final float MUSICA_VOLUMEN = 0.135f;
    public static final float MUSICA_VOLUMEN_PAUSA = 0.05f;
    public static final float AUDIO_CALCETIN = 0.9f;
    public static final float AUDIO_PIEDRA = 0.7f;
    public static final float AUDIO_RECOLECCION_CACA = 1f;
    public static final float AUDIO_RECOLECCION_VIDA = 0.7f;
    public static final float AUDIO_RECOLECCION_ORO = 0.6f;
    public static final float AUDIO_DANYO = 0.9f;
    public static final float AUDIO_MUERTE = 0.7f;
    public static final float AUDIO_UPGRADE = 0.5f;
    public static final float AUDIO_PAUSA = 0.4f;
}

package com.sticklike.core.utilidades.gestores;

/**
 * Clase que gestiona las constantes globales del juego para facilitar su mantenimiento y modificación.
 */
public class GestorConstantes { // todo -> si escala demasiado habrá que gestionarlo con clases internas

    // -------------------------------------------------
    // Resolución virtual
    // -------------------------------------------------
    public static final float VIRTUAL_WIDTH = 1440;
    public static final float VIRTUAL_HEIGHT = 810f;

    // -------------------------------------------------
    // Configuración de la cámara
    // -------------------------------------------------
    public static final float CAMERA_OFFSET_Y = -95f;
    public static final float CAMERA_JUGADOR_OFFSET_X = 7.5f;
    public static final float CAMERA_JUGADOR_OFFSET_Y = 132.5f;

    // -------------------------------------------------
    // CONFIGURACIÓN DEL HUD
    // -------------------------------------------------
    public static final float HUD_HEIGHT = 175f;
    public static final float GRID_CELL_SIZE = 38f * 1.09375f;
    public static final float GRID_CELL_SIZE_CORRECTION = 18f * 1.09375f;
    public static final float HUD_BAR_WIDTH = 200f * 1.4814814815f;
    public static final float HUD_BAR_HEIGHT = 11.5f * 1.09375f;
    public static final float HUD_BAR_X = 380f * 1.4814814815f;
    public static final float HUD_BAR_Y_OFFSET = 10f * 1.09375f;
    public static final float HEART_SIZE = 20f * 1.09375f;
    public static final float HEART_X = 410f * 1.4814814815f;
    public static final float HEART_Y_OFFSET = 2.5f * 1.09375f;
    public static final float DESPLAZAMIENTO_VERTICAL_HUD = -30f * 1.09375f;
    public static final float TEXT_X_CORRECTION = 10f * 1.4814814815f;
    public static final float TEXT_Y_CORRECTION = 14f * 1.09375f;
    public static final float NUMBER_Y_CORRECTION = 9.75f * 1.09375f;
    public static final float XPBAR_Y_CORRECTION = 25f * 1.09375f;
    public static final float BASIC_OFFSET = 1f;
    public static final float BASIC_OFFSET2 = 2f;
    public static final float UNDER_OFFSET = 0.8f;
    public static final float GROSOR_MARCO = 0.9f * 1.09375f;
    public static final float GROSOR_SOMBRA = 0.7f * 1.09375f;
    public static final float BORDER_NEGATIVE = 1.5f * 1.09375f;
    public static final float BORDER_POSITIVE = 3f * 1.09375f;
    public static final float BORDER_CORRECTION = 2f * 1.09375f;
    public static final float XPTEXT_Y_CORRECTION = 2f * 1.09375f;
    public static final float STATS_X_CORRECTION = 835f * 1.4814814815f;
    public static final float STATS_X_CORRECTION2 = 680f * 1.4814814815f;
    public static final float STATS_Y_CORRECTION = 38f * 1.09375f;
    public static final float ESPACIADO = 18f * 1.09375f;
    public static final float ANCHO_DESC1 = 75f * 1.4814814815f;
    public static final float ANCHO_DESC2 = 75f * 1.4814814815f;
    public static final float STATS_ICON_SIZE = 16f * 1.09375f;
    public static final float ICON_Y_CORRECTION = 2f * 1.09375f;
    public static final float ESPACIADO_LATERAL = 12.5f * 1.4814814815f;

    // -------------------------------------------------
    // Pop_up
    // -------------------------------------------------
    public static final float POPUP_WIDTH = 325f * 1.4814814815f;
    public static final float POPUP_HEIGHT = 375f * 1.09375f;
    public static final float POPUP_HEADER_PADDING = 50f;
    public static final float POPUP_ROW_PADDING = 8f;
    public static final float LABEL_WIDTH = 6f * 1.4814814815f;
    public static final float BUTTON_WIDTH = 225f * 1.4814814815f;
    public static final float BUTTON_PADDING = 5f;
    public static final float POPUP_POSITION_CORRECTION = 150f * 1.09375f;
    public static final String[] POPUP_BUTTON_LABELS = {"X", "Y", "B", "A"};

    // -------------------------------------------------
    // Timer
    // -------------------------------------------------
    public static final float TIMER_Y_POS = 290f * 1.09375f;
    public static final float TIMER_SCALE = 0.85f;
    public static final float TIMER_SECONDS = 60f;

    // -------------------------------------------------
    // Texto HUD
    // -------------------------------------------------
    public static final String TEXTO_LVL = "LVL:  ";
    public static final String POPUP_HEADER = "\n\n<< < M E J O R A S > >> ";
    public static final String POPUP_FOOTER = "  \n";
    public static final String POPUP_FOOTER2 = "   ";
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
    public static final String PAUSA = "P A U S A";

    // -------------------------------------------------
    // Texto GameOver
    // -------------------------------------------------
    public static final String GAMEOVER = "G A M E   O V E R";
    public static final String GAMEOVER_TEXT = "Pulsa R para Reiniciar o Q para Quitar";
    public static final float GAMEOVER_TEXT_Y = 100f * 1.09375f;

    // -------------------------------------------------
    // ManuPausa
    // -------------------------------------------------
    public static final int BUTTON_START = 6; // se mantiene
    public static final float BUTTON_PAUSE_Y_CORRECTION = 25f * 1.09375f;
    public static final float START_TEXT_OFFSET_X = 0.18875f * VIRTUAL_WIDTH;
    public static final float START_TEXT_OFFSET_Y = 0.18333f * VIRTUAL_HEIGHT;
    public static final float PAUSE_TEXT_OFFSET_X = 0.1f * VIRTUAL_WIDTH;
    public static final float PAUSE_TEXT_OFFSET_Y = 0.4444f * VIRTUAL_HEIGHT;


    // -------------------------------------------------
    // Configuración del jugador
    // -------------------------------------------------
    public static final float VEL_MOV_JUGADOR = 125;
    public static final float DANYO = 25f;
    public static final float VEL_ATAQUE_JUGADOR = 0.74f;
    public static final int VIDA_JUGADOR = 75;
    public static final int VIDAMAX_JUGADOR = 75;
    public static final float RANGO_ATAQUE = 175f;
    public static final float INTERVALO_DISPARO = 1.35f;
    public static final float INTERVALO_MIN_DISPARO = 0.09f;
    public static final float ATAQUE_CALCETIN_INTERVALO = 2.25f;
    public static final int NUM_PROYECTILES_INICIALES = 2;
    public static final float RESISTENCIA = 0.05f;
    public static final float CRITICO = 0.07f;
    public static final float REGENERACION_VIDA = 0.001f;
    public static final float PODER_JUGADOR = 10f;
    public static final float WIDTH_JUGADOR = 12f;
    public static final float HEIGHT_JUGADOR = 34f;
    public static final float PARPADEO_JUGADOR_DANYO = 0.2f;
    public static final float PARPADEO_JUGADOR_VIDA = 0.2f;

    // -------------------------------------------------
    // Configuración de los enemigos
    // -------------------------------------------------
    public static final float VIDA_ENEMIGOCULO = 35f;
    public static final float VIDA_ENEMIGO_POLLA = 23f;
    public static final float VIDA_ENEMIGOREGLA = 80f;
    public static final float VIDA_ENEMIGO_EXAMEN = 27f;
    public static final float VIDA_ENEMIGO_VATER = 300f;
    public static final float VIDA_ENEMIGO_ALARMA = 88f;
    public static final float DANYO_CULO = 2f;
    public static final float DANYO_REGLA = 3.5f;
    public static final float DANYO_POLLA = 1.25f;
    public static final float DANYO_EXAMEN = 1.5f;
    public static final float DANYO_VATER = 50f;
    public static final float COOLDOWN_ENEMIGOCULO = 1f;
    public static final float COOLDOWN_POLLA = 1f;
    public static final float COOLDOWN_ENEMIGOREGLA = 1.5f;
    public static final float COOLDOWN_EXAMEN = 1.2f;
    public static final float COOLDOWN_VATER = 1f;
    public static final float VEL_BASE_CULO = 35f;
    public static final float VEL_BASE_POLLA = 45f;
    public static final float VEL_BASE_EXAMEN = 90f;
    public static final float VEL_BASE_VATER = 25f;
    public static final float MULT_VELOCIDAD_CULO = 0.85f;
    public static final float MULT_VELOCIDAD_REGLA = 3.8f;
    public static final float MULT_VELOCIDAD_POLLA = 1.5f;
    public static final float MULT_VELOCIDAD_EXAMEN = 1.75f;
    public static final float TEMPORIZADOR_DANYO = 0f;
    public static final float ENEMY_MAX_PAUSE = 0.75f;
    public static final float ENEMY_MIN_PAUSE = 0.25f;
    public static final float ENEMY_MIN_MOVE_DURATION = 1.0f;
    public static final float ENEMY_MAX_MOVE_DURATION = 10.0f;
    public static final float BORDER_SPAWN_MARGIN = 200f;
    public static final float INTERVALO_SPAWN = 1f;
    public static final float MAX_OFFSET = 100f;
    public static final float AJUSTE_OFFSET_X = 65f;
    public static final float AJUSTE_OFFSET_Y = 50f;
    public static final float TIEMPO_CARGA_REGLA = 0.75f;
    public static final float TIEMPO_CARGA_EXAMEN = 1;
    public static final float TIEMPO_LINEAL_EXAMEN = 5;
    public static final float TIEMPO_PARABOLA_EXAMEN = 3;
    public static final float CORRECCION_SPAWN = 75f;

    // -------------------------------------------------
    // Sombras enemigos
    // -------------------------------------------------
    public static final float SHADOW_WIDTH_CULO = 0.9f;
    public static final float SHADOW_HEIGHT_CULO = 0.35f;
    public static final float SHADOW_OFFSET = 2.25f;
    public static final float SHADOW_OFFSET_POLLA = 2f;

    // -------------------------------------------------
    // Animaciones enemigos
    // -------------------------------------------------
    public static final float TIEMPO_PARPADEO_RESTANTE = 0f;
    public static final float DURACION_PARPADEO_ENEMIGO = 0.085f;
    public static final float DURACION_FADE_ENEMIGO = 0.25f;
    public static final float TIEMPO_FADE_RESTANTE = 0f;
    public static final float TIEMPO_FADE_TOTAL = 0.25f;
    public static final float ALPHA_ACTUAL = 1f;
    public static final float VELOCIDAD_EMPUJE = 0f;
    public static final float TEMPORIZADOR_EMPUJE = 0f;
    public static final float DURACION_EMPUJE = 0.25f;
    public static final float TEMPORIZADOR_ANIMACION_MOV = 0f;

    // -------------------------------------------------
    // Configuración del proyectil
    // -------------------------------------------------
    public static final float PROJECTILE_PIEDRA_SPEED = 250f;
    public static final float PROJECTILE_CALCETIN_SPEED = 185f;
    public static final int VEL_ROTACION_CALCETIN = 1080;
    public static final float DANYO_CALCETIN = 13f;
    public static final float DANYO_PAPELCULO = 45f;
    public static final float EMPUJE_BASE_PAPELCULO = 250f;
    public static final float SPEED_MULT = 1.8f;
    public static final float PIEDRA_SIZE = 6f;
    public static final float CALCETIN_W_SIZE = 20f;
    public static final float CALCETIN_H_SIZE = 24f;
    public static final float PAPELCULO_W_SIZE = 16;
    public static final float PAPELCULO_H_SIZE = 19;
    public static final float PAPELCULO_SPEED = 666f;
    public static final float PROJECTILE_BASE_DAMAGE = 8f; //todo --> gestionar en un futuro
    public static final float TEMPORIZADOR_DISPARO = 0f;
    public static final float MAX_DISTANCIA = 350f;
    public static final float DISTANCIA_RECORRIDA = 0f;
    public static final float EMPUJE_BASE_CALCETIN = 100f;
    public static final float EMPUJE_BASE_PIEDRA = 75f;
    public static final float MULT_DANYO = 1f;
    public static final float TAZO_SIZE = 14f;
    public static final float DURACION_NUBE_PEDO = 8f;
    public static final float RADIO_TAZOS = 52.5f;
    public static final float RADIO_TAZOS_JUGADOR = 75f;
    public static final float VEL_ROTACION = 180f;
    public static final float INTERVALO_TAZOS = 0.1f;
    public static final float DANYO_TAZOS = 3.5f;
    public static final int NUM_TAZOS = 2;
    public static final float INTERVALO_DANYO_TAZOS = 0.5f;
    public static final float DANYO_PEDO = 2.5f;
    public static final float DELAY_ENTRE_PEDOS = 5f;

    // -------------------------------------------------
    // Configuración TextoFlotante
    // -------------------------------------------------
    public static final float DESPLAZAMIENTOY_TEXTO = 5f * 1.09375f;
    public static final float DESPLAZAMIENTOX_TEXTO = 5f * 1.4814814815f;
    public static final float DESPLAZAMIENTOY_TEXTO2 = 5f * 1.09375f;
    public static final float DURACION_TEXTO = 0.3f;
    public static final float TEXTO_WIDTH = 0.6f;
    public static final float TEXTO_HEIGHT = 1.4f;

    // -------------------------------------------------
    // Configuración recolectables
    // -------------------------------------------------
    public static final float DISTANCIA_ACTIVACION = 60f;
    public static final float VEL_ATRACCION = 300f;
    public static final float OBJETO_VIDA_WIDTH = 12f * 1.09375f;
    public static final float OBJETO_VIDA_HEIGHT = 12f * 1.09375f;
    public static final float OBJETO1_XP_WIDTH = 12.5f;
    public static final float OBJETO1_XP_HEIGHT = 14f;
    public static final float OBJETO_ORO_WIDTH = 14f;
    public static final float OBJETO_ORO_HEIGHT = 14f;
    public static final float OBJETO_PWUP_WIDTH = 8f;
    public static final float OBJETO_PWUP_HEIGHT = 20f;
    public static final float VIDA_DESTRUCTIBLE = 150f;
    public static final float ANCHO_DESTRUCT = 40f;
    public static final float ALTO_DESTRUCT = 36f;
    public static final float ANCHO_DESTRUCT1 = 44f;
    public static final float ALTO_DESTRUCT1 = 44;
    public static final float ANCHO_DESTRUCT2 = 40f;
    public static final float ALTO_DESTRUCT2 = 40f;
    public static final float ANCHO_DESTRUCT3 = 38f;
    public static final float ALTO_DESTRUCT3 = 40f;
    public static final float ANCHO_BOOST = 18f;
    public static final float ALTO_BOOST = 18f;

    // -------------------------------------------------
    // Controlador Evento
    // -------------------------------------------------
    public static final int LVL_EVENTO1 = 3;
    public static final int LVL_EVENTO2 = 5;
    public static final int LVL_EVENTO3 = 7;
    public static final int LVL_EVENTO4 = 9;
    public static final int LVL_EVENTO5 = 10;
    public static final float EVENTO1_SPAWN_RATE = 0.5f;
    public static final float EVENTO1_SPEED_MULT = 1.25f;
    public static final float EVENTO2_SPAWN_RATE = 0.3f;
    public static final float EVENTO2YMEDIO_SPAWN_RATE = 0.275f;
    public static final float EVENTO2_SPEED_MULT = 1.45f;
    public static final float EVENTO3_SPEED_MULT = 2f;
    public static final float EVENTO3_SPAWN_RATE = 0.05f;
    public static final String[] LISTA_POLLAS = {"POLLA"};
    public static final String[] LISTA_BOSSPOLLA = {"BOSSPOLLA", "POLLA"};
    public static final String[] LISTA_EXAMEN = {"EXAMEN", "ALARMA", "REGLA"};
    public static final String[] TIPOS_ENEMIGOS = {"POLLA", "CULO", "CULO", "CULO", "CULO", "CULO"};
    public static final String[] TIPOS_ENEMIGOS2 = {"CULO", "CULO", "CULO", "CULO", "CULO", "CULO", "CULO", "CULO", "CULO", "REGLA"};
    public static final String[] TIPOS_ENEMIGOS3 = {"POLLA", "CULO", "CULO", "CULO", "CULO", "CULO"};
    public static final float EVENTO_POLLAS_SPAWN_RATE = 0.000000001f;

    // -------------------------------------------------
    // Configuración borrones
    // -------------------------------------------------
    public static final float MIN_DIST_SAME_TEXTURE = 1000f;
    public static final float MIN_DIST_SAME_TEXTURE2 = 500f;
    public static final int MAX_ATTEMPTS = 5;
    public static final int CANTIDAD_BORRONES = 1000; // todo --> dibujar más borrones
    public static final float MAP_MIN_X = -10000;
    public static final float MAP_MAX_X = 10500;
    public static final float MAP_MIN_Y = -10000;
    public static final float MAP_MAX_Y = 10500;

    // -------------------------------------------------
    // Configuración recolectables mapa
    // -------------------------------------------------
    public static final float MAP_MIN_X_DROP = -2000;
    public static final float MAP_MAX_X_DROP = 2500;
    public static final float MAP_MIN_Y_DROP = -2000;
    public static final float MAP_MAX_Y_DROP = 2500;

    // -------------------------------------------------
    // Controlador AUDIO
    // -------------------------------------------------
    public static final float MUSICA_VOLUMEN = 0.135f;
    public static final float MUSICA_VOLUMEN_PAUSA = 0.05f;
    public static final float AUDIO_CALCETIN = 0.9f;
    public static final float AUDIO_PIEDRA = 0.7f;
    public static final float AUDIO_RECOLECCION_CACA = 1f;
    public static final float AUDIO_RECOLECCION_VIDA = 0.7f;
    public static final float AUDIO_RECOLECCION_ORO = 0.6f;
    public static final float AUDIO_RECOLECCION_PWUP = 1f;
    public static final float AUDIO_DANYO = 0.9f;
    public static final float AUDIO_MUERTE = 0.7f;
    public static final float AUDIO_UPGRADE = 0.5f;
    public static final float AUDIO_PAUSA = 0.4f;
    public static final int MAX_INSTANCIAS_SONIDO = 3;
}

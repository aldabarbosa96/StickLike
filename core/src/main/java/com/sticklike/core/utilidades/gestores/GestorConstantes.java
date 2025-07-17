package com.sticklike.core.utilidades.gestores;

/**
 * Clase que gestiona las constantes globales del juego para facilitar su mantenimiento y modificación.
 */
public class GestorConstantes { // todo -> si escala demasiado habrá que gestionarlo con clases internas

    // -------------------------------------------------
    // Resolución
    // -------------------------------------------------
    public static final float VIRTUAL_WIDTH = 1440f;
    public static final float VIRTUAL_HEIGHT = 810f;
    public static final float REAL_WIDTH = 2560f;

    // -------------------------------------------------
    // Configuración de la cámara
    // -------------------------------------------------
    public static final float CAMERA_OFFSET_Y = -95f;
    public static final float CAMERA_JUGADOR_OFFSET_X = 23.5f;
    public static final float CAMERA_JUGADOR_OFFSET_Y = 142.5f;
    public static final float MARGEN_LIMITES_MAPA = 50f;

    // -------------------------------------------------
    // CONFIGURACIÓN DEL HUD
    // -------------------------------------------------
    public static final float HUD_HEIGHT = 175f;
    public static final float GRID_CELL_SIZE = 32;
    public static final float GRID_CELL_SIZE_HUD = 40;
    public static final float GRID_CELL_SIZE_CORRECTION = 18f * 1.09375f;
    public static final float HUD_BAR_WIDTH = 200f * 1.4814814815f;
    public static final float HUD_BAR_HEIGHT = 11.5f * 1.09375f;
    public static final float HUD_BAR_X = 380f * 1.4814814815f;
    public static final float HUD_BAR_Y_OFFSET = 10f * 1.09375f;
    public static final float HUD_BAR_Y_OFFSET2 = 20f * 1.09375f;
    public static final float HEART_SIZE = 20f * 1.09375f;
    public static final float HEART_X = 410f * 1.4814814815f;
    public static final float HEART_Y_OFFSET = 2.5f * 1.09375f;
    public static final float DESPLAZAMIENTO_VERTICAL_HUD = -30f * 1.09375f;
    public static final float TEXT_X_CORRECTION = 10f * 1.4814814815f;
    public static final float TEXT_Y_CORRECTION = 14f * 1.09375f;
    public static final float NUMBER_Y_CORRECTION = 9.75f * 1.09375f;
    public static final float XPBAR_Y_CORRECTION = 25f * 1.09375f;
    public static final float BASIC_OFFSET = 1f;
    public static final float BASIC_OFFSET2 = 5f;
    public static final float UNDER_OFFSET = 0.8f;
    public static final float GROSOR_MARCO = 0.9f * 1.09375f;
    public static final float GROSOR_SOMBRA = 0.7f * 1.09375f;
    public static final float BORDER_NEGATIVE = 1.5f * 1.09375f;
    public static final float BORDER_POSITIVE = 3f * 1.09375f;
    public static final float BORDER_CORRECTION = 2f * 1.09375f;
    public static final float STATS_X_CORRECTION = 835f * 1.4814814815f;
    public static final float STATS_X_CORRECTION2 = 680f * 1.4814814815f;
    public static final float STATS_Y_CORRECTION = 38f * 1.09375f;
    public static final float ESPACIADO = 18f * 1.09375f;
    public static final float ANCHO_DESC1 = 75f * 1.4814814815f;
    public static final float ANCHO_DESC2 = 75f * 1.4814814815f;
    public static final float STATS_ICON_SIZE = 16f * 1.09375f;
    public static final float ICON_Y_CORRECTION = 2f * 1.09375f;
    public static final float ESPACIADO_LATERAL = 12.5f * 1.4814814815f;
    public static final float START_BUTTON_CORRECTION = 2f;


    // -------------------------------------------------
    // Pop_up
    // -------------------------------------------------
    public static final float POPUP_WIDTH = 330f * 1.4814814815f;
    public static final float POPUP_HEIGHT = 400f * 1.09375f;
    public static final float POPUP_HEADER_PADDING = 75f;
    public static final float POPUP_ROW_PADDING = 13f;
    public static final float LABEL_WIDTH = 6f * 1.4814814815f;
    public static final float BUTTON_WIDTH = 225f * 1.4814814815f;
    public static final float BUTTON_PADDING = 12f;
    public static final float POPUP_POSITION_CORRECTION = 150f * 1.09375f;
    public static final String[] POPUP_BUTTON_LABELS = {"X", "Y", "B", "A"};

    // -------------------------------------------------
    // Timer
    // -------------------------------------------------
    public static final float TIMER_Y_POS = 290f * 1.09375f;
    public static final float TIMER_SECONDS = 60f;

    // -------------------------------------------------
    // Texto HUD
    // -------------------------------------------------
    public static final String TEXTO_LVL = "LVL:";
    public static final String TITULO_POPUP = "<<< M E J O R A S >>> ";
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
    public static final int BUTTON_START = 6;
    public static final float BUTTON_PAUSE_Y_CORRECTION = 25f * 1.09375f;
    public static final float START_TEXT_OFFSET_X = 0.18875f * VIRTUAL_WIDTH;
    public static final float START_TEXT_OFFSET_Y = 0.18333f * VIRTUAL_HEIGHT;
    public static final float PAUSE_TEXT_OFFSET_X = 0.1f * VIRTUAL_WIDTH;
    public static final float PAUSE_TEXT_OFFSET_Y = 0.4444f * VIRTUAL_HEIGHT;


    // -------------------------------------------------
    // Configuración del jugador
    // -------------------------------------------------
    public static final float VEL_MOV_JUGADOR = 125f;
    public static final float DANYO = 25f;
    public static final float VEL_ATAQUE_JUGADOR = 0.74f;
    public static final int VIDA_JUGADOR = 50;
    public static final int VIDAMAX_JUGADOR = 50;
    public static final float RANGO_ATAQUE = 175f;
    public static final float INTERVALO_DISPARO = 1.35f;
    public static final float INTERVALO_MIN_DISPARO = 0.005f;
    public static final float ATAQUE_CALCETIN_INTERVALO = 2.25f;
    public static final int NUM_PROYECTILES_INICIALES = 1;
    public static final float RESISTENCIA = 0.05f;
    public static final float CRITICO = 0.07f;
    public static final float REGENERACION_VIDA = 0.001f;
    public static final float PODER_JUGADOR = 10f;
    public static final float WIDTH_JUGADOR = 14.5f;
    public static final float HEIGHT_JUGADOR = 39f;
    public static final float PARPADEO_JUGADOR_DANYO = 0.2f;
    public static final float PARPADEO_JUGADOR_VIDA = 0.2f;

    // -------------------------------------------------
    // Configuración de los enemigos
    // -------------------------------------------------
    public static final float VIDA_ENEMIGOCULO = 15f;
    public static final float VIDA_ENEMIGO_POLLA = 23f;
    public static final float VIDA_ENEMIGOREGLA = 5f;
    public static final float VIDA_ENEMIGO_EXAMEN = 27f;
    public static final float VIDA_ENEMIGO_VATER = 300f;
    public static final float VIDA_ENEMIGO_ALARMA = 88f;
    public static final float VIDA_ENEMIGO_CALCULADORA = 56f;
    public static final float VIDA_ENEMIGO_LIBRO = 23f;
    public static final float VIDA_ENEMIGO_TETA = 1f;
    public static final float VIDA_ENEMIGO_COGOLLO = 75f;
    public static final float DANYO_CULO = 2f;
    public static final float DANYO_CALCULADORA = 5f;
    public static final float DANYO_LIBRO = 12.5f;
    public static final float DANYO_REGLA = 3.5f;
    public static final float DANYO_POLLA = 1.25f;
    public static final float DANYO_TETA = 1.33f;
    public static final float DANYO_EXAMEN = 1.5f;
    public static final float DANYO_VATER = 40f;
    public static final float DANYO_COGOLLO = 4.5f;
    public static final float COOLDOWN_ENEMIGOCULO = 1f;
    public static final float COOLDOWN_POLLA = 1f;
    public static final float COOLDOWN_ENEMIGOREGLA = 1.5f;
    public static final float COOLDOWN_EXAMEN = 1.2f;
    public static final float COOLDOWN_VATER = 1f;
    public static final float VEL_BASE_CULO = 45f;
    public static final float VEL_BASE_POLLA = 55f;
    public static final float VEL_BASE_EXAMEN = 90f;
    public static final float VEL_BASE_VATER = 30f;
    public static final float VEL_BASE_ALARMA = 75f;
    public static final float VEL_BASE_CALCULADORA = 85f;
    public static final float VEL_BASE_LIBRO = 85f;
    public static final float VEL_BASE_GRAPADORA = 90f;
    public static final float VEL_BASE_COGOLLO = 90f;
    public static final float MULT_VELOCIDAD_CULO = 0.85f;
    public static final float MULT_VELOCIDAD_REGLA = 3.8f;
    public static final float MULT_VELOCIDAD_CONDON = 3.25f;
    public static final float MULT_VELOCIDAD_POLLA = 1.5f;
    public static final float MULT_VELOCIDAD_TETA = 1.15f;
    public static final float MULT_VELOCIDAD_EXAMEN = 1.75f;
    public static final float TEMPORIZADOR_DANYO = 0f;
    public static final float ENEMY_MAX_PAUSE = 0.75f;
    public static final float ENEMY_MIN_PAUSE = 0.25f;
    public static final float ENEMY_MIN_MOVE_DURATION = 1.0f;
    public static final float ENEMY_MAX_MOVE_DURATION = 10.0f;
    public static final float BORDER_SPAWN_MARGIN = 200f;
    public static final float INTERVALO_SPAWN = 0.75f;
    public static final float MAX_OFFSET = 100f;
    public static final float AJUSTE_OFFSET_X = 65f;
    public static final float AJUSTE_OFFSET_Y = 50f;
    public static final float TIEMPO_CARGA_REGLA = 0.75f;
    public static final float TIEMPO_CARGA_EXAMEN = 1;
    public static final float TIEMPO_LINEAL_EXAMEN = 5;
    public static final float TIEMPO_PARABOLA_EXAMEN = 3;
    public static final float CORRECCION_SPAWN = 25f;

    // -------------------------------------------------
    // Sombras enemigos
    // -------------------------------------------------
    public static final float SHADOW_WIDTH_CULO = 0.9f;
    public static final float SHADOW_HEIGHT_CULO = 0.35f;
    public static final float SHADOW_OFFSET = 2.25f;
    public static final float SHADOW_OFFSET_POLLA = 2f;
    public static final float SHADOW_WIDTH_PERF = 0.8f;
    public static final float SHADOW_HEIGH_PERF = 0.3f;
    // -------------------------------------------------
    // Animaciones enemigos
    // -------------------------------------------------
    public static final float TIEMPO_PARPADEO_RESTANTE = 0f;
    public static final float DURACION_PARPADEO_ENEMIGO = 0.09f;
    public static final float DURACION_FADE_ENEMIGO = 0.4f;
    public static final float DURACION_FADE_BOSS_POLLA = 1.5f;
    public static final float DURACION_FADE_BOSS_PROFE = 1f;
    public static final float TIEMPO_FADE_RESTANTE = 0f;
    public static final float TIEMPO_FADE_TOTAL = 1f;
    public static final float ALPHA_ACTUAL = 1f;
    public static final float VELOCIDAD_EMPUJE = 0f;
    public static final float TEMPORIZADOR_EMPUJE = 0f;
    public static final float DURACION_EMPUJE = 0.25f;
    public static final float TEMPORIZADOR_ANIMACION_MOV = 0f;
    public static final float DAMAGE_SPRITE_MUERTE_TIMER = 0.08f;

    // -------------------------------------------------
    // Configuración del proyectil
    // -------------------------------------------------
    public static final float PROJECTILE_PIEDRA_SPEED = 250f;
    public static final float PROJECTILE_CALCETIN_SPEED = 185f;
    public static final int VEL_ROTACION_CALCETIN = 1080;
    public static final float DANYO_CALCETIN = 13f;
    public static final float DANYO_PAPELCULO = 45f;
    public static final float DANYO_BOLIBIC = 25f;
    public static final float DANYO_DILDO = 40f;
    public static final float EMPUJE_BASE_PAPELCULO = 200f;
    public static final float SPEED_MULT = 1.8f;
    public static final float PIEDRA_SIZE = 7f;
    public static final float CALCETIN_W_SIZE = 20.5f;
    public static final float CALCETIN_H_SIZE = 24.5f;
    public static final float PAPELCULO_W_SIZE = 18;
    public static final float PAPELCULO_H_SIZE = 21;
    public static final float PAPELCULO_SPEED = 666f;
    public static final float PROJECTILE_BASE_DAMAGE = 8f; //todo --> gestionar en un futuro
    public static final float TEMPORIZADOR_DISPARO = 0f;
    public static final float MAX_DISTANCIA = 350f;
    public static final float DISTANCIA_RECORRIDA = 0f;
    public static final float EMPUJE_BASE_CALCETIN = 100f;
    public static final float EMPUJE_BASE_PIEDRA = 75f;
    public static final float EMPUJE_BASE_BOLI = 150f;
    public static final float EMPUJE_BASE_DILDO = 135f;
    public static final float MULT_DANYO = 1f;
    public static final float TAZO_SIZE = 18f;
    public static final float DURACION_NUBE_PEDO = 8f;
    public static final float RADIO_TAZOS = 25f;
    public static final float RADIO_TAZOS_JUGADOR = 75f;
    public static final float VEL_ROTACION = 165f;
    public static final float INTERVALO_TAZOS = 0.1f;
    public static final float DANYO_TAZOS = 3.5f;
    public static final int NUM_TAZOS = 2;
    public static final float INTERVALO_DANYO_TAZOS = 0.5f;
    public static final float DANYO_PEDO = 2.5f;
    public static final float DELAY_ENTRE_PEDOS = 3.5f;
    public static final float IMPACTO_DURACION = 0.1f;

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
    public static final float DISTANCIA_ACTIVACION = 75f;
    public static final float VEL_ATRACCION = 300f;
    public static final float OBJETO_VIDA_WIDTH = 13f * 1.09375f;
    public static final float OBJETO_VIDA_HEIGHT = 14f * 1.09375f;
    public static final float OBJETO1_XP_WIDTH = 16f;
    public static final float OBJETO1_XP_HEIGHT = 17f;
    public static final float OBJETO_ORO_WIDTH = 16f;
    public static final float OBJETO_ORO_HEIGHT = 16f;
    public static final float OBJETO_IMAN_HEIGHT = 24f;
    public static final float OBJETO_IMAN_WIDTH = 24f;
    public static final float OBJETO_PWUP_WIDTH = 8f;
    public static final float OBJETO_PWUP_HEIGHT = 25f;
    public static final float VIDA_DESTRUCTIBLE = 150f;
    public static final float VIDA_DESTRUCTIBLE2 = 75f;
    public static final float VIDA_TRAGAPERRAS = 50;
    public static final float ANCHO_DESTRUCT = 42f;
    public static final float ALTO_DESTRUCT = 38f;
    public static final float ANCHO_DESTRUCT1 = 46f;
    public static final float ALTO_DESTRUCT1 = 46;
    public static final float ANCHO_DESTRUCT2 = 42f;
    public static final float ALTO_DESTRUCT2 = 42f;
    public static final float ANCHO_DESTRUCT3 = 40f;
    public static final float ALTO_DESTRUCT3 = 42f;
    public static final float ANCHO_DESTRUCT_LATA = 42f;
    public static final float ALTO_DESTRUCT_LATA = 62f;
    public static final float ALTO_TRAGAPERRAS = 120;
    public static final float ANCHO_TRAGAPERRAS = 120f;
    public static final float ANCHO_BOOST = 22f;
    public static final float ALTO_BOOST = 22f;

    // -------------------------------------------------
    // Controlador Evento
    // -------------------------------------------------
    public static final int LVL_EVENTO1 = 3;
    public static final int LVL_EVENTO2 = 5;
    public static final int LVL_EVENTO3 = 7;
    public static final int LVL_EVENTO4 = 10;
    public static final int LVL_EVENTO5 = 12;
    public static final int LVL_EVENTO6 = 13;
    public static final int LVL_EVENTO7 = 14;
    public static final int LVL_EVENTO8 = 15;
    public static final int LVL_EVENTO9 = 16;
    public static final int LVL_EVENTO10 = 18;
    public static final int LVL_EVENTO11 = 20;
    public static final int LVL_EVENTO12 = 21;
    public static final float EVENTO1_SPAWN_RATE = 0.33f;
    public static final float EVENTO1_SPEED_MULT = 1.1f;
    public static final float EVENTO2_SPAWN_RATE = 0.2f;
    public static final float EVENTO2YMEDIO_SPAWN_RATE = 0.275f;
    public static final float EVENTO2_SPEED_MULT = 1.3f;
    public static final float EVENTO3_SPEED_MULT = 1.5f;
    public static final float EVENTO3_SPAWN_RATE = 0.1f;
    public static final float EVENTO4_SPAWN_RATE = 0.05f;
    public static final float EVENTO5_SPAWN_RATE = 0.025f;
    public static final float EVENTO6_SPAWN_RATE = 0.02f;
    public static final float EVENTO7_SPAWN_RATE = 0.015f;
    public static final float EVENTO8_SPAWN_RATE = 0.0125f;
    public static final float EVENTO_TETAS_SPAWN_RATE = 0.000001f;
    public static final String[] LISTA_TETAS = {"TETA", "TETA", "CULO"};
    public static final String[] LISTA_EXAMEN = {"EXAMEN"};
    public static final String[] LISTA_ALARMA = {"ALARMA", "ALARMA", "ALARMA", "ALARMA", "ALARMA", "ALARMA", "ALARMA", "REGLA"};
    public static final String[] LISTA_REGLA = {"REGLA"};
    public static final String[] TIPOS_ENEMIGOS = {"CULO"};
    public static final String[] TIPOS_ENEMIGOS2 = {"CULO", "CULO", "CULO", "CULO", "CULO", "CULO", "CULO", "POLLA", "POLLA", "CONDON"};
    public static final String[] TIPOS_ENEMIGOS3 = {"POLLA", "TETA"};
    public static final String[] TIPOS_ENEMIGOS4 = {"CALCULADORA", "LIBRO"};
    public static final String[] TIPOS_ENEMIGOS5 = {"GRAPADORA","CALCULADORA","LIBRO"};
    public static final String[] TIPOS_ENEMIGOS6 = {"PERFORADORA","GRAPADORA","CALCULADORA","LIBRO"};
    public static final String[] TIPOS_ENEMIGOS9 = {"PERFORADORA","GRAPADORA","CALCULADORA","LIBRO","EXAMEN","REGLA","ALARMA"};
    public static final String[] TIPOS_ENEMIGOS10 = {"COGOLLO"};

    // -------------------------------------------------
    // Configuración borrones
    // -------------------------------------------------
    public static final float MIN_DIST_SAME_TEXTURE = 1000f;
    public static final float MIN_DIST_SAME_TEXTURE2 = 750;
    public static final int MAX_ATTEMPTS = 5;
    public static final int CANTIDAD_BORRONES = 1500; // todo --> dibujar más borrones
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
    public static final float AUDIO_CALCETIN = 0.8f;
    public static final float AUDIO_PAPEL = 1;
    public static final float AUDIO_PIEDRA = 0.75f;
    public static final float AUDIO_RECOLECCION_CACA = 0.9f;
    public static final float AUDIO_RECOLECCION_VIDA = 1f;
    public static final float AUDIO_RECOLECCION_ORO = 0.85f;
    public static final float AUDIO_RECOLECCION_PWUP = 0.75f;
    public static final float AUDIO_DANYO = 0.75f;
    public static final float AUDIO_MUERTE = 0.85f;
    public static final float AUDIO_UPGRADE = 0.75f;
    public static final float AUDIO_PAUSA = 0.75f;
    public static final int MAX_INSTANCIAS_SONIDO = 3;
    public static final int MAX_INSTANCIAS_SONIDO_EXPL = 2;
}

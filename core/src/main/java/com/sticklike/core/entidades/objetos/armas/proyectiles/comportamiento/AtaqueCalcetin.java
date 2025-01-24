package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilCalcetin;

/**
 * La clase AtaqueCalcetin maneja la lógica para disparar calcetines en todas las direcciones.
 */
public class AtaqueCalcetin {

    private final int NUM_CALCETINES = 4;
    private final float ANGULO_SEPARACION = 90f;
    private float temporizadorDisparo = 0f;
    private float intervaloDisparo = 1.0f;

    /**
     * Constructor de AtaqueCalcetin.
     * Permite configurar el intervalo de disparo.
     *
     * @param intervaloDisparoInicial Intervalo inicial entre disparos en segundos.
     */
    public AtaqueCalcetin(float intervaloDisparoInicial) {
        this.intervaloDisparo = intervaloDisparoInicial;
    }

    /**
     * Procesa el ataque especial del jugador, generando 3 calcetines en todas las direcciones.
     *
     * @param jug              referencia al {@link Jugador} que ataca
     * @param controladorAudio referencia al {@link ControladorAudio} para reproducir sonidos
     */
    public void procesarAtaque(Jugador jug, ControladorAudio controladorAudio) {
        // Obtenemos coordenadas del centro del jugador
        float startX = jug.getSprite().getX() + jug.getSprite().getWidth() / 2f;
        float startY = jug.getSprite().getY() + jug.getSprite().getHeight() / 2f;

        // Calculamos los ángulos para los calcetines
        float anguloInicial = 45f;
        float anguloSeparacion = 90f;

        // Calculamos los ángulos para los calcetines
        for (int i = 0; i < NUM_CALCETINES; i++) {
            float angulo = anguloInicial + i * anguloSeparacion;
            float radianes = (float) Math.toRadians(angulo);
            float direccionX = (float) Math.cos(radianes);
            float direccionY = (float) Math.sin(radianes);

            ProyectilCalcetin calcetin = new ProyectilCalcetin(startX, startY, direccionX, direccionY, 175f, 1.8f);
            jug.getControladorProyectiles().anyadirNuevoProyectil(calcetin);

        }
        controladorAudio.reproducirEfecto("lanzarCalcetin",0.75f);
    }

    /**
     * Maneja el disparo del ataque especial y actualiza el temporizador de disparo.
     *
     * @param delta            Tiempo transcurrido desde el último frame.
     * @param jugador          Referencia al jugador.
     * @param controladorAudio Referencia al controlador de audio.
     */
    public void manejarDisparo(float delta, Jugador jugador, ControladorAudio controladorAudio) {
        temporizadorDisparo += delta;

        if (temporizadorDisparo >= intervaloDisparo) {
            temporizadorDisparo = 0;
            procesarAtaque(jugador, controladorAudio);
        }
    }

    /**
     * Permite ajustar el intervalo de disparo después de la inicialización.
     *
     * @param nuevoIntervaloNuevo Intervalo en segundos.
     */
    public void setIntervaloDisparo(float nuevoIntervaloNuevo) {
        this.intervaloDisparo = nuevoIntervaloNuevo;
    }
}

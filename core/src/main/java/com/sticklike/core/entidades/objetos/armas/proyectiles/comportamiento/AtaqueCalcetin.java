package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilCalcetin;
import com.sticklike.core.utilidades.GestorConstantes;

/**
 * La clase AtaqueCalcetin maneja la lógica para disparar calcetines en todas las direcciones.
 */
public class AtaqueCalcetin {
    private float temporizadorDisparo = GestorConstantes.TEMPORIZADOR_DISPARO;
    private float intervaloDisparo;

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

        // Ángulos para las direcciones (diagonales primero, luego cardinales)
        float[] angulos = {45, 135, 225, 315, 0, 90, 180, 270}; // NO, NE, SO, SE, N, E, S, O
        for (int i = 0; i < jug.getProyectilesPorDisparo() + 2; i++) { // todo --> aplicar límite de 8 proyectiles de tipo calcetín en un futuro
            float angulo = angulos[i]; // Selecciona el ángulo según el índice
            float radianes = (float) Math.toRadians(angulo);
            float direccionX = (float) Math.cos(radianes);
            float direccionY = (float) Math.sin(radianes);

            // Crear el proyectil
            ProyectilCalcetin calcetin = new ProyectilCalcetin(startX, startY, direccionX, direccionY,
                GestorConstantes.PROJECTILE_CALCETIN_SPEED, GestorConstantes.SPEED_MULT);

            // Añadir el proyectil al controlador del jugador
            jug.getControladorProyectiles().anyadirNuevoProyectil(calcetin);
        }

        controladorAudio.reproducirEfecto("lanzarCalcetin", GestorConstantes.AUDIO_CALCETIN);
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
}

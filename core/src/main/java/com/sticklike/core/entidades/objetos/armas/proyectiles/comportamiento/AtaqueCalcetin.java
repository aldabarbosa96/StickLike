package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilCalcetin;

import static com.sticklike.core.utilidades.GestorConstantes.*;

public class AtaqueCalcetin {
    private float temporizadorDisparo = TEMPORIZADOR_DISPARO;
    private float intervaloDisparo;

    // Campos para almacenar las bonificaciones de la mejora:
    private int proyectilesExtra = 0;  // Bonificación en el número de proyectiles
    private float extraDamage = 0f;      // Bonificación en daño base (en puntos)

    public AtaqueCalcetin(float intervaloDisparoInicial) {
        this.intervaloDisparo = intervaloDisparoInicial;
    }

    public void incrementarNumeroProyectiles(int incremento) {
        proyectilesExtra += incremento;
    }

    public void aumentarDamage(float incremento) {
        extraDamage += DANYO_CALCETIN + (DANYO_CALCETIN * incremento);
    }

    public void procesarAtaque(Jugador jug, GestorDeAudio gestorDeAudio) {
        // Obtenemos las coordenadas del centro del jugador
        float startX = jug.getSprite().getX() + jug.getSprite().getWidth() / 2f;
        float startY = jug.getSprite().getY() + jug.getSprite().getHeight() / 2f;

        // Ángulos para las direcciones (diagonales primero, luego cardinales)
        float[] angulos = {45, 135, 225, 315, 0, 90, 180, 270}; // NO, NE, SO, SE, N, E, S, O

        int totalProyectiles = Math.min(4 + proyectilesExtra, angulos.length);

        for (int i = 0; i < totalProyectiles; i++) {
            int index = i % angulos.length;
            float angulo = angulos[index];
            float radianes = (float) Math.toRadians(angulo);
            float direccionX = (float) Math.cos(radianes);
            float direccionY = (float) Math.sin(radianes);

            float poderHabilidad = jug.getPoderJugador();
            // Se crea el proyectil pasando también el extraDamage para aumentar el daño base
            ProyectilCalcetin calcetin = new ProyectilCalcetin(
                startX, startY, direccionX, direccionY,
                PROJECTILE_CALCETIN_SPEED, SPEED_MULT,
                poderHabilidad, extraDamage
            );

            // Se añade el proyectil al controlador del jugador
            jug.getControladorProyectiles().anyadirNuevoProyectil(calcetin);
        }

        gestorDeAudio.reproducirEfecto("lanzarCalcetin", AUDIO_CALCETIN);
    }

    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        temporizadorDisparo += delta;

        if (temporizadorDisparo >= intervaloDisparo) {
            temporizadorDisparo = 0;
            procesarAtaque(jugador, gestorDeAudio);
        }
    }
}

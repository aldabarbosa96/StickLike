package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilCalcetin;

import static com.sticklike.core.utilidades.GestorConstantes.*;

public class AtaqueCalcetin {
    private float temporizadorDisparo = TEMPORIZADOR_DISPARO;
    private float intervaloDisparo = 2.5f;
    private final float MIN_INTERVALO_DISPARO = 0.25f;

    // Campos para almacenar las bonificaciones de la mejora:
    private int proyectilesExtra = 0;
    private float extraDamage = 0f;
    private boolean ultimateUp = false;

    public void procesarAtaque(Jugador jug, GestorDeAudio gestorDeAudio) {
        // Obtenemos las coordenadas del centro del jugador
        float startX = jug.getSprite().getX() + jug.getSprite().getWidth() / 2f - 10f;
        float startY = jug.getSprite().getY() + jug.getSprite().getHeight() / 2f - 5f;

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

        if (!ultimateUp) {
            gestorDeAudio.reproducirEfecto("lanzarCalcetin", AUDIO_CALCETIN);
        }
    }

    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        temporizadorDisparo += delta;

        if (temporizadorDisparo >= intervaloDisparo) {
            temporizadorDisparo = 0;
            procesarAtaque(jugador, gestorDeAudio);
        }
    }

    public void incrementarNumeroProyectiles(int incremento) {
        proyectilesExtra += incremento;
    }

    public void aumentarDamage(float incremento) {
        extraDamage += DANYO_CALCETIN + (DANYO_CALCETIN * incremento);
    }

    public void aumentarVelocidadDisparo(float factorReduccion) {
        // Reducir el intervalo en un porcentaje, pero sin bajar del mínimo
        intervaloDisparo *= (1 - factorReduccion);

        // Asegurar que el intervalo no sea menor que el mínimo permitido
        if (intervaloDisparo < MIN_INTERVALO_DISPARO) {
            intervaloDisparo = MIN_INTERVALO_DISPARO;
        }
    }
    public void ultimateCALCETIN(float incremento) {
        this.intervaloDisparo = intervaloDisparo - (intervaloDisparo * incremento);
        ultimateUp = true;

    }

}

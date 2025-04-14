package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.proyectil.LatigoDildo;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;


public class AtaqueDildo {
    private float cooldownTimer = 0f;
    private final float intervaloDisparo = 3f;
    private float extraDamage = 0f;
    private boolean ataqueEnProgreso = false;
    private float timerAtaque = 0f;

    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        if (!ataqueEnProgreso) {
            cooldownTimer += delta;
            if (cooldownTimer >= intervaloDisparo) {
                // Se inicia el ataque: se dispara el proyectil derecho
                float poderHabilidad = jugador.getPoderJugador();
                LatigoDildo latigoDerecha = new LatigoDildo(jugador, 1, poderHabilidad, extraDamage);
                jugador.getControladorProyectiles().anyadirNuevoProyectil(latigoDerecha);
                gestorDeAudio.reproducirEfecto("dildo", 0.6f);

                // Se inicia el temporizador del ataque y se reinicia el cooldown
                ataqueEnProgreso = true;
                timerAtaque = 0f;
                cooldownTimer = 0f;
            }
        } else {
            // Se está en espera para disparar el proyectil izquierdo.
            timerAtaque += delta;
            if (timerAtaque >= 0.5f) {
                float poderHabilidad = jugador.getPoderJugador();
                LatigoDildo latigoIzquierda = new LatigoDildo(jugador, -1, poderHabilidad, extraDamage);
                jugador.getControladorProyectiles().anyadirNuevoProyectil(latigoIzquierda);
                gestorDeAudio.reproducirEfecto("dildo", 0.6f);
                // Finalizamos el ataque para poder procesar el siguiente.
                ataqueEnProgreso = false;
            }
        }
    }

    public void aumentarDamage(float incremento) {
        extraDamage += DANYO_DILDO + (DANYO_DILDO * incremento);
    }
}

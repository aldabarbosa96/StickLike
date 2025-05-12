package com.sticklike.core.entidades.objetos.armas.comportamiento;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.ProyectilPapelCulo;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class AtaquePapelCulo {
    private float temporizadorDisparo = 0f;
    private float intervaloDisparo = 3f;
    private final float MIN_INTERVALO_DISPARO = 0.5f;
    private float extraDamage = 0f;
    private boolean ladoDisparoDerecho = true;
    private boolean mejoraAmbosLados = false;
    private boolean fragmentado = false;

    public void procesarAtaque(Jugador jug, GestorDeAudio gestorDeAudio) {
        float startX = jug.getSprite().getX() + jug.getSprite().getWidth() / 2f;
        float startY = jug.getSprite().getY() + jug.getSprite().getHeight() / 2f - 5f;

        float anguloLanzamiento = 45f;

        float poderHabilidad = jug.getPoderJugador();

        float direccionHorizontal;
        float offsetX;

        if (ladoDisparoDerecho) {
            direccionHorizontal = 1f;
            offsetX = 5f;
        } else {
            direccionHorizontal = -1f;
            offsetX = -5f;
        }
        startX += offsetX;

        // Alternamos el lado para cada disparo
        ladoDisparoDerecho = !ladoDisparoDerecho;


        ProyectilPapelCulo papelCulo = new ProyectilPapelCulo(startX, startY, anguloLanzamiento, PAPELCULO_SPEED, poderHabilidad, extraDamage, jug, direccionHorizontal, false);
        jug.getControladorProyectiles().anyadirNuevoProyectil(papelCulo);

        gestorDeAudio.reproducirEfecto("lanzarCalcetin", AUDIO_PAPEL);
    }

    public void procesarAtaqueAmbosLados(Jugador jug, GestorDeAudio gestorDeAudio) {
        float startX = jug.getSprite().getX() + jug.getSprite().getWidth() / 2f;
        float startY = jug.getSprite().getY() + jug.getSprite().getHeight() / 2f - 5f;
        float anguloLanzamiento = 45f;
        float poderHabilidad = jug.getPoderJugador();

        // Disparo hacia la derecha
        float offsetXDerecha = 5f;
        float direccionDerecha = 1f;
        float startXDerecha = startX + offsetXDerecha;
        ProyectilPapelCulo proyectilDerecha = new ProyectilPapelCulo(startXDerecha, startY, anguloLanzamiento, PAPELCULO_SPEED, poderHabilidad, extraDamage, jug, direccionDerecha, false);
        jug.getControladorProyectiles().anyadirNuevoProyectil(proyectilDerecha);

        // Disparo hacia la izquierda
        float offsetXIzquierda = -5f;
        float direccionIzquierda = -1f;
        float startXIzquierda = startX + offsetXIzquierda;
        ProyectilPapelCulo proyectilIzquierda = new ProyectilPapelCulo(startXIzquierda, startY, anguloLanzamiento, PAPELCULO_SPEED, poderHabilidad, extraDamage, jug, direccionIzquierda, false);
        jug.getControladorProyectiles().anyadirNuevoProyectil(proyectilIzquierda);

        gestorDeAudio.reproducirEfecto("lanzarCalcetin", AUDIO_PAPEL);
    }

    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        temporizadorDisparo += delta;

        if (temporizadorDisparo >= intervaloDisparo) {
            temporizadorDisparo = 0;
            procesarAtaque(jugador, gestorDeAudio);
            if (mejoraAmbosLados) {
                procesarAtaqueAmbosLados(jugador, gestorDeAudio);
            }
        }

    }

    public void setMejoraAmbosLados(boolean mejoraAmbosLados) {
        this.mejoraAmbosLados = mejoraAmbosLados;
    }

    // todo --> valorar si se integra la mejora de daño con alguna de las existentes (o independientemente)
    public void aumentarDamage(float incremento) {
        extraDamage += DANYO_PAPELCULO + (DANYO_PAPELCULO * incremento);
    }

    public void aumentarVelocidadDisparo(float factorReduccion) {
        intervaloDisparo *= (1 - factorReduccion);
        if (intervaloDisparo < MIN_INTERVALO_DISPARO) {
            intervaloDisparo = MIN_INTERVALO_DISPARO;
        }
    }

    public boolean isFragmentado() {
        return fragmentado;
    }

    public void setFragmentado(boolean fragmentado) {
        this.fragmentado = fragmentado;
    }

    public float getCooldownDuration() {
        return intervaloDisparo;
    }

    public float getTimeUntilNextShot() {
        return Math.max(0f, intervaloDisparo - temporizadorDisparo);
    }
}

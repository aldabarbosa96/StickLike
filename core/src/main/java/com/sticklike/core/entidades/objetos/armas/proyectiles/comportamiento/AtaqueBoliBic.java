package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.entidades.jugador.InputsJugador;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilBoliBic;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

public class AtaqueBoliBic {
    private float temporizadorDisparo = 0f;
    private float intervaloDisparo = 0.5f;

    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        temporizadorDisparo += delta;
        if (temporizadorDisparo >= intervaloDisparo) {
            temporizadorDisparo = 0f;
            procesarAtaque(jugador, gestorDeAudio);
        }
    }

    public void procesarAtaque(Jugador jug, GestorDeAudio gestorDeAudio) {
        float playerCenterX = jug.getSprite().getX() + jug.getSprite().getWidth() / 2f;
        float playerCenterY = jug.getSprite().getY() + jug.getSprite().getHeight() / 2f;

        // Obtenemos la dirección del disparo a partir del input
        InputsJugador.ResultadoInput input = jug.getInputController().procesarInput(0);
        float dirX = input.movX;
        float dirY = input.movY;
        if (dirX == 0 && dirY == 0) {
            dirX = 1f; // Por defecto a la derecha
        }
        // Normalizamos el vector dirección para evitar velocidades mayores en diagonal
        float magnitud = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        if (magnitud != 0) {
            dirX /= magnitud;
            dirY /= magnitud;
        }

        // Creamos el proyectil usando la posición y la dirección normalizada
        ProyectilBoliBic proyectil = new ProyectilBoliBic(playerCenterX, playerCenterY, dirX, dirY, 500, jug);
        jug.getControladorProyectiles().anyadirNuevoProyectil(proyectil);

        // gestorDeAudio.reproducirEfecto("lanzarBoliBic", AUDIO_BOLIBIC);
    }

}

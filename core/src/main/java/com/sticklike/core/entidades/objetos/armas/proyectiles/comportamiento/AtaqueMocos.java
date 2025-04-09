package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.LluviaMocos;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

public class AtaqueMocos {
    private float temporizadorDisparo = 0f;
    private float intervaloDisparo = 0.8f;

    public void procesarAtaque(Jugador jug, GestorDeAudio gestorDeAudio) {
        OrthographicCamera cam = jug.getControladorEnemigos().getVentanaJuego1().getOrtographicCamera();

        float left = cam.position.x - cam.viewportWidth / 2;
        float right = left + cam.viewportWidth;
        float top = cam.position.y + cam.viewportHeight / 2;
        float randomX = MathUtils.random(left, right - 20);
        float fallSpeed = MathUtils.random(250, 300);

        LluviaMocos mocos = new LluviaMocos(randomX, top, fallSpeed, gestorDeAudio);
        jug.getControladorProyectiles().anyadirNuevoProyectil(mocos);
    }

    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        temporizadorDisparo += delta;
        if (temporizadorDisparo >= intervaloDisparo) {
            temporizadorDisparo = 0;
            procesarAtaque(jugador, gestorDeAudio);
        }
    }
}

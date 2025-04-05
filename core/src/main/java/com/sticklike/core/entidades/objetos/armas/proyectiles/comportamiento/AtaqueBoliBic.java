package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.entidades.jugador.InputsJugador;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilBoliBic;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

public class AtaqueBoliBic {
    private float temporizadorDisparo = 0f;
    private float intervaloDisparo = 0.75f;

    // Vector de puntería continuo que se actualiza cada frame; se inicializa a (1,0) y se irá modificando
    private float aimX = 1f;
    private float aimY = 0f;

    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        // Actualizamos la puntería en cada frame según el input
        actualizarAim(delta, jugador);

        temporizadorDisparo += delta;
        if (temporizadorDisparo >= intervaloDisparo) {
            temporizadorDisparo = 0f;
            procesarAtaque(jugador, gestorDeAudio);
        }
    }

    // Actualiza el vector de puntería de forma continua
    private void actualizarAim(float delta, Jugador jug) {
        InputsJugador.ResultadoInput input = jug.getInputController().procesarInput(0);

        // Usamos el input como dirección objetivo
        // Si no hay input (por ejemplo, teclas soltadas), mantenemos el vector actual
        float targetX = input.movX;
        float targetY = input.movY;
        if (targetX == 0 && targetY == 0) {
            targetX = aimX;
            targetY = aimY;
        }

        // Normalizamos el vector objetivo para asegurar magnitud 1
        float magTarget = (float) Math.sqrt(targetX * targetX + targetY * targetY);
        if (magTarget != 0) {
            targetX /= magTarget;
            targetY /= magTarget;
        }

        // Calculamos la diferencia angular entre la dirección actual y la objetivo. Se usa atan2 para obtener los ángulos en grados
        float currentAngle = (float) Math.toDegrees(Math.atan2(aimY, aimX));
        float targetAngle = (float) Math.toDegrees(Math.atan2(targetY, targetX));
        float diff = (((targetAngle - currentAngle) + 180 + 360) % 360) - 180;

        // Si el cambio es muy grande (por ejemplo, ≥ 90°), saltamos instantáneamente
        if (Math.abs(diff) >= 90f) {
            aimX = targetX;
            aimY = targetY;
        } else {
            // Si el cambio es moderado, se interpola de forma suave
            float lerpFactor = 5f * delta;
            aimX = lerp(aimX, targetX, lerpFactor);
            aimY = lerp(aimY, targetY, lerpFactor);
            // Normalizamos nuevamente para evitar errores por la interpolación.
            float magAim = (float) Math.sqrt(aimX * aimX + aimY * aimY);
            if (magAim != 0) {
                aimX /= magAim;
                aimY /= magAim;
            }
        }
    }

    // Función lineal de interpolación (lerp) entre dos valores
    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    public void procesarAtaque(Jugador jug, GestorDeAudio gestorDeAudio) {
        float playerCenterX = jug.getSprite().getX() + jug.getSprite().getWidth() / 2f;
        float playerCenterY = jug.getSprite().getY() + jug.getSprite().getHeight() / 2f;

        // Usamos el vector de puntería actual para determinar la dirección del disparo
        float dirX = aimX;
        float dirY = aimY;

        // Creamos el proyectil con la dirección obtenida
        ProyectilBoliBic proyectil = new ProyectilBoliBic(playerCenterX, playerCenterY, dirX, dirY, 500);
        jug.getControladorProyectiles().anyadirNuevoProyectil(proyectil);

        gestorDeAudio.reproducirEfecto("boli", 0.5f);
    }
}

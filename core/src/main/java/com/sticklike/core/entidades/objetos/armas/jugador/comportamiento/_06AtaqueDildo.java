package com.sticklike.core.entidades.objetos.armas.jugador.comportamiento;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.jugador._06LatigoDildo;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Lógica de disparo del Látigo‑dildo con gestión de sus upgrades.
 */
public class _06AtaqueDildo {
    private float cooldownTimer = 0f;
    private final float intervaloDisparo = 3f;

    private boolean ataqueEnProgreso = false;
    private int totalSwings;
    private int currentSwing;
    private float timerSwing = 0f;
    private float delayEntreSwings = 0.5f;

    private float extraDamage = 0f;
    private boolean haloActivo = false;

    private int swingsPerSide = 1;
    private boolean rapidoActivo = false;

    public void aumentarDamage(float incremento) {
        extraDamage += DANYO_DILDO * incremento;
    }

    public void activarHaloEnergia() {
        haloActivo = true;
    }

    public void mejorarCrackDoble() {
        delayEntreSwings *= 0.5f;
    }

    public void activarGolpeDoble() {
        swingsPerSide++;
    }

    public void mejorarVelocidadSwing() {
        rapidoActivo = true;
    }

    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio audio) {
        if (!ataqueEnProgreso) {
            cooldownTimer += delta;
            if (cooldownTimer >= intervaloDisparo) {
                ataqueEnProgreso = true;
                cooldownTimer = 0f;
                timerSwing = 0f;
                currentSwing = 0;
                totalSwings = swingsPerSide * 2;
                ejecutarSwing(jugador, audio);
            }
        } else {
            timerSwing += delta;
            if (timerSwing >= delayEntreSwings) {
                if (currentSwing < totalSwings) {
                    ejecutarSwing(jugador, audio);
                } else {
                    ataqueEnProgreso = false;
                }
                timerSwing = 0f;
            }
        }
    }

    private void ejecutarSwing(Jugador jugador, GestorDeAudio audio) {
        currentSwing++;
        int group = (currentSwing - 1) / swingsPerSide;
        int lado = (group % 2 == 0) ? 1 : -1;

        float poder = jugador.getPoderJugador();
        _06LatigoDildo latigo = new _06LatigoDildo(jugador, lado, poder, extraDamage, haloActivo, rapidoActivo);
        jugador.getControladorProyectiles().anyadirNuevoProyectil(latigo);
        audio.reproducirEfecto("dildo", 0.6f);
    }

    public float getCooldownDuration() {
        return intervaloDisparo;
    }

    public float getTimeUntilNextShot() {
        if (ataqueEnProgreso) {
            // Durante la ráfaga de swings, no mostramos overlay de cooldown
            return 0f;
        }
        // Estamos en fase de cooldown: tiempo restante hasta próximo ataque
        return Math.max(0f, intervaloDisparo - cooldownTimer);
    }
}

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
    private float intervaloDisparo = 3f;

    private boolean ataqueEnProgreso = false;
    private int totalSwings;
    private int currentSwing;
    private float timerSwing = 0f;
    private float delayEntreSwings = 0.5f;

    private float extraDamage = 0f;
    private boolean haloActivo = false;

    private int swingsPerSide = 1;
    private boolean rapidoActivo = false;
    private boolean simultaneoActivo = false;

    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio audio) {
        if (!ataqueEnProgreso) {
            cooldownTimer += delta;
            if (cooldownTimer >= intervaloDisparo) {
                ataqueEnProgreso = true;
                cooldownTimer = 0f;
                timerSwing = 0f;
                currentSwing = 0;

                totalSwings = simultaneoActivo ? swingsPerSide
                    : swingsPerSide * 2;

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
        float poder = jugador.getPoderJugador();

        if (simultaneoActivo) {
            /*  ▶  Disparamos los dos lados a la vez  */
            lanzarLatigo(jugador, 1, poder, audio);
            lanzarLatigo(jugador, -1, poder, null); // null para evitar doble sonido
        } else {
            /*  ▶  Patrón alterno D-I-D-I… original  */
            int group = (currentSwing - 1) / swingsPerSide;
            int lado = (group % 2 == 0) ? 1 : -1;
            lanzarLatigo(jugador, lado, poder, audio);
        }
    }

    /* factoriza la creación de proyectil + sonido */
    private void lanzarLatigo(Jugador jugador, int lado,
                              float poder, GestorDeAudio audio) {
        _06LatigoDildo latigo = new _06LatigoDildo(
            jugador, lado, poder, extraDamage,
            haloActivo, rapidoActivo);
        jugador.getControladorProyectiles().anyadirNuevoProyectil(latigo);
        if (audio != null) audio.reproducirEfecto("dildo", 0.6f);
    }

    public float getCooldownDuration() {
        return intervaloDisparo;
    }

    public void reducirCooldown(float intervaloARestar) {
        this.intervaloDisparo -= intervaloARestar;
    }

    public float getTimeUntilNextShot() {
        if (ataqueEnProgreso) {
            // Durante la ráfaga de swings, no mostramos overlay de cooldown
            return 0f;
        }
        // Estamos en fase de cooldown: tiempo restante hasta próximo ataque
        return Math.max(0f, intervaloDisparo - cooldownTimer);
    }

    public void activarGolpeSimultaneo() {
        simultaneoActivo = true;
    }

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
}

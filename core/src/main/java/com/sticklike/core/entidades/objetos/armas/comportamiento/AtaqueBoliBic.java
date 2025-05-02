package com.sticklike.core.entidades.objetos.armas.comportamiento;

import com.sticklike.core.entidades.jugador.InputsJugador;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.ProyectilBoliBic;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

public class AtaqueBoliBic {

    // ――― AJUSTES DE DISPARO ―――
    private float temporizadorDisparo = 0f;
    private float intervaloDisparo = 0.75f;

    // ráfagas acumulables -----------
    private int proyectilesPorRafaga = 1;   // 1 = disparo simple
    private int rafagaRestante = 0;
    private static final float INTERVALO_RAFAGA = 0.12f;

    // otros upgrades ----------------
    private boolean ricochet = false;
    private boolean splitShotActivo = false;
    private static final float OFFSET_SPLIT = 8f;

    // vector de puntería continuo
    private float aimX = 1f, aimY = 0f;

    public void mejorarDoubleTap() {
        proyectilesPorRafaga++;
    }

    public void activarRicochet() {
        ricochet = true;
    }

    public void activarSplitShot() {
        splitShotActivo = true;
    }

    /*-----------------------------------------------------------
     *  BUCLE DE DISPARO
     *----------------------------------------------------------*/
    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio audio) {
        actualizarAim(delta, jugador);
        temporizadorDisparo += delta;

        if (proyectilesPorRafaga > 1) {                     // ――― modo ráfaga ―――
            if (rafagaRestante > 0) {
                if (temporizadorDisparo >= INTERVALO_RAFAGA) {
                    temporizadorDisparo = 0f;
                    procesarAtaque(jugador, audio);
                    rafagaRestante--;
                }
            } else if (temporizadorDisparo >= intervaloDisparo) {
                temporizadorDisparo = 0f;
                procesarAtaque(jugador, audio);
                rafagaRestante = proyectilesPorRafaga - 1;  // -1 porque ya lanzamos el primero
            }
        } else {                                            // ――― disparo simple ―――
            if (temporizadorDisparo >= intervaloDisparo) {
                temporizadorDisparo = 0f;
                procesarAtaque(jugador, audio);
            }
        }
    }

    /*-----------------------------------------------------------
     *  LÓGICA DE PUNTERÍA
     *----------------------------------------------------------*/
    private void actualizarAim(float delta, Jugador jug) {
        InputsJugador.ResultadoInput i = jug.getInputController().procesarInput();
        float tx = (i.movX != 0 || i.movY != 0) ? i.movX : aimX;
        float ty = (i.movY != 0 || i.movX != 0) ? i.movY : aimY;

        float mag = (float) Math.hypot(tx, ty);
        if (mag != 0) {
            tx /= mag;
            ty /= mag;
        }

        float a0 = (float) Math.toDegrees(Math.atan2(aimY, aimX));
        float a1 = (float) Math.toDegrees(Math.atan2(ty, tx));
        float diff = (((a1 - a0) + 180 + 360) % 360) - 180;

        if (Math.abs(diff) >= 90f) {
            aimX = tx;
            aimY = ty;
        } else {
            float f = 5f * delta;
            aimX = lerp(aimX, tx, f);
            aimY = lerp(aimY, ty, f);
            float m2 = (float) Math.hypot(aimX, aimY);
            if (m2 != 0) {
                aimX /= m2;
                aimY /= m2;
            }
        }
    }

    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    /*-----------------------------------------------------------
     *  CREACIÓN DE PROYECTILES
     *----------------------------------------------------------*/
    private void procesarAtaque(Jugador jug, GestorDeAudio audio) {
        float px = jug.getSprite().getX() + jug.getSprite().getWidth() / 2f;
        float py = jug.getSprite().getY() + jug.getSprite().getHeight() / 2f;

        float dx = aimX, dy = aimY;

        if (splitShotActivo) {                       // abanico ±4°
            double rad = Math.toRadians(OFFSET_SPLIT * .5f);
            float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
            lanzarBoli(px, py, dx * c - dy * s, dx * s + dy * c, jug, audio);
            lanzarBoli(px, py, dx * c + dy * s, -dx * s + dy * c, jug, audio);
        } else {
            lanzarBoli(px, py, dx, dy, jug, audio);
        }
    }

    private void lanzarBoli(float x, float y, float dx, float dy, Jugador jug, GestorDeAudio audio) {
        ProyectilBoliBic p = new ProyectilBoliBic(x, y, dx, dy, 500);
        if (ricochet) p.enableBounce(1);
        jug.getControladorProyectiles().anyadirNuevoProyectil(p);
        audio.reproducirEfecto("boli", 0.5f);
    }

    public float getCooldownDuration() {
        if (rafagaRestante > 0) {
            return INTERVALO_RAFAGA;
        }
        return intervaloDisparo;
    }

    public float getTimeUntilNextShot() {
        // evitamos valores negativos
        return Math.max(0f, getCooldownDuration() - temporizadorDisparo);
    }
}

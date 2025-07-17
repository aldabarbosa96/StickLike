package com.sticklike.core.entidades.objetos.armas.jugador.comportamiento;

import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.jugador._08ProyectilPelota;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

public class _08AtaquePelota {
    private float temporizadorDisparo = 0f;
    private float intervaloDisparo;
    private boolean maxBounces = false;
    private boolean maxPower = false;

    public _08AtaquePelota(float cooldownInicialSegundos) {
        this.intervaloDisparo = cooldownInicialSegundos;
    }

    public void manejarDisparo(float delta, Jugador jug, GestorDeAudio audio) {
        temporizadorDisparo += delta;
        if (temporizadorDisparo < intervaloDisparo) return;

        Enemigo target = encontrarEnemigoMasCercano(jug);
        if (target == null) return;

        float spawnX = jug.getSprite().getX() + jug.getSprite().getWidth() * .5f;
        float spawnY = jug.getSprite().getY() + jug.getSprite().getHeight() * .5f;

        float dirX = (target.getX() + target.getSprite().getWidth() * .5f) - spawnX;
        float dirY = (target.getY() + target.getSprite().getHeight() * .5f) - spawnY;
        float len = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        if (len == 0) len = 1f;
        dirX /= len;
        dirY /= len;

        _08ProyectilPelota pelota = new _08ProyectilPelota(spawnX, spawnY, dirX, dirY, jug);
        if (maxBounces) {
            pelota.setMaxRebotes(pelota.getMaxRebotes() * 2);
        }
        if (maxPower) {
            pelota.setMaxPower();
        }
        jug.getControladorProyectiles().anyadirNuevoProyectil(pelota);
        audio.reproducirEfecto("pelota", 0.75f);

        temporizadorDisparo = 0f;
    }

    private Enemigo encontrarEnemigoMasCercano(Jugador jug) {
        float minDist = Float.MAX_VALUE;
        Enemigo mejor = null;

        for (Enemigo e : jug.getControladorEnemigos().getEnemigos()) {
            if (e == null || e.estaMuerto()) continue;
            float dx = e.getX() - jug.getSprite().getX();
            float dy = e.getY() - jug.getSprite().getY();
            float dist = dx * dx + dy * dy;
            if (dist < minDist && dist <= jug.getRangoAtaqueJugador() * jug.getRangoAtaqueJugador() * 2) {
                minDist = dist;
                mejor = e;
            }
        }
        return mejor;
    }

    public float getCooldownDuration() {
        return intervaloDisparo;
    }

    public float getTimeUntilNextShot() {
        return Math.max(0f, intervaloDisparo - temporizadorDisparo);
    }

    public void reducirIntervaloDisparo(float nuevoIntervalo) {
        intervaloDisparo -= nuevoIntervalo;
    }

    public void setMaxBounces() {
        maxBounces = true;
    }

    public void setMaxPower() {
        this.maxPower = true;
    }
}

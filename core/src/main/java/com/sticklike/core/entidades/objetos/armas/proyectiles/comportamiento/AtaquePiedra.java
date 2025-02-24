package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilPiedra;
import com.sticklike.core.interfaces.Enemigo;
import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Gestiona ataque Piedra; dispara proyectiles de piedra en ráfagas hacia el enemigo más cercano dentro del rango.
 */
public class AtaquePiedra {
    private float temporizadorDisparo = TEMPORIZADOR_DISPARO;
    private float intervaloDisparo;
    private int proyectilesPendientes = 0;
    private float temporizadorEntreBalas = 0f;
    private final float intervaloEntreBalas = 0.1f; // Retardo entre cada bala en segundos
    private Enemigo target;

    public AtaquePiedra(float intervaloDisparoInicial) {
        this.intervaloDisparo = intervaloDisparoInicial;
    }

    public boolean iniciarAtaque(Jugador jug, GestorDeAudio gestorDeAudio) {
        if (jug.getControladorEnemigos() == null) return false;

        target = encontrarEnemigoMasCercano(jug);
        if (target == null) {
            // No se encontró enemigo, no se inicia la ráfaga y no se reinicia el temporizadorDisparo.
            return false;
        }

        // Se ha encontrado un enemigo en rango: se inicia el ataque
        gestorDeAudio.reproducirEfecto("lanzarPiedra", AUDIO_PIEDRA);
        proyectilesPendientes = jug.getProyectilesPorDisparo();
        temporizadorEntreBalas = 0f;
        return true;
    }

    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        temporizadorDisparo += delta;

        // Si no hay proyectiles pendientes y ha pasado el intervalo, se intenta iniciar el ataque.
        if (proyectilesPendientes == 0 && temporizadorDisparo >= intervaloDisparo) {
            if (iniciarAtaque(jugador, gestorDeAudio)) {
                temporizadorDisparo = 0;
                // Dispara el primer proyectil de inmediato.
                if (target != null && !target.estaMuerto()) {
                    dispararProyectil(jugador);
                    proyectilesPendientes--;
                }
                temporizadorEntreBalas = 0f;
            }
        }

        if (proyectilesPendientes > 0) {
            // Si el objetivo se ha vuelto nulo o ha muerto, se aborta la ráfaga.
            if (target == null || target.estaMuerto()) {
                proyectilesPendientes = 0;
                return;
            }

            // Dispara cada proyectil secuencialmente con un retardo entre ellos.
            temporizadorEntreBalas += delta;
            if (temporizadorEntreBalas >= intervaloEntreBalas) {
                dispararProyectil(jugador);
                proyectilesPendientes--;
                temporizadorEntreBalas = 0f;
            }
        }
    }

    private void dispararProyectil(Jugador jugador) {
        float spawnX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
        float spawnY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;

        // Recalcula la dirección hacia el centro actual del enemigo
        float targetX = target.getX() + target.getSprite().getWidth() / 2f;
        float targetY = target.getY() + target.getSprite().getHeight() / 2f;
        float[] dir = calcularDireccionNormalizada(spawnX, spawnY, targetX, targetY);

        float velocidadAleatoria = MathUtils.random(1.5f, 1.75f);
        ProyectilPiedra piedra = new ProyectilPiedra(spawnX, spawnY, dir[0], dir[1], velocidadAleatoria, jugador);
        jugador.getControladorProyectiles().anyadirNuevoProyectil(piedra);
    }

    private Enemigo encontrarEnemigoMasCercano(Jugador jug) {
        float closestDist = Float.MAX_VALUE;
        Enemigo closest = null;

        for (Enemigo e : jug.getControladorEnemigos().getEnemigos()) {
            if (!e.estaMuerto()) {
                float dx = e.getX() - jug.getSprite().getX();
                float dy = e.getY() - jug.getSprite().getY();
                float dist = (float) Math.sqrt(dx * dx + dy * dy);

                if (dist < closestDist && dist <= jug.getRangoAtaqueJugador()) {
                    closestDist = dist;
                    closest = e;
                }
            }
        }
        return closest;
    }

    /**
     * Calcula y devuelve la dirección normalizada (vector unitario) entre dos puntos.
     */
    private float[] calcularDireccionNormalizada(float sx, float sy, float tx, float ty) {
        float dx = tx - sx;
        float dy = ty - sy;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) dist = 1f;
        return new float[]{dx / dist, dy / dist};
    }

    public void setIntervaloDisparo(float nuevoIntervaloNuevo) {
        this.intervaloDisparo = nuevoIntervaloNuevo;
    }
}

package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilPiedra;
import com.sticklike.core.interfaces.Enemigo;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class AtaquePiedra {
    // Temporizador para el cooldown de la ráfaga (se cuenta desde que se termina la ráfaga)
    private float temporizadorDisparo = 0f;
    // Intervalo de cooldown entre ráfagas (se inicia después de terminar la ráfaga)
    private float intervaloDisparo;
    // Número de proyectiles pendientes para disparar en la ráfaga actual
    private int proyectilesPendientes = 0;
    // Temporizador para disparar cada bala dentro de la ráfaga
    private float temporizadorEntreBalas = 0f;
    // Intervalo entre cada bala de la ráfaga
    private final float intervaloEntreBalas = 0.1f;
    // Target actual (en la primera bala se calcula y se almacena)
    private Enemigo target;
    // Dirección almacenada al iniciar la ráfaga
    private float[] storedDirection;
    // Flag para indicar que la ráfaga ha finalizado (ya se dispararon todos los proyectiles)
    private boolean volleyFinished = true;

    public AtaquePiedra(float intervaloDisparoInicial) {
        this.intervaloDisparo = intervaloDisparoInicial;
        storedDirection = null;
    }

    /**
     * Intenta iniciar una nueva ráfaga.
     * Se busca el enemigo más cercano y se almacena la dirección.
     * @return true si se inicia la ráfaga, false si no se encontró enemigo.
     */
    public boolean iniciarAtaque(Jugador jug, GestorDeAudio gestorDeAudio) {
        if (jug.getControladorEnemigos() == null) return false;
        target = encontrarEnemigoMasCercano(jug);
        if (target == null) return false;
        // Calcular y almacenar la dirección de disparo
        float spawnX = jug.getSprite().getX() + jug.getSprite().getWidth() / 2f;
        float spawnY = jug.getSprite().getY() + jug.getSprite().getHeight() / 2f;
        float targetX = target.getX() + target.getSprite().getWidth() / 2f;
        float targetY = target.getY() + target.getSprite().getHeight() / 2f;
        storedDirection = calcularDireccionNormalizada(spawnX, spawnY, targetX, targetY);
        gestorDeAudio.reproducirEfecto("lanzarPiedra", AUDIO_PIEDRA);
        // Establecer el número de balas a disparar basado en el valor actual de proyectiles del jugador
        proyectilesPendientes = jug.getProyectilesPorDisparo();
        temporizadorEntreBalas = 0f;
        return true;
    }

    /**
     * Maneja el disparo de la ráfaga. Si la ráfaga ya terminó, espera el cooldown.
     * Una vez iniciada, dispara todas las balas pendientes, incluso si el target muere; pero si el target muere se intenta redirigir a un nuevo enemigo para los siguientes disparos.
     */
    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        if (volleyFinished) {
            // Estando en cooldown, acumulamos tiempo hasta iniciar una nueva ráfaga
            temporizadorDisparo += delta;
            if (temporizadorDisparo >= intervaloDisparo) {
                // Intentar iniciar la ráfaga
                if (iniciarAtaque(jugador, gestorDeAudio)) {
                    temporizadorDisparo = 0;
                    volleyFinished = false;
                }
            }
        } else {
            // Ráfaga en progreso
            if (proyectilesPendientes > 0) {
                temporizadorEntreBalas += delta;
                if (temporizadorEntreBalas >= intervaloEntreBalas) {
                    // Si el target actual está muerto, buscar uno nuevo y actualizamos dirección
                    if (target == null || target.estaMuerto()) {
                        Enemigo nuevoTarget = encontrarEnemigoMasCercano(jugador);
                        if (nuevoTarget != null) {
                            target = nuevoTarget;
                            float spawnX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
                            float spawnY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;
                            float targetX = target.getX() + target.getSprite().getWidth() / 2f;
                            float targetY = target.getY() + target.getSprite().getHeight() / 2f;
                            storedDirection = calcularDireccionNormalizada(spawnX, spawnY, targetX, targetY);
                        }
                    }
                    dispararProyectil(jugador);
                    proyectilesPendientes--;
                    temporizadorEntreBalas = 0f;
                    // Si ya se dispararon todas las balas, finalizamos la ráfaga y reiniciamos el cooldown
                    if (proyectilesPendientes <= 0) {
                        volleyFinished = true;
                        temporizadorDisparo = 0;
                    }
                }
            } else {
                // Por si acaso
                volleyFinished = true;
                temporizadorDisparo = 0;
            }
        }
    }

    private void dispararProyectil(Jugador jugador) {
        float spawnX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
        float spawnY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;
        float[] dir = (storedDirection != null) ? storedDirection : new float[]{1, 0};
        // Si el target sigue vivo, actualizar la dirección
        if (target != null && !target.estaMuerto()) {
            float targetX = target.getX() + target.getSprite().getWidth() / 2f;
            float targetY = target.getY() + target.getSprite().getHeight() / 2f;
            dir = calcularDireccionNormalizada(spawnX, spawnY, targetX, targetY);
            storedDirection = dir;
        }
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
     * Calcula la dirección normalizada (vector unitario) entre dos puntos.
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

package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilPiedra;
import com.sticklike.core.interfaces.Enemigo;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class AtaquePiedra {

    // Temporizador para disparar ataques completos
    private float temporizadorDisparo = TEMPORIZADOR_DISPARO;
    private float intervaloDisparo;

    // Variables para disparar las "balas" de forma secuencial
    private int proyectilesPendientes = 0;
    private float temporizadorEntreBalas = 0f;
    private final float intervaloEntreBalas = 0.1f; // Retardo entre cada bala en segundos

    // Almacenamos el objetivo (enemigo) encontrado al iniciar el ataque
    private Enemigo target;

    public AtaquePiedra(float intervaloDisparoInicial) {
        this.intervaloDisparo = intervaloDisparoInicial;
    }

    /**
     * Busca al enemigo más cercano y prepara el ataque.
     * Se almacena el objetivo para que cada disparo de la ráfaga se dirija hacia él.
     */
    public void iniciarAtaque(Jugador jug, GestorDeAudio gestorDeAudio) {
        if (jug.getControladorEnemigos() == null) return;

        target = encontrarEnemigoMasCercano(jug);
        if (target == null) return;

        gestorDeAudio.reproducirEfecto("lanzarPiedra", AUDIO_PIEDRA);

        // Establece la cantidad de proyectiles a disparar (usando el valor actual del jugador)
        proyectilesPendientes = jug.getProyectilesPorDisparo();
        temporizadorEntreBalas = 0f;
    }

    /**
     * Se llama cada frame para gestionar el disparo.
     * Si se cumple el intervalo y no hay una ráfaga en curso, se inicia el ataque.
     * Luego, se dispara cada proyectil uno a uno recalculando la dirección con las posiciones actuales.
     */
    public void manejarDisparo(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        temporizadorDisparo += delta;

        // Si no hay una ráfaga en curso y se cumple el intervalo, iniciar ataque
        if (temporizadorDisparo >= intervaloDisparo && proyectilesPendientes == 0) {
            iniciarAtaque(jugador, gestorDeAudio);
            temporizadorDisparo = 0; // Reinicia el temporizador de disparo completo
        }

        // Si el objetivo se ha vuelto nulo o ha muerto, abortar la ráfaga
        if (target == null || target.estaMuerto()) {
            proyectilesPendientes = 0;
            return;
        }

        // Si ya se ha iniciado un ataque (hay proyectiles pendientes)
        if (proyectilesPendientes > 0) {
            temporizadorEntreBalas += delta;
            if (temporizadorEntreBalas >= intervaloEntreBalas) {
                // Obtener la posición actual del centro del jugador
                float spawnX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
                float spawnY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;

                // Recalcular la dirección actual usando la posición actual del objetivo
                float targetX = target.getX() + target.getSprite().getWidth() / 2f;
                float targetY = target.getY() + target.getSprite().getHeight() / 2f;
                float[] dir = calcularDireccionNormalizada(spawnX, spawnY, targetX, targetY);

                float velocidadAleatoria = 0.8f + (float) Math.random() * (1.2f - 0.8f);
                ProyectilPiedra piedra = new ProyectilPiedra(spawnX, spawnY, dir[0], dir[1], velocidadAleatoria);
                jugador.getControladorProyectiles().anyadirNuevoProyectil(piedra);

                proyectilesPendientes--;
                temporizadorEntreBalas = 0;
            }
        }
    }

    /**
     * Busca al enemigo más cercano dentro del rango de ataque del jugador.
     */
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

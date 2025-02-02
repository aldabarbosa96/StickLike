package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilPiedra;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;
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
     * Se calcula el objetivo y se almacena en la variable 'target' para que cada proyectil
     * se dispare utilizando la posición actual del jugador y la posición actual del objetivo.
     */
    public void iniciarAtaque(Jugador jug, ControladorAudio controladorAudio) {
        if (jug.getControladorEnemigos() == null) return;

        target = encontrarEnemigoMasCercano(jug);
        if (target == null) return;

        // Reproducir sonido de disparo
        controladorAudio.reproducirEfecto("lanzarPiedra", AUDIO_PIEDRA);

        // Establecer el número de proyectiles a disparar (todos se dispararán hacia el objetivo)
        proyectilesPendientes = jug.getProyectilesPorDisparo();
        temporizadorEntreBalas = 0f;
    }

    /**
     * Este método se llama cada frame para gestionar el disparo.
     * Cuando se cumple el intervalo de disparo y no hay ráfaga en curso se inicia el ataque;
     * luego, se disparan los proyectiles uno a uno, recalculando para cada uno la dirección
     * en función de las posiciones actuales del jugador y del enemigo.
     */
    public void manejarDisparo(float delta, Jugador jugador, ControladorAudio controladorAudio) {
        temporizadorDisparo += delta;

        // Si se cumple el intervalo de disparo y no hay una ráfaga en curso, iniciamos el ataque
        if (temporizadorDisparo >= intervaloDisparo && proyectilesPendientes == 0) {
            iniciarAtaque(jugador, controladorAudio);
            temporizadorDisparo = 0; // Reiniciar el temporizador de disparo completo
        }

        // Si ya se ha iniciado un ataque (hay proyectiles pendientes)
        if (proyectilesPendientes > 0 && target != null && !target.estaMuerto()) {
            temporizadorEntreBalas += delta;
            if (temporizadorEntreBalas >= intervaloEntreBalas) {
                // Calcular la posición actual del centro del jugador
                float spawnX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
                float spawnY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;

                // Calcular la dirección actual usando el centro del jugador y el centro actual del target
                float targetX = target.getX() + target.getSprite().getWidth() / 2f;
                float targetY = target.getY() + target.getSprite().getHeight() / 2f;
                float[] dir = calcularDireccionNormalizada(spawnX, spawnY, targetX, targetY);

                // Generar el proyectil con la dirección actual
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

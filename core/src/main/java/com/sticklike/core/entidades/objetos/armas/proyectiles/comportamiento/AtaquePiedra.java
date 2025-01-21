package com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento;

import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.ProyectilPiedra;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.gameplay.managers.ControladorProyectiles;
import com.sticklike.core.utilidades.GestorConstantes;

import java.lang.Math;

/**
 * {@code AtaquePiedra} maneja la lógica de ataque automático del jugador:
 * - Encontrar el enemigo más cercano en el rango
 * - Calcular la dirección de disparo
 * - Crear los proyectiles y asignarlos al {@link ControladorProyectiles}
 */
public class AtaquePiedra {

    private float temporizadorDisparo = 0f;
    private float intervaloDisparo = 0.5f; // Intervalo en segundos (ajustar según necesidad)

    /**
     * Constructor de AtaquePiedra.
     * Permite configurar el intervalo de disparo.
     *
     * @param intervaloDisparoInicial Intervalo inicial entre disparos en segundos.
     */
    public AtaquePiedra(float intervaloDisparoInicial) {
        this.intervaloDisparo = intervaloDisparoInicial;
    }

    /**
     * Procesa el ataque automático del jugador, si hay un enemigo en rango,
     * genera los proyectiles correspondientes.
     *
     * @param jug referencia al {@link Jugador} que ataca
     */
    public void procesarAtaque(Jugador jug, ControladorAudio controladorAudio) {

        // Si no hay enemigos controlados, no hacemos nada
        if (jug.getControladorEnemigos() == null) return;

        // Buscamos un objetivo en rango
        Enemigo target = encontrarEnemigoMasCercano(jug);
        if (target == null) return;
        controladorAudio.reproducirEfecto2();

        // Obtenemos coordenadas del centro del jugador
        float startX = jug.getSprite().getX() + jug.getSprite().getWidth() / 2f;
        float startY = jug.getSprite().getY() + jug.getSprite().getHeight() / 2f;

        // Coordenadas del centro del enemigo
        float targetX = target.getX() + target.getSprite().getWidth() / 2f;
        float targetY = target.getY() + target.getSprite().getHeight() / 2f;

        // Calculamos dirección normalizada (dx, dy)
        float[] dir = calcularDireccionNormalizada(startX, startY, targetX, targetY);


        // Disparamos tantos proyectiles como indique "proyectilesPorDisparo"
        for (int i = 0; i < jug.getProyectilesPorDisparo(); i++) {
            // separamos un poco los proyectiles por ángulo
            float angleOffset = (i - (jug.getProyectilesPorDisparo() - 1) / 2f) * 5f;
            float adjustedX = (float) (dir[0] * Math.cos(Math.toRadians(angleOffset))
                - dir[1] * Math.sin(Math.toRadians(angleOffset)));
            float adjustedY = (float) (dir[0] * Math.sin(Math.toRadians(angleOffset))
                + dir[1] * Math.cos(Math.toRadians(angleOffset)));

            float velocidadAleatoria = 0.8f + (float) Math.random() * (1.2f - 0.8f);

            ProyectilPiedra piedra = new ProyectilPiedra(startX, startY, adjustedX, adjustedY, velocidadAleatoria);
            // Añadimos el nuevo proyectil al ControladorProyectiles
            jug.getControladorProyectiles().anyadirNuevoProyectil(piedra);

        }
    }

    /**
     * Busca el enemigo más cercano dentro del rango de ataque del jugador.
     *
     * @param jug el {@link Jugador} cuyo rango de ataque se utilizará
     * @return el enemigo más cercano, o null si no hay enemigos en rango
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
     * Calcula la dirección normalizada entre (sx, sy) y (tx, ty).
     *
     * @return array de 2 floats {dx, dy} representando la dirección.
     */
    private float[] calcularDireccionNormalizada(float sx, float sy, float tx, float ty) {
        float dx = tx - sx;
        float dy = ty - sy;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) dist = 1f;
        return new float[]{dx / dist, dy / dist};
    }

    /**
     * Maneja el disparo del jugador y actualiza el temporizador de disparo
     */
    public void manejarDisparo(float delta, Jugador jugador, ControladorAudio controladorAudio) {
        temporizadorDisparo += delta;

        if (temporizadorDisparo >= intervaloDisparo) {
            temporizadorDisparo = 0;
            procesarAtaque(jugador, controladorAudio);
        }
    }

    /**
     * Permite ajustar el intervalo de disparo después de la inicialización.
     *
     * @param nuevoIntervaloNuevo Intervalo en segundos.
     */
    public void setIntervaloDisparo(float nuevoIntervaloNuevo) {
        this.intervaloDisparo = nuevoIntervaloNuevo;
    }
}

package com.sticklike.core.logics.movement;

import com.sticklike.core.entities.Enemigo;
import com.sticklike.core.entities.Jugador;
import com.sticklike.core.managers.ControladorProyectiles;
import java.lang.Math;

/**
 * {@code AtaqueJugador} maneja la lógica de ataque automático del jugador:
 * - Encontrar el enemigo más cercano en el rango
 * - Calcular la dirección de disparo
 * - Crear los proyectiles y asignarlos al {@link ControladorProyectiles}
 */
public class AtaqueJugador {

    /**
     * Procesa el ataque automático del jugador, si hay un enemigo en rango,
     * genera los proyectiles correspondientes.
     *
     * @param jug referencia al {@link Jugador} que ataca
     */
    public void procesarAtaque(Jugador jug) {
        // Si no hay enemigos controlados, no hacemos nada
        if (jug.getControladorEnemigos() == null) return;

        // Buscamos un objetivo en rango
        Enemigo target = encontrarEnemigoMasCercano(jug);
        if (target == null) return;

        // Obtenemos coordenadas del centro del jugador
        float startX = jug.getSprite().getX() + jug.getSprite().getWidth()  / 2f;
        float startY = jug.getSprite().getY() + jug.getSprite().getHeight() / 2f;

        // Coordenadas del centro del enemigo
        float targetX = target.getX() + target.getSprite().getWidth()  / 2f;
        float targetY = target.getY() + target.getSprite().getHeight() / 2f;

        // Calculamos dirección normalizada (dx, dy)
        float[] dir = calcularDireccionNormalizada(startX, startY, targetX, targetY);

        // Disparamos tantos proyectiles como indique "proyectilesPorDisparo"
        for (int i = 0; i < jug.getProyectilesPorDisparo(); i++) {
            // Ejemplo: separamos un poco los proyectiles por ángulo
            float angleOffset = (i - (jug.getProyectilesPorDisparo() - 1) / 2f) * 5f;
            float adjustedX = (float)(dir[0] * Math.cos(Math.toRadians(angleOffset))
                - dir[1] * Math.sin(Math.toRadians(angleOffset)));
            float adjustedY = (float)(dir[0] * Math.sin(Math.toRadians(angleOffset))
                + dir[1] * Math.cos(Math.toRadians(angleOffset)));

            // Añadimos el nuevo proyectil al ControladorProyectiles
            jug.getControladorProyectiles().anyadirNuevoProyectil(
                startX,
                startY,
                adjustedX,
                adjustedY,
                target
            );
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
                float dist = (float)Math.sqrt(dx*dx + dy*dy);

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
        float dist = (float)Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) dist = 1f;
        return new float[] { dx / dist, dy / dist };
    }
}

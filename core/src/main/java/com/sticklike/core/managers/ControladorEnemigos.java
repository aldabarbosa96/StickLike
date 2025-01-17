package com.sticklike.core.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entities.Enemigo;
import com.sticklike.core.entities.Jugador;
import com.sticklike.core.entities.ObjetoXP;
import com.sticklike.core.screens.VentanaJuego;
import com.sticklike.core.utils.GestorConstantes;

/**
 * La clase EnemyManager se encarga de:
 * <p>
 * Generar enemigos periódicamente en posiciones aleatorias
 * Actualizar la lógica de cada Enemigo (movimiento, muerte, etc.)
 * Renderizar los enemigos en el {@link SpriteBatch}
 * Gestionar la liberación de recursos al final
 * <p>
 * Interactúa con el {@link Jugador} para evitar spawnear enemigos
 * demasiado cerca de él y también para comprobar si el jugador ha muerto
 */
public class ControladorEnemigos {
    private VentanaJuego ventanaJuego;
    private Array<Enemigo> enemigos;
    private Jugador jugador;
    private float intervaloDeAparicion, temporizadorDeAparicion;
    private static final float BORDER_MARGIN = GestorConstantes.BORDER_SPAWN_MARGIN;
    private Array<Enemigo> enemigosAEliminar = new Array<>();

    /**
     * @param jugador              referencia al {@link Jugador}, para conocer su posición y estado de vida
     * @param intervaloDeAparicion intervalo (en segundos) para generar nuevos enemigos
     * @param ventanaJuego         referencia a la pantalla principal ({@link VentanaJuego}), necesaria para añadir
     *                             {@link ObjetoXP} al morir un enemigo
     */
    public ControladorEnemigos(Jugador jugador, float intervaloDeAparicion, VentanaJuego ventanaJuego) {
        this.enemigos = new Array<>();
        this.jugador = jugador;
        this.intervaloDeAparicion = intervaloDeAparicion;
        this.temporizadorDeAparicion = 0;
        this.ventanaJuego = ventanaJuego;
    }

    /**
     * Lógica de actualización para "spawnear" enemigos y procesar cada uno de ellos
     *
     * @param delta tiempo transcurrido desde el último frame
     */
    public void actualizarSpawnEnemigos(float delta) {
        if (jugador.estaMuerto()) {
            return;
        }

        // Spawnea un enemigo pasado el tiempo de intervaloDeAparicion
        temporizadorDeAparicion += delta;
        if (temporizadorDeAparicion >= intervaloDeAparicion) {
            spawnEnemigo();
            temporizadorDeAparicion = 0;
        }

        // Actualiza cada enemigo
        for (Enemigo enemigo : enemigos) {
            enemigo.actualizarEnemigo(delta);

            // Si el enemigo muere y no ha soltado XP todavía, lo suelta
            if (enemigo.estaMuerto() && !enemigo.isProcesado()) {
                ObjetoXP xpObject = enemigo.sueltaObjetoXP();
                if (xpObject != null) {
                    ventanaJuego.addXPObject(xpObject);
                }
                enemigo.setProcesado(true);
                enemigosAEliminar.add(enemigo);
            }
        }

        // Elimina enemigos marcados después de actualizar todos
        for (Enemigo enemigo : enemigosAEliminar) {
            enemigos.removeValue(enemigo, true);
        }
        enemigosAEliminar.clear();
    }

    /**
     * Renderiza los enemigos en el {@link SpriteBatch}.
     * Utilizado en la clase Enemigo para renderizar todos los enemigos
     *
     * @param batch SpriteBatch para dibujar
     */
    public void renderizarEnemigos(SpriteBatch batch) {
        if (jugador.estaMuerto()) return;

        enemigos.sort((e1, e2) -> Float.compare(e2.getY(), e1.getY()));
        for (Enemigo enemigo : enemigos) {
            enemigo.renderizarEnemigo(batch);
        }
    }

    /**
     * Genera un nuevo enemigo en una posición aleatoria alrededor del jugador, respetando una distancia mínima.
     */
    private void spawnEnemigo() {
        float minDistance = 300f;
        float x, y;

        // Posición del jugador (centro)
        float playerX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float playerY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2;

        // Cálculo de límites dinámicos según la posición del jugador
        float leftLimit = playerX - VentanaJuego.WORLD_WIDTH / 2 + BORDER_MARGIN;
        float rightLimit = playerX + VentanaJuego.WORLD_WIDTH / 2 - BORDER_MARGIN;
        float bottomLimit = playerY - VentanaJuego.WORLD_HEIGHT / 2 + BORDER_MARGIN;
        float topLimit = playerY + VentanaJuego.WORLD_HEIGHT / 2 - BORDER_MARGIN;

        do {
            // Genera coordenadas aleatorias dentro de los límites dinámicos
            x = leftLimit + (float) (Math.random() * (rightLimit - leftLimit));
            y = bottomLimit + (float) (Math.random() * (topLimit - bottomLimit));

            // Repetimos mientras estemos demasiado cerca (menos de minDistance)
        } while (Math.sqrt(Math.pow(x - playerX, 2) + Math.pow(y - playerY, 2)) < minDistance);

        // Velocidad aleatoria entre 50 y 100
        float randomSpeed = 50f + (float) Math.random() * 50f;

        enemigos.add(new Enemigo(x, y, jugador, randomSpeed));
    }

    public void dispose() {
        for (Enemigo enemigo : enemigos) {
            enemigo.dispose();
        }
    }

    /**
     * @return lista de enemigos activos
     */
    public Array<Enemigo> getEnemigos() {
        return enemigos;
    }
}

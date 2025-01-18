package com.sticklike.core.gameplay.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.interfaces.Enemigo;
import com.sticklike.core.entidades.enemigos.EnemigoCulo;
import com.sticklike.core.entidades.interfaces.ObjetosXP;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.pantallas.VentanaJuego;
import com.sticklike.core.utilidades.GestorConstantes;

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

    public ControladorEnemigos(Jugador jugador, float intervaloDeAparicion, VentanaJuego ventanaJuego) {
        this.enemigos = new Array<>();
        this.jugador = jugador;
        this.intervaloDeAparicion = intervaloDeAparicion;
        this.temporizadorDeAparicion = 0;
        this.ventanaJuego = ventanaJuego;
    }

    public void actualizarSpawnEnemigos(float delta) {

        if (jugador.estaVivo()) {
            return;
        }
        System.out.println("Generando enemigo...");
        temporizadorDeAparicion += delta;
        if (temporizadorDeAparicion >= intervaloDeAparicion) {
            spawnEnemigo();
            temporizadorDeAparicion = 0;
        }

        for (Enemigo enemigo : enemigos) {
            enemigo.actualizar(delta);

            if (enemigo.estaMuerto() && !enemigo.isProcesado()) {
                ObjetosXP xpObject = enemigo.sueltaObjetoXP();
                if (xpObject != null) {
                    ventanaJuego.addXPObject(xpObject);
                }
                enemigo.setProcesado(true);
                enemigosAEliminar.add(enemigo);
            }

        }

        for (Enemigo enemigo : enemigosAEliminar) {
            enemigos.removeValue(enemigo, true);
        }
        enemigosAEliminar.clear();
    }

    public void renderizarEnemigos(SpriteBatch batch) {
        if (jugador.estaVivo()) return;

        enemigos.sort((e1, e2) -> Float.compare(e2.getY(), e1.getY()));
        for (Enemigo enemigo : enemigos) {
            enemigo.renderizar(batch);
        }
    }

    private void spawnEnemigo() {
        float minDistance = 300f;
        float x, y;

        float playerX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float playerY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2;

        float leftLimit = playerX - VentanaJuego.WORLD_WIDTH / 2 + BORDER_MARGIN;
        float rightLimit = playerX + VentanaJuego.WORLD_WIDTH / 2 - BORDER_MARGIN;
        float bottomLimit = playerY - VentanaJuego.WORLD_HEIGHT / 2 + BORDER_MARGIN;
        float topLimit = playerY + VentanaJuego.WORLD_HEIGHT / 2 - BORDER_MARGIN;

        do {
            x = leftLimit + (float) (Math.random() * (rightLimit - leftLimit));
            y = bottomLimit + (float) (Math.random() * (topLimit - bottomLimit));
        } while (Math.sqrt(Math.pow(x - playerX, 2) + Math.pow(y - playerY, 2)) < minDistance);

        float randomSpeed = 50f + (float) Math.random() * 50f;
        String[] tiposDeEnemigos = {"CULO"};
        String tipoElegido = tiposDeEnemigos[(int) (Math.random() * tiposDeEnemigos.length)];

        // Crear y añadir el enemigo
        enemigos.add(fabricaEnemigos(tipoElegido, x, y, jugador, randomSpeed));
    }

    public void dispose() {
        for (Enemigo enemigo : enemigos) {
            if (enemigo != null) {
                enemigo.dispose();
            }
        }
    }

    public Array<Enemigo> getEnemigos() {
        return enemigos;
    }

    public static Enemigo fabricaEnemigos(String tipo, float x, float y, Jugador jugador, float velocidad) {
        switch (tipo) {
            case "CULO":
                return new EnemigoCulo(x, y, jugador, velocidad);
            /*case "ENEMIGO_TIPO2":
                return new EnemigoTipo2(x, y, jugador, velocidad);
            case "ENEMIGO_TIPO3":
                return new EnemigoTipo3(x, y, jugador, velocidad);*/
            // todo --> añadir más enemigos próximamente
            default:
                throw new IllegalArgumentException("Tipo de enemigo no reconocido: " + tipo);
        }
    }
}

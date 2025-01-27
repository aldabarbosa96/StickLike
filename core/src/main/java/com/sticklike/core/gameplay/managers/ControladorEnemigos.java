package com.sticklike.core.gameplay.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.enemigos.polla.EnemigoPolla;
import com.sticklike.core.entidades.enemigos.regla.EnemigoRegla;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.entidades.enemigos.culo.EnemigoCulo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.pantallas.juego.VentanaJuego;
import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * La clase EnemyManager se encarga de:
 * <p>
 * Generar enemigos periódicamente en posiciones aleatorias
 * Actualizar la lógica de cada Enemigo (movimiento, muerte, etc.)
 * Renderizar los enemigos en el {@link SpriteBatch}
 * Gestionar la liberación de recursos al final
 * <p>
 * Interactúa con el {@link Jugador} para evitar spawnear enemigos demasiado cerca de él y también para comprobar si el jugador ha muerto
 */
public class ControladorEnemigos {
    private VentanaJuego ventanaJuego;
    private Array<Enemigo> enemigos;
    private Jugador jugador;
    private OrthographicCamera camera;
    private float intervaloDeAparicion, temporizadorDeAparicion;
    private int spawnCounter = 0; // todo --> necesario en un futuro para controlar algunos eventos (según el spawn sucederá algo)
    private Array<Enemigo> enemigosAEliminar = new Array<>();
    private String[] tiposDeEnemigos = {"POLLA", "CULO", "CULO", "CULO", "CULO", "CULO", "REGLA"};


    public ControladorEnemigos(Jugador jugador, float intervaloDeAparicion, VentanaJuego ventanaJuego) {
        this.enemigos = new Array<>();
        this.jugador = jugador;
        this.intervaloDeAparicion = intervaloDeAparicion;
        this.temporizadorDeAparicion = 0;
        this.ventanaJuego = ventanaJuego;
        camera = ventanaJuego.getOrtographicCamera();
    }

    public void actualizarSpawnEnemigos(float delta) {

        if (jugador.estaVivo()) {
            return;
        }
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
        spawnCounter++;
        // Centro del jugador
        float playerX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float playerY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2;

        // Calcula los límites visibles de la cámara menos un borde para que no aparezcan demasiado lejos de los márgenes visibles
        float leftLimit = playerX - VentanaJuego.worldWidth / 2 - CORRECCION_SPAWN;
        float rightLimit = playerX + VentanaJuego.worldWidth / 2 - CORRECCION_SPAWN;
        float bottomLimit = playerY - VentanaJuego.worldHeight / 2 - CORRECCION_SPAWN;
        float topLimit = playerY + VentanaJuego.worldHeight / 2 - CORRECCION_SPAWN;

        float x = 0, y = 0;

        // Elegir aleatoriamente un borde: 0=arriba, 1=abajo, 2=izquierda, 3=derecha
        int side = MathUtils.random(3);
        switch (side) {
            case 0: // arriba
                y = topLimit;
                x = MathUtils.random(leftLimit, rightLimit);
                break;
            case 1: // abajo
                y = bottomLimit;
                x = MathUtils.random(leftLimit, rightLimit);
                break;
            case 2: // izquierda
                x = leftLimit;
                y = MathUtils.random(bottomLimit, topLimit);
                break;
            case 3: // derecha
                x = rightLimit;
                y = MathUtils.random(bottomLimit, topLimit);
                break;

        }

        float randomSpeed = 35f + (float) Math.random() * 45f;
        String tipoElegido = tiposDeEnemigos[(int) (Math.random() * tiposDeEnemigos.length)];
        enemigos.add(fabricaEnemigos(tipoElegido, x, y, jugador, randomSpeed, camera));
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


    // todo --> igual debería aislar este método en una clase
    public static Enemigo fabricaEnemigos(String tipoEnemigo, float x, float y, Jugador jugador, float velocidad, OrthographicCamera camera) {
        switch (tipoEnemigo) {
            case "CULO":
                return new EnemigoCulo(x, y, jugador, velocidad * MULT_VELOCIDAD_CULO);
            case "REGLA":
                return new EnemigoRegla(x, y, jugador, velocidad * MULT_VELOCIDAD_REGLA, camera);
            case "POLLA":
                return new EnemigoPolla(x, y, jugador, velocidad * 1.75f);
            // todo --> añadir más enemigos próximamente
            default:
                throw new IllegalArgumentException("Tipo de enemigo no reconocido: " + tipoEnemigo);
        }
    }

    public void setIntervaloDeAparicion(float intervaloDeAparicion) {
        this.intervaloDeAparicion = intervaloDeAparicion;
    }

    public float getIntervaloDeAparicion() {
        return intervaloDeAparicion;
    }

    public void setTiposDeEnemigos(String[] nuevosTipos) {
        this.tiposDeEnemigos = nuevosTipos;
    } // con esto podremos crear enemigo según queramos con el sistema de eventos

}

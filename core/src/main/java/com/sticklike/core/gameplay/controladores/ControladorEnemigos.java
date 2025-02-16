package com.sticklike.core.gameplay.controladores;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.enemigos.animacion.RenderSombrasEnemigos;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoExamen;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoRegla;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoOro;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.pantallas.juego.VentanaJuego1;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Clase que gestiona el spawn, actualización y render de enemigos.
 */
public class ControladorEnemigos {

    private final VentanaJuego1 ventanaJuego1;
    private final Array<Enemigo> enemigos;
    private final Jugador jugador;
    private final OrthographicCamera camera;
    private float intervaloDeAparicion;
    private float temporizadorDeAparicion;
    private int killCounter = 0;
    private int spawnCounter = 0;
    private final Array<Enemigo> enemigosAEliminar = new Array<>();
    private String[] tiposDeEnemigos = TIPOS_ENEMIGOS;
    private boolean bossSpawned = false;
    private final RenderSombrasEnemigos renderSombrasEnemigos;

    public ControladorEnemigos(Jugador jugador, float intervaloDeAparicion, VentanaJuego1 ventanaJuego1) {
        this.enemigos = new Array<>();
        this.jugador = jugador;
        this.intervaloDeAparicion = intervaloDeAparicion;
        this.temporizadorDeAparicion = 0;
        this.ventanaJuego1 = ventanaJuego1;
        this.camera = ventanaJuego1.getOrtographicCamera();
        this.renderSombrasEnemigos = new RenderSombrasEnemigos();
    }


    public void actualizarSpawnEnemigos(float delta) {
        // Si el jugador está vivo, no actualizamos la IA de enemigos (nota: tu boolean invertido sugiere lo contrario, revisa si es intencional)
        if (jugador.estaVivo()) {
            return;
        }

        // Temporizador para spawnear enemigos de manera periódica
        temporizadorDeAparicion += delta;
        if (temporizadorDeAparicion >= intervaloDeAparicion) {
            spawnEnemigo();
            temporizadorDeAparicion = 0;
        }

        // Actualizamos cada enemigo
        for (Enemigo enemigo : enemigos) {
            enemigo.actualizar(delta);

            // Si un enemigo muere, procesamos su XP / drop
            if (enemigo.estaMuerto() && !enemigo.isProcesado()) {
                killCounter++;

                float random = MathUtils.random(0f, 100f);
                // 1º - 2% de probabilidad de soltar Caca Dorada
                if (random < 2f) {
                    ventanaJuego1.addXPObject(new ObjetoOro(enemigo.getX() + 10f, enemigo.getY() + 10f));
                }
                // 2º - Si no suelta Caca Dorada, suelta XP normal (si es que retorna algo)
                else {
                    ObjetosXP xpObject = enemigo.sueltaObjetoXP();
                    if (xpObject != null) {
                        ventanaJuego1.addXPObject(xpObject);
                    }
                }

                enemigo.setProcesado(true);
                enemigosAEliminar.add(enemigo);
            }
        }

        // Eliminamos de la lista los que ya murieron y se procesaron
        for (Enemigo enemigo : enemigosAEliminar) {
            enemigos.removeValue(enemigo, true);
        }
        enemigosAEliminar.clear();
    }

    public void renderizarEnemigos(SpriteBatch batch) {
        if (jugador.estaVivo()) {
            return;
        }

        // Ordenar por Y descendente para simular Z-order
        enemigos.sort((e1, e2) -> Float.compare(e2.getY(), e1.getY()));

        for (Enemigo enemigo : enemigos) {
            enemigo.renderizar(batch);
        }
    }

    public void dibujarSombrasEnemigos(ShapeRenderer shapeRenderer) {
        if (jugador.estaVivo()) {
            return;
        }

        // Ordenar por Y descendente
        enemigos.sort((e1, e2) -> Float.compare(e2.getY(), e1.getY()));

        renderSombrasEnemigos.dibujarSombrasEnemigos(shapeRenderer, enemigos, camera);
    }


    private void spawnEnemigo() {
        spawnCounter++;

        Vector2 spawnPos = getRandomSpawnPosition();

        float randomSpeed = 35f + (float) Math.random() * 45f;
        String tipoElegido = tiposDeEnemigos[(int) (Math.random() * tiposDeEnemigos.length)];

        // Construimos el enemigo mediante la fábrica
        Enemigo enemigo = fabricaEnemigos(tipoElegido, spawnPos.x, spawnPos.y, jugador, randomSpeed, camera);
        enemigos.add(enemigo);
    }


    private Vector2 getRandomSpawnPosition() {
        // Centro del jugador
        float playerX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float playerY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2;

        // Límites de la cámara
        float leftLimit = playerX - (VentanaJuego1.worldWidth / 2f) - CORRECCION_SPAWN;
        float rightLimit = playerX + (VentanaJuego1.worldWidth / 2f) - CORRECCION_SPAWN;
        float bottomLimit = playerY - (VentanaJuego1.worldHeight / 2f) - CORRECCION_SPAWN;
        float topLimit = playerY + (VentanaJuego1.worldHeight / 2f) - CORRECCION_SPAWN;

        float x = 0, y = 0;

        // Escogemos un borde al azar: 0=arriba, 1=abajo, 2=izquierda, 3=derecha
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
        return new Vector2(x, y);
    }

    public static Enemigo fabricaEnemigos(String tipoEnemigo, float x, float y, Jugador jugador, float velocidad, OrthographicCamera camera) {
        switch (tipoEnemigo) {
            case "CULO":
                return new EnemigoCulo(x, y, jugador, velocidad * MULT_VELOCIDAD_CULO);
            case "REGLA":
                return new EnemigoRegla(x, y, jugador, velocidad * MULT_VELOCIDAD_REGLA, camera);
            case "POLLA":
                return new EnemigoPolla(x, y, jugador, velocidad * MULT_VELOCIDAD_POLLA);
            case "EXAMEN":
                return new EnemigoExamen(x, y, jugador, velocidad * MULT_VELOCIDAD_EXAMEN);
            default:
                throw new IllegalArgumentException("Tipo de enemigo no reconocido: " + tipoEnemigo);
        }
    }


    public void spawnBossPollaAleatorio() {
        if (bossSpawned) {
            return;
        }
        Vector2 spawnPos = getRandomSpawnPosition();
        BossPolla boss = new BossPolla(jugador, spawnPos.x, spawnPos.y);
        enemigos.add(boss);
        bossSpawned = true;
    }

    public void dispose() {
        for (Enemigo enemigo : enemigos) {
            if (enemigo != null) {
                enemigo.dispose();
            }
        }
        enemigos.clear();
    }

    public Array<Enemigo> getEnemigos() {
        return enemigos;
    }

    public float getIntervaloDeAparicion() {
        return intervaloDeAparicion;
    }

    public void setIntervaloDeAparicion(float intervaloDeAparicion) {
        this.intervaloDeAparicion = intervaloDeAparicion;
    }

    public void setTiposDeEnemigos(String[] nuevosTipos) {
        this.tiposDeEnemigos = nuevosTipos;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public int getKillCounter() {
        return killCounter;
    }
}

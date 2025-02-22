package com.sticklike.core.gameplay.controladores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.enemigos.renderizado.RenderBaseEnemigos;
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
    private final Array<Enemigo> enemigos = new Array<>();
    private final Jugador jugador;
    private float intervaloDeAparicion;
    private float temporizadorDeAparicion;
    private int killCounter = 0;
    private int spawnCounter = 0; // seguramente lo necesite en un futuro
    private final Array<Enemigo> enemigosAEliminar = new Array<>();
    private String[] tiposDeEnemigos = TIPOS_ENEMIGOS;
    private boolean bossSpawned = false;
    private final RenderBaseEnemigos renderBaseEnemigos;
    private static final int MAX_ENEMIGOS = 300;
    private boolean ventanaRedimensionada = false;
    private float temporizadorRedimension = 0f;
    private static final float TIEMPO_ESPERA_REDIMENSION = 0.5f;
    private boolean necesitaOrdenar = false;
    private float temporizadorGruposExamen = 0f;
    private static final float INTERVALO_GRUPO_EXAMEN = 5f;

    public ControladorEnemigos(Jugador jugador, float intervaloDeAparicion, VentanaJuego1 ventanaJuego1) {
        this.jugador = jugador;
        this.intervaloDeAparicion = intervaloDeAparicion;
        this.temporizadorDeAparicion = 0;
        this.ventanaJuego1 = ventanaJuego1;
        this.renderBaseEnemigos = new RenderBaseEnemigos();
    }


    public void actualizarSpawnEnemigos(float delta) {
        if (jugador.estaMuerto()) {
            return;
        }

        if (temporizadorGruposExamen > 0) {
            temporizadorGruposExamen -= delta;
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

                if (MathUtils.randomBoolean(0.01f)) {
                    ventanaJuego1.addXPObject(new ObjetoOro(enemigo.getX() + 10f, enemigo.getY() + 10f));
                } else {
                    ObjetosXP xpObject = enemigo.sueltaObjetoXP();
                    if (xpObject != null) {
                        ventanaJuego1.addXPObject(xpObject);
                    }
                }

                enemigo.setProcesado(true);
                enemigosAEliminar.add(enemigo);
            }
        }

        enemigos.removeAll(enemigosAEliminar, true);
        enemigosAEliminar.clear();
        reposicionarEnemigos();
    }

    public void renderizarEnemigos(SpriteBatch batch) {
        if (jugador.estaMuerto()) {
            return;
        }

        enemigos.sort((e1, e2) -> Float.compare(e2.getY(), e1.getY()));

        for (Enemigo enemigo : enemigos) {
            enemigo.renderizar(batch);
        }
    }

    public void dibujarSombrasEnemigos(ShapeRenderer shapeRenderer) {
        if (jugador.estaMuerto()) {
            return;
        }
        renderBaseEnemigos.dibujarSombrasEnemigos(shapeRenderer, enemigos, ventanaJuego1.getOrtographicCamera());
    }


    private void spawnEnemigo() {
        if (enemigos.size >= MAX_ENEMIGOS) return;

        String tipoElegido = tiposDeEnemigos[MathUtils.random(tiposDeEnemigos.length - 1)];
        float randomSpeed = MathUtils.random(45f, 55f); // todo --> valorar si se gestiona individualmente para cada enemigo
        seleccionarTipoSpawn(tipoElegido,randomSpeed);

        necesitaOrdenar = true;
    }

    private void seleccionarTipoSpawn(String tipoElegido, float randomSpeed){ // todo --> hacer más flexible en un futuro para manejar los diferentes tipos de enemigos
        if ("EXAMEN".equals(tipoElegido)) {
            if (temporizadorGruposExamen > 0) {
                return;
            }
            temporizadorGruposExamen = INTERVALO_GRUPO_EXAMEN;

            int groupSize = 18;
            if (enemigos.size + groupSize > MAX_ENEMIGOS) {
                groupSize = MAX_ENEMIGOS - enemigos.size;
            }

            Vector2 basePos = getRandomSpawnPosition();
            for (int i = 0; i < groupSize; i++) {
                Vector2 spawnPos = new Vector2(basePos.x , basePos.y );
                Enemigo enemigo = fabricaEnemigos(tipoElegido, spawnPos.x, spawnPos.y, jugador, randomSpeed, ventanaJuego1.getOrtographicCamera());
                enemigos.add(enemigo);
            }
        } else {
            // Para los demás enemigos se usa el spawn normal
            Vector2 spawnPos = getRandomSpawnPosition();
            Enemigo enemigo = fabricaEnemigos(tipoElegido, spawnPos.x, spawnPos.y, jugador, randomSpeed, ventanaJuego1.getOrtographicCamera());
            enemigos.add(enemigo);
        }
    }

    private Vector2 getRandomSpawnPosition() {
        float playerX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float playerY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2;

        float leftLimit = playerX - (VentanaJuego1.worldWidth / 2f) - CORRECCION_SPAWN;
        float rightLimit = playerX + (VentanaJuego1.worldWidth / 2f) - CORRECCION_SPAWN;
        float bottomLimit = playerY - (VentanaJuego1.worldHeight / 2f) - CORRECCION_SPAWN;
        float topLimit = playerY + (VentanaJuego1.worldHeight / 2f) - CORRECCION_SPAWN;

        return switch (MathUtils.random(3)) {
            case 0 -> new Vector2(MathUtils.random(leftLimit, rightLimit), topLimit);
            case 1 -> new Vector2(MathUtils.random(leftLimit, rightLimit), bottomLimit);
            case 2 -> new Vector2(leftLimit, MathUtils.random(bottomLimit, topLimit));
            default -> new Vector2(rightLimit, MathUtils.random(bottomLimit, topLimit));
        };
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

    private void reposicionarEnemigos() {
        if (ventanaRedimensionada) {
            temporizadorRedimension += Gdx.graphics.getDeltaTime();
            if (temporizadorRedimension < TIEMPO_ESPERA_REDIMENSION) {
                return;
            }
            ventanaRedimensionada = false;
        }

        float margin = 50f;
        float leftBound = ventanaJuego1.getOrtographicCamera().position.x - (ventanaJuego1.getViewport().getWorldWidth() / 2) - margin;
        float rightBound = ventanaJuego1.getOrtographicCamera().position.x + (ventanaJuego1.getViewport().getWorldWidth() / 2) + margin;
        float bottomBound = ventanaJuego1.getOrtographicCamera().position.y - (ventanaJuego1.getViewport().getWorldHeight() / 2) - margin;
        float topBound = ventanaJuego1.getOrtographicCamera().position.y + (ventanaJuego1.getViewport().getWorldHeight() / 2) + margin;

        for (Enemigo enemigo : enemigos) {
            Sprite sprite = enemigo.getSprite();
            float x = sprite.getX();
            float y = sprite.getY();

            if (x < leftBound) sprite.setX(rightBound);
            else if (x > rightBound) sprite.setX(leftBound);

            if (y < bottomBound) sprite.setY(topBound);
            else if (y > topBound) sprite.setY(bottomBound);
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
        enemigos.forEach(Enemigo::dispose);
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

    public int getEnemigosActuales() {
        return enemigos.size;
    }

    public RenderBaseEnemigos getRenderBaseEnemigos() {
        return renderBaseEnemigos;
    }

    public void setVentanaRedimensionada(boolean ventanaRedimensionada) {
        this.ventanaRedimensionada = ventanaRedimensionada;
        this.temporizadorRedimension = 0f;
    }
}

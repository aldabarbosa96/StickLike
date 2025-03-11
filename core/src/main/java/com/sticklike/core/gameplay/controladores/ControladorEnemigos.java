package com.sticklike.core.gameplay.controladores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.enemigos.mobs.*;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.enemigos.mobs.Destructibles;
import com.sticklike.core.entidades.objetos.recolectables.Boost;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.PoissonPoints;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Clase que gestiona el spawn, actualización y render de enemigos (se incluyen destructibles por tratarse como enemigos).
 */
public class ControladorEnemigos {

    private final VentanaJuego1 ventanaJuego1;
    private final Array<Enemigo> enemigos = new Array<>();
    private final Jugador jugador;
    private float intervaloDeAparicion;
    private float temporizadorDeAparicion;
    private int killCounter = 0;
    private int spawnCounter = 0; // posiblemente se use en un futuro
    private final Array<Enemigo> enemigosAEliminar = new Array<>();
    private String[] tiposDeEnemigos = TIPOS_ENEMIGOS;
    private boolean bossSpawned = false;
    private final RenderBaseEnemigos renderBaseEnemigos;
    private static final int MAX_ENEMIGOS = 999;
    private static final int MAX_DESTRUCTIBLES = 250;
    private boolean destructiblesSpawned = false; // para asegurarnos que solo spawnean 1 vez
    private boolean ventanaRedimensionada = false;
    private float temporizadorRedimension = 0f;
    private static final float TIEMPO_ESPERA_REDIMENSION = 0.5f;
    private boolean necesitaOrdenar = false;
    private float temporizadorGruposExamen = 0f;
    private static final float INTERVALO_GRUPO_EXAMEN = 5f;
    private float temporizadorVaterSpawn = 0f;
    private static float intervaloVaterSpawn = 30f;

    public ControladorEnemigos(Jugador jugador, float intervaloDeAparicion, VentanaJuego1 ventanaJuego1) {
        this.jugador = jugador;
        this.intervaloDeAparicion = intervaloDeAparicion;
        this.temporizadorDeAparicion = 0;
        this.ventanaJuego1 = ventanaJuego1;
        this.renderBaseEnemigos = new RenderBaseEnemigos();

        spawnDestructibles();
    }


    public void actualizarSpawnEnemigos(float delta) {
        if (jugador.estaMuerto()) {
            return;
        }

        if (temporizadorGruposExamen > 0) {
            temporizadorGruposExamen -= delta;
        }

        // Spawn periódico de enemigos normales
        temporizadorDeAparicion += delta;
        if (temporizadorDeAparicion >= intervaloDeAparicion) {
            spawnEnemigo();
            temporizadorDeAparicion = 0;
        }

        // Spawn específico para EnemigoVater
        temporizadorVaterSpawn += delta;
        if (temporizadorVaterSpawn >= intervaloVaterSpawn) {
            spawnVaterEnemigo();
            temporizadorVaterSpawn = 0;
        }

        // Actualización de cada enemigo
        for (Enemigo enemigo : enemigos) {
            enemigo.actualizar(delta);
            if (enemigo.estaMuerto() && !enemigo.isProcesado()) {
                killCounter++;

                // Si el enemigo es un destructible, suelta un boost
                if (enemigo instanceof Destructibles) {
                    Destructibles destructible = (Destructibles) enemigo;
                    Boost boost = destructible.sueltaBoost(ventanaJuego1.getRenderHUDComponents());
                    ventanaJuego1.addXPObject(boost);
                } else {
                    // Para los demás enemigos, soltamos el objeto XP habitual
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

        //Gdx.app.log("DEBUG", "Total de enemigos activos: " + enemigos.size);

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
        float randomSpeed = MathUtils.random(45f, 55f);
        seleccionarTipoSpawn(tipoElegido, randomSpeed);
        necesitaOrdenar = true;
    }

    private void seleccionarTipoSpawn(String tipoElegido, float randomSpeed) {
        if ("EXAMEN".equals(tipoElegido)) {
            spawnEnGrupo(tipoElegido, randomSpeed);
        } else {
            // Spawn normal para enemigos: se usa posición cercana al jugador
            Vector2 spawnPos = getRandomSpawnPosition();
            Enemigo enemigo = fabricaEnemigos(tipoElegido, spawnPos.x, spawnPos.y, jugador, randomSpeed, ventanaJuego1.getOrtographicCamera());
            enemigos.add(enemigo);
        }
    }

    // Posición de spawn para enemigos cercanos al jugador
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

    private void spawnEnGrupo(String tipoElegido, float randomSpeed) {
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
            Vector2 spawnPos = new Vector2(basePos.x, basePos.y);
            Enemigo enemigo = fabricaEnemigos(tipoElegido, spawnPos.x, spawnPos.y, jugador, randomSpeed, ventanaJuego1.getOrtographicCamera());
            enemigos.add(enemigo);
        }
    }

    private void spawnVaterEnemigo() {
        if (enemigos.size >= MAX_ENEMIGOS) return;
        Vector2 spawnPos = getRandomSpawnPosition();
        Enemigo enemigo = fabricaEnemigos("VATER", spawnPos.x, spawnPos.y, jugador, 0, ventanaJuego1.getOrtographicCamera());
        enemigos.add(enemigo);
    }


    public void spawnDestructibles() {
        if (destructiblesSpawned) {
            return;
        }
        int count = 0;
        Array<Vector2> candidatePositions = PoissonPoints.getInstance().generatePoissonPoints(MAP_MIN_X, MAP_MIN_Y, MAP_MAX_X, MAP_MAX_Y, MIN_DIST_SAME_TEXTURE, 30);

        // Barajamos las posiciones para mayor aleatoriedad
        candidatePositions.shuffle();

        float playerCenterX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
        float playerCenterY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;
        Vector2 playerCenter = new Vector2(playerCenterX, playerCenterY);

        for (Vector2 candidateCenter : candidatePositions) {
            if (candidateCenter.dst(playerCenter) < 750f) {
                continue;
            }

            float spawnX = candidateCenter.x - ANCHO_DESTRUCT / 2f;
            float spawnY = candidateCenter.y - ALTO_DESTRUCT / 2f;

            // Verificamos que el destructible quepa en el mapa
            if (spawnX < MAP_MIN_X || spawnX + ANCHO_DESTRUCT > MAP_MAX_X || spawnY < MAP_MIN_Y || spawnY + ALTO_DESTRUCT > MAP_MAX_Y) {
                continue;
            }

            // Creamos el destructible y lo añadimos al array global de enemigos
            Destructibles d = new Destructibles(spawnX, spawnY, renderBaseEnemigos);
            enemigos.add(d);
            count++;

            if (count >= MAX_DESTRUCTIBLES) {
                break;
            }
        }
        destructiblesSpawned = true;
    }

    public static Enemigo fabricaEnemigos(String tipoEnemigo, float x, float y, Jugador jugador, float velocidad, OrthographicCamera camera) {
        switch (tipoEnemigo) {
            case "CULO":
                return new EnemigoCulo(x, y, jugador);
            case "REGLA":
                return new EnemigoRegla(x, y, jugador, velocidad * MULT_VELOCIDAD_REGLA, camera);
            case "POLLA":
                return new EnemigoPolla(x, y, jugador, velocidad * MULT_VELOCIDAD_POLLA);
            case "EXAMEN":
                return new EnemigoExamen(x, y, jugador, velocidad * MULT_VELOCIDAD_EXAMEN);
            case "VATER":
                return new EnemigoVater(x, y, jugador);
            default:
                throw new IllegalArgumentException("Tipo de enemigo no reconocido: " + tipoEnemigo);
        }
    }

    // Reposiciona enemigos que salen del área visible para que vuelvan a aparecer por el otro lado.
    private void reposicionarEnemigos() {
        if (ventanaRedimensionada) {
            temporizadorRedimension += Gdx.graphics.getDeltaTime();
            if (temporizadorRedimension < TIEMPO_ESPERA_REDIMENSION) {
                return;
            }
            ventanaRedimensionada = false;
        }
        float margin = 175f;
        float leftBound = ventanaJuego1.getOrtographicCamera().position.x - (ventanaJuego1.getViewport().getWorldWidth() / 2) - margin;
        float rightBound = ventanaJuego1.getOrtographicCamera().position.x + (ventanaJuego1.getViewport().getWorldWidth() / 2) + margin;
        float bottomBound = ventanaJuego1.getOrtographicCamera().position.y - (ventanaJuego1.getViewport().getWorldHeight() / 2) - margin;
        float topBound = ventanaJuego1.getOrtographicCamera().position.y + (ventanaJuego1.getViewport().getWorldHeight() / 2) + margin;
        for (Enemigo enemigo : enemigos) {
            if (enemigo instanceof Destructibles) {
                continue;
            }
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

    public VentanaJuego1 getVentanaJuego1() {
        return ventanaJuego1;
    }
}

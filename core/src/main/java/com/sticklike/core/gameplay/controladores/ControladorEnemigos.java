package com.sticklike.core.gameplay.controladores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.entidades.mobiliario.destructibles.Destructibles;
import com.sticklike.core.entidades.mobiliario.destructibles.Destructibles2;
import com.sticklike.core.entidades.enemigos.mobs.escuela.*;
import com.sticklike.core.entidades.enemigos.mobs.generico.*;
import com.sticklike.core.entidades.enemigos.mobs.sexo.*;
import com.sticklike.core.entidades.mobiliario.tragaperras.Tragaperras;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.Boost;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.PoissonPoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Clase que gestiona el spawn, actualización y render de enemigos (se incluyen destructibles por tratarse como enemigos).
 */
public class ControladorEnemigos {
    private final VentanaJuego1 ventanaJuego1;
    private final Array<Enemigo> enemigos = new Array<>();
    private final Array<Tragaperras> tragaperras = new Array<>();
    private final Jugador jugador;
    private float intervaloDeAparicion;
    private float temporizadorDeAparicion;
    private int killCounter = 0;
    private int spawnCounter = 0; // posiblemente se use en un futuro
    //private final Array<Enemigo> enemigosAEliminar = new Array<>();
    private String[] tiposDeEnemigos = TIPOS_ENEMIGOS;
    private boolean bossSpawned = false;
    private final RenderBaseEnemigos renderBaseEnemigos;
    private static final int MAX_ENEMIGOS = 1400;
    private static final int MAX_DESTRUCTIBLES = 250;
    private static final int MAX_DESTRUCTIBLES2 = 450;
    private boolean destructiblesSpawned = false; // para asegurarnos que solo spawnean 1 vez
    private boolean yaSpawneadosDestructibles2 = false;
    private boolean ventanaRedimensionada = false;
    private float temporizadorRedimension = 0f;
    //private static final float TIEMPO_ESPERA_REDIMENSION = 0.5f;
    private boolean necesitaOrdenar = false;
    private float temporizadorGruposExamen = 0f;
    private static final float INTERVALO_GRUPO_EXAMEN = 0.8f;
    private float temporizadorVaterSpawn = 0f;
    private static float intervaloVaterSpawn = 40f;
    private final Vector2 tmpRandomSpawn = new Vector2();
    private final Vector2 tmpPlayerCenter = new Vector2();
    private final Vector2 tmpNearSpawn = new Vector2();
    private static final Comparator<Enemigo> COMP_Y = (a, b) -> Float.compare(b.getY(), a.getY());
    private static final int SORT_INTERVAL = 10;
    private int sortCounter = 0;
    private float speedMult = 1;
    private boolean tragaperrasSpawned = false;

    public ControladorEnemigos(Jugador jugador, float intervaloDeAparicion, VentanaJuego1 ventanaJuego1) {
        this.jugador = jugador;
        this.intervaloDeAparicion = intervaloDeAparicion;
        this.temporizadorDeAparicion = 0;
        this.ventanaJuego1 = ventanaJuego1;
        this.renderBaseEnemigos = new RenderBaseEnemigos();

        spawnDestructibles();
        spawnDestructibles2();
        spawnTragaperrasInicial();
    }

    public void actualizarSpawnEnemigos(float delta) {
        if (jugador.estaMuerto()) return;

        /* ─── 1. Actualizamos timers de spawn ─────────────────────────── */
        temporizadorGruposExamen = Math.max(0f, temporizadorGruposExamen - delta);

        temporizadorDeAparicion += delta;
        if (temporizadorDeAparicion >= intervaloDeAparicion) {
            spawnEnemigo();
            temporizadorDeAparicion = 0f;
        }

        temporizadorVaterSpawn += delta;
        if (temporizadorVaterSpawn >= intervaloVaterSpawn) {
            spawnVaterEnemigo();
            temporizadorVaterSpawn = 0f;
        }

        /* ─── 2. Calculamos límites de reposición SOLO una vez ────────── */
        final float margin = 175f;
        OrthographicCamera cam = ventanaJuego1.getOrtographicCamera();
        float halfW = ventanaJuego1.getViewport().getWorldWidth() * 0.5f;
        float halfH = ventanaJuego1.getViewport().getWorldHeight() * 0.5f;
        float camX = cam.position.x, camY = cam.position.y;
        float left = camX - halfW - margin;
        float right = camX + halfW + margin;
        float bottom = camY - halfH - margin;
        float top = camY + halfH + margin;

        /* ─── 3. UNA sola pasada por la lista ─────────────────────────── */
        for (int i = enemigos.size - 1; i >= 0; i--) {
            Enemigo e = enemigos.get(i);
            e.actualizar(delta);

            /* 3A. Si muere -> procesamos drop y quitamos del array */
            if (e.estaMuerto()) {
                if (!e.isProcesado()) {
                    if (e instanceof Destructibles d) {
                        Boost boost = d.sueltaBoost(ventanaJuego1.getRenderHUDComponents());
                        ventanaJuego1.addXPObject(boost);
                    } else {
                        ObjetosXP xp = e.sueltaObjetoXP();
                        if (xp != null) ventanaJuego1.addXPObject(xp);
                    }
                    e.setProcesado(true);
                    if (e instanceof Tragaperras t) {
                        tragaperras.removeValue(t, true);
                    }
                }
                enemigos.removeIndex(i);
                killCounter++;
                continue;
            }

            /* 3B. Reposición si sale del viewport y NO es destructible */
            if (!esEntidadEstatica(e)) reposiciona(e, left, right, bottom, top);
        }

        //Gdx.app.log("ControladorEnemigos", "Enemigos vivos: " + enemigos.size);
    }


    public void renderizarEnemigos(SpriteBatch batch) {
        if (jugador.estaMuerto()) {
            return;
        }

        if (++sortCounter >= SORT_INTERVAL) {
            enemigos.sort(COMP_Y);
            sortCounter = 0;
        }

        OrthographicCamera cam = ventanaJuego1.getOrtographicCamera();

        // Precalculamos límites de la cámara
        float halfW = cam.viewportWidth * cam.zoom * 0.5f;
        float halfH = cam.viewportHeight * cam.zoom * 0.5f;
        float left = cam.position.x - halfW;
        float right = cam.position.x + halfW;
        float bottom = cam.position.y - halfH;
        float top = cam.position.y + halfH;

        for (Enemigo enemigo : enemigos) {
            Sprite sprite = enemigo.getSprite();
            float x = sprite.getX();
            float y = sprite.getY();
            float w = sprite.getWidth();
            float h = sprite.getHeight();

            // Frustum culling manual
            if (x + w < left || x > right || y + h < bottom || y > top) {
                continue;
            }

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
        float randomSpeed = MathUtils.random(45f, 55f) * speedMult;
        seleccionarTipoSpawn(tipoElegido, randomSpeed);
        necesitaOrdenar = true;
    }

    private void seleccionarTipoSpawn(String tipoElegido, float randomSpeed) {
        if ("EXAMEN".equals(tipoElegido)) {
            spawnEnGrupo(tipoElegido, randomSpeed);
        } else {
            Vector2 spawnPos = getRandomSpawnPosition();
            Enemigo enemigo = fabricaEnemigos(tipoElegido, spawnPos.x, spawnPos.y, jugador, randomSpeed, ventanaJuego1.getOrtographicCamera());
            enemigos.add(enemigo);
        }
    }

    // Calculamos una posición aleatoria de spawn en base a la posición del jugador
    private Vector2 getRandomSpawnPosition() {
        float playerX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float playerY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2;
        float leftLimit = playerX - (VentanaJuego1.worldWidth / 2f) + CORRECCION_SPAWN;
        float rightLimit = playerX + (VentanaJuego1.worldWidth / 2f) - CORRECCION_SPAWN;
        float bottomLimit = playerY - (VentanaJuego1.worldHeight / 2f) - CORRECCION_SPAWN;
        float topLimit = playerY + (VentanaJuego1.worldHeight / 2f) - CORRECCION_SPAWN;
        switch (MathUtils.random(3)) {
            case 0 -> tmpRandomSpawn.set(MathUtils.random(leftLimit, rightLimit), topLimit);
            case 1 -> tmpRandomSpawn.set(MathUtils.random(leftLimit, rightLimit), bottomLimit);
            case 2 -> tmpRandomSpawn.set(leftLimit, MathUtils.random(bottomLimit, topLimit));
            default -> tmpRandomSpawn.set(rightLimit, MathUtils.random(bottomLimit, topLimit));
        }
        return tmpRandomSpawn;
    }

    private void spawnEnGrupo(String tipoElegido, float randomSpeed) {
        if (temporizadorGruposExamen > 0) {
            return;
        }
        temporizadorGruposExamen = INTERVALO_GRUPO_EXAMEN;
        int groupSize = Math.min(18, MAX_ENEMIGOS - enemigos.size);
        // getRandomSpawnPosition() ya setea tmpRandomSpawn y lo devuelve
        Vector2 basePos = getRandomSpawnPosition();
        for (int i = 0; i < groupSize; i++) {
            // No creamos un nuevo Vector2, usamos directamente basePos
            Enemigo enemigo = fabricaEnemigos(tipoElegido, basePos.x, basePos.y, jugador, randomSpeed, ventanaJuego1.getOrtographicCamera());
            enemigos.add(enemigo);
        }
    }

    void spawnTragaperrasInicial() {

        if (tragaperrasSpawned) return;
        tragaperrasSpawned = true;

        final float MIN_DIST = 2500f;
        final float MAX_DIST = 7500f;
        final float MAX_PERP = 1250f;

        // centro actual del jugador
        float cx = jugador.getSprite().getX() + jugador.getSprite().getWidth() * 0.5f;
        float cy = jugador.getSprite().getY() + jugador.getSprite().getHeight() * 0.5f;

        /* 1- Escogemos tres direcciones distintas al azar. */
        List<Integer> dirs = new ArrayList<>(List.of(0, 1, 2, 3));  // 0=N,1=S,2=E,3=O
        Collections.shuffle(dirs);
        dirs = dirs.subList(0, 3);

        int idx = 0;
        /* 2- Spawneamos una tragaperras por dirección. */
        for (int d : dirs) {

            float dist = MathUtils.random(MIN_DIST, MAX_DIST);
            float offset = MathUtils.random(-MAX_PERP, MAX_PERP);

            float x = cx, y = cy;
            switch (d) {
                case 0 -> {
                    y += dist;
                    x += offset;
                }      // Norte
                case 1 -> {
                    y -= dist;
                    x += offset;
                }      // Sur
                case 2 -> {
                    x += dist;
                    y += offset;
                }      // Este
                case 3 -> {
                    x -= dist;
                    y += offset;
                }      // Oeste
            }

            Tragaperras.Direccion dirEnum = switch (d) {
                case 0 -> Tragaperras.Direccion.NORTE;
                case 1 -> Tragaperras.Direccion.SUR;
                case 2 -> Tragaperras.Direccion.ESTE;
                default -> Tragaperras.Direccion.OESTE;
            };

            Gdx.app.log("TRAGAPERRAS", String.format(
                "[%d] Dir=%s  Pos=(%.1f, %.1f)  Dist=%.0f  Offset=%.0f",
                idx++, dirEnum, x, y, dist, offset));

            Tragaperras slot = new Tragaperras(x, y, renderBaseEnemigos, ventanaJuego1, dirEnum);
            enemigos.add(slot);
            tragaperras.add(slot);
        }
    }


    private void spawnVaterEnemigo() {
        if (enemigos.size >= MAX_ENEMIGOS) return;
        Vector2 spawnPos = getRandomSpawnPosition();
        Enemigo enemigo = fabricaEnemigos("VATER", spawnPos.x, spawnPos.y, jugador, 10 * speedMult, ventanaJuego1.getOrtographicCamera());
        enemigos.add(enemigo);
    }

    public void spawnDestructibles() {
        if (destructiblesSpawned) {
            return;
        }
        int count = 0;
        Array<Vector2> candidatePositions = PoissonPoints.getInstance().generatePoissonPoints(MAP_MIN_X, MAP_MIN_Y, MAP_MAX_X, MAP_MAX_Y, MIN_DIST_SAME_TEXTURE, 30);
        candidatePositions.shuffle();

        // Calculamos y guardamos el centro del jugador en tmpPlayerCenter
        tmpPlayerCenter.set(jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f, jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f);

        for (Vector2 candidateCenter : candidatePositions) {
            if (candidateCenter.dst(tmpPlayerCenter) < 750f) {
                continue;
            }
            float spawnX = candidateCenter.x - ANCHO_DESTRUCT / 2f;
            float spawnY = candidateCenter.y - ALTO_DESTRUCT / 2f;
            if (spawnX < MAP_MIN_X || spawnX + ANCHO_DESTRUCT > MAP_MAX_X || spawnY < MAP_MIN_Y || spawnY + ALTO_DESTRUCT > MAP_MAX_Y) {
                continue;
            }
            Destructibles d = new Destructibles(spawnX, spawnY, renderBaseEnemigos);
            enemigos.add(d);
            if (++count >= MAX_DESTRUCTIBLES) {
                break;
            }
        }
        destructiblesSpawned = true;
    }

    public void spawnDestructibles2() {
        if (yaSpawneadosDestructibles2) {
            return;
        }
        yaSpawneadosDestructibles2 = true;

        // La primera posición cerca del jugador
        Vector2 firstPos = getSpawnPositionNearPlayer(500f, 500f);
        enemigos.add(new Destructibles2(firstPos.x, firstPos.y, renderBaseEnemigos));

        int count = 1;
        Array<Vector2> candidatePositions = PoissonPoints.getInstance().generatePoissonPoints(MAP_MIN_X, MAP_MIN_Y, MAP_MAX_X, MAP_MAX_Y, MIN_DIST_SAME_TEXTURE2, 30);
        candidatePositions.shuffle();

        // Recalculamos tmpPlayerCenter
        tmpPlayerCenter.set(jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f, jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f);

        for (Vector2 candidateCenter : candidatePositions) {
            if (candidateCenter.dst(tmpPlayerCenter) < 750f) {
                continue;
            }
            float spawnX = candidateCenter.x - ANCHO_DESTRUCT / 2f;
            float spawnY = candidateCenter.y - ALTO_DESTRUCT / 2f;
            if (spawnX < MAP_MIN_X || spawnX + ANCHO_DESTRUCT > MAP_MAX_X || spawnY < MAP_MIN_Y || spawnY + ALTO_DESTRUCT > MAP_MAX_Y) {
                continue;
            }
            enemigos.add(new Destructibles2(spawnX, spawnY, renderBaseEnemigos));
            if (++count >= MAX_DESTRUCTIBLES2) {
                break;
            }
        }
    }

    private Vector2 getSpawnPositionNearPlayer(float minDistance, float maxDistance) {
        float playerX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
        float playerY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;
        float angle = MathUtils.random(0, MathUtils.PI2);
        float distance = (minDistance == maxDistance) ? minDistance : MathUtils.random(minDistance, maxDistance);
        float spawnX = playerX + MathUtils.cos(angle) * distance;
        float spawnY = playerY + MathUtils.sin(angle) * distance;
        spawnX = MathUtils.clamp(spawnX, MAP_MIN_X, MAP_MAX_X - ANCHO_DESTRUCT_LATA - 50f);
        spawnY = MathUtils.clamp(spawnY, MAP_MIN_Y, MAP_MAX_Y - ALTO_DESTRUCT_LATA - 50f);
        tmpNearSpawn.set(spawnX, spawnY);
        return tmpNearSpawn;
    }

    public static Enemigo fabricaEnemigos(String tipoEnemigo, float x, float y, Jugador jugador, float velocidad, OrthographicCamera camera) {
        switch (tipoEnemigo) {
            case "CULO":
                return new EnemigoCulo(x, y, jugador);
            case "POLLA":
                return new EnemigoPolla(x, y, jugador, velocidad * MULT_VELOCIDAD_POLLA);
            case "CONDON":
                return new EnemigoCondon(jugador, x, y, velocidad * MULT_VELOCIDAD_CONDON, camera);
            case "TETA":
                return new EnemigoTeta(jugador, x, y, velocidad * MULT_VELOCIDAD_TETA);
            case "REGLA":
                return new EnemigoRegla(x, y, jugador, velocidad * MULT_VELOCIDAD_REGLA, camera);
            case "EXAMEN":
                return new EnemigoExamen(x, y, jugador, velocidad * MULT_VELOCIDAD_EXAMEN);
            case "VATER":
                return new EnemigoVater(x, y, jugador);
            case "ALARMA":
                return new EnemigoAlarma(x, y, jugador);
            default:
                throw new IllegalArgumentException("Tipo de enemigo no reconocido: " + tipoEnemigo);
        }
    }

    private static boolean esEntidadEstatica(Enemigo e) {
        return e instanceof Destructibles || e instanceof Destructibles2 || e instanceof Tragaperras;   // ← añadido
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


    private void reposiciona(Enemigo e, float left, float right, float bottom, float top) {

        Sprite s = e.getSprite();
        float x = s.getX(), y = s.getY();

        if (x < left) s.setX(right);
        else if (x > right) s.setX(left);

        if (y < bottom) s.setY(top);
        else if (y > top) s.setY(bottom);
    }

    /*public void resetTimers() {
        temporizadorDeAparicion = 0f;
        temporizadorVaterSpawn = 0f;
        temporizadorGruposExamen = 0f;
    }*/


    public void dispose() {
        for (Enemigo enemigo : enemigos) {
            enemigo.dispose();
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

    public float getSpeedMult() {
        return speedMult;
    }

    public void setSpeedMult(float speedMult) {
        this.speedMult = speedMult;
    }

    public Array<Tragaperras> getTragaperras() {
        return tragaperras;
    }
}

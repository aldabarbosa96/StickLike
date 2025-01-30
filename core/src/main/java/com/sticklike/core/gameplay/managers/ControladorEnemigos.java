package com.sticklike.core.gameplay.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoRegla;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.pantallas.juego.VentanaJuego;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * La clase ControladorEnemigos se encarga de:
 * - Generar enemigos periódicamente en posiciones aleatorias
 * - Actualizar la lógica de cada Enemigo (movimiento, muerte, etc.)
 * - Renderizar los enemigos en el SpriteBatch
 * - Gestionar la liberación de recursos al final.
 *
 * Interactúa con el Jugador para evitar spawnear enemigos demasiado cerca de él y también para comprobar si el jugador ha muerto
 */
public class ControladorEnemigos {
    private VentanaJuego ventanaJuego;
    private Array<Enemigo> enemigos;
    private Jugador jugador;
    private OrthographicCamera camera;

    private float intervaloDeAparicion;
    private float temporizadorDeAparicion;

    private int spawnCounter = 0; // todo --> para en un futuro controlar eventos

    private Array<Enemigo> enemigosAEliminar = new Array<>();
    private String[] tiposDeEnemigos = TIPOS_ENEMIGOS;

    // Flag para asegurar que el Boss solo aparezca UNA vez
    private boolean bossSpawned = false;

    public ControladorEnemigos(Jugador jugador, float intervaloDeAparicion, VentanaJuego ventanaJuego) {
        this.enemigos = new Array<>();
        this.jugador = jugador;
        this.intervaloDeAparicion = intervaloDeAparicion;
        this.temporizadorDeAparicion = 0;
        this.ventanaJuego = ventanaJuego;
        this.camera = ventanaJuego.getOrtographicCamera();
    }

    public void actualizarSpawnEnemigos(float delta) {
        // Si el jugador no está vivo, no spawneamos ni actualizamos nada (booleano invertido)
        if (jugador.estaVivo()) {
            return;
        }

        // Temporizador para spawnear enemigos de forma periódica
        temporizadorDeAparicion += delta;
        if (temporizadorDeAparicion >= intervaloDeAparicion) {
            spawnEnemigo();
            temporizadorDeAparicion = 0;
        }

        // Actualizamos cada enemigo
        for (Enemigo enemigo : enemigos) {
            enemigo.actualizar(delta);

            // Si ha muerto y aún no está procesado (para soltar XP o similares)
            if (enemigo.estaMuerto() && !enemigo.isProcesado()) {
                ObjetosXP xpObject = enemigo.sueltaObjetoXP();
                if (xpObject != null) {
                    ventanaJuego.addXPObject(xpObject);
                }
                enemigo.setProcesado(true);
                enemigosAEliminar.add(enemigo);
            }
        }

        // Eliminamos de la lista aquellos que ya murieron y fueron procesados
        for (Enemigo enemigo : enemigosAEliminar) {
            enemigos.removeValue(enemigo, true);
        }
        enemigosAEliminar.clear();
    }

    public void dibujarSombrasEnemigos(ShapeRenderer shapeRenderer) {
        // Ordenar enemigos por Y (descendente) para manejar un Z-order más coherente
        enemigos.sort((e1, e2) -> Float.compare(e2.getY(), e1.getY()));

        for (Enemigo enemigo : enemigos) {
            if (enemigo instanceof BossPolla) {
                dibujarSombraBossPolla(enemigo, shapeRenderer);
            }
            else if (enemigo instanceof EnemigoCulo) {
                dibujarSombraCulo(enemigo, shapeRenderer);
            }
            else if (enemigo instanceof EnemigoPolla) {
                dibujarSombraPolla((EnemigoPolla) enemigo, shapeRenderer);
            }
            else {
                dibujarSombraDefault(enemigo, shapeRenderer);
            }
        }
    }

    /**
     * Renderiza todos los enemigos en el SpriteBatch.
     */
    public void renderizarEnemigos(SpriteBatch batch) {
        // Si el jugador no está vivo, mostramos los enemigos (lógica de “booleano invertido”)
        if (jugador.estaVivo()) {
            return;
        }

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

        // Límites de la cámara (margen extra para que aparezcan fuera de pantalla).
        float leftLimit = playerX - (float) VentanaJuego.worldWidth / 2 - CORRECCION_SPAWN;
        float rightLimit = playerX + (float) VentanaJuego.worldWidth / 2 - CORRECCION_SPAWN;
        float bottomLimit = playerY - (float) VentanaJuego.worldHeight / 2 - CORRECCION_SPAWN;
        float topLimit = playerY + (float) VentanaJuego.worldHeight / 2 - CORRECCION_SPAWN;

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

        float randomSpeed = 35f + (float) Math.random() * 45f;
        String tipoElegido = tiposDeEnemigos[(int) (Math.random() * tiposDeEnemigos.length)];

        // Si el tipo es BOSSPOLLA y ya se ha spawneado antes, no lo creamos otra vez
        if ("BOSSPOLLA".equals(tipoElegido) && bossSpawned) {
            return;
        }

        // Si es BOSSPOLLA por primera vez, marcamos que ya se ha spawneado
        if ("BOSSPOLLA".equals(tipoElegido)) {
            bossSpawned = true;
        }

        // Construimos el enemigo usando la fábrica
        enemigos.add(fabricaEnemigos(tipoElegido, x, y, jugador, randomSpeed, camera));
    }

    /**
     * Fábrica genérica de enemigos. Añadimos el caso BOSSPOLLA para el Boss.
     */
    public static Enemigo fabricaEnemigos(String tipoEnemigo, float x, float y,
                                          Jugador jugador, float velocidad, OrthographicCamera camera)
    {
        switch (tipoEnemigo) {
            case "CULO":
                return new EnemigoCulo(x, y, jugador, velocidad * MULT_VELOCIDAD_CULO);
            case "REGLA":
                return new EnemigoRegla(x, y, jugador, velocidad * MULT_VELOCIDAD_REGLA, camera);
            case "POLLA":
                return new EnemigoPolla(x, y, jugador, velocidad * MULT_VELOCIDAD_POLLA);
            case "BOSSPOLLA":
                return new BossPolla(jugador, x, y);
            default:
                throw new IllegalArgumentException("Tipo de enemigo no reconocido: " + tipoEnemigo);
        }
    }

    private void dibujarSombraBossPolla(Enemigo enemigo, ShapeRenderer shapeRenderer) {
        float x = enemigo.getX();
        float y = enemigo.getY();
        float w = enemigo.getSprite().getWidth();
        float h = enemigo.getSprite().getHeight();

        float centerX = x + w / 2f;

        float shadowWidth = w;
        float shadowHeight = h * 0.2f;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y - 8f;

        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.5f);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void dibujarSombraCulo(Enemigo enemigo, ShapeRenderer shapeRenderer) {
        float x = enemigo.getX();
        float y = enemigo.getY();
        float w = enemigo.getSprite().getWidth();
        float h = enemigo.getSprite().getHeight();

        float centerX = x + w / 2f;
        float shadowWidth = w * SHADOW_WIDTH_CULO;
        float shadowHeight = h * SHADOW_HEIGHT_CULO;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y - SHADOW_OFFSET;

        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.5f);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void dibujarSombraPolla(EnemigoPolla polla, ShapeRenderer shapeRenderer) {
        float offset = polla.getMovimientoPolla().getCurrentOffset();

        float x = polla.getX();
        float y = polla.getY();
        float w = polla.getSprite().getWidth();
        float h = polla.getSprite().getHeight();

        float centerX = x + w / SHADOW_OFFSET;

        float baseShadowWidth = w * 0.9f;
        float baseShadowHeight = h * 0.3f;
        float baseShadowY = y - 2.5f;

        float maxZigzag = polla.getMovimientoPolla().getAmplitudZigzag();

        // Normalizamos offset en [-1, 1].
        float normalizado = offset / maxZigzag;
        if (normalizado > 1) normalizado = 1;
        if (normalizado < -1) normalizado = -1;

        float topScale = 0.2f;
        float bottomScale = 0.6f;
        float factor = bottomScale + (topScale - bottomScale) * ((normalizado + 1f) / 2f);

        float finalShadowWidth = baseShadowWidth * factor;
        float finalShadowHeight = baseShadowHeight * factor;
        float finalShadowX = centerX - finalShadowWidth / 2f;

        shapeRenderer.setColor(0.3f, 0.3f, 0.1f, 0.5f);
        shapeRenderer.ellipse(finalShadowX, baseShadowY, finalShadowWidth, finalShadowHeight);
    }

    private void dibujarSombraDefault(Enemigo enemigo, ShapeRenderer shapeRenderer) {
        float x = enemigo.getX();
        float y = enemigo.getY();
        float w = enemigo.getSprite().getWidth();
        float h = enemigo.getSprite().getHeight();

        float centerX = x + w / 2f;
        float shadowWidth = w * 0.75f;
        float shadowHeight = h * 0.3f;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y - 2.5f;

        shapeRenderer.setColor(0.35f, 0.35f, 0.35f, 0.5f);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
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

    public float getIntervaloDeAparicion() {
        return intervaloDeAparicion;
    }

    public void setIntervaloDeAparicion(float intervaloDeAparicion) {
        this.intervaloDeAparicion = intervaloDeAparicion;
    }

    /**
     * Permite modificar los tipos de enemigos que se spawnean (por ejemplo, para introducir BOSSPOLLA en una fase concreta)
     */
    public void setTiposDeEnemigos(String[] nuevosTipos) {
        this.tiposDeEnemigos = nuevosTipos;
    }

    public Jugador getJugador() {
        return jugador;
    }
}

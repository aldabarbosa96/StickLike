package com.sticklike.core.gameplay.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.enemigos.tipos.culo.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.tipos.polla.EnemigoPolla;
import com.sticklike.core.entidades.enemigos.tipos.regla.EnemigoRegla;
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
 * - Gestionar la liberación de recursos al final
 * Interactúa con el Jugador para evitar spawnear enemigos demasiado cerca de él y también para comprobar si el jugador ha muerto
 * todo --> refactorizar en un futuro para separar dependencias (solamente debe encargarse del spawn y eliminación de los enemigos)
 */
public class ControladorEnemigos {
    private VentanaJuego ventanaJuego;
    private Array<Enemigo> enemigos;
    private Jugador jugador;
    private OrthographicCamera camera;
    private float intervaloDeAparicion, temporizadorDeAparicion;
    private int spawnCounter = 0; // todo --> para en un futuro controlar eventos
    private Array<Enemigo> enemigosAEliminar = new Array<>();
    private String[] tiposDeEnemigos = TIPOS_ENEMIGOS;

    public ControladorEnemigos(Jugador jugador, float intervaloDeAparicion, VentanaJuego ventanaJuego) {
        this.enemigos = new Array<>();
        this.jugador = jugador;
        this.intervaloDeAparicion = intervaloDeAparicion;
        this.temporizadorDeAparicion = 0;
        this.ventanaJuego = ventanaJuego;
        this.camera = ventanaJuego.getOrtographicCamera();
    }

    /**
     * Actualiza el spawn de enemigos y la lógica de cada uno.
     */
    public void actualizarSpawnEnemigos(float delta) {
        // Si el jugador no está vivo, no spawneamos ni actualizamos nada.
        if (jugador.estaVivo()) { // el booleano está invertido, significa que está muerto
            return;
        }

        // Temporizador para spawnear enemigos
        temporizadorDeAparicion += delta;
        if (temporizadorDeAparicion >= intervaloDeAparicion) {
            spawnEnemigo();
            temporizadorDeAparicion = 0;
        }

        // Actualizamos cada enemigo
        for (Enemigo enemigo : enemigos) {
            enemigo.actualizar(delta);

            // Si ha muerto y aún no está procesado
            if (enemigo.estaMuerto() && !enemigo.isProcesado()) {
                ObjetosXP xpObject = enemigo.sueltaObjetoXP();
                if (xpObject != null) {
                    ventanaJuego.addXPObject(xpObject);
                }
                enemigo.setProcesado(true);
                enemigosAEliminar.add(enemigo);
            }
        }

        // Eliminamos los que ya murieron y fueron procesados
        for (Enemigo enemigo : enemigosAEliminar) {
            enemigos.removeValue(enemigo, true);
        }
        enemigosAEliminar.clear();
    }

    public void dibujarSombrasEnemigos(ShapeRenderer shapeRenderer) {
        // Ordenar enemigos si quieres conservar el z-order
        enemigos.sort((e1, e2) -> Float.compare(e2.getY(), e1.getY()));

        for (Enemigo enemigo : enemigos) {
            if (enemigo instanceof EnemigoCulo || enemigo instanceof EnemigoPolla) {
                dibujarSombraEnemigo(enemigo, shapeRenderer);
            }
        }
    }

    public void renderizarEnemigos(SpriteBatch batch) {
        // Si el jugador no está vivo, mostramos los enemigos
        if (jugador.estaVivo()) { // booleano invertido
            return;
        }

        // Ordenamos enemigos para el z-order (Y descendente)
        enemigos.sort((e1, e2) -> Float.compare(e2.getY(), e1.getY()));

        for (Enemigo enemigo : enemigos) {
            enemigo.renderizar(batch);
        }
    }

    private void dibujarSombraEnemigo(Enemigo enemigo, ShapeRenderer shapeRenderer) {

        if (enemigo instanceof EnemigoCulo) {
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

            return;
        }
        if (enemigo instanceof EnemigoPolla polla) {
            float offset = polla.getMovimientoPolla().getCurrentOffset();

            float x = polla.getX();
            float y = polla.getY();
            float w = polla.getSprite().getWidth();
            float h = polla.getSprite().getHeight();
            float centerX = x + w / SHADOW_OFFSET;

            float baseShadowWidth = w * 0.9f;
            float baseShadowHeight = h * 0.28f;
            float baseShadowY = y - 2.5f;

            float maxZigzag = polla.getMovimientoPolla().getAmplitudZigzag();

            // Normalizamos offset en [-1, 1]
            // offset=+amplitud => normalizado=+1
            // offset=-amplitud => normalizado=-1
            float normalizado = offset / maxZigzag;
            if (normalizado > 1) normalizado = 1;
            if (normalizado < -1) normalizado = -1;

            // Escalas para arriba/abajo:
            // Arriba => 0.1f (casi imperceptible)
            // Abajo => 1.2f (más grande)
            float topScale = 0.2f;
            float bottomScale = 0.6f;

            float factor = bottomScale + (topScale - bottomScale) * ((normalizado + 1f) / 2f);

            float finalShadowWidth = baseShadowWidth * factor;
            float finalShadowHeight = baseShadowHeight * factor;
            float finalShadowX = centerX - finalShadowWidth / 2f;

            // Dibujamos la elipse
            shapeRenderer.setColor(0.3f, 0.3f, 0.1f, 0.5f);
            shapeRenderer.ellipse(finalShadowX, baseShadowY, finalShadowWidth, finalShadowHeight);
            return;
        }

        // Sombra DEFAULT para otros enemigos
        float x = enemigo.getX();
        float y = enemigo.getY();
        float w = enemigo.getSprite().getWidth();
        float h = enemigo.getSprite().getHeight();
        float centerX = x + w / 2f;
        float shadowWidth = w * 0.9f;
        float shadowHeight = h * 0.3f;
        float shadowX = centerX - (shadowWidth / 2f);
        float shadowY = y - 2f;

        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.5f);
        shapeRenderer.ellipse(shadowX, shadowY, shadowWidth, shadowHeight);
    }

    private void spawnEnemigo() { // Crea un nuevo enemigo y lo añade al array de fabricaEnemigos
        spawnCounter++;
        // Centro del jugador
        float playerX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float playerY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2;

        // Límites de la cámara
        float leftLimit = playerX - VentanaJuego.worldWidth / 2 - CORRECCION_SPAWN;
        float rightLimit = playerX + VentanaJuego.worldWidth / 2 - CORRECCION_SPAWN;
        float bottomLimit = playerY - VentanaJuego.worldHeight / 2 - CORRECCION_SPAWN;
        float topLimit = playerY + VentanaJuego.worldHeight / 2 - CORRECCION_SPAWN;

        float x = 0, y = 0;
        // Escogemos un borde al azar
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

    // fábrica de enemigos, para crear diferentes tipos según la cadena tipoEnemigo
    public static Enemigo fabricaEnemigos(String tipoEnemigo, float x, float y, Jugador jugador,
                                          float velocidad, OrthographicCamera camera) {
        switch (tipoEnemigo) {
            case "CULO":
                return new EnemigoCulo(x, y, jugador, velocidad * MULT_VELOCIDAD_CULO);
            case "REGLA":
                return new EnemigoRegla(x, y, jugador, velocidad * MULT_VELOCIDAD_REGLA, camera);
            case "POLLA":
                return new EnemigoPolla(x, y, jugador, velocidad * MULT_VELOCIDAD_POLLA);
            default:
                throw new IllegalArgumentException("Tipo de enemigo no reconocido: " + tipoEnemigo);
        }
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

    public void setTiposDeEnemigos(String[] nuevosTipos) { // permite cambiar el tipo de enemigos que se spawnean en tiempo de ejecución
        this.tiposDeEnemigos = nuevosTipos;
    }
}

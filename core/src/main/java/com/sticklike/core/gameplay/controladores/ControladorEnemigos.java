package com.sticklike.core.gameplay.controladores;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sticklike.core.entidades.enemigos.animacion.RenderSombrasEnemigos;
import com.sticklike.core.entidades.enemigos.bosses.BossPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoCulo;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoRegla;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoOro;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.pantallas.juego.VentanaJuego;

import static com.sticklike.core.utilidades.GestorConstantes.*;

public class ControladorEnemigos {

    private final VentanaJuego ventanaJuego;
    private final Array<Enemigo> enemigos;
    private final Jugador jugador;
    private final OrthographicCamera camera;

    private float intervaloDeAparicion;
    private float temporizadorDeAparicion;

    private int spawnCounter = 0; // para en un futuro controlar eventos

    private final Array<Enemigo> enemigosAEliminar = new Array<>();
    private String[] tiposDeEnemigos = TIPOS_ENEMIGOS;

    // Flag para asegurar que el Boss solo aparezca UNA vez
    private boolean bossSpawned = false;
    private final RenderSombrasEnemigos renderSombrasEnemigos;

    public ControladorEnemigos(Jugador jugador, float intervaloDeAparicion, VentanaJuego ventanaJuego) {
        this.enemigos = new Array<>();
        this.jugador = jugador;
        this.intervaloDeAparicion = intervaloDeAparicion;
        this.temporizadorDeAparicion = 0;
        this.ventanaJuego = ventanaJuego;
        this.camera = ventanaJuego.getOrtographicCamera();
        this.renderSombrasEnemigos = new RenderSombrasEnemigos();
    }

    public void actualizarSpawnEnemigos(float delta) {
        // Si el jugador está vivo, no actualizamos nada (lógica booleano invertida)
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

            if (enemigo.estaMuerto() && !enemigo.isProcesado()) {
                float random = MathUtils.random(0f, 100f);

                // 1º - Comprobamos si suelta Caca Dorada (2.5% de probabilidad)
                if (random < 2f) {
                    ventanaJuego.addXPObject(new ObjetoOro(enemigo.getX() + 10f, enemigo.getY() + 10f));
                }
                // 2º - Si NO suelta Caca Dorada, suelta XP normal (si el enemigo lo tiene)
                else {
                    ObjetosXP xpObject = enemigo.sueltaObjetoXP();
                    if (xpObject != null) {
                        ventanaJuego.addXPObject(xpObject);
                    }
                }

                enemigo.setProcesado(true);
                enemigosAEliminar.add(enemigo);
            }
        }

        // Eliminamos de la lista aquellos que ya murieron y se han procesado
        for (Enemigo enemigo : enemigosAEliminar) {
            enemigos.removeValue(enemigo, true);
        }
        enemigosAEliminar.clear();
    }

    public void renderizarEnemigos(SpriteBatch batch) {
        // Si el jugador está vivo, no se dibujan enemigos (booleano invertido)
        if (jugador.estaVivo()) {
            return;
        }

        // Ordenar para dibujar en orden (Z-order)
        enemigos.sort((e1, e2) -> Float.compare(e2.getY(), e1.getY()));

        for (Enemigo enemigo : enemigos) {
            enemigo.renderizar(batch);
        }
    }

    public void dibujarSombrasEnemigos(ShapeRenderer shapeRenderer) {
        if (jugador.estaVivo()) {
            return;
        }

        // Ordenamos según la Y para que el sombreado coincida con el orden de sprites
        enemigos.sort((e1, e2) -> Float.compare(e2.getY(), e1.getY()));

        renderSombrasEnemigos.dibujarSombrasEnemigos(shapeRenderer, enemigos);
    }

    private void spawnEnemigo() {
        spawnCounter++;

        // Centro del jugador
        float playerX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float playerY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2;

        // Límites de la cámara (margen extra para que aparezcan fuera de pantalla).
        float leftLimit = playerX - (VentanaJuego.worldWidth / 2f) - CORRECCION_SPAWN;
        float rightLimit = playerX + (VentanaJuego.worldWidth / 2f) - CORRECCION_SPAWN;
        float bottomLimit = playerY - (VentanaJuego.worldHeight / 2f) - CORRECCION_SPAWN;
        float topLimit = playerY + (VentanaJuego.worldHeight / 2f) - CORRECCION_SPAWN;

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

    public static Enemigo fabricaEnemigos(String tipoEnemigo, float x, float y, Jugador jugador, float velocidad, OrthographicCamera camera) {
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

    public void setTiposDeEnemigos(String[] nuevosTipos) {
        this.tiposDeEnemigos = nuevosTipos;
    }

    public Jugador getJugador() {
        return jugador;
    }
}

package com.sticklike.core.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;
import com.sticklike.core.ui.HUD;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.sticklike.core.utilidades.GestorDeAssets.*;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class RenderVentanaJuego {

    private ShapeRenderer shapeRenderer;
    private final int tamanyoCeldas;
    private Array<Borron> borronesMapa;
    private AtomicBoolean borronesListos = new AtomicBoolean(false);
    private static final float POISSON_MIN_DISTANCE = 500f;
    private static final int POISSON_K = 30;

    // Clase interna que gestiona los borrones del mapa
    private static class Borron { // todo --> (posteriormente se podría mover a una clase separada)
        Texture textura;
        float posX, posY;
        float scale;
        float rotation;

        public Borron(Texture textura, float posX, float posY, float scale, float rotation) {
            this.textura = textura;
            this.posX = posX;
            this.posY = posY;
            this.scale = scale;
            this.rotation = rotation;
        }
    }

    public RenderVentanaJuego(int tamanyoCeldas, Jugador jugador) {
        this.shapeRenderer = new ShapeRenderer();
        this.tamanyoCeldas = tamanyoCeldas;

        final Vector2 posicionInicialJugador = new Vector2(jugador.getSprite().getX(), jugador.getSprite().getY());

        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                generarBorronesRandom(CANTIDAD_BORRONES, posicionInicialJugador);
                long elapsed = System.currentTimeMillis() - startTime;
                // Si la generación ha tardado menos de 1 segundo, esperamos el tiempo restante.
                if (elapsed < 500) {
                    try {
                        Thread.sleep(500 - elapsed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                borronesListos.set(true);
            }
        }).start();
    }


    public void renderizarVentana(float delta, VentanaJuego1 ventanaJuego1, Jugador jugador, Array<ObjetosXP> objetosXP, ControladorEnemigos controladorEnemigos,
                                  Array<TextoFlotante> textosDanyo, HUD hud, SpriteBatch spriteBatch, OrthographicCamera camara) {

        ventanaJuego1.actualizarPosCamara();

        if (!borronesListos.get()) {
            spriteBatch.begin();
            spriteBatch.draw(loadingTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.end();
            return;
        }
        // 1) Limpiamos la pantalla
        if (jugador.getVidaJugador() <= 15) {
            if (jugador.getRenderJugador().isEnParpadeo()) {
                Gdx.gl.glClearColor(0.95f, 0.75f, 0.75f, 1);
            } else {
                Gdx.gl.glClearColor(0.92f, 0.8f, 0.8f, 1);
            }
        } else {
            if (jugador.getRenderJugador().isEnParpadeo()) {
                Gdx.gl.glClearColor(0.92f, 0.85f, 0.85f, 1);
            } else {
                Gdx.gl.glClearColor(0.89f, 0.89f, 0.89f, 1);
            }
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2) Ajustamos la matriz de proyección del SpriteBatch a la cámara actual
        spriteBatch.setProjectionMatrix(camara.combined);

        // 3) Dibujamos borrones
        spriteBatch.begin();
        for (Borron b : borronesMapa) {
            float drawWidth = b.textura.getWidth() * b.scale;
            float drawHeight = b.textura.getHeight() * b.scale;
            float originX = drawWidth / 2f;
            float originY = drawHeight / 2f;

            spriteBatch.draw(b.textura, b.posX, b.posY, originX, originY, drawWidth, drawHeight, 1f, 1f, b.rotation,
                0, 0, b.textura.getWidth(), b.textura.getHeight(), false, false);
        }
        spriteBatch.end();

        // 4) Renderizamos la cuadrícula
        renderizarLineasCuadricula(camara, jugador);

        // 5) Dibujar sombras de los enemigos
        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        controladorEnemigos.dibujarSombrasEnemigos(shapeRenderer);
        shapeRenderer.end();

        // 6) Dibujo de entidades (usando SpriteBatch)
        spriteBatch.begin();
        // Objetos XP
        for (ObjetosXP xp : objetosXP) xp.renderizarObjetoXP(spriteBatch);
        // Proyectiles
        jugador.getControladorProyectiles().renderizarProyectiles(spriteBatch);
        // Jugador
        jugador.aplicarRenderizadoAlJugador(spriteBatch, shapeRenderer);
        // Enemigos
        controladorEnemigos.renderizarEnemigos(spriteBatch);
        // Textos flotantes
        for (TextoFlotante txt : textosDanyo) txt.renderizarTextoFlotante(spriteBatch);
        spriteBatch.end();

        // 7) Renderizar HUD
        hud.renderizarHUD(delta);
    }

    public void renderizarLineasCuadricula(OrthographicCamera camera, Jugador jugador) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (jugador.getVidaJugador() <= 15) {
            shapeRenderer.setColor(0.9f, 0.64f, 0.7f, 1f);
        } else {
            shapeRenderer.setColor(0.64f, 0.80f, 0.9f, 1f);
        }

        float startX = camera.position.x - camera.viewportWidth / 2;
        float endX = camera.position.x + camera.viewportWidth / 2;
        float startY = camera.position.y - camera.viewportHeight / 2;
        float endY = camera.position.y + camera.viewportHeight / 2;

        // Líneas verticales
        for (float x = startX - (startX % tamanyoCeldas); x <= endX; x += tamanyoCeldas) {
            shapeRenderer.line(x, startY, x, endY);
        }
        // Líneas horizontales
        for (float y = startY - (startY % tamanyoCeldas); y <= endY; y += tamanyoCeldas) {
            shapeRenderer.line(startX, y, endX, y);
        }

        shapeRenderer.end();
    }


    private void generarBorronesRandom(int cantidad, Vector2 posicionInicialJugador) {
        borronesMapa = new Array<>();

        // Definimos la región de generación a partir de los límites del mapa
        float minX = MAP_MIN_X;
        float minY = MAP_MIN_Y;
        float maxX = MAP_MAX_X;
        float maxY = MAP_MAX_Y;

        // Generamos puntos candidatos con Poisson Disk Sampling
        Array<Vector2> candidateCenters = generatePoissonPoints(minX, minY, maxX, maxY, POISSON_MIN_DISTANCE, POISSON_K);
        candidateCenters.shuffle();

        // Iteramos sobre los puntos candidatos y asignamos datos aleatorios a cada borrón
        for (Vector2 center : candidateCenters) {
            if (center.dst(posicionInicialJugador) < 350f) {
                continue;
            }
            if (borronesMapa.size >= cantidad) break;

            // Seleccionar textura, escala y rotación aleatorias
            Texture tex = borrones.random();
            float scale = MathUtils.random(0.5f, 1f);
            float rotation = MathUtils.random(-85f, 85f);
            float drawWidth = tex.getWidth() * scale;
            float drawHeight = tex.getHeight() * scale;

            // Convertir el punto candidato (centro) en la posición (esquina inferior izquierda)
            float posX = center.x - drawWidth / 2f;
            float posY = center.y - drawHeight / 2f;

            // Verificar que el borrón quepa totalmente dentro de los límites del mapa
            if (posX < MAP_MIN_X || posX + drawWidth > MAP_MAX_X || posY < MAP_MIN_Y || posY + drawHeight > MAP_MAX_Y) {
                continue; // descartamos este candidato
            }

            // Comprobar que no se solape con otro borrón ya colocado
            if (seSolapaConOtroBorron(posX, posY, drawWidth, drawHeight)) continue;
            // Comprobar que no esté demasiado cerca de otro borrón con la misma textura
            if (estaDemasiadoCercaMismoBorron(tex, posX, posY, scale)) continue;

            // Si pasa todas las comprobaciones, se añade el borrón
            Borron nuevoBorron = new Borron(tex, posX, posY, scale, rotation);
            borronesMapa.add(nuevoBorron);
        }
    }

    /**
     * Implementación del algoritmo de Poisson Disk Sampling para generar puntos en una región rectangular.
     *
     * @param minX límite mínimo en X
     * @param minY límite mínimo en Y
     * @param maxX límite máximo en X
     * @param maxY límite máximo en Y
     * @param r distancia mínima entre puntos
     * @param k número máximo de intentos por punto activo
     * @return Array de puntos generados
     */
    private Array<Vector2> generatePoissonPoints(float minX, float minY, float maxX, float maxY, float r, int k) {
        Array<Vector2> points = new Array<>();
        Array<Vector2> activeList = new Array<>();

        float width = maxX - minX;
        float height = maxY - minY;
        float cellSize = r / (float)Math.sqrt(2);
        int gridCols = (int)Math.ceil(width / cellSize);
        int gridRows = (int)Math.ceil(height / cellSize);
        Vector2[][] grid = new Vector2[gridCols][gridRows];

        // Elegir un punto inicial aleatorio
        float initialX = MathUtils.random(minX, maxX);
        float initialY = MathUtils.random(minY, maxY);
        Vector2 initial = new Vector2(initialX, initialY);
        points.add(initial);
        activeList.add(initial);
        int gridX = (int)((initial.x - minX) / cellSize);
        int gridY = (int)((initial.y - minY) / cellSize);
        grid[gridX][gridY] = initial;

        while (activeList.size > 0) {
            int index = MathUtils.random(activeList.size - 1);
            Vector2 point = activeList.get(index);
            boolean found = false;
            for (int i = 0; i < k; i++) {
                float angle = MathUtils.random(0, MathUtils.PI2);
                float distance = MathUtils.random(r, 2 * r);
                float newX = point.x + distance * MathUtils.cos(angle);
                float newY = point.y + distance * MathUtils.sin(angle);
                if (newX < minX || newX >= maxX || newY < minY || newY >= maxY) continue;
                Vector2 newPoint = new Vector2(newX, newY);
                int newGridX = (int)((newX - minX) / cellSize);
                int newGridY = (int)((newY - minY) / cellSize);
                boolean ok = true;
                // Comprobar vecinos en el grid
                for (int ix = Math.max(0, newGridX - 2); ix <= Math.min(gridCols - 1, newGridX + 2); ix++) {
                    for (int iy = Math.max(0, newGridY - 2); iy <= Math.min(gridRows - 1, newGridY + 2); iy++) {
                        if (grid[ix][iy] != null) {
                            if (newPoint.dst2(grid[ix][iy]) < r * r) {
                                ok = false;
                                break;
                            }
                        }
                    }
                    if (!ok) break;
                }
                if (ok) {
                    points.add(newPoint);
                    activeList.add(newPoint);
                    grid[newGridX][newGridY] = newPoint;
                    found = true;
                }
            }
            if (!found) {
                activeList.removeIndex(index);
            }
        }
        return points;
    }


    private boolean seSolapaConOtroBorron(float x, float y, float width, float height) {
        float leftA = x;
        float rightA = x + width;
        float bottomA = y;
        float topA = y + height;

        for (Borron b : borronesMapa) {
            float bw = b.textura.getWidth() * b.scale;
            float bh = b.textura.getHeight() * b.scale;

            float leftB = b.posX;
            float rightB = b.posX + bw;
            float bottomB = b.posY;
            float topB = b.posY + bh;

            // Si no hay separación entre los rectángulos, hay solapamiento
            if (!(rightA < leftB || leftA > rightB || topA < bottomB || bottomA > topB)) {
                return true;
            }
        }
        return false;
    }

    private boolean estaDemasiadoCercaMismoBorron(Texture tex, float x, float y, float scale) {
        // Centro del borrón candidato
        float cx = x + tex.getWidth() * scale / 2f;
        float cy = y + tex.getHeight() * scale / 2f;

        for (Borron b : borronesMapa) {
            if (b.textura == tex) {
                // Centro del borrón existente
                float bx = b.posX + b.textura.getWidth() * b.scale / 2f;
                float by = b.posY + b.textura.getHeight() * b.scale / 2f;

                float distX = bx - cx;
                float distY = by - cy;
                float sqDist = distX * distX + distY * distY;

                if (sqDist < (MIN_DIST_SAME_TEXTURE * MIN_DIST_SAME_TEXTURE)) {
                    return true; // Demasiado cerca
                }
            }
        }
        return false;
    }

    public boolean isLoadingComplete() {
        return borronesListos.get();
    }


    public void dispose() {
        shapeRenderer.dispose();
    }
}

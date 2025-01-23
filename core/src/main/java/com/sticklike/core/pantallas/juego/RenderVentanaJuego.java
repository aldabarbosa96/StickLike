package com.sticklike.core.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.gameplay.managers.ControladorEnemigos;
import com.sticklike.core.ui.HUD;
import com.sticklike.core.utilidades.GestorDeAssets;

/**
 * Se encarga de dibujar la ventana principal del juego (mapa) y sus elementos
 */
public class RenderVentanaJuego {

    private ShapeRenderer shapeRenderer;
    private final int tamanyoCeldas;
    private static final float MIN_DIST_SAME_TEXTURE = 1000f;

    // Máximo de reintentos para colocar un borrón sin conflicto
    private static final int MAX_ATTEMPTS = 5;
    private static final int CANTIDAD_BORRONES = 1000;

    // Límites "mapa" para generar borrones
    private static final float MAP_MIN_X = -10000;
    private static final float MAP_MAX_X =  10000;
    private static final float MAP_MIN_Y = -10000;
    private static final float MAP_MAX_Y =  10000;

    // ================================
    //  Clase interna: datos borrón
    // ================================
    private static class Borron {
        Texture textura; // Textura del borrón
        float x, y;      // Posición en el mapa
        float scale;     // Escala (tamaño relativo)
        float rotation;  // Rotación en grados

        public Borron(Texture textura, float x, float y, float scale, float rotation) {
            this.textura = textura;
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.rotation = rotation;
        }
    }

    private Array<Borron> borrones;

    /**
     * Constructor:
     * Crea el ShapeRenderer para la cuadrícula.
     * Genera borrones aleatorios sin que se solapen y sin que la misma textura esté demasiado cerca
     *
     * @param tamanyoCeldas Tamaño de las celdas de la cuadrícula.
     */
    public RenderVentanaJuego(int tamanyoCeldas) {
        this.shapeRenderer = new ShapeRenderer();
        this.tamanyoCeldas = tamanyoCeldas;

        // Genera la lista de borrones aleatorios
        generarBorronesRandom(CANTIDAD_BORRONES);
    }

    /**
     * Genera y almacena borrones con posición, escala, rotación y textura de borrón aleatorias, evitando superposición
     * y evitando proximidad excesiva si la textura es la misma
     */
    private void generarBorronesRandom(int cantidad) {
        borrones = new Array<>();

        for (int i = 0; i < cantidad; i++) {
            int attempts = 0;
            Borron nuevoBorron = null;

            while (attempts < MAX_ATTEMPTS) {
                attempts++;

                // 1) Seleccionar textura aleatoria
                Texture tex = GestorDeAssets.borrones.random();

                // 2) Escala y rotación aleatorias
                float scale = MathUtils.random(0.12f, 0.3f);
                float rotation = MathUtils.random(0f, 360f);

                // 3) Tamaño escalado
                float borrWidth  = tex.getWidth()  * scale;
                float borrHeight = tex.getHeight() * scale;

                // 4) Posición aleatoria
                float x = MathUtils.random(MAP_MIN_X, MAP_MAX_X - borrWidth);
                float y = MathUtils.random(MAP_MIN_Y, MAP_MAX_Y - borrHeight);

                // 5) Comprobar solapamiento
                boolean solapado = seSolapaConOtroBorron(x, y, borrWidth, borrHeight);

                // 6) Comprobar distancia mínima con la misma textura
                boolean demasiadoCerca = estaDemasiadoCercaMismoBorrón(tex, x, y, scale);

                if (!solapado && !demasiadoCerca) {
                    nuevoBorron = new Borron(tex, x, y, scale, rotation);
                    break;
                }
            }

            if (nuevoBorron == null) {
                Texture tex2 = GestorDeAssets.borrones.random();
                float scale2 = MathUtils.random(0.12f, 0.3f);
                float rotation2 = MathUtils.random(0f, 360f);
                float w2  = tex2.getWidth()  * scale2;
                float h2  = tex2.getHeight() * scale2;
                float x2 = MathUtils.random(MAP_MIN_X, MAP_MAX_X - w2);
                float y2 = MathUtils.random(MAP_MIN_Y, MAP_MAX_Y - h2);

                nuevoBorron = new Borron(tex2, x2, y2, scale2, rotation2);
            }

            borrones.add(nuevoBorron);
        }
    }

    /**
     * Comprueba si un rectángulo (x, y, width, height) se solapa con algún borrón ya existente en la lista (usando bounding box)
     */
    private boolean seSolapaConOtroBorron(float x, float y, float width, float height) {
        float leftA   = x;
        float rightA  = x + width;
        float bottomA = y;
        float topA    = y + height;

        for (Borron b : borrones) {
            float bw = b.textura.getWidth() * b.scale;
            float bh = b.textura.getHeight() * b.scale;

            float leftB   = b.x;
            float rightB  = b.x + bw;
            float bottomB = b.y;
            float topB    = b.y + bh;

            // Comprobación de overlap entre dos rectángulos A y B
            if (!(rightA < leftB || leftA > rightB || topA < bottomB || bottomA > topB)) {
                // se solapan
                return true;
            }
        }
        return false;
    }

    /**
     * Comprueba si ya existe un borrón con la misma textura a menos de MIN_DIST_SAME_TEXTURE, considerando la escala
     * y posición (aprox. centros)
     */
    private boolean estaDemasiadoCercaMismoBorrón(Texture tex, float x, float y, float scale) {
        // Centro del borrón candidate
        float cx  = x + tex.getWidth()  * scale / 2f;
        float cy  = y + tex.getHeight() * scale / 2f;

        for (Borron b : borrones) {
            if (b.textura == tex) {
                // Centro del borrón existente
                float bx  = b.x + b.textura.getWidth()  * b.scale / 2f;
                float by  = b.y + b.textura.getHeight() * b.scale / 2f;

                float distX = bx - cx;
                float distY = by - cy;
                float sqDist = distX * distX + distY * distY;

                if (sqDist < (MIN_DIST_SAME_TEXTURE * MIN_DIST_SAME_TEXTURE)) {
                    return true; // Está demasiado cerca
                }
            }
        }
        return false;
    }

    /**
     * Render principal de la ventana de juego.
     */
    public void renderizarVentana(float delta, VentanaJuego ventanaJuego, Jugador jugador, Array<ObjetosXP> objetosXP, ControladorEnemigos controladorEnemigos,
                                  Array<TextoFlotante> textosDanyo, HUD hud, SpriteBatch spriteBatch, OrthographicCamera camara) {
        // 1) Limpiamos la pantalla con un color gris claro
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2) Ajustamos la matriz de proyección del SpriteBatch a la cámara actual
        spriteBatch.setProjectionMatrix(camara.combined);

        // 3) Dibujar los borrones
        spriteBatch.begin();
        for (Borron b : borrones) {
            float drawWidth  = b.textura.getWidth()  * b.scale;
            float drawHeight = b.textura.getHeight() * b.scale;

            float originX = drawWidth  / 2f;
            float originY = drawHeight / 2f;

            spriteBatch.draw(b.textura, b.x, b.y, originX, originY, drawWidth, drawHeight, 1f, 1f,
                b.rotation, 0, 0, b.textura.getWidth(), b.textura.getHeight(), false, false);
        }
        spriteBatch.end();

        // 4) Renderizar la cuadrícula
        ventanaJuego.actualizarPosCamara();
        this.renderizarLineasCuadricula(camara);

        // 5) Dibujo de entidades (jugador, enemigos, proyectiles, etc.)
        spriteBatch.begin();
        jugador.aplicarRenderizadoAlJugador(spriteBatch);
        jugador.getControladorProyectiles().renderizarProyectiles(spriteBatch);


        for (ObjetosXP xp : objetosXP) {
            xp.renderizarObjetoXP(spriteBatch);
        }
        controladorEnemigos.renderizarEnemigos(spriteBatch);

        for (TextoFlotante txt : textosDanyo) {
            txt.renderizarTextoFlotante(spriteBatch);
        }
        spriteBatch.end();

        // 6) Renderizar HUD
        hud.renderizarHUD(delta);
    }

    /**
     * Renderiza las líneas de la cuadrícula en base a la posición de la cámara.
     */
    public void renderizarLineasCuadricula(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.64f, 0.80f, 0.86f, 1);

        float startX = camera.position.x - camera.viewportWidth / 2;
        float endX   = camera.position.x + camera.viewportWidth / 2;
        float startY = camera.position.y - camera.viewportHeight / 2;
        float endY   = camera.position.y + camera.viewportHeight / 2;

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

    /**
     * Libera recursos del renderer.
     */
    public void dispose() {
        shapeRenderer.dispose();
    }
}

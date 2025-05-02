package com.sticklike.core.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.sticklike.core.entidades.renderizado.TrailRender;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;
import com.sticklike.core.ui.HUD;
import com.sticklike.core.ui.Mensajes;
import com.sticklike.core.utilidades.PoissonPoints;

import java.nio.IntBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.borrones;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class RenderVentanaJuego1 {

    private final ShapeRenderer shapeRenderer;
    private final VentanaLoading ventanaLoading;
    private final int tamanyoCeldas;

    // Para prerenderizar BORRONES
    private Array<Borron> borronesMapa;
    private final AtomicBoolean borronesListos = new AtomicBoolean(false);
    private final FrameBuffer borronesFbo;
    private final TextureRegion borronesRegion;
    private volatile boolean borronesReady = false;
    private static final float POISSON_MIN_DISTANCE = 750f;
    private static final int POISSON_K = 30;

    // Cámara para prerender FBO
    private final OrthographicCamera worldCam;
    private final int mapPixelWidth, mapPixelHeight;

    // Control de flash
    private boolean flashVidaActivo = false;
    private float flashVidaTimer = 0f;
    private static final float FLASH_VIDA_DURATION = 0.25f;

    // Hilo background
    private final ExecutorService executorService;
    private final long startLoadTime;
    private static final long MIN_LOAD_DURATION_MS = 1750;

    // Referencias externas
    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camara;

    // Límites mundo
    private final float mapMinX = MAP_MIN_X, mapMinY = MAP_MIN_Y, mapMaxX = MAP_MAX_X, mapMaxY = MAP_MAX_Y;

    private static class Borron {
        final Texture textura;
        final float x, y, s, r;

        Borron(Texture t, float x, float y, float s, float r) {
            textura = t;
            this.x = x;
            this.y = y;
            this.s = s;
            this.r = r;
        }
    }

    public RenderVentanaJuego1(int tamanyoCeldas, Jugador jugador, SpriteBatch spriteBatch, OrthographicCamera camara) {
        this.tamanyoCeldas = tamanyoCeldas;
        this.shapeRenderer = new ShapeRenderer();
        this.ventanaLoading = new VentanaLoading();
        this.spriteBatch = spriteBatch;
        this.camara = camara;

        // dimensiones mapa en píxeles
        mapPixelWidth = Math.round(mapMaxX - mapMinX);
        mapPixelHeight = Math.round(mapMaxY - mapMinY);

        // Cámara global
        worldCam = new OrthographicCamera(mapMaxX - mapMinX, mapMaxY - mapMinY);
        worldCam.position.set(mapMinX + (mapMaxX - mapMinX) / 2f, mapMinY + (mapMaxY - mapMinY) / 2f, 0);
        worldCam.update();

        // FBO solo para BORRONES
        IntBuffer buffer = BufferUtils.newIntBuffer(1);
        Gdx.gl.glGetIntegerv(GL20.GL_MAX_RENDERBUFFER_SIZE, buffer);
        int maxFboSize = buffer.get(0);
        Gdx.app.log("max_fbo_size","Máximo tamaño permitido para FrameBuffer: " + maxFboSize);

        // Limita el tamaño real
        int safeWidth = Math.min(mapPixelWidth, maxFboSize);
        int safeHeight = Math.min(mapPixelHeight, maxFboSize);

        // Crea el FBO con dimensiones seguras
        borronesFbo = new FrameBuffer(Pixmap.Format.RGBA8888, safeWidth, safeHeight, true);
        borronesRegion = new TextureRegion(borronesFbo.getColorBufferTexture());
        Texture borronesTex = borronesFbo.getColorBufferTexture();
        borronesTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        borronesRegion.flip(false, true);
        executorService = Executors.newSingleThreadExecutor();
        startLoadTime = System.currentTimeMillis();
        Vector2 posInit = new Vector2(jugador.getSprite().getX(), jugador.getSprite().getY());

        executorService.submit(() -> {
            generarBorronesRandom(CANTIDAD_BORRONES, posInit);
            borronesListos.set(true);
        });
    }

    public void renderizarVentana(float delta, VentanaJuego1 ventanaJuego1, Jugador jugador, Array<ObjetosXP> objetosXP, ControladorEnemigos ctrlEnemigos, Array<TextoFlotante> textos, HUD hud) {
        ventanaJuego1.actualizarPosCamara();

        // 1) Prerender BORRONES una sola vez
        long elapsed = System.currentTimeMillis() - startLoadTime;

        if (borronesListos.get() && !borronesReady && elapsed >= MIN_LOAD_DURATION_MS) {
            prerenderBorrones();
        }

        if (!borronesReady) {
            ventanaLoading.render(delta);
            return;
        }

        // 2) Clear + flash color
        actualizarColorDeFondo(jugador, delta);

        // 3) Dibujar GRID **cada frame** con ShapeRenderer
        renderizarLineasCuadricula(camara, jugador);

        // 4) Dibujar BORRONES prerenderizados
        // Calculamos origen en floats
        float camXf = camara.position.x - camara.viewportWidth / 2f;
        float camYf = camara.position.y - camara.viewportHeight / 2f;
        // Ancho/alto de región en enteros
        int regionW = Math.round(camara.viewportWidth);
        int regionH = Math.round(camara.viewportHeight);

        // “Snap” de la cámara al píxel de la textura (coordenadas de lectura)
        int iCamX = Math.round(camXf - mapMinX);
        int iCamY = Math.round(camYf - mapMinY);

        // Clamp para no salirse del FBO
        int regionX = MathUtils.clamp(iCamX, 0, mapPixelWidth - regionW);
        int regionY = MathUtils.clamp(mapPixelHeight - iCamY - regionH, 0, mapPixelHeight - regionH);

        // Aplicamos la región
        borronesRegion.setRegion(regionX, regionY, regionW, regionH);

        // Dibujamos “snapeado” a enteros para que srcRegion y dstRect casen píxel a píxel
        spriteBatch.setProjectionMatrix(camara.combined);
        spriteBatch.begin();
        spriteBatch.draw(borronesRegion, Math.round(camXf), Math.round(camYf), camara.viewportWidth, camara.viewportHeight);
        spriteBatch.end();


        // 5) Dinámico: proyectiles fondo, trails, sombras, entidades, HUD…
        spriteBatch.setProjectionMatrix(camara.combined);
        // todo --> si en un futuro hay más proyectiles que usen el renderizado en el fondo se debería gestionar desde aquí el begin/end (actualmente se gestiona solamente para LluviaMocos)
        jugador.getControladorProyectiles().renderizarProyectilesFondo(spriteBatch);
        TrailRender.get().flush(camara.combined);

        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        ctrlEnemigos.dibujarSombrasEnemigos(shapeRenderer);
        shapeRenderer.end();

        spriteBatch.begin();
        for (ObjetosXP xp : objetosXP) xp.renderizarObjetoXP(spriteBatch);
        jugador.aplicarRenderizadoAlJugador(spriteBatch, shapeRenderer);
        ctrlEnemigos.renderizarEnemigos(spriteBatch);
        jugador.getControladorProyectiles().renderizarProyectiles(spriteBatch);
        for (TextoFlotante t : textos) t.renderizarTextoFlotante(spriteBatch);
        spriteBatch.end();

        Mensajes.getInstance().update();
        Mensajes.getInstance().draw(camara);
        hud.renderizarHUD(delta);
    }

    private void prerenderBorrones() {
        borronesFbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(worldCam.combined);
        spriteBatch.begin();
        spriteBatch.setColor(1, 1, 1, 0.6f);
        for (Borron b : borronesMapa) {
            float w = b.textura.getWidth() * b.s;
            float h = b.textura.getHeight() * b.s;
            spriteBatch.draw(b.textura, b.x, b.y, w / 2f, h / 2f, w, h, 1, 1, -b.r, 0, 0, b.textura.getWidth(), b.textura.getHeight(), false, true);
        }
        spriteBatch.setColor(1, 1, 1, 1);
        spriteBatch.end();
        borronesFbo.end();

        borronesReady = true;
    }

    private void renderizarLineasCuadricula(OrthographicCamera camera, Jugador jugador) {
        // Si el jugador está en parpadeo o en flash de vida, no dibujamos la grid
        if (jugador.getRenderJugador().isEnParpadeo() || flashVidaActivo) {
            return;
        }

        // 1) Ajustamos la proyección al viewport de la cámara
        shapeRenderer.setProjectionMatrix(camera.combined);
        // 2) Grosor de línea
        Gdx.gl.glLineWidth(2f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // 3) Color según vida del jugador
        if (jugador.getVidaJugador() <= 15) {
            shapeRenderer.setColor(0.9f, 0.64f, 0.7f, 1f);
        } else {
            shapeRenderer.setColor(0.64f, 0.80f, 0.9f, 1f);
        }

        // 4) Calculamos límites del área visible
        float startX = camera.position.x - camera.viewportWidth / 2f;
        float endX = camera.position.x + camera.viewportWidth / 2f;
        float startY = camera.position.y - camera.viewportHeight / 2f;
        float endY = camera.position.y + camera.viewportHeight / 2f;

        // 5) Líneas verticales, alineadas a la celda más cercana
        float x0 = startX - (startX % tamanyoCeldas);
        for (float x = x0; x <= endX; x += tamanyoCeldas) {
            shapeRenderer.line(x, startY, x, endY);
        }
        // 6) Líneas horizontales
        float y0 = startY - (startY % tamanyoCeldas);
        for (float y = y0; y <= endY; y += tamanyoCeldas) {
            shapeRenderer.line(startX, y, endX, y);
        }

        // 7) Cerramos y restauramos ancho de línea
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1f);
    }


    private void actualizarColorDeFondo(Jugador j, float dt) {
        if (flashVidaActivo) {
            Gdx.gl.glClearColor(0.8f, 0.88f, 0.8f, 1f);
            flashVidaTimer -= dt;
            if (flashVidaTimer <= 0) flashVidaActivo = false;
        } else {
            boolean low = j.getVidaJugador() <= 15;
            boolean blink = j.getRenderJugador().isEnParpadeo();
            float r = low ? (blink ? 1f : 0.93f) : (blink ? 1f : 0.91f);
            float g = low ? (blink ? 0.55f : 0.80f) : (blink ? 0.75f : 0.91f);
            float b = g;
            Gdx.gl.glClearColor(r, g, b, 1f);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void generarBorronesRandom(int cantidad, Vector2 posInit) {
        borronesMapa = new Array<>();
        Array<Vector2> pts = PoissonPoints.getInstance().generatePoissonPoints(mapMinX, mapMinY, mapMaxX, mapMaxY, POISSON_MIN_DISTANCE, POISSON_K);
        pts.shuffle();
        for (Vector2 c : pts) {
            if (borronesMapa.size >= cantidad) break;
            if (c.dst(posInit) < 350f) continue;
            Texture t = borrones.random();
            float s = MathUtils.random(0.75f, 1.35f), r = MathUtils.random(-45f, 45f);
            float w = t.getWidth() * s, h = t.getHeight() * s;
            float x = c.x - w / 2f, y = c.y - h / 2f;
            if (x < mapMinX || x + w > mapMaxX || y < mapMinY || y + h > mapMaxY) continue;
            if (seSolapa(x, y, w, h) || estaCerca(t, x, y, s)) continue;
            borronesMapa.add(new Borron(t, x, y, s, r));
        }
    }

    private boolean seSolapa(float x, float y, float w, float h) {
        for (Borron b : borronesMapa) {
            float bw = b.textura.getWidth() * b.s, bh = b.textura.getHeight() * b.s;
            if (!(x + w < b.x || x > b.x + bw || y + h < b.y || y > b.y + bh)) return true;
        }
        return false;
    }

    private boolean estaCerca(Texture tex, float x, float y, float s) {
        float cx = x + tex.getWidth() * s / 2f, cy = y + tex.getHeight() * s / 2f;
        for (Borron b : borronesMapa)
            if (b.textura == tex) {
                float bx = b.x + b.textura.getWidth() * b.s / 2f, by = b.y + b.textura.getHeight() * b.s / 2f;
                if ((bx - cx) * (bx - cx) + (by - cy) * (by - cy) < MIN_DIST_SAME_TEXTURE * MIN_DIST_SAME_TEXTURE)
                    return true;
            }
        return false;
    }

    public boolean isLoadingComplete() {
        return borronesReady && System.currentTimeMillis() - startLoadTime >= MIN_LOAD_DURATION_MS;
    }

    public void triggerLifeFlash() {
        flashVidaActivo = true;
        flashVidaTimer = FLASH_VIDA_DURATION;
    }

    public void dispose() {
        shapeRenderer.dispose();
        ventanaLoading.dispose();
        borronesFbo.dispose();
        executorService.shutdownNow();
    }
}

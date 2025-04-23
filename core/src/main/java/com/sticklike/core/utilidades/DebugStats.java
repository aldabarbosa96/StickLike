package com.sticklike.core.utilidades;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.entidades.objetos.texto.FontManager;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

public class DebugStats {
    private BitmapFont font;
    private GlyphLayout layout; // Se declara una única instancia a nivel de clase.
    private boolean enabled;
    private float tiempoEjecucion;
    private float acumuladorDelta;
    private int contadorFrames;
    private float deltaPromedio;
    private float frameTimeMs;
    private long accumulatedGCCount;
    private long lastGCTime;
    private long previousGCCount;
    private long previousGCTime;

    public DebugStats() {
        font = FontManager.getDebugFont();
        font.getData().setScale(0.68f);
        font.getData().markupEnabled = true;
        font.setColor(Color.BLACK);
        layout = new GlyphLayout();
        enabled = true;
        tiempoEjecucion = 0;
        acumuladorDelta = 0;
        contadorFrames = 0;
        deltaPromedio = 0;
        frameTimeMs = 0;

        accumulatedGCCount = 0;
        previousGCCount = 0;
        previousGCTime = 0;
        updateGCStats();
    }

    private void updateGCStats() {
        // En Android, ManagementFactory no está disponible, así que saltamos este proceso.
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            return;
        }

        long currentCount = 0;
        long currentTime = 0;
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean bean : gcBeans) {
            long count = bean.getCollectionCount();
            long time = bean.getCollectionTime();
            currentCount += (count < 0 ? 0 : count);
            currentTime += (time < 0 ? 0 : time);
        }
        long deltaCount = currentCount - previousGCCount;
        long deltaTime = currentTime - previousGCTime;

        accumulatedGCCount += deltaCount;
        if (deltaTime > 0) {
            lastGCTime = deltaTime;
        }

        previousGCCount = currentCount;
        previousGCTime = currentTime;
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            enabled = !enabled;
        }
        float delta = Gdx.graphics.getDeltaTime();
        tiempoEjecucion += delta;
        acumuladorDelta += delta;
        contadorFrames++;
        frameTimeMs = delta * 1000f;

        if (acumuladorDelta >= 1f) {
            deltaPromedio = acumuladorDelta / contadorFrames;
            acumuladorDelta = 0;
            contadorFrames = 0;

            // Actualizamos las estadísticas del GC cada segundo
            updateGCStats();
        }
    }

    public void render(SpriteBatch batch, float virtualHeight) {
        if (!enabled) return;

        int fps = Gdx.graphics.getFramesPerSecond();
        float delta = Gdx.graphics.getDeltaTime();
        float javaHeapMB = Gdx.app.getJavaHeap() / (1024f * 1024f);
        float nativeHeapMB = Gdx.app.getNativeHeap() / (1024f * 1024f);
        float memoriaLibreMB = Runtime.getRuntime().freeMemory() / (1024f * 1024f);
        float memoriaTotalMB = Runtime.getRuntime().totalMemory() / (1024f * 1024f);

        float margin = 12f;
        float x = margin;
        float y = virtualHeight - margin;

        String textoDebug = "[GREEN]FPS: " + fps + "[]\n\n" + "[BLACK]Delta: " + String.format("%.3f", delta) + " s\n" + "Frame Time: " + String.format("%.2f", frameTimeMs) + " ms\n" + "JavaH: " + String.format("%.2f", javaHeapMB) + " MB\n" + "NativeH: " + String.format("%.2f", nativeHeapMB) + " MB\n" + "Runtime: " + String.format("%.1f", tiempoEjecucion) + " s\n" + "Avg. Delta: " + String.format("%.3f", deltaPromedio) + " s\n" + "Free RAM: " + String.format("%.2f", memoriaLibreMB) + " MB\n" + "Total RAM: " + String.format("%.2f", memoriaTotalMB) + " MB\n" + "GC Count: " + accumulatedGCCount + "\n" + "GC Time (last): " + lastGCTime + " ms";

        // Actualizamos el layout con el nuevo texto, sin crear una nueva instancia
        layout.setText(font, textoDebug);
        font.draw(batch, layout, x, y);
    }

    public void dispose() {
        font.dispose();
    }
}

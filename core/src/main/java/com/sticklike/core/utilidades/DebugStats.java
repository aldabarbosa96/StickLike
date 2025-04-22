package com.sticklike.core.utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.entidades.objetos.texto.FontManager;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DebugStats {
    private BitmapFont font;
    private GlyphLayout layout;
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
    private float fps1LowVisible;

    private final List<Float> frameTimesMs;

    public DebugStats() {
        font = FontManager.getDebugFont();
        font.getData().setScale(0.675f);
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

        frameTimesMs = new ArrayList<>();
        fps1LowVisible = 0f;
    }

    private void updateGCStats() {
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

        // Tiempo de este frame
        frameTimeMs = delta * 1000f;
        frameTimesMs.add(frameTimeMs);

        // Cada segundo, calcula FPS 1% low y reinicia contadores
        if (acumuladorDelta >= 1f) {
            deltaPromedio = acumuladorDelta / contadorFrames;
            computeOnePercentLow();

            acumuladorDelta = 0;
            contadorFrames = 0;
            frameTimesMs.clear();

            updateGCStats();
        }
    }

    // Calcula el percentil 99 de frame-times y lo convierte a FPS
    private void computeOnePercentLow() {
        if (frameTimesMs.isEmpty()) {
            fps1LowVisible = 0f;
            return;
        }
        Collections.sort(frameTimesMs);
        int idx = Math.min(frameTimesMs.size() - 1, (int)(0.99f * frameTimesMs.size()));
        float p99ms = frameTimesMs.get(idx);
        fps1LowVisible = 1000f / p99ms;
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

        String textoDebug =
            "[BLUE]FPS (current)  " + fps + "\n"
                + "FPS (1% low)  " + String.format("%.1f", fps1LowVisible) + "[]\n\n"
                + "[BLACK]Delta: " + String.format("%.3f", delta) + " s\n"
                + "Frame Time: " + String.format("%.2f", frameTimeMs) + " ms\n"
                + "JavaH: " + String.format("%.2f", javaHeapMB) + " MB\n"
                + "NativeH: " + String.format("%.2f", nativeHeapMB) + " MB\n"
                + "Runtime: " + String.format("%.1f", tiempoEjecucion) + " s\n"
                + "Avg. Delta: " + String.format("%.3f", deltaPromedio) + " s\n"
                + "Free RAM: " + String.format("%.2f", memoriaLibreMB) + " MB\n"
                + "Total RAM: " + String.format("%.2f", memoriaTotalMB) + " MB\n"
                + "GC Count: " + accumulatedGCCount + "\n"
                + "GC Time (last): " + lastGCTime + " ms";

        layout.setText(font, textoDebug);
        font.draw(batch, layout, x, y);
    }

    public void dispose() {
        font.dispose();
        layout = null;
    }
}

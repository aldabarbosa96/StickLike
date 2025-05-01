package com.sticklike.core.utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.sticklike.core.MainGame;
import com.sticklike.core.entidades.objetos.texto.FontManager;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Overlay de métricas de rendimiento (debug). TAB para mostrar/ocultar. */
public class DebugStats {

    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private boolean enabled = true;

    private static final DecimalFormat F1 = new DecimalFormat("0.0");
    private static final DecimalFormat F2 = new DecimalFormat("0.00");
    private static final DecimalFormat F3 = new DecimalFormat("0.000");
    private final StringBuilder sb = new StringBuilder(256);

    private float tiempoEjecucion;
    private float acumuladorDelta;
    private int   contadorFrames;
    private float deltaPromedio;
    private float frameTimeMs;
    private final List<Float> frameTimesMs = new ArrayList<>();
    private float fps1LowVisible;

    private long accumulatedGCCount;
    private long lastGCTime;
    private long previousGCCount;
    private long previousGCTime;

    private final GLProfiler gl = MainGame.getGlProfiler();   // instancia global
    private int drawCalls, shaderSwitches, texBindings, vertices;

    public DebugStats() {
        font = FontManager.getDebugFont();
        font.getData().setScale(0.675f);
        font.getData().markupEnabled = true;
        font.setColor(Color.BLACK);

        updateGCStats();   // inicializar contadores GC
    }


    public void update() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) enabled = !enabled;

        float delta = Gdx.graphics.getDeltaTime();
        tiempoEjecucion += delta;
        acumuladorDelta += delta;
        contadorFrames ++;

        frameTimeMs = delta * 1000f;
        frameTimesMs.add(frameTimeMs);

        if (gl != null && gl.isEnabled()) {
            drawCalls      = gl.getDrawCalls();
            shaderSwitches = gl.getShaderSwitches();
            texBindings    = gl.getTextureBindings();
            vertices       = (int) gl.getVertexCount().total;
            gl.reset();                         // MUY IMPORTANTE
        }

        // ---------- estadísticas / 1 seg ----------
        if (acumuladorDelta >= 1f) {
            deltaPromedio = acumuladorDelta / contadorFrames;
            computeOnePercentLow();

            acumuladorDelta = 0;
            contadorFrames  = 0;
            frameTimesMs.clear();

            updateGCStats();
        }
    }

    public void render(SpriteBatch batch, float virtualHeight) {
        if (!enabled) return;

        // Recopilación de datos
        int fps          = Gdx.graphics.getFramesPerSecond();
        float delta      = Gdx.graphics.getDeltaTime();
        float frameMs    = frameTimeMs;  // calculado en update()
        float javaHeapMB = Gdx.app.getJavaHeap() / (1024f * 1024f);
        float nativeHeapMB = Gdx.app.getNativeHeap() / (1024f * 1024f);
        float memFreeMB  = Runtime.getRuntime().freeMemory() / (1024f * 1024f);
        float memTotalMB = Runtime.getRuntime().totalMemory() / (1024f * 1024f);

        // Coordenadas de dibujo
        float x = 12f;
        float y = virtualHeight - 12f;

        // Construcción eficiente del texto
        sb.setLength(0);
        sb.append("[BLUE]FPS (current)  ").append(fps).append('\n')
            .append("FPS (1% low)  ").append(F1.format(fps1LowVisible)).append("[]\n\n")

            .append("[BLACK]Delta: ").append(F3.format(delta)).append(" s\n")
            .append("Frame Time: ").append(F2.format(frameMs)).append(" ms\n\n")

            .append("JavaH: ").append(F2.format(javaHeapMB)).append(" MB\n")
            .append("NativeH: ").append(F2.format(nativeHeapMB)).append(" MB\n")
            .append("Free RAM: ").append(F2.format(memFreeMB)).append(" MB\n")
            .append("Total RAM: ").append(F2.format(memTotalMB)).append(" MB\n\n")

            .append("Runtime: ").append(F1.format(tiempoEjecucion)).append(" s\n")
            .append("Avg delta: ").append(F3.format(deltaPromedio)).append(" s\n\n")

            .append("GC Count: ").append(accumulatedGCCount).append('\n')
            .append("GC Time (last): ").append(lastGCTime).append(" ms\n\n")

            .append("[PURPLE]DrawCalls: ").append(drawCalls).append('\n')
            .append("ShaderSwitches: ").append(shaderSwitches).append('\n')
            .append("TexBindings: ").append(texBindings).append('\n')
            .append("Vertices: ").append(vertices);

        // Dibujo del texto
        layout.setText(font, sb);
        font.draw(batch, layout, x, y);
    }

    // ------------------------- HELPERS -----------------------------------

    private void updateGCStats() {
        long currCnt = 0, currTime = 0;
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            long c = bean.getCollectionCount();
            long t = bean.getCollectionTime();
            currCnt  += c < 0 ? 0 : c;
            currTime += t < 0 ? 0 : t;
        }
        accumulatedGCCount += (currCnt  - previousGCCount);
        long deltaTime = currTime - previousGCTime;
        if (deltaTime > 0) lastGCTime = deltaTime;

        previousGCCount = currCnt;
        previousGCTime  = currTime;
    }

    /** Calcula FPS 1 % low (percentil 99 de frame-times). */
    private void computeOnePercentLow() {
        if (frameTimesMs.isEmpty()) { fps1LowVisible = 0; return; }
        Collections.sort(frameTimesMs);
        int idx = Math.min(frameTimesMs.size() - 1, (int)(0.99f * frameTimesMs.size()));
        float p99 = frameTimesMs.get(idx);
        fps1LowVisible = 1000f / p99;
    }

    public void dispose() { font.dispose(); }
}

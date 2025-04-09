package com.sticklike.core.utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DebugStats {
    private BitmapFont font;
    private boolean enabled;

    // Variables para estadísticas adicionales
    private float tiempoEjecucion;
    private float acumuladorDelta;
    private int contadorFrames;
    private float deltaPromedio;

    public DebugStats() {
        font = new BitmapFont();
        font.getData().setScale(0.68f);
        font.getData().markupEnabled = true; // Habilitamos el uso de etiquetas de color
        font.setColor(Color.BLACK);
        enabled = true;
        tiempoEjecucion = 0;
        acumuladorDelta = 0;
        contadorFrames = 0;
        deltaPromedio = 0;

    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            enabled = !enabled;
        }
        float delta = Gdx.graphics.getDeltaTime();
        tiempoEjecucion += delta;
        acumuladorDelta += delta;
        contadorFrames++;
        if (acumuladorDelta >= 1f) {
            deltaPromedio = acumuladorDelta / contadorFrames;
            acumuladorDelta = 0;
            contadorFrames = 0;
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

        // Se agrupan todas las estadísticas en un único bloque usando markup para establecer colores colores.
        String textoDebug = "[GREEN]FPS: " + fps + "[]\n\n" +
            "[BLACK]Delta: " + String.format("%.3f", delta) + "\n" +
            "JavaH: " + String.format("%.2f", javaHeapMB) + " MB\n" +
            "NativeH: " + String.format("%.2f", nativeHeapMB) + " MB\n" +
            "Runtime: " + String.format("%.1f", tiempoEjecucion) + " s\n" +
            "Avg. Delta: " + String.format("%.3f", deltaPromedio) + "\n" +
            "Free RAM: " + String.format("%.2f", memoriaLibreMB) + " MB\n" +
            "Total RAM: " + String.format("%.2f", memoriaTotalMB) + " MB";

        GlyphLayout layout = new GlyphLayout(font, textoDebug);
        font.draw(batch, layout, x, y);
    }

    public void dispose() {
        font.dispose();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}

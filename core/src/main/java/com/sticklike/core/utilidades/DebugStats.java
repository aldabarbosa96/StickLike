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

    public DebugStats() {
        font = new BitmapFont();
        font.getData().setScale(0.75f);
        font.setColor(Color.BLACK);
        enabled = true;
    }


    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            enabled = !enabled;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void render(SpriteBatch batch, float virtualHeight) {
        if (!enabled) return;

        int fps = Gdx.graphics.getFramesPerSecond();
        float delta = Gdx.graphics.getDeltaTime();

        float javaHeapMB = Gdx.app.getJavaHeap() / (1024f * 1024f);
        float nativeHeapMB = Gdx.app.getNativeHeap() / (1024f * 1024f);

        float margin = 12f;
        float x = margin;
        float y = virtualHeight - margin;

        font.setColor(Color.GREEN);
        String fpsText = "FPS: " + fps;
        font.draw(batch, fpsText, x, y);
        font.setColor(Color.BLACK);
        String otherStats = "\nDelta: " + String.format("%.3f", delta) + "\nJavaH: " + String.format("%.2f", javaHeapMB) + " MB" + "\nNativeH: " + String.format("%.2f", nativeHeapMB) + " MB";

        GlyphLayout layout = new GlyphLayout(font, otherStats);
        font.draw(batch, layout, x, y);
    }

    public void dispose() {
        font.dispose();
    }
}

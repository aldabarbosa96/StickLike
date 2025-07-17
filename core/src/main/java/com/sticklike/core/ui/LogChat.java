/*
 *  LogChat – versión optimizada 2
 *  · Anchos de texto correctos (ya no se superpone nada).
 *  · Sin nuevas asignaciones por frame.
 *  · Misma API que tu clase original: copia / pega y compila.
 */
package com.sticklike.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;
import com.sticklike.core.interfaces.GameEvent;
import com.sticklike.core.interfaces.GameEventListener;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class LogChat implements GameEventListener {

    private static final float MARGIN = HUD_BAR_Y_OFFSET2;
    private static final float FADE_IN_TIME = 0.75f;
    private static final float FADE_OUT_TIME = 0.75f;
    private static final int MAX_LINES = 6;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final GlyphLayout layout = new GlyphLayout();
    private final LinkedList<ChatMessage> messages = new LinkedList<>();

    private static final Color COLOR_PANEL = new Color(1f, 1f, 1f, 0.35f);
    private static final Color COLOR_TIME = new Color(0.25f, 0.25f, 0.25f, 1f);
    private static final Color COLOR_TEXT = new Color(0f, 0.8f, 0f, 1f);

    private final Color tmpColorA = new Color();
    private final Color tmpColorB = new Color();

    private float cachedMaxWidth = 0f;

    private static final Pool<StringBuilder> SB_POOL = new Pool<>() {
        @Override
        protected StringBuilder newObject() {
            return new StringBuilder(128);
        }
    };

    public LogChat(SpriteBatch batch, BitmapFont font) {
        this.batch = batch;
        this.font = font;
        this.shapeRenderer = new ShapeRenderer();
    }

    /* ─────────────────────────── Eventos ─────────────────────────── */

    @Override
    public void onEvent(GameEvent event) {
        addMessage(event.getType(), event.getLogMessage());
    }

    private void addMessage(GameEventBus.EventType type, String text) {

        StringBuilder sb = SB_POOL.obtain();
        sb.setLength(0);
        String time = TIME_FMT.format(LocalTime.now());

        sb.append("- [").append(time).append("] - ");
        String timePart = sb.toString();

        sb.setLength(0);
        sb.append('(').append(type.name()).append("): ");
        String typePart = sb.toString();

        sb.setLength(0);
        sb.append(timePart).append(typePart).append(text);
        String fullText = sb.toString();
        SB_POOL.free(sb);

        /* medir anchos una sola vez */
        float timeWidth, typeWidth, fullWidth;
        layout.setText(font, timePart);
        timeWidth = layout.width;
        layout.setText(font, typePart);
        typeWidth = layout.width;
        layout.setText(font, fullText);
        fullWidth = layout.width;

        cachedMaxWidth = Math.max(cachedMaxWidth, fullWidth);

        messages.addLast(new ChatMessage(timePart, typePart, text, type, timeWidth, typeWidth, fullWidth));

        if (messages.size() > MAX_LINES) {
            ChatMessage removed = messages.removeFirst();
            if (Math.abs(removed.fullWidth - cachedMaxWidth) < 0.01f) recalcMaxWidth();
        }
    }

    /* ─────────────────────────── Render ─────────────────────────── */

    public void renderAboveHUD(float alturaDelHUD) {

        /* actualizar estado de mensajes */
        float delta = Gdx.graphics.getDeltaTime();
        Iterator<ChatMessage> it = messages.iterator();
        while (it.hasNext()) {
            ChatMessage m = it.next();
            if (m.state != ChatMessage.State.DISPLAY) m.timer += delta;
            if (m.state == ChatMessage.State.FADE_IN && m.timer >= FADE_IN_TIME) m.state = ChatMessage.State.DISPLAY;
            if (m.state == ChatMessage.State.FADE_OUT && m.timer >= FADE_OUT_TIME) it.remove();
        }
        if (messages.isEmpty()) return;

        /* layout general */
        int lines = messages.size();
        float lineHeight = font.getLineHeight();
        float padding = 5f;
        float spacing = 1f;
        float y0 = MARGIN + alturaDelHUD;

        float panelW = cachedMaxWidth + padding * 2f;
        float panelH = padding * 2f + lines * (lineHeight + spacing);
        float panelX = MARGIN - padding;
        float panelY = y0 - padding;

        /* fondo */
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(COLOR_PANEL);

        float r = 4f;
        int segs = 20;
        shapeRenderer.rect(panelX + r, panelY, panelW - 2 * r, panelH);
        shapeRenderer.rect(panelX, panelY + r, r, panelH - 2 * r);
        shapeRenderer.rect(panelX + panelW - r, panelY + r, r, panelH - 2 * r);
        shapeRenderer.arc(panelX + r, panelY + r, r, 180f, 90f, segs);
        shapeRenderer.arc(panelX + panelW - r, panelY + r, r, 270f, 90f, segs);
        shapeRenderer.arc(panelX + panelW - r, panelY + panelH - r, r, 0f, 90f, segs);
        shapeRenderer.arc(panelX + r, panelY + panelH - r, r, 90f, 90f, segs);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        /* texto */
        batch.begin();
        for (int i = 0; i < lines; i++) {
            ChatMessage m = messages.get(i);
            float alpha = m.getAlpha();

            float yPos = y0 + (lines - 1 - i) * (lineHeight + spacing);
            float xPos = MARGIN;

            /* hora */
            tmpColorA.set(COLOR_TIME);
            tmpColorA.a = alpha;
            font.setColor(tmpColorA);
            font.draw(batch, m.timePart, xPos, yPos + lineHeight);
            xPos += m.timeWidth;

            /* tipo */
            tmpColorB.set(colorForType(m.type));
            tmpColorB.a = alpha;
            font.setColor(tmpColorB);
            font.draw(batch, m.typePart, xPos, yPos + lineHeight);
            xPos += m.typeWidth;

            /* texto */
            tmpColorA.set(COLOR_TIME);
            tmpColorA.a = alpha;
            font.setColor(tmpColorA);
            font.draw(batch, m.text, xPos, yPos + lineHeight);
        }
        batch.end();
    }

    /* ─────────────────────────── Util ─────────────────────────── */

    private Color colorForType(GameEventBus.EventType t) {
        return switch (t) {
            case LVL -> COLOR_TEXT;
            case RECOLECCIÓN -> Color.ORANGE;
            case BOOST -> Color.MAGENTA;
            case FASE -> Color.RED;
            case MEJORA -> Color.BLUE;
            case BOSS -> Color.PURPLE;
            default -> Color.WHITE;
        };
    }

    private void recalcMaxWidth() {
        cachedMaxWidth = 0f;
        for (ChatMessage m : messages)
            if (m.fullWidth > cachedMaxWidth) cachedMaxWidth = m.fullWidth;
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

    /* ───────────────────────── Data Class ───────────────────────── */

    private static class ChatMessage {
        enum State {FADE_IN, DISPLAY, FADE_OUT}

        final String timePart, typePart, text;
        final GameEventBus.EventType type;
        final float timeWidth, typeWidth, fullWidth;

        State state = State.FADE_IN;
        float timer = 0f;

        ChatMessage(String timePart, String typePart, String text, GameEventBus.EventType type, float timeWidth, float typeWidth, float fullWidth) {
            this.timePart = timePart;
            this.typePart = typePart;
            this.text = text;
            this.type = type;
            this.timeWidth = timeWidth;
            this.typeWidth = typeWidth;
            this.fullWidth = fullWidth;
        }

        float getAlpha() {
            return switch (state) {
                case FADE_IN -> MathUtils.clamp(timer / FADE_IN_TIME, 0f, 1f);
                case DISPLAY -> 1f;
                case FADE_OUT -> MathUtils.clamp(1f - (timer / FADE_OUT_TIME), 0f, 1f);
            };
        }
    }
}

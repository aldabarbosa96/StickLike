package com.sticklike.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.interfaces.GameEvent;
import com.sticklike.core.gameplay.sistemas.eventBus.GameEventBus;
import com.sticklike.core.interfaces.GameEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class LogChat implements GameEventListener {
    private static final float MARGIN = HUD_BAR_Y_OFFSET2;
    private static final float FADE_IN_TIME = 0.75f;
    private static final float FADE_OUT_TIME = 0.75f;
    private static final int MAX_LINES = 6;
    private static final SimpleDateFormat TIME_FMT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final GlyphLayout layout = new GlyphLayout();
    private final LinkedList<ChatMessage> messages = new LinkedList<>();
    private static final Color COLOR_TIME = new Color(0.25f, 0.25f, 0.25f, 1f);
    private static final Color COLOR_TEXT = new Color(0f, 0.8f, 0f, 1f);

    public LogChat(SpriteBatch batch, BitmapFont font) {
        this.batch = batch;
        this.font = font;
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void onEvent(GameEvent event) {
        addMessage(event.getType(), event.getLogMessage());
    }

    private void addMessage(GameEventBus.EventType type, String text) {
        messages.addLast(new ChatMessage(TIME_FMT.format(new Date()), type, text));
        if (messages.size() > MAX_LINES) {
            messages.removeFirst();
        }
    }

    public void renderAboveHUD(float alturaDelHUD) {
        float y0 = MARGIN + alturaDelHUD;
        float lineHeight = font.getLineHeight();
        float delta = Gdx.graphics.getDeltaTime();
        float padding = 5f;
        float lineSpacing = 1f;

        // 1) Actualizar timers y eliminar expirados
        Iterator<ChatMessage> it = messages.iterator();
        while (it.hasNext()) {
            ChatMessage m = it.next();
            if (m.state == ChatMessage.State.FADE_IN || m.state == ChatMessage.State.FADE_OUT) {
                m.timer += delta;
            }
            if (m.state == ChatMessage.State.FADE_IN && m.timer >= FADE_IN_TIME) {
                m.state = ChatMessage.State.DISPLAY;
            }
            if (m.state == ChatMessage.State.FADE_OUT && m.timer >= FADE_OUT_TIME) {
                it.remove();
            }
        }

        int lines = messages.size();
        if (lines == 0) return;

        // 2) Medir ancho máximo de las líneas
        float maxTextWidth = 0;
        for (ChatMessage m : messages) {
            String timePart = "- [" + m.time + "] - ";
            String typePart = "(" + m.type.name() + "): ";
            String full = timePart + typePart + m.text;
            layout.setText(font, full);
            maxTextWidth = Math.max(maxTextWidth, layout.width);
        }

        // 3) Dimensiones del panel translúcido
        float panelWidth = maxTextWidth + padding * 2;
        float panelHeight = padding * 2 + lines * lineHeight * lineSpacing;
        float panelX = MARGIN - padding;
        float panelY = y0 - padding;

        // 4) Dibujar fondo translúcido (blanco con alfa baja)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 0.33f);

        float radius = 4f;
        int segments = 20;

        shapeRenderer.rect(panelX + radius, panelY, panelWidth - 2 * radius, panelHeight);
        shapeRenderer.rect(panelX, panelY + radius, radius, panelHeight - 2 * radius);
        shapeRenderer.rect(panelX + panelWidth - radius, panelY + radius, radius, panelHeight - 2 * radius);
        shapeRenderer.arc(panelX + radius, panelY + radius, radius, 180f, 90f, segments);
        shapeRenderer.arc(panelX + panelWidth - radius, panelY + radius, radius, 270f, 90f, segments);
        shapeRenderer.arc(panelX + panelWidth - radius, panelY + panelHeight - radius, radius,   0f, 90f, segments);
        shapeRenderer.arc(panelX + radius, panelY + panelHeight - radius, radius,  90f, 90f, segments);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 5) Dibujar texto
        batch.begin();
        for (int i = 0; i < lines; i++) {
            ChatMessage m = messages.get(i);
            float alpha = m.getAlpha();

            float yPos = y0 + (lines - 1 - i) * (lineHeight + lineSpacing);
            float x = MARGIN;

            String timePart = "- [" + m.time + "] - ";
            layout.setText(font, timePart);
            Color cTime = new Color(COLOR_TIME);
            cTime.a = alpha;
            font.setColor(cTime);
            font.draw(batch, timePart, x, yPos + layout.height);
            x += layout.width;

            String typePart = "(" + m.type.name() + "): ";
            layout.setText(font, typePart);
            Color cType = new Color(colorForType(m.type));
            cType.a = alpha;
            font.setColor(cType);
            font.draw(batch, typePart, x, yPos + layout.height);
            x += layout.width;

            Color cText = new Color(COLOR_TIME);
            cText.a = alpha;
            font.setColor(cText);
            font.draw(batch, m.text, x, yPos + layout.height);
        }
        batch.end();
    }

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

    public void dispose() {
        shapeRenderer.dispose();
    }

    private static class ChatMessage {
        enum State {FADE_IN, DISPLAY, FADE_OUT}

        final String time;
        final GameEventBus.EventType type;
        final String text;

        State state;
        float timer;

        ChatMessage(String time, GameEventBus.EventType type, String text) {
            this.time = time;
            this.type = type;
            this.text = text;
            this.state = State.FADE_IN;
            this.timer = 0f;
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

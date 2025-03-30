package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.HUD_HEIGHT;

public class MensajesChat {
    private static MensajesChat instance;

    private RenderHUDComponents renderHUDComponents;
    private Table table;
    private Stage hudStage;
    private ArrayList<ChatMessage> messages;
    private float displayDuration;
    private BitmapFont font;
    private Label.LabelStyle labelStyle;
    private static final float FADE_IN_DURATION = 0.5f;
    private static final float FADE_OUT_DURATION = 1f;

    // Constructor privado para evitar instanciación directa
    private MensajesChat(Stage hudStage, RenderHUDComponents renderHUDComponents) {
        this.hudStage = hudStage;
        this.messages = new ArrayList<>();
        // La duración total de cada mensaje (incluyendo fade in/out)
        this.displayDuration = 15;
        this.font = new BitmapFont();
        this.font.getData().markupEnabled = true;
        this.labelStyle = new Label.LabelStyle();
        this.renderHUDComponents = renderHUDComponents;
        font.getData().setScale(0.9f);
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        table = new Table();
        table.setFillParent(true);
        table.bottom().left();
        table.padLeft(15);
        table.padBottom(HUD_HEIGHT + 5);
        hudStage.addActor(table);
    }

    public static void init(Stage hudStage, RenderHUDComponents renderHUDComponents) {
        if (instance == null) {
            instance = new MensajesChat(hudStage, renderHUDComponents);
        }
    }

    public static MensajesChat getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ERROR: MensajesChat no ha sido inicializado. Llamar a init() primero.");
        }
        return instance;
    }

    public void addMessage(String nombre, String mensaje) {
        String timeStamp = getCurrentTimeStamp();
        boolean esOjete = nombre.toLowerCase().contains("ojete");
        String colorNombre = esOjete ? "[#FF4500]" : "[BLUE]";

        String fullMessage = "[GREEN][" + timeStamp + "][] " + colorNombre + nombre + ":  []" + "[BLACK]" + mensaje + "[]";

        Label label = new Label(fullMessage, labelStyle);
        label.setWrap(true);
        label.setAlignment(Align.left);

        ChatMessage chatMessage = new ChatMessage(fullMessage, displayDuration, label, renderHUDComponents.getTiempoTranscurrido());
        messages.add(chatMessage);

        float margen = 50;
        float maxWidth = hudStage.getWidth() - margen;
        table.add(label).width(maxWidth).padBottom(5).row();
    }

    public void update() {
        float currentTime = renderHUDComponents.getTiempoTranscurrido();
        boolean refreshNeeded = false;
        Iterator<ChatMessage> iterator = messages.iterator();
        while (iterator.hasNext()) {
            ChatMessage msg = iterator.next();
            float elapsed = currentTime - msg.startingTime;
            float alpha = 1f;
            // Fade in
            if (elapsed < FADE_IN_DURATION) {
                alpha = elapsed / FADE_IN_DURATION;
            }
            // Fade out
            else if (elapsed > msg.remainingTime - FADE_OUT_DURATION) {
                alpha = (msg.remainingTime - elapsed) / FADE_OUT_DURATION;
                if (alpha < 0) alpha = 0;
            }

            Color currentColor = msg.label.getColor();
            currentColor.a = alpha;
            msg.label.setColor(currentColor);

            if (elapsed >= msg.remainingTime) {
                msg.label.remove();
                iterator.remove();
                refreshNeeded = true;
            }
        }
        if (refreshNeeded) {
            refreshTable();
        }
    }

    private void refreshTable() {
        table.clear();
        float margen = 50;
        float maxWidth = hudStage.getWidth() - margen;
        for (ChatMessage msg : messages) {
            table.add(msg.label).width(maxWidth).padBottom(5).row();
        }
    }

    private String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    public static void reset() {
        if (instance != null) {
            instance.table.clear();
            instance.messages.clear();
            instance = null;
        }
    }

    // Clase interna para representar cada mensaje con su tiempo de vida y Label asociado
    private class ChatMessage {
        String text;
        float startingTime;
        float remainingTime;
        Label label;

        public ChatMessage(String text, float remainingTime, Label label, float startingTime) {
            this.text = text;
            this.startingTime = startingTime;
            this.remainingTime = remainingTime;
            this.label = label;
        }
    }
}

package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.sticklike.core.gameplay.sistemas.SistemaDeNiveles;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.HUD_HEIGHT;

public class MensajesChat {

    // Instancia única (singleton)
    private static MensajesChat instance;

    private Table table;
    private Stage hudStage;
    private ArrayList<ChatMessage> messages;
    private float displayDuration;
    private BitmapFont font;
    private Label.LabelStyle labelStyle;

    // Constructor privado para evitar instanciación directa
    private MensajesChat(Stage hudStage) {
        this.hudStage = hudStage;
        this.messages = new ArrayList<>();
        this.displayDuration = 15f;
        this.font = new BitmapFont();
        this.labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.BLUE;

        table = new Table();
        table.setFillParent(true);
        table.bottom().left();
        table.padLeft(25);
        table.padBottom(HUD_HEIGHT);
        hudStage.addActor(table);
    }


    public static void init(Stage hudStage) {
        if(instance == null) {
            instance = new MensajesChat(hudStage);
        }
    }


    public static MensajesChat getInstance() {
        if(instance == null) {
            throw new IllegalStateException("MensajesChat no ha sido inicializado. Llama a init() primero.");
        }
        return instance;
    }

    public void addMessage(String nombre, String mensaje) {
        String timeStamp = getCurrentTimeStamp();
        String fullMessage = "[" + timeStamp + "] " + nombre + ": " + mensaje;

        Label label = new Label(fullMessage, labelStyle);
        label.setWrap(true);
        label.setAlignment(Align.left);

        ChatMessage chatMessage = new ChatMessage(fullMessage, displayDuration, label);
        messages.add(chatMessage);

        float margen = 50;
        float maxWidth = hudStage.getWidth() - margen;
        table.add(label).width(maxWidth).padBottom(5).row();
    }

    public void update(float delta) {
        boolean refreshNeeded = false;
        Iterator<ChatMessage> iterator = messages.iterator();
        while (iterator.hasNext()) {
            ChatMessage msg = iterator.next();
            msg.remainingTime -= delta;
            if (msg.remainingTime <= 0) {
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

    // Clase interna para representar cada mensaje con su tiempo de vida y Label asociado
    private class ChatMessage {
        String text;
        float remainingTime;
        Label label;

        public ChatMessage(String text, float remainingTime, Label label) {
            this.text = text;
            this.remainingTime = remainingTime;
            this.label = label;
        }
    }
}

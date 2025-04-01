package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import java.util.ArrayList;
import java.util.Iterator;

import com.sticklike.core.interfaces.Enemigo;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.VIRTUAL_WIDTH;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.VIRTUAL_HEIGHT;

public class MensajesChat {

    private static MensajesChat instance;

    // Stage para mensajes del jugador (HUD) y para mensajes de enemigos (mundo)
    private Stage chatStage;
    private Stage enemyStage;
    private RenderHUDComponents renderHUDComponents;
    private ArrayList<ChatMessage> messages;
    private float displayDuration;
    private BitmapFont font;
    private Label.LabelStyle labelStyle;
    private static final float FADE_IN_DURATION = 0.25f;
    private static final float FADE_OUT_DURATION = 0.25f;
    private static final float FOLLOW_OFFSET_Y = 5f;

    // Constructor privado: se reciben los dos stages (uno para chat y otro para mensajes de enemigos)
    private MensajesChat(Stage chatStage, RenderHUDComponents renderHUDComponents, Stage enemyStage) {
        this.chatStage = chatStage;
        this.renderHUDComponents = renderHUDComponents;
        this.enemyStage = enemyStage;

        this.messages = new ArrayList<>();
        this.displayDuration = MathUtils.random(4,6);
        this.font = new BitmapFont();
        this.font.getData().markupEnabled = true;
        this.font.getData().setScale(0.8f);
        this.labelStyle = new Label.LabelStyle();
        this.labelStyle.font = font;
        this.labelStyle.fontColor = Color.WHITE;
    }

    public static void init(Stage chatStage, RenderHUDComponents renderHUDComponents, OrthographicCamera worldCamera, com.badlogic.gdx.graphics.g2d.SpriteBatch spriteBatch) {
        if (instance == null) {
            // Creamos un stage para mensajes de enemigos con un ExtendViewport usando la cámara del mundo
            Stage enemyStage = new Stage(new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, worldCamera), spriteBatch);
            instance = new MensajesChat(chatStage, renderHUDComponents, enemyStage);
        }
    }

    public static MensajesChat getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ERROR: MensajesChat no ha sido inicializado. Llama a init() primero.");
        }
        return instance;
    }

    public void addMessage(String nombre, String mensaje, float posX, float posY, Enemigo enemy, boolean esEnemigo) {
        //String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        boolean esEnemigoTexto = nombre.toLowerCase().contains("oj") || nombre.toLowerCase().contains("polla");
        String colorNombre = esEnemigoTexto ? "[#B00020]" : "[#3A5FCD]";
        String fullMessage = /*"[GREEN][" + timeStamp + "][] " + */colorNombre + nombre + ":  []" + "[#454545]" + mensaje + "[]";

        Label label = new Label(fullMessage, labelStyle);
        label.setWrap(true);
        label.setAlignment(Align.center);
        label.setPosition(posX, posY);

        ChatMessage cm = new ChatMessage(fullMessage, displayDuration, label, renderHUDComponents.getTiempoTranscurrido(), enemy, esEnemigo);
        messages.add(cm);
        if (esEnemigo) {
            enemyStage.addActor(label);
        } else {
            chatStage.addActor(label);
        }
    }

    // Métodos sobrecargados para mensajes del jugador
    public void addMessage(String nombre, String mensaje, float posX, float posY) {
        addMessage(nombre, mensaje, posX, posY, null, false);
    }

    // Métodos sobrecargados para mensajes de los enemigos
    public void addEnemyMessage(String nombre, String mensaje, Enemigo enemy) {
        float posX = enemy.getSprite().getX();
        float posY = enemy.getSprite().getY() + enemy.getSprite().getHeight() + FOLLOW_OFFSET_Y;
        addMessage(nombre, mensaje, posX, posY, enemy, true);
    }

    public void update() {
        float currentTime = renderHUDComponents.getTiempoTranscurrido();
        Iterator<ChatMessage> iter = messages.iterator();
        while (iter.hasNext()) {
            ChatMessage msg = iter.next();
            float elapsed = currentTime - msg.startingTime;

            if (msg.esEnemigo && msg.enemy != null && msg.enemy.estaMuerto()) {
                float newRemainingTime = (currentTime - msg.startingTime) + FADE_OUT_DURATION;
                if (newRemainingTime < msg.remainingTime) {
                    msg.remainingTime = newRemainingTime;
                }
            }

            float alpha = 1f;
            if (elapsed < FADE_IN_DURATION) {
                alpha = elapsed / FADE_IN_DURATION;
            } else if (elapsed > msg.remainingTime - FADE_OUT_DURATION) {
                alpha = (msg.remainingTime - elapsed) / FADE_OUT_DURATION;
                if (alpha < 0) alpha = 0;
            }
            Color c = msg.label.getColor();
            c.a = alpha;
            msg.label.setColor(c);

            if (msg.esEnemigo && msg.enemy != null) {
                // Actualizamos la posición en base a la posición actual del enemigo
                float newX = msg.enemy.getSprite().getX();
                float newY = msg.enemy.getSprite().getY() + msg.enemy.getSprite().getHeight() + FOLLOW_OFFSET_Y;
                msg.label.setPosition(newX, newY);
            }

            if (elapsed >= msg.remainingTime) {
                msg.label.remove();
                iter.remove();
            }
        }
        chatStage.act();
        enemyStage.act();
    }

    public void draw(OrthographicCamera worldCamera) {
        chatStage.draw();
        enemyStage.draw();
    }

    public static void reset() {
        if (instance != null) {
            instance.messages.clear();
            instance.chatStage.clear();
            instance.enemyStage.clear();
            instance = null;
        }
    }

    private class ChatMessage {
        String text;
        float startingTime;
        float remainingTime;
        Label label;
        Enemigo enemy;
        boolean esEnemigo;

        public ChatMessage(String text, float remainingTime, Label label, float startingTime, Enemigo enemy, boolean esEnemigo) {
            this.text = text;
            this.startingTime = startingTime;
            this.remainingTime = remainingTime;
            this.label = label;
            this.enemy = enemy;
            this.esEnemigo = esEnemigo;
        }
    }
}

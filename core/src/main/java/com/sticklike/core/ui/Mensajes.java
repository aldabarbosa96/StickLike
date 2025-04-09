package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;
import java.util.Iterator;

import com.sticklike.core.interfaces.Enemigo;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.VIRTUAL_WIDTH;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.VIRTUAL_HEIGHT;

public class Mensajes {
    private static Mensajes instance;
    private Stage chatStage;
    private Stage enemyStage;
    private RenderHUDComponents renderHUDComponents;
    private ArrayList<ChatMessage> messages;
    private float displayDuration;
    private BitmapFont font;
    private Label.LabelStyle labelStyle;
    private Drawable whiteBackground;
    // Referencia a la textura creada para el fondo redondeado, para poder disponerla después
    private Texture roundedBackgroundTexture;
    private static final float FADE_IN_DURATION = 0.25f;
    private static final float FADE_OUT_DURATION = 0.25f;
    private static final float FOLLOW_OFFSET_Y = 5f;

    // Constructor privado
    private Mensajes(Stage chatStage, RenderHUDComponents renderHUDComponents, Stage enemyStage) {
        this.chatStage = chatStage;
        this.renderHUDComponents = renderHUDComponents;
        this.enemyStage = enemyStage;
        this.messages = new ArrayList<>();
        this.displayDuration = MathUtils.random(4, 6);

        this.font = new BitmapFont();
        this.font.getData().markupEnabled = true;
        this.font.getData().setScale(0.7f);
        this.labelStyle = new Label.LabelStyle();
        this.labelStyle.font = font;
        this.labelStyle.fontColor = Color.WHITE;

        // Se crea el drawable con fondo redondeado directamente, eliminando el Pixmap 1x1 innecesario
        this.whiteBackground = createRoundedDrawable(500, 50, 20, Color.WHITE);
    }

    // Inicialización del Singleton
    public static void init(Stage chatStage, RenderHUDComponents renderHUDComponents, OrthographicCamera worldCamera, com.badlogic.gdx.graphics.g2d.SpriteBatch spriteBatch) {
        if (instance == null) {
            Stage enemyStage = new Stage(new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, worldCamera), spriteBatch);
            instance = new Mensajes(chatStage, renderHUDComponents, enemyStage);
        }
    }

    public static Mensajes getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ERROR: Mensajes no ha sido inicializado. Llama a init() primero.");
        }
        return instance;
    }

    public void addMessage(String nombre, String mensaje, float posX, float posY, Enemigo enemy, boolean esEnemigo) {
        String colorNombre = esEnemigo ? "[RED]" : "[#0A0AFF]";
        String fullMessage = colorNombre + "[" + nombre + "]:  []" + "[DARK_GRAY]" + mensaje + "[]";

        // Crear el Label (actor del texto)
        Label label = new Label(fullMessage, labelStyle);
        label.setAlignment(Align.center);
        label.pack();

        // Crear el fondo usando whiteBackground
        Image backgroundImage = new Image(whiteBackground);
        float paddingX = 7.5f;
        float paddingY = 5;
        backgroundImage.setSize(label.getPrefWidth() + paddingX, label.getPrefHeight() + paddingY);

        // Crear un Group para agrupar fondo y texto
        Group messageGroup = new Group();
        messageGroup.addActor(backgroundImage);
        label.setPosition(paddingX / 2f, paddingY / 2f);
        messageGroup.addActor(label);
        messageGroup.setSize(backgroundImage.getWidth(), backgroundImage.getHeight());

        backgroundImage.setColor(1, 1, 1, 0.75f);

        // Posicionar el Group en el Stage
        messageGroup.setPosition(posX, posY);

        // Crear y almacenar el ChatMessage con las referencias
        ChatMessage cm = new ChatMessage(fullMessage, displayDuration, label, backgroundImage, messageGroup, renderHUDComponents.getTiempoTranscurrido(), enemy, esEnemigo);
        messages.add(cm);

        // Agregar el Group al Stage correspondiente
        if (esEnemigo) {
            enemyStage.addActor(messageGroup);
        } else {
            chatStage.addActor(messageGroup);
        }
    }

    // Sobrecarga para mensajes del jugador
    public void addMessage(String nombre, String mensaje, float posX, float posY) {
        addMessage(nombre, mensaje, posX, posY, null, false);
    }

    // Sobrecarga para mensajes de enemigos
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

            Color labelColor = msg.label.getColor();
            labelColor.a = alpha;
            msg.label.setColor(labelColor);

            Color bgColor = msg.background.getColor();
            bgColor.a = 0.75f * alpha;
            msg.background.setColor(bgColor);

            if (msg.esEnemigo && msg.enemy != null) {
                float newX = msg.enemy.getSprite().getX();
                float newY = msg.enemy.getSprite().getY() + msg.enemy.getSprite().getHeight() + FOLLOW_OFFSET_Y;
                msg.group.setPosition(newX, newY);
            }

            if (elapsed >= msg.remainingTime) {
                msg.group.remove();
                iter.remove();
            }
        }
        chatStage.act();
        enemyStage.act();
    }

    public void draw(OrthographicCamera worldCamera) {
        chatStage.draw();
        enemyStage.draw();
        chatStage.getBatch().flush();
        chatStage.getBatch().setColor(1, 1, 1, 1);
        enemyStage.getBatch().flush();
        enemyStage.getBatch().setColor(1, 1, 1, 1);
    }

    public static void reset() {
        if (instance != null) {
            instance.messages.clear();
            instance.chatStage.clear();
            instance.enemyStage.clear();
            if (instance.font != null) {
                instance.font.dispose();
            }
            if (instance.roundedBackgroundTexture != null) {
                instance.roundedBackgroundTexture.dispose();
            }
            instance.enemyStage.dispose();
            instance = null;
        }
    }

    public Drawable createRoundedDrawable(int width, int height, int radius, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(color);

        // Rellenar la zona central y laterales sin las esquinas
        pixmap.fillRectangle(radius, 0, width - 2 * radius, height);
        pixmap.fillRectangle(0, radius, width, height - 2 * radius);

        // Rellenar las cuatro esquinas con círculos
        pixmap.fillCircle(radius, radius, radius);
        pixmap.fillCircle(width - radius, radius, radius);
        pixmap.fillCircle(radius, height - radius, radius);
        pixmap.fillCircle(width - radius, height - radius, radius);

        // Crear la textura y guardar la referencia para disponerla en reset()
        roundedBackgroundTexture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(roundedBackgroundTexture));
    }

    private class ChatMessage {
        String text;
        float startingTime;
        float remainingTime;
        Label label;
        Image background;
        Group group;
        Enemigo enemy;
        boolean esEnemigo;

        public ChatMessage(String text, float remainingTime, Label label, Image background, Group group, float startingTime, Enemigo enemy, boolean esEnemigo) {
            this.text = text;
            this.startingTime = startingTime;
            this.remainingTime = remainingTime;
            this.label = label;
            this.background = background;
            this.group = group;
            this.enemy = enemy;
            this.esEnemigo = esEnemigo;
        }
    }
}

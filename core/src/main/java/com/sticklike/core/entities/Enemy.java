package com.sticklike.core.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    private Sprite sprite;
    private float health = 100;

    public Enemy(float x, float y) {
        Texture texture = new Texture("enemy01.png");
        sprite = new Sprite(texture);
        sprite.setSize(35, 30);
        sprite.setPosition(x, y);
    }

    public void render(SpriteBatch batch) {
        if (!isDead()) { // Verifica si el enemigo aún tiene vida.
            sprite.draw(batch); // Dibuja el sprite en la pantalla.
        }
    }

    public void update(float delta) {
        // TODO: Implementar lógica de comportamiento, como movimiento o animaciones.
    }

    // Verifica si un proyectil impacta en el enemigo.
    public boolean isHitBy(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        Rectangle rect = sprite.getBoundingRectangle();
        // Calcula el centro del proyectil para una detección más precisa.
        float projectileCenterX = projectileX + projectileWidth / 2;
        float projectileCenterY = projectileY + projectileHeight / 2;
        return rect.contains(projectileCenterX, projectileCenterY); // Devuelve si el proyectil está dentro del área del enemigo.
    }

    // Getters/setters
    public void reduceHealth(float amount) {
        health -= amount;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public Sprite getSprite() {
        return sprite;
    }
}

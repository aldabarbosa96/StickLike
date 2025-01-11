package com.sticklike.core.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.utils.AssetLoader;

public class Projectile {
    private static Texture texture;
    private Sprite sprite;
    private float speed = 175f;
    private float directionX, directionY;
    private boolean active;
    private Enemy target;

    public Projectile(float x, float y, float directionX, float directionY, Enemy target) {
        if (texture == null) {
            texture = AssetLoader.weapon01;
        }
        sprite = new Sprite(texture);
        sprite.setSize(8, 8);
        sprite.setPosition(x, y);

        this.directionX = directionX;
        this.directionY = directionY;
        this.active = true;
        this.target = target;
    }

    public void update(float delta) {
        if (active) {
            sprite.translate(directionX * speed * delta, directionY * speed * delta); // Movimiento del proyectil.
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            sprite.draw(batch);
        }
    }

    public void deactivate() {active = false;}

    public boolean isActive() {
        return active;
    }

    public Enemy getTarget() {
        return target;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public com.badlogic.gdx.math.Rectangle getBoundingRectangle() {
        return sprite.getBoundingRectangle(); // Devuelve el rectángulo de colisión para gestionar los impactos.
    }
}

package com.sticklike.core.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Projectile {
    private static Texture texture;
    private Sprite sprite;
    private float speed = 300;
    private float directionX, directionY;
    private boolean active;
    private Enemy target; // Enemigo objetivo del proyectil.

    public Projectile(float x, float y, float directionX, float directionY, Enemy target) {
        if (texture == null) {
            texture = new Texture("bow_arrow.png");
        }
        sprite = new Sprite(texture);
        sprite.setSize(8, 8);
        sprite.setPosition(x, y);

        this.directionX = directionX;
        this.directionY = directionY;
        this.active = true; // El proyectil está activo inicialmente.
        this.target = target; // Asignamos el enemigo objetivo.
    }

    public void update(float delta) {
        if (active) {
            sprite.translate(directionX * speed * delta, directionY * speed * delta); // Mueve el proyectil.
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            sprite.draw(batch); // Dibuja el proyectil solo si está activo.
        }
    }

    public void deactivate() {
        active = false; // Desactiva el proyectil tras un impacto.
    }

    public boolean isActive() {
        return active;
    }

    public Enemy getTarget() {
        return target; // Devuelve el enemigo objetivo.
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
        return sprite.getBoundingRectangle(); // Devuelve el rectángulo de colisión.
    }
}

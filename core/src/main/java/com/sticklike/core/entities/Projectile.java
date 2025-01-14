package com.sticklike.core.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.utils.AssetLoader;
import com.sticklike.core.utils.GameConfig;

public class Projectile {
    private static Texture texture;
    private Sprite sprite;
    private float speed = GameConfig.PROJECTILE_SPEED;
    private float speedMultiplier; // Nuevo atributo para modificar la velocidad
    private float directionX, directionY;
    private boolean active;
    private Enemy target;

    public Projectile(float x, float y, float directionX, float directionY, Enemy target, float speedMultiplier) {
        if (texture == null) {
            texture = AssetLoader.weapon01;
        }
        sprite = new Sprite(texture);
        sprite.setSize(GameConfig.PROJECTILE_SIZE, GameConfig.PROJECTILE_SIZE);
        sprite.setPosition(x, y);

        this.directionX = directionX;
        this.directionY = directionY;
        this.active = true;
        this.target = target;
        this.speedMultiplier = speedMultiplier; // Asignar el multiplicador de velocidad
    }

    public void update(float delta) {
        if (active) {
            // Aplicamos el multiplicador de velocidad
            sprite.translate(directionX * speed * speedMultiplier * delta, directionY * speed * speedMultiplier * delta);
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            sprite.draw(batch);
        }
    }

    public void deactivate() { active = false; }

    public boolean isActive() { return active; }

    public Enemy getTarget() { return target; }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    public float getX() { return sprite.getX(); }

    public float getY() { return sprite.getY(); }

    public com.badlogic.gdx.math.Rectangle getBoundingRectangle() {
        return sprite.getBoundingRectangle(); // Devuelve el rectángulo de colisión para gestionar los impactos.
    }
}

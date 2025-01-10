package com.sticklike.core.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    private Sprite sprite;
    private float health = 100;

    public Enemy(float x, float y){
        Texture texture = new Texture("enemy01.png");
        sprite = new Sprite(texture);
        sprite.setSize(35,30);
        sprite.setPosition(x,y);
    }

    public void render(SpriteBatch batch) {
        if (!isDead()) {
            sprite.draw(batch);
        }
    }

    public void update(float delta) {
    }

    public boolean isHitBy(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {

        Rectangle rect = sprite.getBoundingRectangle();

        float projectileCenterX = projectileX + projectileWidth / 2;
        float projectileCenterY = projectileY + projectileHeight / 2;

        return rect.contains(projectileCenterX, projectileCenterY);
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void reduceHealth(float amount) {
        health -= amount;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public float getHealth() {
        return health;
    }
}

package com.sticklike.core.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Projectile {
    private Sprite sprite;
    private float speed = 300;
    private float directionX, directionY;

    public Projectile(float x, float y, float directionX, float directionY) {
        Texture texture = new Texture("bow_arrow.png");
        sprite = new Sprite(texture);

        sprite.setSize(25, 5);
        sprite.setPosition(x, y);


        this.directionX = directionX;
        this.directionY = directionY;
    }

    public void update(float delta) {
        sprite.translate(directionX * speed * delta, directionY * speed * delta);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
    public com.badlogic.gdx.math.Rectangle getBoundingRectangle() {
        return sprite.getBoundingRectangle();
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }
}

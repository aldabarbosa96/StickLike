package com.sticklike.core.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.utils.AssetLoader;

public class Enemy {
    private Sprite sprite;
    private Player player;
    private float health = 100;
    private float speed;
    private float moveTimer, pauseDuration, moveDuration;
    private boolean isMoving;
    private float damageCooldown = 1f;
    private float damageTimer = 0f;
    private static final float MAX_PAUSE = 0.75f;
    private static final float MIN_PAUSE = 0.25f;
    private static final float MIN_MOVE_DURATION = 1.5f;
    private static final float MAX_MOVE_DURATION = 3.0f;


    public Enemy(float x, float y, Player player, float speed) {
        sprite = new Sprite(AssetLoader.enemy01);
        sprite.setSize(38, 33);
        sprite.setPosition(x, y);
        this.player = player;
        this.speed = speed;
        this.moveTimer = 1;
        this.isMoving = true;
        this.pauseDuration = (float) (MIN_PAUSE + Math.random() * (MAX_PAUSE - MIN_PAUSE));
        this.moveDuration = MIN_MOVE_DURATION + (float) Math.random() * (MAX_MOVE_DURATION - MIN_MOVE_DURATION);
    }

    public void renderEnemy(SpriteBatch batch) {
        if (!isDead()) {
            sprite.draw(batch);
        }
    }

    public void updateEnemy(float delta) {
        moveTimer += delta;

        if (isMoving) {
            if (moveTimer >= moveDuration) {
                isMoving = false;
                moveTimer = 0;
                pauseDuration = MIN_PAUSE + (float) Math.random() * (MAX_PAUSE - MIN_PAUSE);
            } else {

                float enemyPosX = getX();
                float enemyPosY = getY();

                float playerPosX = player.getSprite().getX();
                float playerPosY = player.getSprite().getY();

                float difX = playerPosX - enemyPosX;
                float difY = playerPosY - enemyPosY;

                // Añadimos un desplazamiento aleatorio para simular movimiento diagonal.
                float randomOffsetX = (float) Math.random() * 100 - 50;
                float randomOffsetY = (float) Math.random() * 100;

                difX += randomOffsetX;
                difY += randomOffsetY;

                float distance = (float) Math.sqrt(difX * difX + difY * difY);

                if (distance != 0) {
                    difX /= distance;
                    difY /= distance;
                }
                float movementX = difX * speed * delta;
                float movementY = difY * speed * delta;

                sprite.translate(movementX, movementY);
            }
        } else {
            if (moveTimer >= pauseDuration) {
                isMoving = true;
                moveTimer = 0;
                moveDuration = MIN_MOVE_DURATION + (float) Math.random() * (MAX_MOVE_DURATION - MIN_MOVE_DURATION);
            }
        }
        if ( damageTimer> 0) {
            damageTimer -= delta;
        }
    }

    public boolean isHitBy(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }


    public boolean canDealDamage() {
        return damageTimer <= 0;
    }

    public void resetDamageTimer() {
        damageTimer = damageCooldown;
    }

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

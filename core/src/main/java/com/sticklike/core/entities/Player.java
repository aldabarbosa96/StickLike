package com.sticklike.core.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.utils.Array;
import com.sticklike.core.managers.EnemyManager;
import com.sticklike.core.managers.ProjectileManager;
import com.sticklike.core.utils.AssetLoader;
import com.sticklike.core.utils.GameConfig;

public class Player {

    private enum Direction { LEFT, RIGHT, IDLE }

    private Sprite sprite;
    private EnemyManager enemyManager;
    private ProjectileManager projectileManager;

    private float velocidadPlayer;
    private float health;
    private float maxHealth;
    private float attackRange;
    private float shootInterval;
    private float temporizadorDisparo = 0;
    private int projectilePerShot = 1;
    private boolean isDead;
    private Direction currentDirection = Direction.IDLE;

    // Animaciones
    private Animation<TextureRegion> movementAnimationRight;
    private Animation<TextureRegion> movementAnimationLeft;
    private Animation<TextureRegion> movementIdle;
    private float animationTimer = 0;

    public Player(float startX, float startY) {
        this.velocidadPlayer = GameConfig.PLAYER_SPEED;
        this.health          = GameConfig.PLAYER_HEALTH;
        this.maxHealth       = GameConfig.PLAYER_MAX_HEALTH;
        this.attackRange     = GameConfig.PLAYER_ATTACK_RANGE;
        this.shootInterval   = GameConfig.PLAYER_SHOOT_INTERVAL;

        sprite = new Sprite(AssetLoader.stickman);
        sprite.setSize(15, 45);
        sprite.setPosition(startX, startY);

        projectileManager = new ProjectileManager();
        isDead = false;

        // Cargamos animaciones
        movementIdle           = AssetLoader.animations.get("idle");
        movementAnimationRight = AssetLoader.animations.get("moveRight");
        movementAnimationLeft  = AssetLoader.animations.get("moveLeft");
    }

    public void updatePlayer(float delta, boolean paused, Array<InGameText> dmgText) {
        if (isDead) return;

        if (!paused) {
            // Actualizamos input y lógica
            handleInput(delta);
            temporizadorDisparo += delta;
            if (temporizadorDisparo >= shootInterval) {
                temporizadorDisparo = 0;
                basicShot();
            }
            // Chequeo de colisión con enemigos
            if (enemyManager != null) {
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (isCollidingWith(enemy) && enemy.canDealDamage()) {
                        takeDamage(2);
                        enemy.resetDamageTimer();
                    }
                }
            }
        } else {
            // Si está pausado, forzamos idle (aunque se pulse A o D).
            currentDirection = Direction.IDLE;
        }

        // Siempre avanzamos el timer para animaciones (idle "respira")
        animationTimer += delta;

        // Actualizamos proyectiles
        projectileManager.update(delta,
            (enemyManager != null ? enemyManager.getEnemies() : null),
            dmgText);
    }

    private void handleInput(float delta) {
        float movX = 0;
        float movY = 0;

        boolean pressLeft  = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean pressRight = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean pressUp    = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean pressDown  = Gdx.input.isKeyPressed(Input.Keys.S);

        if (pressLeft) {
            movX -= velocidadPlayer * delta;
            currentDirection = Direction.LEFT;
        } else if (pressRight) {
            movX += velocidadPlayer * delta;
            currentDirection = Direction.RIGHT;
        } else {
            currentDirection = Direction.IDLE;
        }

        if (pressUp) {
            movY += velocidadPlayer * delta;
        }
        if (pressDown) {
            movY -= velocidadPlayer * delta;
        }

        // Movimiento diagonal
        if (movX != 0 && movY != 0) {
            float factor = (float)(1 / Math.sqrt(2));
            movX *= factor;
            movY *= factor;
        }

        sprite.translate(movX, movY);
    }

    private void basicShot() {
        if (enemyManager == null) return;
        Enemy target = findClosestEnemyInRange();
        if (target == null) return;

        float startX = sprite.getX() + sprite.getWidth()/2;
        float startY = sprite.getY() + sprite.getHeight()/2;

        float targetX = target.getX() + target.getSprite().getWidth()/2;
        float targetY = target.getY() + target.getSprite().getHeight()/2;

        float[] dir = calculateNormalizedDirection(startX, startY, targetX, targetY);

        for (int i = 0; i < projectilePerShot; i++) {
            float angleOffset = (i - (projectilePerShot - 1)/2f) * 5f;
            float adjustedX = (float)(dir[0]*Math.cos(Math.toRadians(angleOffset)) - dir[1]*Math.sin(Math.toRadians(angleOffset)));
            float adjustedY = (float)(dir[0]*Math.sin(Math.toRadians(angleOffset)) + dir[1]*Math.cos(Math.toRadians(angleOffset)));

            projectileManager.addProjectile(startX, startY, adjustedX, adjustedY, target);
        }
    }

    private Enemy findClosestEnemyInRange() {
        if (enemyManager == null) return null;
        float closestDist = Float.MAX_VALUE;
        Enemy closest = null;
        for (Enemy e : enemyManager.getEnemies()) {
            if (!e.isDead()) {
                float dx = e.getX() - sprite.getX();
                float dy = e.getY() - sprite.getY();
                float dist = (float)Math.sqrt(dx*dx + dy*dy);
                if (dist < closestDist && dist <= attackRange) {
                    closestDist = dist;
                    closest = e;
                }
            }
        }
        return closest;
    }

    private float[] calculateNormalizedDirection(float sx, float sy, float tx, float ty) {
        float dx = tx - sx;
        float dy = ty - sy;
        float dist = (float)Math.sqrt(dx*dx + dy*dy);
        if (dist == 0) dist = 1f;
        return new float[]{ dx/dist, dy/dist };
    }

    public void renderPlayerAndProjectile(SpriteBatch batch) {
        if (!isDead) {
            TextureRegion currentFrame;
            switch (currentDirection) {
                case LEFT:
                    currentFrame = movementAnimationLeft.getKeyFrame(animationTimer, true);
                    break;
                case RIGHT:
                    currentFrame = movementAnimationRight.getKeyFrame(animationTimer, true);
                    break;
                default:
                    currentFrame = movementIdle.getKeyFrame(animationTimer, true);
                    break;
            }
            batch.draw(currentFrame, sprite.getX(), sprite.getY(),
                sprite.getWidth(), sprite.getHeight());
        }
        projectileManager.render(batch);
    }

    private boolean isCollidingWith(Enemy enemy) {
        return sprite.getBoundingRectangle().overlaps(enemy.getSprite().getBoundingRectangle());
    }

    public void takeDamage(float amount) {
        if (isDead) return;
        health -= amount;
        if (health <= 0) {
            health = 0;
            die();
        }
    }

    private void die() {
        isDead = true;
        System.out.println("GAME OVER (player died)");
    }

    // --------------------------------------
    // Dispose
    // --------------------------------------
    public void dispose() {
        // Liberamos la textura del sprite
        if (sprite != null && sprite.getTexture() != null) {
            sprite.getTexture().dispose();
        }
        // Liberamos manager de proyectiles
        if (projectileManager != null) {
            projectileManager.dispose();
        }
    }

    // --------------------------------------
    // Métodos "upgrade"
    // --------------------------------------
    public void increaseSpeed(float percentage) {
        velocidadPlayer += velocidadPlayer * percentage;
    }
    public void increaseAttackRange(float percentage) {
        attackRange += attackRange * percentage;
    }
    public void increaseDamage(float amount) {
        projectileManager.increaseDamage(amount);
    }
    public void reduceShootInterval(float percentage) {
        shootInterval *= (1 - percentage);
        if (shootInterval < 0.1f) {
            shootInterval = 0.1f;
        }
    }
    public void increaseProjectilesPerShot(int amount) {
        projectilePerShot += amount;
    }

    // --------------------------------------
    // Getters / Setters
    // --------------------------------------
    public float getHealth() {
        return health;
    }
    public float getMaxHealth() {
        return maxHealth;
    }
    public float getHealthPercentage() {
        return health / maxHealth;
    }
    public boolean isDead() {
        return isDead;
    }
    public Sprite getSprite() {
        return sprite;
    }
    public ProjectileManager getProjectileManager() {
        return projectileManager;
    }
    public void setEnemyManager(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
    }
}

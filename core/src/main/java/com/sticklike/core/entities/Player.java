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
    private Enemy enemyTarget;
    private EnemyManager enemyManager;
    private ProjectileManager projectileManager;
    private Sprite sprite;
    private float velocidadPlayer;
    private float health;
    private float maxHealth;
    private float attackRange;
    private float shootInterval;
    private float temporizadorDisparo = 0;
    private int projectilePerShot = 1;
    private boolean isDead;
    private float currentExperience = 0f;
    private float experienceToNextLevel = 100f;
    private int level = 1;
    private Animation<TextureRegion> movementAnimationRight, movementAnimationLeft, movementIdle;
    private float animationTimer = 0;

    public Player(float startX, float startY) {
        this.velocidadPlayer = GameConfig.PLAYER_SPEED;
        this.health = GameConfig.PLAYER_HEALTH;
        this.maxHealth = GameConfig.PLAYER_MAX_HEALTH;
        this.attackRange = GameConfig.PLAYER_ATTACK_RANGE;
        this.shootInterval = GameConfig.PLAYER_SHOOT_INTERVAL;

        sprite = new Sprite(AssetLoader.stickman);
        sprite.setSize(15, 45);
        projectileManager = new ProjectileManager();
        isDead = false;

        // Acceso a animaciones desde el HashMap
        movementIdle = AssetLoader.animations.get("idle");
        movementAnimationRight = AssetLoader.animations.get("moveRight");
        movementAnimationLeft = AssetLoader.animations.get("moveLeft");
    }

    public boolean isDead() {
        return isDead;
    }

    private void die() {
        isDead = true;
        System.out.println("GAME OVER");
        // todo -> implementar lógica adicional al morir
    }

    public void renderPlayerAndProjectile(SpriteBatch batch) {
        if (!isDead) {
            TextureRegion currentFrame;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) { // Moviéndose a la derecha
                animationTimer += Gdx.graphics.getDeltaTime();
                currentFrame = movementAnimationRight.getKeyFrame(animationTimer, true);
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) { // Moviéndose a la izquierda
                animationTimer += Gdx.graphics.getDeltaTime();
                currentFrame = movementAnimationLeft.getKeyFrame(animationTimer, true);
            } else {
                animationTimer += Gdx.graphics.getDeltaTime();
                currentFrame = movementIdle.getKeyFrame(animationTimer, true);
            }
            batch.draw(currentFrame, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        }
        projectileManager.render(batch);
    }


    public void updatePlayer(float delta, Array<InGameText> dmgText) {
        if (enemyManager == null) {
            System.err.println("EnemyManager no está inicializado.");
            return;
        }
        if (isDead) return;

        animationTimer += delta;

        // Verifica si hay enemigos en colisión y aplica daño al player.
        for (Enemy enemy : enemyManager.getEnemies()) {
            if (isCollidingWith(enemy) && enemy.canDealDamage()) {
                takeDamage(2);
                enemy.resetDamageTimer();
            }
        }
        handleInput(delta);
        updateTarget();

        temporizadorDisparo += delta;
        if (temporizadorDisparo >= shootInterval) {
            temporizadorDisparo = 0;
            basicShot();
        }

        projectileManager.update(delta, enemyManager.getEnemies(),dmgText);
    }

    private void updateTarget() {
        float closestDistance = Float.MAX_VALUE;
        Enemy closestEnemy = null;

        for (Enemy enemy : enemyManager.getEnemies()) {
            if (!enemy.isDead()) {
                float distance = (float) Math.sqrt(
                    Math.pow(enemy.getX() - sprite.getX(), 2) +
                        Math.pow(enemy.getY() - sprite.getY(), 2));

                if (distance < closestDistance && distance <= attackRange) {
                    closestDistance = distance;
                    closestEnemy = enemy;
                }
            }
        }

        // Solo actualizamos el objetivo si encontramos uno válido en rango.
        if (closestEnemy != null) {
            enemyTarget = closestEnemy;
        } else {
            enemyTarget = null;
        }
    }

    private float[] calculateNormalizedDirection(float startX, float startY, float targetX, float targetY) {
        float dx = targetX - startX;
        float dy = targetY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return new float[]{dx / distance, dy / distance};
    }

    private void basicShot() {
        if (enemyTarget == null || enemyTarget.isDead()) return;

        float distanceToTarget = (float) Math.sqrt(
            Math.pow(enemyTarget.getX() - sprite.getX(), 2) +
                Math.pow(enemyTarget.getY() - sprite.getY(), 2)
        );

        if (distanceToTarget > attackRange) {
            updateTarget();
        }

        if (enemyTarget == null || enemyTarget.isDead()) return;

        float startX = sprite.getX() + sprite.getWidth() / 2;
        float startY = sprite.getY() + sprite.getHeight() / 2;

        float targetX = enemyTarget.getX() + enemyTarget.getSprite().getWidth() / 2;
        float targetY = enemyTarget.getY() + enemyTarget.getSprite().getHeight() / 2;

        float[] direction = calculateNormalizedDirection(startX, startY, targetX, targetY);

        for (int i = 0; i < projectilePerShot; i++) {
            // Ajuste para disparar en ángulos levemente diferentes para cada proyectil.
            float angleOffset = (i - (projectilePerShot - 1) / 2f) * 5f; // Ángulo entre proyectiles.
            float adjustedDirectionX = (float) (direction[0] * Math.cos(Math.toRadians(angleOffset)) - direction[1] * Math.sin(Math.toRadians(angleOffset)));
            float adjustedDirectionY = (float) (direction[0] * Math.sin(Math.toRadians(angleOffset)) + direction[1] * Math.cos(Math.toRadians(angleOffset)));

            projectileManager.addProjectile(startX, startY, adjustedDirectionX, adjustedDirectionY, enemyTarget);
        }
    }

    public void increaseSpeed(float percentage) {
        this.velocidadPlayer += this.velocidadPlayer * percentage;
        System.out.println("Nueva velocidad: " + this.velocidadPlayer);
    }

    public void increaseAttackRange(float percentage) {
        this.attackRange += this.attackRange * percentage;
        System.out.println("Nuevo rango de ataque: " + this.attackRange);
    }
    public void increaseDamage(float amount) {
        projectileManager.increaseDamage(amount);
        System.out.println("Daño de proyectiles aumentado.");
    }

    public void reduceShootInterval(float percentage) {
        this.shootInterval *= (1 - percentage);
        if (this.shootInterval < 0.1f) {
            this.shootInterval = 0.1f;
        }
        System.out.println("Nuevo intervalo de disparo: " + this.shootInterval);
    }

    public void increaseProjectilesPerShot(int amount) {
        projectilePerShot += amount;
        System.out.println("Número de proyectiles por disparo aumentado a: " + projectilePerShot);
    }
    private void handleInput(float delta) {
        float movimientoX = 0;
        float movimientoY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            movimientoX -= velocidadPlayer * delta;
            animationTimer += delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            movimientoX += velocidadPlayer * delta;
            animationTimer += delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) movimientoY += velocidadPlayer * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) movimientoY -= velocidadPlayer * delta;

        if (movimientoX != 0 && movimientoY != 0) {
            float factorNormalizacion = (float) (1 / Math.sqrt(2));
            movimientoX *= factorNormalizacion;
            movimientoY *= factorNormalizacion;
        }

        sprite.translate(movimientoX, movimientoY);
    }


    private boolean isCollidingWith(Enemy enemy) {
        return sprite.getBoundingRectangle().overlaps(enemy.getSprite().getBoundingRectangle());
    }

    public void takeDamage(float damage) {
        if (isDead) return;

        health -= damage;
        if (health <= 0) {
            health = 0;
            die();
        }
    }

    public void dispose() {
        sprite.getTexture().dispose();
        projectileManager.dispose();
    }

    public void setEnemyManager(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public float getHealth() {
        return health;
    }

    public float getHealthPercentage() {
        return health / maxHealth;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public ProjectileManager getProjectileManager() {
        return projectileManager;
    }


}

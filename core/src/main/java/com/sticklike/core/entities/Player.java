package com.sticklike.core.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.managers.EnemyManager;
import com.sticklike.core.managers.ProjectileManager;
import com.sticklike.core.screens.GameScreen;

import java.util.Random;

public class Player {
    private Enemy enemyTarget;
    private EnemyManager enemyManager;
    private ProjectileManager projectileManager;
    private Sprite sprite;
    private float velocidadPlayer = 125;
    private float health = 50f;
    private float maxHealth = 50;
    private float temporizadorDisparo = 0;
    private static final float SHOOT_INTERVAL = 0.9f; // Intervalo mínimo entre disparos en segundos.
    private float attackRange = 200f;
    private static final Random random = new Random();

    public Player(float startX, float startY) {
        Texture texture = new Texture("stickman.png");
        sprite = new Sprite(texture);
        sprite.setSize(20, 70);
        sprite.setPosition(startX - sprite.getWidth() / 2, startY - sprite.getHeight() / 2);
        projectileManager = new ProjectileManager();
    }


    public void renderPlayerAndProjectile(SpriteBatch batch) {
        sprite.draw(batch);
        projectileManager.render(batch);
    }

    public void updatePlayer(float delta, Array<InGameText> dmgText) {
        float movimientoX = 0;
        float movimientoY = 0;

        // Verifica si hay enemigos en colisión y aplica daño al player.
        for (Enemy enemy : enemyManager.getEnemies()) {
            if (isCollidingWith(enemy) && enemy.canDealDamage()) {
                takeDamage(1);
                enemy.resetDamageTimer();
            }
        }

        // Movimiento del jugador.
        if (Gdx.input.isKeyPressed(Input.Keys.A)) movimientoX -= velocidadPlayer * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) movimientoX += velocidadPlayer * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) movimientoY += velocidadPlayer * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) movimientoY -= velocidadPlayer * delta;

        if (movimientoX != 0 && movimientoY != 0) {
            float factorNormalizacion = (float) (1 / Math.sqrt(2));
            movimientoX *= factorNormalizacion;
            movimientoY *= factorNormalizacion;
        }

        sprite.translate(movimientoX, movimientoY); // Aplica el movimiento al sprite.

        updateTarget();

        temporizadorDisparo += delta;
        if (temporizadorDisparo >= SHOOT_INTERVAL) {
            temporizadorDisparo = 0;
            basicShot();
        }

        projectileManager.update(delta, dmgText);
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

    private void basicShot() {
        if (enemyTarget == null || enemyTarget.isDead()) return;

        // Verifica si el objetivo actual sigue dentro del rango.
        float distanceToTarget = (float) Math.sqrt(
            Math.pow(enemyTarget.getX() - sprite.getX(), 2) +
                Math.pow(enemyTarget.getY() - sprite.getY(), 2));

        if (distanceToTarget > attackRange) {
            updateTarget();
        }

        if (enemyTarget == null || enemyTarget.isDead()) return;

        // Calcula la posición inicial del proyectil.
        float startX = sprite.getX() + sprite.getWidth() / 2;
        float startY = sprite.getY() + sprite.getHeight() / 2;

        // Calcula la dirección hacia el enemigo.
        float targetX = enemyTarget.getX() + enemyTarget.getSprite().getWidth() / 2;
        float targetY = enemyTarget.getY() + enemyTarget.getSprite().getHeight() / 2;

        float dx = targetX - startX;
        float dy = targetY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= attackRange) {
            dx /= distance; // Normaliza la dirección.
            dy /= distance;
            projectileManager.addProjectile(startX, startY, dx, dy, enemyTarget);
        }
    }

    private boolean isCollidingWith(Enemy enemy) {
        return sprite.getBoundingRectangle().overlaps(enemy.getSprite().getBoundingRectangle());
    }

    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            alMorir();
        }
    }

    private void alMorir() {
        System.out.println("GAME OVER");
        // todo -> hay que gestionar una pantalla de game over
    }

    public void dispose() {
        sprite.getTexture().dispose();
        projectileManager.dispose();
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
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
}

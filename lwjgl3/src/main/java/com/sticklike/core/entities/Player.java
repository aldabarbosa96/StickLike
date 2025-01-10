package com.sticklike.core.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.sticklike.core.screens.GameScreen;

import java.util.ArrayList;
import java.util.Iterator;

public class Player {
    private Enemy enemyTarget;
    private ArrayList<Projectile> projectiles;
    private Sprite sprite;
    private float velocidadPlayer = 125;
    private float temporizadorDisparo = 0;
    private static final float SHOOT_INTERVAL = 1;
    private float attackRange = 250f;

    public Player() {
        Texture texture = new Texture("stickman.png");
        sprite = new Sprite(texture);
        sprite.setSize(85, 75);
        sprite.setPosition(
            (GameScreen.WORLD_WIDTH / 2f) - sprite.getWidth() / 2,
            (GameScreen.WORLD_HEIGHT / 2f) - sprite.getHeight() / 2);

        projectiles = new ArrayList<>();
    }

    public void renderPlayer(SpriteBatch batch) {
        sprite.draw(batch);

        for (Projectile projectile : projectiles) {
            projectile.render(batch);
        }
    }

    public void updatePlayer(float delta) {
        float movimientoX = 0;
        float movimientoY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            movimientoX -= velocidadPlayer * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            movimientoX += velocidadPlayer * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            movimientoY += velocidadPlayer * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            movimientoY -= velocidadPlayer * delta;
        }

        // Estandariza el movimiento diagonal para ajustar la velocidad con la vertical/horizontal
        if (movimientoX != 0 && movimientoY != 0) {
            float factorNormalizacion = (float) (1 / Math.sqrt(2));
            movimientoX *= factorNormalizacion;
            movimientoY *= factorNormalizacion;
        }

        sprite.translate(movimientoX, movimientoY);

        // Disparo automático cada SHOOT_INTERVAL
        temporizadorDisparo += delta;
        if (temporizadorDisparo >= SHOOT_INTERVAL) {
            temporizadorDisparo = 0;
            basicShot();
        }

        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update(delta);

            // Comprobamos si el proyectil colisiona con el enemigo para aplicar efectos
            if (enemyTarget != null && enemyTarget.isHitBy(
                projectile.getX(),
                projectile.getY(),
                projectile.getBoundingRectangle().width,
                projectile.getBoundingRectangle().height)) {

                enemyTarget.reduceHealth(34f);
                iterator.remove();
            }

        }

    }
    public Sprite getSprite() {
        return sprite;
    }

    public void setEnemyTarget(Enemy enemy) {
        this.enemyTarget = enemy;
    }

    private void basicShot() {
        if (enemyTarget == null || enemyTarget.isDead()) return;

        float startX = sprite.getX() + sprite.getWidth() / 2;
        float startY = sprite.getY() + sprite.getHeight() / 2;

        float targetX = enemyTarget.getX() + enemyTarget.getSprite().getWidth() / 2;
        float targetY = enemyTarget.getY() + enemyTarget.getSprite().getHeight() / 2;

        // Calculamos distancia con el target para comparar con attackRange
        float dx = targetX - startX;
        float dy = targetY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Solo disparamos si el enemigo está dentro de attackRange para posibilitar futura escalabilidad
        if (distance <= attackRange) {
            float length = distance;
            if (length != 0) {
                dx /= length;
                dy /= length;
            }
            projectiles.add(new Projectile(startX, startY, dx, dy));


            //debugging logs
            Gdx.app.log("Projectile", "Start: (" + startX + ", " + startY + ")");
            Gdx.app.log("Projectile", "Direction: (" + dx + ", " + dy + ")");
        }
    }


}

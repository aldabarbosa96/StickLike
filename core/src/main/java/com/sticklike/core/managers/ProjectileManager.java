package com.sticklike.core.managers;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entities.Enemy;
import com.sticklike.core.entities.InGameText;
import com.sticklike.core.entities.Projectile;
import java.util.Iterator;

public class ProjectileManager {
    private ArrayList<Projectile> projectiles;
    private float damageMultiplier = 1.0f;


    public ProjectileManager() {
        projectiles = new ArrayList<>();
    }

    public void addProjectile(float startX, float startY, float dx, float dy, Enemy target) {
        float randomSpeedMultiplier = 0.7f + (float) Math.random() * 0.3f;
        projectiles.add(new Projectile(startX, startY, dx, dy, target, randomSpeedMultiplier));
    }

    public void update(float delta, Array<Enemy> enemies, Array<InGameText> dmgText) {
        Iterator<Projectile> iterator = projectiles.iterator();

        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update(delta);

            for (Enemy enemy : enemies) {
                if (!enemy.isDead() && projectile.isActive() &&
                    enemy.isHitBy(projectile.getX(), projectile.getY(), projectile.getBoundingRectangle().width, projectile.getBoundingRectangle().height)) {

                    // Cálculo del daño con el multiplicador aplicado
                    float baseDamage = 25 + (float) Math.random() * 10; // Daño base aleatorio entre 25 y 34
                    float damage = baseDamage * damageMultiplier;

                    enemy.reduceHealth(damage);

                    // Mostrar el daño infligido como texto flotante
                    dmgText.add(new InGameText(
                        String.valueOf((int) damage),
                        enemy.getX() + enemy.getSprite().getWidth() / 2,
                        enemy.getY() + enemy.getSprite().getHeight() + 20,
                        0.5f
                    ));

                    projectile.deactivate(); // El proyectil impacta y se desactiva
                    break; // No necesitamos seguir verificando más enemigos para este proyectil
                }
            }

            if (!projectile.isActive()) {
                iterator.remove(); // Eliminar proyectiles inactivos
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Projectile projectile : projectiles) {
            projectile.render(batch);
        }
    }

    public void increaseDamage(float multiplier) {
        damageMultiplier += multiplier;
        System.out.println("Multiplicador de daño actualizado a: " + damageMultiplier);
    }

    public void dispose() {
        for (Projectile projectile : projectiles) {
            projectile.dispose();
        }
    }
}


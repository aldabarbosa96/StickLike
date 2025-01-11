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

    public ProjectileManager() {
        projectiles = new ArrayList<>();
    }

    public void addProjectile(float startX, float startY, float dx, float dy, Enemy target) {
        projectiles.add(new Projectile(startX, startY, dx, dy, target));
    }

    public void update(float delta, Array<InGameText> dmgText) {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update(delta);

            Enemy target = projectile.getTarget();

            if (projectile.isActive() && target != null && !target.isDead() && target.isHitBy(
                projectile.getX(),
                projectile.getY(),
                projectile.getBoundingRectangle().width,
                projectile.getBoundingRectangle().height)) {

                float damage = 25 + (float) Math.random() * 10; // Daño aleatorio entre 25 y 34.
                target.reduceHealth(damage);

                dmgText.add(new InGameText(
                    String.valueOf((int) damage),
                    target.getX() + target.getSprite().getWidth() / 2,
                    target.getY() + target.getSprite().getHeight() + 20,
                    0.5f
                ));

                projectile.deactivate();
                iterator.remove();
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Projectile projectile : projectiles) {
            projectile.render(batch);
        }
    }

    public void dispose() {
        for (Projectile projectile : projectiles) {
            projectile.dispose();
        }
    }
}


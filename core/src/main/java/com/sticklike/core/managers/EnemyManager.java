package com.sticklike.core.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entities.Enemy;
import com.sticklike.core.entities.Player;
import com.sticklike.core.screens.GameScreen;

public class EnemyManager {
    private Array<Enemy> enemies;
    private Player player;
    private float spawnInterval, spawnTimer;
    private static final float BORDER_MARGIN = 100f;

    public EnemyManager(Player player, float spawnInterval) {
        this.enemies = new Array<>();
        this.player = player;
        this.spawnInterval = spawnInterval;
        this.spawnTimer = 0;
    }

    public void update(float delta) {
        spawnTimer += delta;
        if (spawnTimer >= spawnInterval) {
            spawnEnemy();
            spawnTimer = 0;
        }

        for (Enemy enemy : enemies) {
            enemy.updateEnemy(delta);
            if (enemy.isDead()) {
                enemies.removeValue(enemy, true);
            }
        }
    }

    public void render(SpriteBatch batch) {
        enemies.sort((e1, e2) -> Float.compare(e2.getY(), e1.getY()));
        for (Enemy enemy : enemies) {
            enemy.renderEnemy(batch);
        }
    }

    public void dispose() {
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }

    private void spawnEnemy() {
        float minDistance = 300f;
        float x, y;

        // Calcula los límites dinámicos alrededor del jugador en base a su posición.
        float playerX = player.getSprite().getX() + player.getSprite().getWidth() / 2;
        float playerY = player.getSprite().getY() + player.getSprite().getHeight() / 2;

        float leftLimit = playerX - GameScreen.WORLD_WIDTH / 2 + BORDER_MARGIN;
        float rightLimit = playerX + GameScreen.WORLD_WIDTH / 2 - BORDER_MARGIN;
        float bottomLimit = playerY - GameScreen.WORLD_HEIGHT / 2 + BORDER_MARGIN;
        float topLimit = playerY + GameScreen.WORLD_HEIGHT / 2 - BORDER_MARGIN;

        do {
            // Genera coordenadas aleatorias dentro de los límites dinámicos.
            x = leftLimit + (float) (Math.random() * (rightLimit - leftLimit));
            y = bottomLimit + (float) (Math.random() * (topLimit - bottomLimit));

        } while (Math.sqrt(Math.pow(x - playerX, 2) + Math.pow(y - playerY, 2)) < minDistance);

        float randomSpeed = 40f + (float) Math.random() * 45f;

        enemies.add(new Enemy(x, y, player, randomSpeed));
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }
}

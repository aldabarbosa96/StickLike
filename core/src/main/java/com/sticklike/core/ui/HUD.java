package com.sticklike.core.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sticklike.core.entities.Player;

public class HUD {
    private ShapeRenderer shapeRenderer;
    private Player player;

    public HUD(Player player) {
        this.player = player;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void renderHUD(SpriteBatch batch) {
        renderHealthBar();
    }

    private void renderHealthBar() {
        float healthPercentage = player.getHealthPercentage();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(1, 0, 0, 1); // Barra de vida
        shapeRenderer.rect(10, 680, 200, 20);

        shapeRenderer.setColor(0, 1, 0, 1); // Vida restante
        shapeRenderer.rect(10, 680, 200 * healthPercentage, 20);

        shapeRenderer.end();
    }

    public void dispose(){
        shapeRenderer.dispose();
    }
}

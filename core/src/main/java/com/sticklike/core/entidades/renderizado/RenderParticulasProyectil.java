package com.sticklike.core.entidades.renderizado;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class RenderParticulasProyectil {

    private Array<Vector2> positions;
    private int maxLength;
    private float width;
    private Color color;
    private ShapeRenderer shapeRenderer;

    public RenderParticulasProyectil(int maxLength, float width, Color color) {
        this.maxLength = maxLength;
        this.width = width;
        this.color = color;
        this.positions = new Array<>();
        this.shapeRenderer = new ShapeRenderer();
    }

    public void update(Vector2 position) {
        positions.add(position.cpy());
        if (positions.size > maxLength) {
            positions.removeIndex(0);
        }
    }

    public void render(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < positions.size - 1; i++) {
            Vector2 p1 = positions.get(i);
            Vector2 p2 = positions.get(i + 1);

            float angle = MathUtils.atan2(p2.y - p1.y, p2.x - p1.x);
            float halfWidth = width / 2;
            float x1 = p1.x - halfWidth * MathUtils.cos(angle + MathUtils.PI / 2);
            float y1 = p1.y - halfWidth * MathUtils.sin(angle + MathUtils.PI / 2);
            float x2 = p2.x - halfWidth * MathUtils.cos(angle + MathUtils.PI / 2);
            float y2 = p2.y - halfWidth * MathUtils.sin(angle + MathUtils.PI / 2);
            float x3 = p2.x + halfWidth * MathUtils.cos(angle + MathUtils.PI / 2);
            float y3 = p2.y + halfWidth * MathUtils.sin(angle + MathUtils.PI / 2);
            float x4 = p1.x + halfWidth * MathUtils.cos(angle + MathUtils.PI / 2);
            float y4 = p1.y + halfWidth * MathUtils.sin(angle + MathUtils.PI / 2);

            // Calculamos el valor alpha para el fade
            float alpha = ((float) i / (positions.size - 1));
            shapeRenderer.setColor(color.r, color.g, color.b, alpha);

            // Dibujamos la forma como dos triángulos que forman un cuadrilátero
            shapeRenderer.triangle(x1, y1, x2, y2, x3, y3);
            shapeRenderer.triangle(x1, y1, x3, y3, x4, y4);
        }

        shapeRenderer.end();
        batch.begin();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }


    public void dispose() {
        shapeRenderer.dispose();
    }
}

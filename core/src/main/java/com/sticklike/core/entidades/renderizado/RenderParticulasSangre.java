package com.sticklike.core.entidades.renderizado;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Clase que maneja la creación y renderizado de manchas (salpicaduras) de sangre, las cuales aparecen estáticas en el mapa y van desapareciendo (fade out) con el tiempo.
 */
public class RenderParticulasSangre {

    /**
     * Clase interna que representa cada partícula (gota/mancha) de sangre.
     */
    private class BloodParticle {
        Vector2 position;
        float radius;
        float lifetime;
        float elapsed;
        Color color;
        float initialAlpha;

        public BloodParticle(Vector2 position, float radius, float lifetime, Color color) {
            this.position = new Vector2(position);
            this.radius = radius;
            this.lifetime = lifetime;
            this.elapsed = 0f;
            this.color = new Color(color);
            this.initialAlpha = color.a;
        }


        public boolean update(float delta) {
            elapsed += delta;
            float alpha = initialAlpha *(1f - (elapsed / lifetime));
            if (alpha <= 0f) {
                return false;
            }
            color.a = alpha;
            return true;
        }

        public void render(ShapeRenderer shapeRenderer) {
            shapeRenderer.setColor(color);
            shapeRenderer.circle(position.x, position.y, radius);
        }
    }

    // Lista de partículas (manchas) activas
    private Array<BloodParticle> particles;
    private ShapeRenderer shapeRenderer;

    public RenderParticulasSangre() {
        this.particles = new Array<>();
        this.shapeRenderer = new ShapeRenderer();
    }

    public void spawnBlood(Vector2 center, int cantidadGotas) {
        for (int i = 0; i < cantidadGotas; i++) {
            float offsetX = MathUtils.random(-7.5f, 7.5f);
            float offsetY = MathUtils.random(-7.5f, 7.5f);
            Vector2 spawnPos = new Vector2(center.x + offsetX, center.y + offsetY);

            float radius = MathUtils.random(1f, 3f);
            float lifetime = MathUtils.random(2.5f, 5f);
            Color color = new Color(0.75f, 0, 0, 0.75f);

            particles.add(new BloodParticle(spawnPos, radius, lifetime, color));
        }
    }

    public void update(float delta) {
        for (int i = particles.size - 1; i >= 0; i--) {
            boolean alive = particles.get(i).update(delta);
            if (!alive) {
                particles.removeIndex(i);
            }
        }
    }


    public void render(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (BloodParticle p : particles) {
            p.render(shapeRenderer);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}


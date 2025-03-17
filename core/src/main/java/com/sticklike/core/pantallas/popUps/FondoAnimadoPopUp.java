package com.sticklike.core.pantallas.popUps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Dibuja y actualiza el fondo animado con partículas de experiencia que caen para mejora visual del pop-up de mejoras.
 */

public class FondoAnimadoPopUp extends Actor {
    // Cargamos las texturas una sola vez para poder reutilizarlas
    private static final Texture xpTexture1 = new Texture(GestorDeAssets.RECOLECTABLE_XP);
    private static final Texture xpTexture2 = new Texture(GestorDeAssets.RECOLECTABLE_XP2);

    private List<Particle> particles;
    private float spawnTimer;
    private float gravity = -200f;

    public FondoAnimadoPopUp() {
        this.particles = new ArrayList<>();
        this.spawnTimer = 0f;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        spawnTimer += delta;
        // Genera una nueva partícula cada 0.1 segundos
        if (spawnTimer >= 0.075f) {
            spawnTimer = 0f;
            spawnParticle();
        }
        // Actualiza todas las partículas y elimina aquellas que ya han caído fuera de la pantalla
        Iterator<Particle> iter = particles.iterator();
        while (iter.hasNext()) {
            Particle particle = iter.next();
            particle.update(delta, gravity);
            if (particle.y + particle.texture.getHeight() < 0) {
                iter.remove();
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (Particle p : particles) {
            batch.draw(p.texture, p.x, p.y, 25, 25);
        }
    }


    private void spawnParticle() {
        float x = MathUtils.random() * VIRTUAL_WIDTH;
        float y = VIRTUAL_HEIGHT;
        float vx = (MathUtils.random() - 0.5f) * 50; // entre -25 y 25 píxeles/segundo
        float vy = -(MathUtils.random() * 50 + 50);  // entre -50 y -100 píxeles/segundo (hacia abajo)
        Texture chosenTexture = selectTexture();
        particles.add(new Particle(x, y, vx, vy, chosenTexture));
    }


    private Texture selectTexture() {
        float randomTexture = MathUtils.random(10);
        return randomTexture <= 5 ? xpTexture1 : xpTexture2;
    }

    /**
     * Clase interna que representa cada partícula (fragmento de experiencia)
     * Cada partícula almacena la textura que se le asignó al nacer.
     */
    private static class Particle {
        float x, y;
        float vx, vy;
        Texture texture;

        public Particle(float x, float y, float vx, float vy, Texture texture) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.texture = texture;
        }

        public void update(float delta, float gravity) {
            vy += gravity * delta;
            x += vx * delta;
            y += vy * delta;
        }
    }
}

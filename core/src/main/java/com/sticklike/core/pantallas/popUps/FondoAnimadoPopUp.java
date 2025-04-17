package com.sticklike.core.pantallas.popUps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.manager;

/**
 * Dibuja y actualiza el fondo animado con partículas de experiencia que caen, rotan y se desplazan como si fueran impulsadas bajo el agua.
 */
public class FondoAnimadoPopUp extends Actor {
    private static final Texture xpTexture1 = manager.get(RECOLECTABLE_XP, Texture.class);
    private static final Texture xpTexture2 =  manager.get(RECOLECTABLE_XP2, Texture.class);
    private static final Texture xpTexture3 =  manager.get(RECOLECTABLE_XP3, Texture.class);
    private List<Particle> particles;
    private float spawnTimer;
    private static final float DAMPING = 0.98f; // Factor de damping para simular resistencia del agua (aplicamos solo a vx para que baje)
    private static final float HUD_THRESHOLD = 240;

    public FondoAnimadoPopUp() {
        this.particles = new ArrayList<>();
        this.spawnTimer = 0f;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        spawnTimer += delta;
        if (spawnTimer >= 0.085f) {
            spawnTimer = 0f;
            spawnParticle();
        }
        // Actualizamos las partículas y marcamos para fade-out o eliminamos las que ya han desaparecido
        Iterator<Particle> iter = particles.iterator();
        while (iter.hasNext()) {
            Particle p = iter.next();
            p.update(delta);
            // Cuando la partícula alcanza el umbral del HUD, inicia el fade-out
            if (!p.fading && (p.y + p.height < HUD_THRESHOLD)) {
                p.fading = true;
            }
            // Si la partícula ya está en fade-out y se ha desvanecido completamente, se elimina
            if (p.fading && p.alpha <= 0) {
                iter.remove();
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Guardamosd el color original del batch para restaurarlo luego
        Color originalColor = batch.getColor();
        for (Particle p : particles) {
            batch.setColor(1, 1, 1, p.alpha);
            // Dibuja la partícula con rotación; el origen es el centro
            batch.draw(p.texture, p.x, p.y, p.width / 2, p.height / 2, p.width, p.height, 1, 1, p.rotation, 0, 0, p.texture.getWidth(), p.texture.getHeight(), false, false);
        }
        batch.setColor(originalColor);
    }

    private void spawnParticle() {
        float x = MathUtils.random() * VIRTUAL_WIDTH;
        // Velocidad horizontal aleatoria
        float vx = (MathUtils.random() - 0.5f) * 50;
        // Velocidad vertical negativa para que caiga
        float vy = -(MathUtils.random() * 50 + 50);
        float size = MathUtils.random(30, 40);
        particles.add(new Particle(x, VIRTUAL_HEIGHT, vx, vy, selectTexture(), size, size));
    }

    private Texture selectTexture() {
        float randomTexture = MathUtils.random(10);
        if (randomTexture <= 4.5f) return xpTexture1;
        else if (randomTexture <= 9) return xpTexture2;
        else return xpTexture3;
    }

    /**
     * Representa una partícula con posición, velocidad, textura, dimensiones, rotación, y efecto de fade-out.
     */
    private static class Particle {
        float x, y;
        float vx, vy;
        float width, height;
        Texture texture;
        float rotation;
        float angularVelocity; // Velocidad angular en grados/segundo
        float time;
        boolean fading;
        float alpha;
        private static final float FADE_RATE = 2f;

        public Particle(float x, float y, float vx, float vy, Texture texture, float width, float height) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.texture = texture;
            this.width = width;
            this.height = height;
            this.rotation = MathUtils.random(360);
            this.angularVelocity = MathUtils.random(-90, 90);
            this.time = 0;
            this.fading = false;
            this.alpha = 1f;
        }

        public void update(float delta) {
            time += delta;
            float oscillation = MathUtils.sin(time * 5) * 10; // Oscilación horizontal de 10 píxeles
            x += (vx * delta) + oscillation * delta;
            y += vy * delta;  // Actualiza la posición vertical
            vx *= DAMPING;   // Aplica damping solo a la velocidad horizontal
            rotation += angularVelocity * delta;
            if (fading) {
                alpha -= FADE_RATE * delta;
                if (alpha < 0) {
                    alpha = 0;
                }
            }
        }
    }
}

package com.sticklike.core.pantallas.popUps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class FondoAnimadoPopUp extends Actor {
    private final Texture[] particleTextures;
    private static final float SPAWN_INTERVAL = .085f;
    private static final float DAMPING = .98f;
    private static final float HUD_THRESHOLD = 240f;
    private final Array<Particle> live = new Array<>(false, 128);
    private final Pool<Particle> pool = new Pool<>(64, 256) {
        @Override
        protected Particle newObject() {
            return new Particle();
        }
    };
    private float spawnTimer = 0f;
    private final Color tmpColor = new Color();
    private float x, y;


    public FondoAnimadoPopUp(float x, float y, Texture... textures) {
        this.particleTextures = textures;
        this.x = x;
        this.y = y;
    }

    @Override
    public void act(float delta) {
        spawnTimer += delta;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer -= SPAWN_INTERVAL;
            spawnParticle();
        }

        for (int i = live.size - 1; i >= 0; i--) {
            Particle p = live.get(i);
            p.update(delta);
            if (!p.fading && p.y + p.height < HUD_THRESHOLD) p.fading = true;
            if (p.fading && p.alpha <= 0f) {
                live.removeIndex(i);
                pool.free(p);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        tmpColor.set(batch.getColor());
        for (Particle p : live) {
            batch.setColor(1, 1, 1, p.alpha);
            batch.draw(p.tex, p.x, p.y, p.width * .5f, p.height * .5f, p.width, p.height, 1, 1, p.rot, 0, 0, p.tex.getWidth(), p.tex.getHeight(), false, false);
        }
        batch.setColor(tmpColor);
    }

    // helpers

    private void spawnParticle() {
        Particle p = pool.obtain();
        float size = MathUtils.random(x, y);
        float vx = (MathUtils.random() - .5f) * 50f;
        float vy = -(MathUtils.random() * 50f + 50f);
        p.init(MathUtils.random() * VIRTUAL_WIDTH, VIRTUAL_HEIGHT, vx, vy, selectTex(), size);
        live.add(p);
    }

    private Texture selectTex() {
        if (particleTextures == null || particleTextures.length == 0) {
            throw new IllegalStateException("Amigo te faltan las texturas...");
        }
        int idx = MathUtils.random(particleTextures.length - 1);
        return particleTextures[idx];
    }

    public void clearParticles() {
        for (Particle p : live) pool.free(p);
        live.clear();
    }

    // pool partículas
    private static class Particle implements Pool.Poolable {
        float x, y, vx, vy, width, height, rot, angVel, alpha;
        Texture tex;
        boolean fading;
        float t;

        void init(float x, float y, float vx, float vy, Texture tex, float size) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.tex = tex;
            this.width = this.height = size;
            this.rot = MathUtils.random(360f);
            this.angVel = MathUtils.random(-90f, 90f);
            this.alpha = 1f;
            this.fading = false;
            this.t = 0f;
        }

        void update(float d) {
            t += d;
            x += (vx * d) + MathUtils.sin(t * 5f) * 10f * d;
            y += vy * d;
            vx *= DAMPING;
            rot += angVel * d;
            if (fading) alpha = Math.max(0f, alpha - 2f * d);
        }

        @Override
        public void reset() {
        } // nada
    }
}

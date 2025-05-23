package com.sticklike.core.entidades.renderizado.particulas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton que centraliza la creación, actualización y renderizado
 * de ParticleEffects usando un pool para evitar new/dispose continuos, con soporte de capas: fondo y primer plano.
 */
public class ParticleManager {
    private static final ParticleManager INSTANCE = new ParticleManager();

    public static ParticleManager get() {
        return INSTANCE;
    }

    private final Map<String, ParticleEffectPool> pools = new HashMap<>();
    private final Array<ParticleEffectPool.PooledEffect> backgroundEffects = new Array<>();
    private final Array<ParticleEffectPool.PooledEffect> foregroundEffects = new Array<>();

    private ParticleManager() {
    }

    public void loadAllParticles() {
        load("piedra", "particulas/basic_particle.p");
        load("calcetin", "particulas/calcetin_particle.p");
        load("pedo", "particulas/pedo_particle.p");
        load("tazo", "particulas/tazo_particle.p");
        load("boli", "particulas/boli_particle.p");
        load("pipi", "particulas/pipi_particle.p");
        load("papel", "particulas/papel_particle.p");
        load("papelExplosion", "particulas/papelExplosion_particle.p");
        load("pelota", "particulas/pelota_particle.p");
        load("dildo", "particulas/dildo_particle.p");
    }

    private void load(String id, String effectPath) {
        ParticleEffectPool.PooledEffect tmp = null;
        com.badlogic.gdx.graphics.g2d.ParticleEffect effect = new com.badlogic.gdx.graphics.g2d.ParticleEffect();
        effect.load(Gdx.files.internal(effectPath), Gdx.files.internal("particulas"));
        pools.put(id, new ParticleEffectPool(effect, 5, 50));
    }


    public ParticleEffectPool.PooledEffect obtainEffect(String id, float x, float y, boolean isBackground) {
        ParticleEffectPool pool = pools.get(id);
        if (pool == null) throw new RuntimeException("Efecto no cargado o error de nombre de archivo: " + id);
        ParticleEffectPool.PooledEffect e = pool.obtain();
        e.setPosition(x, y);
        e.start();
        if (isBackground) backgroundEffects.add(e);
        else foregroundEffects.add(e);
        return e;
    }

    public ParticleEffectPool.PooledEffect obtainEffect(String id, float x, float y) {
        return obtainEffect(id, x, y, false);
    }

    public void update(float delta) {
        for (int i = backgroundEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect e = backgroundEffects.get(i);
            e.update(delta);
            if (e.isComplete()) {
                e.free();
                backgroundEffects.removeIndex(i);
            }
        }
        for (int i = foregroundEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect e = foregroundEffects.get(i);
            e.update(delta);
            if (e.isComplete()) {
                e.free();
                foregroundEffects.removeIndex(i);
            }
        }
    }

    public void renderBackground(SpriteBatch batch) {
        for (ParticleEffectPool.PooledEffect e : backgroundEffects) {
            e.draw(batch);
        }
    }

    public void renderForeground(SpriteBatch batch) {
        for (ParticleEffectPool.PooledEffect e : foregroundEffects) {
            e.draw(batch);
        }
    }

    /**
     * Libera manualmente un efecto
     */
    public void freeEffect(ParticleEffectPool.PooledEffect e) {
        if (backgroundEffects.removeValue(e, true) || foregroundEffects.removeValue(e, true)) {
            e.free();
        }
    }

    /**
     * Limpia todos los efectos.
     */
    public void clear() {
        for (ParticleEffectPool.PooledEffect e : backgroundEffects) e.free();
        for (ParticleEffectPool.PooledEffect e : foregroundEffects) e.free();
        backgroundEffects.clear();
        foregroundEffects.clear();
    }
}

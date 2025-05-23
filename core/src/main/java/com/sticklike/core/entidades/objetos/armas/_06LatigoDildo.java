package com.sticklike.core.entidades.objetos.armas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.renderizado.particulas.ParticleManager;
import com.sticklike.core.entidades.renderizado.particulas.RenderParticulasProyectil;
import com.sticklike.core.entidades.renderizado.particulas.TrailRender;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Proyectil «Látigo-dildo» con rastro curvo de luz y proyección horizontal.
 */
public final class _06LatigoDildo implements Proyectiles {
    private static final float BASE_DURATION = 0.33f;
    private static final float SWING_DIST = 62.5f;
    private static final float KNOCKBACK = EMPUJE_BASE_DILDO;
    private static final float IMPACT_COLOR_TIME = IMPACTO_DURACION;
    private static final float HALO_DMG_M = 0.25f;
    private static final float HALO_V = 650f;
    private static final float HALO_RANGE = 280f;
    private static final float HALO_W = 1f;
    private static final float HALO_H = 32f;
    private static final float PROJ_START_PERC = 0.66f;
    private static final int ARC_SAMPLES = 20;
    private static final float SAMPLE_STEP = PROJ_START_PERC / ARC_SAMPLES;

    private static Texture TEXTURE;
    private static final float SW_W = 25f, SW_H = 50f;
    private static final float ORG_X = SW_W * 0.25f, ORG_Y = SW_H * 0.5f;
    private static final float COLL_S = SW_W * 1.1f;

    private final Jugador jugador;
    private final int lado;
    private final boolean haloUpgrade, rapidoUpgrade;

    private final Sprite sprite;
    private final Rectangle rect;
    private final RenderParticulasProyectil particles;
    private final RenderParticulasProyectil particlesHalo;
    private final ParticleEffectPool.PooledEffect efecto;

    // buffer circular para arco
    private final Vector2[] arcPoints;
    private int arcCount = 0;
    private float nextSamplePct = 0f;

    // una copia de las posiciones al iniciar la proyección
    private Vector2[] projectedPoints = null;
    private int projectedCount = 0;

    private boolean projectionStarted = false;
    private float haloTravel = 0f;

    private final Set<Enemigo> impactados = new HashSet<>(4);

    private final Vector2 tmpVec1 = new Vector2();
    private final Vector2 tmpVec2 = new Vector2();

    private final float baseDamage;
    private float duration, invDuration;
    private boolean activo = true, critico = false;
    private float tSwing = 0f, impactT = 0f;

    private static final Color COL_ORIG = new Color(1f, 1f, 1f, 1f);
    private static final Color COL_IMPACT = new Color(0f, 0f, 1f, 1f);
    private static final Color COL_PART = new Color(0.85f, 0.4f, 0.7f, 1f);
    private static final Color COL_PART_HIT = new Color(0.051f, 0.596f, 1f, 1f);
    private static final Color COL_HALO = new Color(1f, 0.2f, 0.8f, 1f);
    private static final Color COL_HALO_HIT = new Color(0f, 0.8f, 1f, 1f);

    public _06LatigoDildo(Jugador jugador, int lado, float poderJugador, float extraDamage, boolean haloUpgrade, boolean rapidoUpgrade) {
        if (TEXTURE == null) TEXTURE = manager.get(ARMA_DILDO, Texture.class);
        this.jugador = jugador;
        this.lado = lado;
        this.haloUpgrade = haloUpgrade;
        this.rapidoUpgrade = rapidoUpgrade;

        // duración del swing
        duration = BASE_DURATION * (rapidoUpgrade ? 0.5f : 1f);
        invDuration = 1f / duration;

        // sprite y colisión
        sprite = new Sprite(TEXTURE);
        sprite.setSize(SW_W, SW_H);
        sprite.setOrigin(ORG_X, ORG_Y);
        if (lado == -1) sprite.flip(false, true);
        sprite.setColor(COL_ORIG);
        rect = new Rectangle(0, 0, COLL_S, COLL_S);

        // daño base
        baseDamage = (DANYO_DILDO + extraDamage + MathUtils.random(4f)) * (1f + poderJugador / 100f);

        // renderizado de partículas
        particles = new RenderParticulasProyectil(20, 45, COL_PART);
        particles.setAlphaMult(0.8f);
        particlesHalo = new RenderParticulasProyectil(20, 10, COL_HALO);
        particlesHalo.setAlphaMult(1f);

        float cX = sprite.getX() + SW_W * 0.5f;
        float cY = sprite.getY() + SW_H * 0.5f;
        efecto = ParticleManager.get().obtainEffect("dildo", cX, cY);

        // inicializar buffer de arco
        arcPoints = new Vector2[ARC_SAMPLES + 1];
        for (int i = 0; i < arcPoints.length; i++) {
            arcPoints[i] = new Vector2();
        }
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        /* -------- 1. Avance del swing -------- */
        tSwing += delta;
        float p = Math.min(tSwing * invDuration, 1f);

        // rotación y posición del sprite
        float theta = MathUtils.PI * (0.5f + p);
        float R = SWING_DIST;
        Sprite js = jugador.getSprite();
        float cx = js.getX() + js.getWidth() * .5f - SW_W * .5f;
        float cy = js.getY() + js.getHeight() * .5f;
        float nx = cx - lado * R * MathUtils.cos(theta);
        float ny = cy + R * MathUtils.sin(theta) - SW_H * .5f;
        sprite.setPosition(nx, ny);
        float vx = lado * R * MathUtils.sin(theta);
        float vy = R * MathUtils.cos(theta);
        sprite.setRotation(MathUtils.atan2(vy, vx) * MathUtils.radiansToDegrees);

        /* -------- 2. Trail principal y emitter (solo antes del halo) -------- */
        Vector2 tip = calcPunta();
        if (!projectionStarted) {
            efecto.setPosition(tip.x, tip.y);
            particles.update(tip);
            if (tSwing < duration) {
                TrailRender.get().submit(particles);
            }
        }

        /* -------- 3. Restaurar colores tras impacto -------- */
        if (impactT > 0f) {
            impactT -= delta;
            if (impactT <= 0f) {
                sprite.setColor(COL_ORIG);
                particles.setColor(COL_PART);
                particles.setAlphaMult(0.8f);
                particlesHalo.setColor(COL_HALO);
                particlesHalo.setAlphaMult(1f);
            }
        }

        /* -------- 4. Muestreo del arco para el halo -------- */
        if (haloUpgrade && !projectionStarted) {
            while (p >= nextSamplePct && nextSamplePct <= PROJ_START_PERC && arcCount < arcPoints.length) {
                float thetaS = MathUtils.PI * (0.5f + nextSamplePct);
                float sx = cx - lado * R * MathUtils.cos(thetaS) + SW_W * .5f;
                float sy = cy + R * MathUtils.sin(thetaS) - SW_H * .5f + SW_H * .5f;
                arcPoints[arcCount++].set(sx, sy);
                particlesHalo.update(arcPoints[arcCount - 1]);
                nextSamplePct += SAMPLE_STEP;
            }
        }

        /* -------- 5. Arranque de la proyección del halo -------- */
        if (!projectionStarted && haloUpgrade && p >= PROJ_START_PERC) {
            projectionStarted = true;
            projectedCount = arcCount;
            projectedPoints = new Vector2[projectedCount];
            System.arraycopy(arcPoints, 0, projectedPoints, 0, projectedCount);
            haloTravel = 0f;

            // Detenemos inmediatamente el emitter del ParticleManager
            efecto.allowCompletion();
            // Para liberar de forma instantánea, podrías usar efecto.free();
        }

        /* -------- 6. Avance del halo proyectado -------- */
        if (projectionStarted && haloUpgrade) {
            haloTravel += HALO_V * delta;
            for (int i = 0; i < projectedCount; i++) {
                Vector2 base = projectedPoints[i];
                float x = base.x + lado * haloTravel;
                float y = base.y;
                tmpVec2.set(x, y);
                particlesHalo.update(tmpVec2);
            }
            TrailRender.get().submit(particlesHalo);

            if (haloTravel >= HALO_RANGE) {
                detenerParticulas();
                activo = false;
            }
        }

        /* -------- 7. Fin del swing cuando no hay halo -------- */
        if (!haloUpgrade && tSwing >= duration) {
            detenerParticulas();
            activo = false;
        }
    }

    private Vector2 calcPunta() {
        float localX = SW_W * 0.5f - sprite.getOriginX();

        float localY = (lado == -1) ? -sprite.getOriginY() : SW_H - sprite.getOriginY();

        float rad = sprite.getRotation() * MathUtils.degreesToRadians;
        float cos = MathUtils.cos(rad);
        float sin = MathUtils.sin(rad);

        float worldX = sprite.getX() + sprite.getOriginX() + localX * cos - localY * sin;
        float worldY = sprite.getY() + sprite.getOriginY() + localX * sin + localY * cos;

        return tmpVec1.set(worldX, worldY);
    }


    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (!activo) return;

        // el rastro se dibuja vía TrailRender.flush(...)
        if (tSwing < duration) {
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        TEXTURE = null;
        particles.dispose();
        particlesHalo.dispose();
        efecto.free();
    }

    @Override
    public float getX() {
        return sprite.getX();
    }

    @Override
    public float getY() {
        return sprite.getY();
    }

    @Override
    public Rectangle getRectanguloColision() {
        return rect;
    }

    @Override
    public boolean isProyectilActivo() {
        return activo;
    }

    @Override
    public void desactivarProyectil() {
        detenerParticulas();
        activo = false;
    }

    @Override
    public boolean isPersistente() {
        return true;
    }

    @Override
    public float getBaseDamage() {
        if (projectionStarted) return baseDamage * HALO_DMG_M;
        if (MathUtils.random() < jugador.getCritico()) {
            critico = true;
            return baseDamage * 1.5f;
        }
        critico = false;
        return baseDamage;
    }

    @Override
    public float getKnockbackForce() {
        return projectionStarted ? KNOCKBACK * 0.5f : KNOCKBACK;
    }

    @Override
    public void registrarImpacto(Enemigo e) {
        if (impactados.add(e)) {
            sprite.setColor(COL_IMPACT);
            particles.setColor(COL_PART_HIT);
            particles.setAlphaMult(1f);
            particlesHalo.setColor(COL_HALO_HIT);
            particlesHalo.setAlphaMult(1f);
            impactT = IMPACT_COLOR_TIME;
            GestorDeAudio.getInstance().reproducirEfecto("dildo", 0.8f);
        }
    }

    private void detenerParticulas() {
        particles.reset();
        particlesHalo.reset();
        efecto.allowCompletion();
    }

    @Override
    public boolean yaImpacto(Enemigo e) {
        return impactados.contains(e);
    }

    @Override
    public boolean esCritico() {
        return critico;
    }

    public boolean isHaloActivo() {
        return projectionStarted && haloUpgrade;
    }

    public Vector2[] getPuntosHalo() {
        return projectedPoints != null ? projectedPoints : new Vector2[0];
    }

    public float getHaloTravel() {
        return haloTravel;
    }

    public float getHaloW() {
        return HALO_W;
    }

    public float getHaloH() {
        return HALO_H;
    }

    public int getLado() {
        return lado;
    }
}

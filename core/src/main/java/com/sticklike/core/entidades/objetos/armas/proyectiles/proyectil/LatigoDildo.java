package com.sticklike.core.entidades.objetos.armas.proyectiles.proyectil;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.renderizado.RenderParticulasProyectil;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Proyectil «Látigo‑dildo» con rastro curvo de luz y proyección horizontal.
 */
public final class LatigoDildo implements Proyectiles {
    private static final float BASE_DURATION = 0.33f;
    private float duration;
    private float invDuration;
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
    private final boolean haloUpgrade;
    private final boolean rapidoUpgrade;

    private final Sprite sprite;
    private final Rectangle rect;
    private final RenderParticulasProyectil particles;
    private final RenderParticulasProyectil particlesHalo;

    private final List<Vector2> arcPoints = new ArrayList<>();
    private List<Vector2> projectedPoints;
    private boolean projectionStarted = false;
    private float haloTravel = 0f;

    private final Set<Enemigo> impactados = new HashSet<>(4);

    private float nextSamplePct;

    private static final Color COL_ORIG = new Color(1f, 1f, 1f, 1f);
    private static final Color COL_IMPACT = new Color(0f, 0f, 1f, 1f);
    private static final Color COL_PART = new Color(0.85f, 0.4f, 0.7f, 1f);
    private static final Color COL_PART_HIT = new Color(0.051f, 0.596f, 1f, 1f);
    private static final Color COL_HALO = new Color(1f, 0.2f, 0.8f, 1f);
    private static final Color COL_HALO_HIT = new Color(0f, 0.8f, 1f, 1f);

    private final float baseDamage;

    private boolean activo = true;
    private boolean critico = false;
    private float tSwing = 0f;
    private float impactT = 0f;

    public LatigoDildo(Jugador jugador, int lado, float poderJugador, float extraDamage, boolean haloUpgrade, boolean rapidoUpgrade) {
        if (TEXTURE == null) TEXTURE = manager.get(ARMA_DILDO, Texture.class);
        this.jugador = jugador;
        this.lado = lado;
        this.haloUpgrade = haloUpgrade;
        this.rapidoUpgrade = rapidoUpgrade;

        // Duración de swing ajustada
        this.duration = BASE_DURATION;
        if (rapidoUpgrade) this.duration *= 0.5f;
        this.invDuration = 1f / this.duration;

        sprite = new Sprite(TEXTURE);
        sprite.setSize(SW_W, SW_H);
        sprite.setOrigin(ORG_X, ORG_Y);
        if (lado == -1) sprite.flip(false, true);
        sprite.setColor(COL_ORIG);

        rect = new Rectangle(0, 0, COLL_S, COLL_S);

        baseDamage = (DANYO_DILDO + extraDamage + MathUtils.random(4f)) * (1f + poderJugador / 100f);

        particles = new RenderParticulasProyectil(20, 45, COL_PART);
        particles.setAlphaMult(0.8f);

        particlesHalo = new RenderParticulasProyectil(20, 10, COL_HALO);
        particlesHalo.setAlphaMult(1f);

        // Inicializar muestreo fijo
        this.nextSamplePct = 0f;
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        // ——— Swing —————————
        tSwing += delta;
        float p = Math.min(tSwing * invDuration, 1f);

        float theta = MathUtils.PI * (0.5f + p);
        float R = SWING_DIST;
        Sprite js = jugador.getSprite();

        // Posición y rotación del sprite
        float cx = js.getX() + js.getWidth() * 0.5f - SW_W * 0.5f;
        float cy = js.getY() + js.getHeight() * 0.5f;
        float nx = cx - lado * R * MathUtils.cos(theta);
        float ny = cy + R * MathUtils.sin(theta) - SW_H * 0.5f;
        sprite.setPosition(nx, ny);

        float vx = lado * R * MathUtils.sin(theta);
        float vy = R * MathUtils.cos(theta);
        sprite.setRotation(MathUtils.atan2(vy, vx) * MathUtils.radiansToDegrees);

        Vector2 center = new Vector2(nx + SW_W * 0.5f, ny + SW_H * 0.5f);
        particles.update(center);

        // — Restaurar color tras impacto —
        if (impactT > 0f) {
            impactT -= delta;
            if (impactT <= 0f) {
                // Sprite vuelve a blanco
                sprite.setColor(COL_ORIG);
                // Partículas del látigo vuelven a su color y alpha original
                particles.setColor(COL_PART);
                particles.setAlphaMult(0.8f);
                // Partículas del halo vuelven a su color y alpha original
                particlesHalo.setColor(COL_HALO);
                particlesHalo.setAlphaMult(1f);
            }
        }

        // Muestreo fijo del arco antes de proyectar
        if (haloUpgrade && !projectionStarted) {
            while (p >= nextSamplePct && nextSamplePct <= PROJ_START_PERC) {
                float thetaSample = MathUtils.PI * (0.5f + nextSamplePct);
                Vector2 sampleCenter = preCalcularCenterPos(js, thetaSample);
                arcPoints.add(sampleCenter);
                particlesHalo.update(sampleCenter);
                nextSamplePct += SAMPLE_STEP;
            }
        }

        // Iniciar proyección al llegar al umbral
        if (!projectionStarted && haloUpgrade && p >= PROJ_START_PERC) {
            projectionStarted = true;
            projectedPoints = new ArrayList<>(arcPoints);
            haloTravel = 0f;
        }

        // Desactivar tras swing si no hay halo
        if (!haloUpgrade && tSwing >= duration) activo = false;
        if (projectionStarted && haloUpgrade) {
            // ——— Halo ——————————
            haloTravel += HALO_V * delta;
            for (Vector2 base : projectedPoints) {
                float x = base.x + lado * haloTravel;
                float y = base.y;
                particlesHalo.update(new Vector2(x, y));
            }
            if (haloTravel >= HALO_RANGE) activo = false;
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (!activo) return;
        if (projectionStarted && haloUpgrade) particlesHalo.render(batch);
        if (tSwing < duration) {
            particles.render(batch);
            sprite.draw(batch);
        }
    }
    /**
     * Calcula la posición del centro del sprite para un ángulo dado.
     */
    private Vector2 preCalcularCenterPos(Sprite js, float theta) {
        float cx = js.getX() + js.getWidth() * 0.5f - SW_W * 0.5f;
        float cy = js.getY() + js.getHeight() * 0.5f;
        float nx = cx - lado * SWING_DIST * MathUtils.cos(theta);
        float ny = cy + SWING_DIST * MathUtils.sin(theta) - SW_H * 0.5f;
        return new Vector2(nx + SW_W * 0.5f, ny + SW_H * 0.5f);
    }

    @Override
    public void dispose() {
        particles.dispose();
        particlesHalo.dispose();
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
        activo = false;
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
    public boolean isPersistente() {
        return true;
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

    public List<Vector2> getPuntosHalo() {
        return projectedPoints;
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

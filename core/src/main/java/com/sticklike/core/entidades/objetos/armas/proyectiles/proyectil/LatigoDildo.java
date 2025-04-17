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

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public final class LatigoDildo implements Proyectiles {
    private static final float DURATION = 0.33f;
    private static final float INV_DURATION = 1f / DURATION;
    private static final float SWING_DISTANCE = 62.5f;
    private static final float KNOCKBACK_FORCE = EMPUJE_BASE_DILDO;
    private static final float IMPACT_DURATION = IMPACTO_DURACION;

    private static Texture TEXTURE;
    private static final float SPRITE_WIDTH = 25f;
    private static final float SPRITE_HEIGHT = 50f;
    private static final float ORIGIN_X = SPRITE_WIDTH * 0.25f;
    private static final float ORIGIN_Y = SPRITE_HEIGHT * 0.5f;
    private static final float COLLISION_RADIUS = SPRITE_WIDTH * 0.55f;
    private static final float COLLISION_SIZE = COLLISION_RADIUS * 2f;

    private final Sprite sprite;
    private final Rectangle collisionRect = new Rectangle(0, 0, COLLISION_SIZE, COLLISION_SIZE);
    private final Vector2 center = new Vector2();
    private final RenderParticulasProyectil particles;
    private final Color originalColor = new Color(1f, 1f, 1f, 1f);
    private final Color impactColor = new Color(0f, 0f, 1f, 1f);
    private final Color colorParticles = new Color(0.85f, 0.4f, 0.7f, 1f);
    private final Color colorParticlesHit = new Color(0.051f, 0.596f, 1f, 1f);

    private final Jugador jugador;
    private final int lado;
    private final float baseDamage;
    private boolean activo = true;
    private float timer = 0f;
    private boolean critico;
    private float impactTimer = 0f;
    private final java.util.Set<Enemigo> impactados = new java.util.HashSet<>(4);

    public LatigoDildo(Jugador jugador, int lado, float poderJugador, float extraDamage) {
        if (TEXTURE == null) {
            TEXTURE = manager.get(ARMA_DILDO, Texture.class);
        }
        this.jugador = jugador;
        this.lado = lado;

        sprite = new Sprite(TEXTURE);
        sprite.setSize(SPRITE_WIDTH, SPRITE_HEIGHT);
        sprite.setOrigin(ORIGIN_X, ORIGIN_Y);
        if (lado == -1) {
            sprite.flip(false, true);
        }
        sprite.setColor(originalColor);

        this.baseDamage = (DANYO_DILDO + extraDamage + MathUtils.random(4f)) * (1f + jugador.getPoderJugador() / 100f);

        particles = new RenderParticulasProyectil(20, 45, colorParticles);
        particles.setAlphaMult(0.8f);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        timer += delta;
        float progress = timer * INV_DURATION;
        if (progress > 1f) progress = 1f;

        float theta = MathUtils.PI * (0.5f + progress);
        float R = SWING_DISTANCE;

        Sprite ps = jugador.getSprite();
        float px = ps.getX();
        float py = ps.getY();
        float pw = ps.getWidth();
        float ph = ps.getHeight();

        float cx = px + pw * 0.5f - SPRITE_WIDTH * 0.5f;
        float cy = py + ph * 0.5f;

        float newX = cx - lado * R * MathUtils.cos(theta);
        float newY = cy + R * MathUtils.sin(theta) - SPRITE_HEIGHT * 0.5f;
        sprite.setPosition(newX, newY);

        float dx = lado * R * MathUtils.sin(theta);
        float dy = R * MathUtils.cos(theta);
        sprite.setRotation(MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees);

        float centerX = newX + SPRITE_WIDTH * 0.5f;
        float centerY = newY + SPRITE_HEIGHT * 0.5f;
        collisionRect.setPosition(centerX - COLLISION_SIZE * 0.5f, centerY - COLLISION_SIZE * 0.5f);

        center.set(centerX, centerY);
        particles.update(center);

        if (impactTimer > 0f) {
            impactTimer -= delta;
            if (impactTimer <= 0f) {
                sprite.setColor(originalColor);
                particles.setColor(colorParticles);
            }
        }

        if (timer >= DURATION) {
            activo = false;
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (activo) {
            particles.render(batch);
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        particles.dispose();
    }

    @Override public float getX() { return sprite.getX(); }
    @Override public float getY() { return sprite.getY(); }
    @Override public Rectangle getRectanguloColision() { return collisionRect; }
    @Override public boolean isProyectilActivo() { return activo; }
    @Override public void desactivarProyectil() { activo = false; }

    @Override
    public float getBaseDamage() {
        if (MathUtils.random() < jugador.getCritico()) {
            critico = true;
            return baseDamage * 1.5f;
        }
        critico = false;
        return baseDamage;
    }

    @Override public float getKnockbackForce() { return KNOCKBACK_FORCE; }
    @Override public boolean isPersistente() { return true; }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        if (impactados.add(enemigo)) {
            sprite.setColor(impactColor);
            particles.setColor(colorParticlesHit);
            particles.setAlphaMult(1f);
            GestorDeAudio.getInstance().reproducirEfecto("dildo", 0.8f);
            impactTimer = IMPACT_DURATION;
        }
    }

    @Override public boolean yaImpacto(Enemigo enemigo) { return impactados.contains(enemigo); }
    @Override public boolean esCritico() { return critico; }
}

package com.sticklike.core.entidades.objetos.armas;

import com.badlogic.gdx.Gdx;
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
 * Proyectil «Calcetín».
 */
public final class _01ProyectilCalcetin implements Proyectiles {
    private static final float SPRITE_WIDTH = CALCETIN_W_SIZE;
    private static final float SPRITE_HEIGHT = CALCETIN_H_SIZE;
    private static final float SPRITE_ORIGIN_X = SPRITE_WIDTH * 0.5f;
    private static final float SPRITE_ORIGIN_Y = SPRITE_HEIGHT * 0.5f;
    private static final float ROTATION_SPEED = VEL_ROTACION_CALCETIN;
    private static final float MAX_DISTANCE = MAX_DISTANCIA;
    private static final float IMPACT_DURATION = IMPACTO_DURACION;
    private static final float PARTICLE_LEN_FACTOR = 17f;
    private static final float PARTICLE_WID_FACTOR = 6f;
    private static final Color DEFAULT_PARTICLE_COLOR = new Color(1f, 1f, 1f, 0.1f);
    private final ParticleEffectPool.PooledEffect efecto;
    private static Texture TEXTURE;
    private final Sprite sprite;
    private final Rectangle collisionRect;
    private final Vector2 center;
    private final RenderParticulasProyectil particles;
    private final Set<Enemigo> impactados;
    private final Jugador jugador;
    private final float damageEscalado;
    private final float velocidadProyectil;
    private final float multiplicadorVelocidad;
    private final float direccionX;
    private final float direccionY;

    private float distanciaRecorrida;
    private boolean activo;
    private boolean esCritico;
    private float impactoTimer;

    public _01ProyectilCalcetin(float x, float y, float direccionX, float direccionY, float velocidadProyectil, float multiplicadorVelocidad, float poderJugador, float extraDamage, Jugador jugador) {

        if (TEXTURE == null) TEXTURE = manager.get(ARMA_CALCETIN, Texture.class);

        this.jugador = jugador;
        this.direccionX = direccionX;
        this.direccionY = direccionY;
        this.velocidadProyectil = velocidadProyectil;
        this.multiplicadorVelocidad = multiplicadorVelocidad;
        this.distanciaRecorrida = 0f;
        this.activo = true;
        this.esCritico = false;
        this.impactoTimer = 0f;

        sprite = new Sprite(TEXTURE);
        sprite.setSize(SPRITE_WIDTH, SPRITE_HEIGHT);
        sprite.setOrigin(SPRITE_ORIGIN_X, SPRITE_ORIGIN_Y);
        sprite.setPosition(x, y);

        float factor = Gdx.graphics.getWidth() / REAL_WIDTH;
        int maxLen = (int) (PARTICLE_LEN_FACTOR * factor);
        float partWid = PARTICLE_WID_FACTOR * factor;
        particles = new RenderParticulasProyectil(maxLen, partWid, DEFAULT_PARTICLE_COLOR);
        particles.setAlphaMult(0.75f);

        center = new Vector2();
        collisionRect = new Rectangle(x, y, SPRITE_WIDTH, SPRITE_HEIGHT);
        impactados = new HashSet<>(4);
        Vector2 initialCenter = new Vector2(x + SPRITE_ORIGIN_X, y + SPRITE_ORIGIN_Y);
        efecto = ParticleManager.get().obtainEffect("calcetin", initialCenter.x, initialCenter.y);

        float baseDamage = DANYO_CALCETIN + extraDamage + MathUtils.random(8f);
        this.damageEscalado = baseDamage * (1f + (poderJugador / 100f));
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        /* ---------- actualización del trail ---------- */
        center.set(sprite.getX() + SPRITE_ORIGIN_X, sprite.getY() + SPRITE_ORIGIN_Y);
        particles.update(center);
        TrailRender.get().submit(particles);
        efecto.setPosition(center.x, center.y);

        /* ---------- movimiento y giro ---------- */
        float despl = velocidadProyectil * multiplicadorVelocidad * delta;
        sprite.translate(direccionX * despl, direccionY * despl);
        distanciaRecorrida += despl;
        sprite.rotate(ROTATION_SPEED * delta);

        /* ---------- colisión ---------- */
        collisionRect.set(sprite.getX(), sprite.getY(), SPRITE_WIDTH, SPRITE_HEIGHT);

        /* ---------- restaurar colores tras impacto ---------- */
        if (!impactados.isEmpty()) {
            impactoTimer += delta;
            if (impactoTimer >= IMPACT_DURATION) {
                impactoTimer = 0f;
                sprite.setColor(1f, 1f, 1f, 1f);
                particles.setColor(DEFAULT_PARTICLE_COLOR);
            }
        } else {
            sprite.setColor(1f, 1f, 1f, 1f);
        }

        /* ---------- distancia máxima ---------- */
        if (distanciaRecorrida >= MAX_DISTANCE) desactivarProyectil();
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (activo) sprite.draw(batch);   // el rastro lo pinta TrailRender
    }

    @Override
    public void dispose() {
        TEXTURE = null;
        particles.dispose();
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
        return collisionRect;
    }

    @Override
    public boolean isProyectilActivo() {
        return activo;
    }

    @Override
    public void desactivarProyectil() {
        particles.reset();
        efecto.allowCompletion();
        activo = false;
    }

    @Override
    public float getBaseDamage() {
        if (MathUtils.random() < jugador.getCritico()) {
            esCritico = true;
            return damageEscalado * 1.5f;
        }
        esCritico = false;
        return damageEscalado;
    }

    @Override
    public float getKnockbackForce() {
        return EMPUJE_BASE_CALCETIN;
    }

    @Override
    public boolean isPersistente() {
        return true;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        if (impactados.add(enemigo)) {
            sprite.setColor(Color.RED);
            particles.setColor(Color.RED);
            impactoTimer = 0f;
            GestorDeAudio.getInstance().reproducirEfecto("impactoBase", 1f);
        }
    }

    @Override
    public boolean yaImpacto(Enemigo e) {
        return impactados.contains(e);
    }

    @Override
    public boolean esCritico() {
        return esCritico;
    }
}

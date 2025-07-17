package com.sticklike.core.entidades.objetos.armas.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Circle;
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

public final class _04ProyectilPapelCulo implements Proyectiles {

    /* ---------- Constantes ---------- */
    private static Texture TEXTURE;
    private static Animation<TextureRegion> IMPACT_ANIMATION;
    private static final float GRAVITY_ASC = 450f;
    private static final float GRAVITY_DESC = 200f;
    private static final float ROTATION_SPEED = 666f;
    private static final float PARTICLE_LEN = 20f;
    private static final float PARTICLE_WID = 10f;
    private static final float IMPACT_SCALE = 5f;
    private static final float IMPACT_CIRCLE = 7.5f;
    private static final float FRAG_FACTOR_HOR = 0.25f;
    private static final Color PARTICLES_COLOR = new Color(0.65f, 0.5f, 0.7f, 1f);
    private final ParticleEffectPool.PooledEffect efecto;
    private ParticleEffectPool.PooledEffect efectoExplosion;
    private final float particleScale;


    /* ---------- Atributos ---------- */
    private final Sprite sprite;
    private final RenderParticulasProyectil particles;
    private final Rectangle collisionRect = new Rectangle();
    private final Circle collisionCircle = new Circle();
    private final Set<Enemigo> impactados = new HashSet<>(8);
    private final Vector2 center = new Vector2();
    private final Jugador jugador;
    private final float damageEscalado;

    private final float anguloLanzamiento;
    private final boolean isFragment;
    private boolean canFragment = true;
    private float velH, velV;
    private boolean landed = false;
    private final float altitudeFinal;
    private boolean activo = true;
    private boolean esCritico;

    private float animationStateTime = 0f;
    private boolean impactoAnimacionActiva = false;
    private float impactX, impactY, impactRotation;
    private final GestorDeAudio audio = GestorDeAudio.getInstance();


    public _04ProyectilPapelCulo(float x, float y, float anguloLanzamiento, float velocidadProyectil, float poderJugador, float extraDamage, Jugador jugador, float direccionHorizontal, boolean esFragmento) {

        // ---------- Carga única de recursos ----------
        if (TEXTURE == null) TEXTURE = manager.get(ARMA_PAPELCULO, Texture.class);
        if (IMPACT_ANIMATION == null) IMPACT_ANIMATION = animations.get("papelCuloImpacto");

        this.jugador = jugador;
        this.anguloLanzamiento = anguloLanzamiento;
        this.isFragment = esFragmento;

        /* === Escala SOLO si es fragmento === */
        float sizeScale = esFragmento ? 0.55f : 1f;   // normal = 1, fragmento = 0.55
        this.particleScale = sizeScale;               // ← para trail / explosión

        /* === Velocidades === */
        velV = velocidadProyectil * MathUtils.sinDeg(anguloLanzamiento);
        velH = direccionHorizontal * velocidadProyectil * MathUtils.cosDeg(anguloLanzamiento) * FRAG_FACTOR_HOR;

        /* === Sprite === */
        sprite = new Sprite(TEXTURE);
        sprite.setSize(PAPELCULO_W_SIZE * sizeScale, PAPELCULO_H_SIZE * sizeScale);
        sprite.setOriginCenter();
        sprite.setPosition(x, y);

        /* === Trail (partículas) === */
        float screenScale = Gdx.graphics.getWidth() / REAL_WIDTH;
        particles = new RenderParticulasProyectil((int) (PARTICLE_LEN * screenScale * particleScale), PARTICLE_WID * screenScale * particleScale, PARTICLES_COLOR);
        particles.setAlphaMult(0.9f);

        /* === Partícula inicial === */
        Vector2 initialCenter = new Vector2(x + sprite.getWidth() * 0.5f, y + sprite.getHeight() * 0.5f);
        efecto = ParticleManager.get().obtainEffect("papel", initialCenter.x, initialCenter.y);
        efecto.scaleEffect(particleScale);

        /* === Altitud final aleatoria === */
        float camHalfH = jugador.getControladorEnemigos().getVentanaJuego1().getOrtographicCamera().viewportHeight / 2f;
        float minY = jugador.getControladorEnemigos().getVentanaJuego1().getOrtographicCamera().position.y - camHalfH;
        float maxY = jugador.getSprite().getY() + jugador.getSprite().getHeight() - 10f;
        altitudeFinal = MathUtils.random(minY, maxY);

        /* === Daño escalado (esto se queda como estaba) === */
        float baseDamage = DANYO_PAPELCULO + extraDamage + MathUtils.random(15f);
        damageEscalado = baseDamage * (1f + poderJugador / 100f);

        /* === Hit-boxes iniciales === */
        collisionRect.set(x, y, sprite.getWidth(), sprite.getHeight());
        collisionCircle.set(x + sprite.getWidth() / 2f, y + sprite.getHeight() / 2f, sprite.getWidth() / 2f);
    }


    /* ---------- Lógica ---------- */
    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        /* 1) Trail */
        center.set(sprite.getX() + sprite.getWidth() * 0.5f, sprite.getY() + sprite.getHeight() * 0.5f);

        /* Solo mientras está volando y no hay animación de impacto */
        if (!landed && !impactoAnimacionActiva) {
            particles.update(center);
            TrailRender.get().submit(particles);
            efecto.setPosition(center.x, center.y);
        }


        /* 2) Animación de impacto */
        if (impactoAnimacionActiva) {
            animationStateTime += delta;
            if (IMPACT_ANIMATION.isAnimationFinished(animationStateTime)) {
                impactoAnimacionActiva = false;
                efecto.allowCompletion();
                desactivarProyectil();
            }
            return;
        }

        /* 3) Movimiento (vuelo / caída) */
        if (!landed) {
            float nx = sprite.getX() + velH * delta;

            velV -= (velV > 0 ? GRAVITY_ASC : GRAVITY_DESC) * delta;
            float ny = sprite.getY() + velV * delta;

            if (ny <= altitudeFinal) {
                ny = altitudeFinal;
                landed = true;
                velH = velV = 0f;
                sprite.setRotation(-5f);        // pequeño giro al aterrizar

                particles.reset();
                efecto.allowCompletion();
            }

            sprite.setPosition(nx, ny);
            sprite.rotate(ROTATION_SPEED * delta);

        } else if (isFragment) {
            // los fragmentos se destruyen al aterrizar
            desactivarProyectil();
        }

        /* 4) Hit-boxes actualizados */
        collisionRect.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        collisionCircle.set(sprite.getX() + sprite.getWidth() / 2f, sprite.getY() + sprite.getHeight() / 2f, sprite.getWidth() / 2f);
    }

    /* ---------- Render ---------- */
    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (!activo) return;

        if (!landed && !impactoAnimacionActiva) {
            sprite.draw(batch);
        } else if (impactoAnimacionActiva) {
            TextureRegion frame = IMPACT_ANIMATION.getKeyFrame(animationStateTime, false);
            batch.draw(frame, impactX, impactY, sprite.getOriginX(), sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX() * IMPACT_SCALE, sprite.getScaleY() * IMPACT_SCALE, impactRotation);
        } else {
            sprite.draw(batch);
        }
    }

    /* ---------- Limpieza ---------- */
    @Override
    public void dispose() {
        TEXTURE = null;
        particles.dispose();
        efecto.free();
        if (efectoExplosion != null) efectoExplosion.free();
    }

    /* ---------- Getters simples ---------- */
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
        if (impactoAnimacionActiva) {
            float w = sprite.getWidth() * IMPACT_SCALE;
            float h = sprite.getHeight() * IMPACT_SCALE;
            collisionRect.set(impactX - sprite.getOriginX() * IMPACT_SCALE, impactY - sprite.getOriginY() * IMPACT_SCALE, w, h);
        }
        return collisionRect;
    }

    public Circle getCirculoColision() {
        if (impactoAnimacionActiva) {
            float r = (sprite.getWidth() * IMPACT_CIRCLE) / 2f;
            collisionCircle.set(impactX, impactY, r);
        }
        return collisionCircle;
    }

    @Override
    public boolean isProyectilActivo() {
        return activo;
    }

    @Override
    public void desactivarProyectil() {
        particles.reset();
        efecto.allowCompletion();
        if (efectoExplosion != null) efectoExplosion.allowCompletion();
        activo = false;
    }

    /* ---------- Daño / knockback ---------- */
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
        return EMPUJE_BASE_PAPELCULO;
    }

    @Override
    public boolean isPersistente() {
        return true;
    }

    /* ---------- Impacto ---------- */
    @Override
    public void registrarImpacto(Enemigo enemigo) {

        /* ---------- 1) Fragmentación ---------- */
        if (jugador.getAtaquePapelCulo().isFragmentado() && canFragment) {
            canFragment = false;

            /* 1-A. Explosión principal (escala = particleScale) */
            efectoExplosion = ParticleManager.get().obtainEffect("papelExplosion", sprite.getX() + sprite.getOriginX(), sprite.getY() + sprite.getOriginY());
            efectoExplosion.scaleEffect(particleScale);   // 0.55 si es frag, 1 si no

            /* 1-B. Generamos los fragmentos */
            int cnt = MathUtils.random(3, 6);
            float origVel = (float) Math.sqrt(velH * velH + velV * velV);

            for (int i = 0; i < cnt; i++) {
                float ang = MathUtils.random(0f, 360f);
                float spdFactor = MathUtils.random(0.3f, 0.7f);
                float spd = origVel * spdFactor;
                float offsetX = MathUtils.random(-15f, 15f);
                float offsetY = MathUtils.random(0f, 10f);

                _04ProyectilPapelCulo frag = new _04ProyectilPapelCulo(sprite.getX() + offsetX, sprite.getY() + offsetY, ang, spd, jugador.getPoderJugador(), 0f, jugador, MathUtils.randomBoolean() ? 1f : -1f, true);         // esFragmento = true

                frag.setCanFragment(false);               // sin fragmentación recursiva
                jugador.getControladorProyectiles().anyadirNuevoProyectil(frag);
            }

            desactivarProyectil();
            if (impactados.add(enemigo)) applyKnockback(enemigo, sprite.getX(), sprite.getY());
            return;
        }

        /* ---------- 2) Impacto normal sin fragmentar ---------- */
        if (!impactoAnimacionActiva) {
            audio.reproducirEfecto(canFragment ? "explosion" : "explosionFragmentada", canFragment ? 1f : 0.75f);

            impactoAnimacionActiva = true;
            animationStateTime = 0f;
            impactX = sprite.getX();
            impactY = sprite.getY();
            impactRotation = sprite.getRotation();

            /* 2-A. Explosión visual (escala = particleScale) */
            efectoExplosion = ParticleManager.get().obtainEffect("papelExplosion", impactX + sprite.getOriginX(), impactY + sprite.getOriginY());
            efectoExplosion.scaleEffect(particleScale * 0.75f);

            particles.reset();
            efecto.allowCompletion();
        }

        if (impactados.add(enemigo)) applyKnockback(enemigo, impactX, impactY);
    }

    private void applyKnockback(Enemigo e, float ix, float iy) {
        float ex = e.getX() + e.getSprite().getWidth() * 0.5f;
        float ey = e.getY() + e.getSprite().getHeight() * 0.5f;
        float dx = ex - ix, dy = ey - iy;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist != 0f) {
            dx /= dist;
            dy /= dist;
        }
        e.aplicarKnockback(getKnockbackForce(), dx, dy);
    }

    @Override
    public boolean yaImpacto(Enemigo e) {
        return impactados.contains(e);
    }

    @Override
    public boolean esCritico() {
        return esCritico;
    }

    /* ---------- Auxiliares público ---------- */
    public boolean isImpactoAnimacionActiva() {
        return impactoAnimacionActiva;
    }

    public float getAnguloLanzamiento() {
        return anguloLanzamiento;
    }

    public float getVelocidadProyectil() {
        return velH != 0 ? velH : velV;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setCanFragment(boolean v) {
        canFragment = v;
    }

    public Sprite getSprite() {
        return sprite;
    }
}

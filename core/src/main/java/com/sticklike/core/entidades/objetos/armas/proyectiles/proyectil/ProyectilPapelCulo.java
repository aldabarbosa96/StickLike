package com.sticklike.core.entidades.objetos.armas.proyectiles.proyectil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.renderizado.RenderParticulasProyectil;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public final class ProyectilPapelCulo implements Proyectiles {
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

    public ProyectilPapelCulo(float x, float y, float anguloLanzamiento, float velocidadProyectil, float poderJugador, float extraDamage, Jugador jugador, float direccionHorizontal, boolean esFragmento) {
        // Carga única de recursos
        if (TEXTURE == null) {
            TEXTURE = manager.get(ARMA_PAPELCULO, Texture.class);
        }
        if (IMPACT_ANIMATION == null) {
            IMPACT_ANIMATION = animations.get("papelCuloImpacto");
        }

        this.jugador = jugador;
        this.anguloLanzamiento = anguloLanzamiento;
        this.isFragment = esFragmento;
        this.velV = velocidadProyectil * MathUtils.sinDeg(anguloLanzamiento);
        this.velH = direccionHorizontal * velocidadProyectil * MathUtils.cosDeg(anguloLanzamiento) * FRAG_FACTOR_HOR;

        // Sprite
        sprite = new Sprite(TEXTURE);
        sprite.setSize(PAPELCULO_W_SIZE, PAPELCULO_H_SIZE);
        sprite.setOriginCenter();
        sprite.setPosition(x, y);

        // Partículas
        float scale = Gdx.graphics.getWidth() / REAL_WIDTH;
        particles = new RenderParticulasProyectil((int) (PARTICLE_LEN * scale), PARTICLE_WID * scale, new Color(0.65f, 0.5f, 0.7f, 1f));

        // Altitud final aleatoria
        float minY = jugador.getControladorEnemigos().getVentanaJuego1().getOrtographicCamera().position.y - jugador.getControladorEnemigos().getVentanaJuego1().getOrtographicCamera().viewportHeight / 2;
        float maxY = jugador.getSprite().getY() + jugador.getSprite().getHeight() - 10f;
        altitudeFinal = MathUtils.random(minY, maxY);

        // Daño escalado
        float baseDamage = DANYO_PAPELCULO + extraDamage + MathUtils.random(15f);
        damageEscalado = baseDamage * (1f + poderJugador / 100f);

        // Inicializar colisión
        collisionRect.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        collisionCircle.set(sprite.getX() + sprite.getWidth() / 2f, sprite.getY() + sprite.getHeight() / 2f, sprite.getWidth() / 2f);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        // Partículas en el centro
        center.set(sprite.getX() + sprite.getWidth() * 0.5f, sprite.getY() + sprite.getHeight() * 0.5f);
        particles.update(center);

        // Si hay animación de impacto activa, avanzamos y luego desactivamos
        if (impactoAnimacionActiva) {
            animationStateTime += delta;
            if (IMPACT_ANIMATION.isAnimationFinished(animationStateTime)) {
                desactivarProyectil();
                impactoAnimacionActiva = false;
            }
            return;
        }

        // Movimiento en vuelo
        if (!landed) {
            float nx = sprite.getX() + velH * delta;
            velV -= (velV > 0 ? GRAVITY_ASC : GRAVITY_DESC) * delta;
            float ny = sprite.getY() + velV * delta;

            if (ny <= altitudeFinal) {
                ny = altitudeFinal;
                landed = true;
                velH = velV = 0f;
                sprite.setRotation(-5f);
            }

            sprite.setPosition(nx, ny);
            sprite.rotate(ROTATION_SPEED * delta);
        } else if (isFragment) {
            // Fragmentos se destruyen al aterrizar
            desactivarProyectil();
        }

        // Actualizar hitboxes
        collisionRect.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        collisionCircle.set(sprite.getX() + sprite.getWidth() / 2f, sprite.getY() + sprite.getHeight() / 2f, sprite.getWidth() / 2f);
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (!activo) return;

        particles.render(batch);

        if (!landed && !impactoAnimacionActiva) {
            sprite.draw(batch);
        } else if (impactoAnimacionActiva) {
            TextureRegion frame = IMPACT_ANIMATION.getKeyFrame(animationStateTime, false);
            batch.draw(frame, impactX, impactY, sprite.getOriginX(), sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX() * IMPACT_SCALE, sprite.getScaleY() * IMPACT_SCALE, impactRotation);
        } else {
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        TEXTURE = null;
        particles.dispose();
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
        activo = false;
    }

    @Override
    public float getBaseDamage() {
        if (MathUtils.random() < jugador.getCritico()) {
            esCritico = true;
            return damageEscalado * 1.5f;
        } else {
            esCritico = false;
            return damageEscalado;
        }
    }

    @Override
    public float getKnockbackForce() {
        return EMPUJE_BASE_PAPELCULO;
    }

    @Override
    public boolean isPersistente() {
        return true;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        // Fragmentación
        if (jugador.getAtaquePapelCulo().isFragmentado() && canFragment) {
            canFragment = false;

            int cnt = MathUtils.random(3, 6);
            float origSpeed = (float) Math.sqrt(velH * velH + velV * velV);

            for (int i = 0; i < cnt; i++) {
                float ang = MathUtils.random(0f, 360f);
                float speedFactor = MathUtils.random(0.3f, 0.7f);
                float spd = origSpeed * speedFactor;
                float offsetX = MathUtils.random(-15f, 15f);
                float offsetY = MathUtils.random(0f, 10f);

                // Creamos el fragmento
                ProyectilPapelCulo frag = new ProyectilPapelCulo(sprite.getX() + offsetX, sprite.getY() + offsetY, ang, spd, jugador.getPoderJugador(), 0f, jugador, MathUtils.randomBoolean() ? 1f : -1f, true);

                frag.setCanFragment(false);
                frag.getSprite().setSize(frag.getSprite().getWidth() * 0.55f, frag.getSprite().getHeight() * 0.55f);
                frag.getSprite().setOriginCenter();
                jugador.getControladorProyectiles().anyadirNuevoProyectil(frag);
            }

            // Desactivar el proyectil original y aplicar knockback
            desactivarProyectil();
            if (impactados.add(enemigo)) {
                applyKnockback(enemigo, impactX, impactY);
            }
            return;
        }

        // Impacto normal
        if (!impactoAnimacionActiva) {
            audio.reproducirEfecto(canFragment ? "explosion" : "explosionFragmentada", canFragment ? 1f : 0.75f);
            impactoAnimacionActiva = true;
            animationStateTime = 0f;
            impactX = sprite.getX();
            impactY = sprite.getY();
            impactRotation = sprite.getRotation();
        }
        if (impactados.add(enemigo)) {
            applyKnockback(enemigo, impactX, impactY);
        }
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
    public boolean yaImpacto(Enemigo enemigo) {
        return impactados.contains(enemigo);
    }

    @Override
    public boolean esCritico() {
        return esCritico;
    }

    // Métodos auxiliares
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

    public void setCanFragment(boolean canFragment) {
        this.canFragment = canFragment;
    }

    public Sprite getSprite() {
        return sprite;
    }
}

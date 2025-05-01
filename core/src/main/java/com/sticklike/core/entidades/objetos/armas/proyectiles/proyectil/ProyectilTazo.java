package com.sticklike.core.entidades.objetos.armas.proyectiles.proyectil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueTazo;
import com.sticklike.core.entidades.renderizado.RenderParticulasProyectil;
import com.sticklike.core.entidades.renderizado.TrailRender;          // ← nuevo
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public final class ProyectilTazo implements Proyectiles {
    private static Texture TEXTURE;

    public enum Phase {GROWING, ACTIVE, COOLDOWN}

    /* ---------- constantes ---------- */
    private static final float MIN_SCALE = 0.1f;
    private static final float MAX_SCALE = 0.9f;
    private static final float GROW_DURATION = 0.5f;
    private static final float ROT_GROW = 720f;
    private static final float ROT_ACTIVE = 1000f;
    private static final float COOLDOWN_DURATION = 3.5f;
    private static final float PARTICLE_LEN = 15f;
    private static final float PARTICLE_WID = 6f;

    /* ---------- recursos ---------- */
    private final Sprite sprite;
    private final RenderParticulasProyectil particles;
    private final GestorDeAudio audio = GestorDeAudio.getInstance();

    /* ---------- estado externo ---------- */
    private final Jugador jugador;
    private AtaqueTazo ataqueTazo;
    private float offsetAngle;
    private float radio;
    private float radioColision;
    private float powerFactor;

    /* ---------- reutilización------------- */
    private final Rectangle collisionRect = new Rectangle();
    private final Vector2 center = new Vector2();
    private final Set<Enemigo> impactados = new HashSet<>(8);

    /* ---------- ciclo ---------- */
    private boolean activo = true;
    private Phase phase = Phase.GROWING;
    private float phaseTimer = 0f;
    private float growthTimer = 0f;
    private float damageTimer = 0f;
    private float rotationAccum = 0f;

    /* ---------- config pública ---------- */
    private float activeDuration = 8.5f;
    private boolean esCritico;

    public ProyectilTazo(Jugador jugador, AtaqueTazo ataqueTazo, float offsetAngle, float radio, GestorDeAudio gestor) {

        if (TEXTURE == null) TEXTURE = manager.get(ARMA_TAZOS, Texture.class);

        this.jugador = jugador;
        this.ataqueTazo = ataqueTazo;
        this.offsetAngle = offsetAngle;
        this.radio = radio;
        this.radioColision = RADIO_TAZOS;
        this.powerFactor = 1f + jugador.getPoderJugador() / 100f;

        sprite = new Sprite(TEXTURE);
        sprite.setSize(TAZO_SIZE, TAZO_SIZE);
        sprite.setOriginCenter();
        sprite.setScale(MIN_SCALE);

        float scale = Gdx.graphics.getWidth() / REAL_WIDTH;
        particles = new RenderParticulasProyectil((int) (PARTICLE_LEN * scale), PARTICLE_WID * scale, Color.RED);
    }

    /* ----------------------------------------------------------- */
    /*                       LÓGICA                                */
    /* ----------------------------------------------------------- */

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;
        phaseTimer += delta;

        /* posición orbital */
        float ang = MathUtils.degreesToRadians * (ataqueTazo.getGlobalAngle() + offsetAngle);
        float px = jugador.getSprite().getX() + jugador.getSprite().getWidth() * .5f;
        float py = jugador.getSprite().getY() + jugador.getSprite().getHeight() * .5f - 5f;
        float x = px + MathUtils.cos(ang) * radio - sprite.getWidth() * .5f;
        float y = py + MathUtils.sin(ang) * radio - sprite.getHeight() * .5f;

        switch (phase) {
            case GROWING -> {
                // Reset al inicio de GROWING para limpiar el trail
                particles.reset();
                growthTimer += delta;
                float gp = Math.min(growthTimer / GROW_DURATION, 1f);
                sprite.setScale(MIN_SCALE + gp * (MAX_SCALE - MIN_SCALE));
                rotationAccum += ROT_GROW * delta;
                sprite.setRotation(rotationAccum);
                if (growthTimer >= GROW_DURATION) {
                    phase = Phase.ACTIVE;
                    phaseTimer = damageTimer = growthTimer = 0f;
                    impactados.clear();
                    sprite.setColor(1f, 1f, 1f, 1f);
                }
            }
            case ACTIVE -> {
                sprite.setScale(MAX_SCALE);
                rotationAccum += ROT_ACTIVE * delta;
                sprite.setRotation(rotationAccum);
                damageTimer += delta;
                if (damageTimer >= INTERVALO_TAZOS) {
                    damageTimer = 0f;
                    impactados.clear();
                    sprite.setColor(1f, 1f, 1f, 1f);
                }
                if (phaseTimer >= ataqueTazo.getDuracionActivaTazo()) {
                    phase = Phase.COOLDOWN;
                    phaseTimer = 0f;
                }
            }
            case COOLDOWN -> {
                if (phaseTimer < GROW_DURATION) {
                    float cp = Math.min(phaseTimer / GROW_DURATION, 1f);
                    sprite.setScale(MAX_SCALE - cp * (MAX_SCALE - MIN_SCALE));
                    sprite.setColor(1f, 1f, 1f, 1f - cp);
                } else {
                    sprite.setColor(1f, 1f, 1f, 0f);
                }
                if (phaseTimer >= COOLDOWN_DURATION) {
                    // Preparamos de nuevo el ciclo
                    phase = Phase.GROWING;
                    phaseTimer = growthTimer = damageTimer = 0f;
                    sprite.setScale(MIN_SCALE);
                    sprite.setColor(1f, 1f, 1f, 1f);
                }
            }
        }

        // Actualizamos posición y trail sólo en GROWING/ACTIVE
        sprite.setPosition(x, y);
        if (phase != Phase.COOLDOWN) {
            center.set(x + sprite.getWidth() * .5f, y + sprite.getHeight() * .5f);
            particles.update(center);
            TrailRender.get().submit(particles);
        } else {
            // al acabar, opcionalmente limpiamos también
            particles.reset();
        }

        // Hit-box
        if (phase == Phase.ACTIVE) {
            collisionRect.set(center.x - radioColision * .5f, center.y - radioColision * .5f, radioColision, radioColision);
        } else {
            collisionRect.set(0, 0, 0, 0);
        }
    }

    /* ----------------------------------------------------------- */
    /*                       RENDER                                */
    /* ----------------------------------------------------------- */

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (!activo) return;
        // el rastro lo pinta TrailRender
        sprite.draw(batch);
    }

    /* ----------------------------------------------------------- */
    /*                       INTERFAZ                              */
    /* ----------------------------------------------------------- */

    @Override
    public Rectangle getRectanguloColision() {
        return collisionRect;
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
    public boolean isProyectilActivo() {
        return activo;
    }

    @Override
    public void desactivarProyectil() {
        activo = false;
        particles.reset();
    }

    @Override
    public float getBaseDamage() {
        if (phase != Phase.ACTIVE) return 0f;
        float base = DANYO_TAZOS + MathUtils.random(3.5f);
        if (MathUtils.random() < jugador.getCritico()) {
            esCritico = true;
            base *= 1.5f;
        } else {
            esCritico = false;
        }
        return base * powerFactor;
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
    public void registrarImpacto(Enemigo e) {
        if (impactados.add(e)) {
            audio.reproducirEfecto("tazo", 1f);
            sprite.setColor(Color.RED);
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

    /* ---------- getters / setters auxiliares ---------- */
    public AtaqueTazo getAtaqueTazo() {
        return ataqueTazo;
    }

    public Phase getPhase() {
        return phase;
    }

    public float getPhaseTimer() {
        return phaseTimer;
    }

    public void setPhase(Phase ph, float t) {
        phase = ph;
        phaseTimer = t;
    }

    public float getActiveDuration() {
        return activeDuration;
    }

    public void setActiveDuration(float d) {
        activeDuration = d;
    }

    public void setOffsetAngle(float o) {
        offsetAngle = o;
    }
}

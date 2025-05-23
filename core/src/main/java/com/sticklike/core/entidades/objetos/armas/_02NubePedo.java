package com.sticklike.core.entidades.objetos.armas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.renderizado.particulas.ParticleManager;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.HashMap;
import java.util.Map;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.DANYO_PEDO;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.ARMA_NUBE_PEDO;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.manager;

/**
 * Proyectil NubePedo optimizado: sigue al jugador y daña enemigos en pulsos vibratorios.
 */
public final class _02NubePedo implements Proyectiles {
    public enum Phase {GROWING, VIBRATE1, PAUSE, VIBRATE2, VIBRATE3, COOLDOWN}

    // Constantes de configuración
    private static final float GROW_DURATION = 0.15f;
    private static final float INV_GROW_DURATION = 1f / GROW_DURATION;
    private static final float VIBRATE1_DURATION = 0.3f;
    private static final float PAUSE_DURATION = 0.25f;
    private static final float VIBRATE2_DURATION = 0.3f;
    private static final float VIBRATE2_EXTRA = 0.2f;
    private static final float COOLDOWN_DURATION = 2.5f;
    private static final float MIN_SCALE = 0.1f;
    private static final float MAX_SCALE = 1.35f;
    private static final float MIN_ALPHA = 0.1f;
    private static final float MAX_ALPHA = 0.65f;
    private static final float VIBRATE_RANGE = 8f;
    private static final float ROTATION_SPEED = 360f;
    private static final float ROTATION_SPEED2 = 2500f;
    private static final float DEFAULT_SIZE = 50f;
    private static final float KNOCKBACK_BASE = 200f;
    private static final float PULSE_FREQUENCY = 4f;

    private final ParticleEffectPool.PooledEffect efectoParticula;

    // Recursos estáticos
    private static Texture TEXTURE;

    // Estado de instancia
    private final Sprite sprite;
    private final Rectangle collisionRect = new Rectangle();
    private final Map<Enemigo, Float> enemigosImpactados = new HashMap<>(8);
    private static final float INTERVALO_IMPACTO = 0.33f;
    private final GestorDeAudio audio;
    private final float powerFactor;
    private final Jugador jugador;

    private Phase phase = Phase.GROWING;
    private float phaseTimer = 0f;
    private boolean activo = true;
    private boolean critico = false;
    private float knockback = KNOCKBACK_BASE;

    public _02NubePedo(Jugador jugador) {
        // Carga única de textura
        if (TEXTURE == null) {
            TEXTURE = manager.get(ARMA_NUBE_PEDO, Texture.class);
        }
        this.jugador = jugador;
        this.audio = GestorDeAudio.getInstance();
        this.powerFactor = 1f + (jugador.getPoderJugador() / 100f);

        // Configuración del sprite
        sprite = new Sprite(TEXTURE);
        sprite.setSize(DEFAULT_SIZE, DEFAULT_SIZE);
        sprite.setOriginCenter();
        sprite.setScale(MIN_SCALE);
        sprite.setColor(0.75f, 0.75f, 0.75f, MIN_ALPHA);

        Vector2 posJugador = jugador.getPosicionTmp();
        float cx = posJugador.x + sprite.getWidth() * 0.5f;
        float cy = posJugador.y + sprite.getHeight() * 0.5f;
        efectoParticula = ParticleManager.get().obtainEffect("pedo", cx, cy, true);

        // Inicializar rectángulo para colisión
        collisionRect.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        float px = sprite.getX() + sprite.getWidth() * 0.5f;
        float py = sprite.getY() + sprite.getHeight() * 0.5f;
        efectoParticula.setPosition(px, py);

        sprite.rotate(-1 * ROTATION_SPEED * delta);

        // Calcular progreso y alpha antes de incrementar timer
        float progress = Math.min(phaseTimer * INV_GROW_DURATION, 1f);
        float currentAlpha = MIN_ALPHA + progress * (MAX_ALPHA - MIN_ALPHA);
        phaseTimer += delta;

        float pulse = 0.5f + 0.5f * MathUtils.sin(phaseTimer * PULSE_FREQUENCY * MathUtils.PI2);

        // Posición base centrada en jugador
        float px1 = jugador.getSprite().getX();
        float py1 = jugador.getSprite().getY();
        float pw = jugador.getSprite().getWidth();
        float ph = jugador.getSprite().getHeight();
        float sw = sprite.getWidth();
        float sh = sprite.getHeight();

        /* todo --> estos colores quedan guays para un futura ultimate
        float r = MathUtils.lerp(0.5f, 0.7f, pulse);
        float g = MathUtils.lerp(0.2f, 0.1f, pulse);
        float b = MathUtils.lerp(0.1f, 0f, pulse);*/

        float r = MathUtils.lerp(0.65f, 0.85f, pulse); // todo --> revisar colores del pulso
        float g = MathUtils.lerp(0.5f, 0.75f, pulse);
        float b = MathUtils.lerp(0.15f, 0.1f, pulse);

        float alphaActual = sprite.getColor().a;

        float baseX = px1 + pw * 0.5f - sw * 0.5f;
        float baseY = py1 + ph * 0.5f - sh * 0.5f;

        switch (phase) {
            case GROWING:
                sprite.rotate(ROTATION_SPEED2 * delta);
                sprite.setScale(MIN_SCALE + progress * (MAX_SCALE - MIN_SCALE));
                sprite.setColor(1f, 0.82f, 0.5f, currentAlpha);
                sprite.setPosition(baseX, baseY);
                if (phaseTimer >= GROW_DURATION) {
                    phase = Phase.VIBRATE1;
                    phaseTimer = 0f;
                }
                break;
            case VIBRATE1:
                audio.reproducirEfecto("pedo", 0.5f);
                sprite.setScale(MAX_SCALE);
                sprite.setColor(1f, 0.65f, 0.1f, MAX_ALPHA);
                sprite.setPosition(baseX + MathUtils.random(-1f, 1f) * VIBRATE_RANGE, baseY + MathUtils.random(-1f, 1f) * VIBRATE_RANGE);
                if (phaseTimer >= VIBRATE1_DURATION) {
                    phase = Phase.PAUSE;
                    phaseTimer = 0f;
                }
                break;
            case PAUSE:
                sprite.setScale(MAX_SCALE);
                sprite.setColor(1f, 0, 0f, currentAlpha);
                sprite.setPosition(baseX, baseY);
                sprite.setColor(r, g, b, alphaActual);
                if (phaseTimer >= PAUSE_DURATION) {
                    phase = Phase.VIBRATE2;
                    phaseTimer = 0f;
                }
                break;
            case VIBRATE2:
                audio.reproducirEfecto("pedo", 0.5f);
                sprite.setScale(MAX_SCALE);
                sprite.setColor(1f, 0.65f, 0.1f, MAX_ALPHA);
                sprite.setPosition(baseX + MathUtils.random(-1f, 1f) * VIBRATE_RANGE, baseY + MathUtils.random(-1f, 1f) * VIBRATE_RANGE);
                if (phaseTimer >= VIBRATE2_DURATION + VIBRATE2_EXTRA) {
                    phase = jugador.getAtaqueNubePedo().isEsTriple() ? Phase.VIBRATE3 : Phase.COOLDOWN;
                    phaseTimer = 0f;
                    sprite.setScale(MIN_SCALE);
                }
                break;
            case VIBRATE3:
                audio.reproducirEfecto("pedo", 0.5f);
                sprite.setScale(MAX_SCALE);
                sprite.setColor(1f, 0.65f, 0.1f, MAX_ALPHA);
                sprite.setPosition(baseX + MathUtils.random(-1f, 1f) * VIBRATE_RANGE + 2f, baseY + MathUtils.random(-1f, 1f) * VIBRATE_RANGE + 2f);
                if (phaseTimer >= VIBRATE2_DURATION) {
                    phase = Phase.COOLDOWN;
                    phaseTimer = 0f;
                    sprite.setScale(MIN_SCALE);

                }
                break;
            case COOLDOWN:
                // Ahora dejamos la nube visible y con daño base
                sprite.setScale(MAX_SCALE);
                sprite.setColor(1f, 0.82f, 0.5f, MAX_ALPHA);
                sprite.setPosition(baseX, baseY);
                sprite.setColor(r, g, b, alphaActual);

                // Tras el cooldown, volvemos a empezar el ciclo
                if (phaseTimer >= COOLDOWN_DURATION) {
                    phase = Phase.GROWING;
                    phaseTimer = 0f;
                }
                break;
        }
        // Actualizar colisión
        collisionRect.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());

        // Actualizar tiempos de impacto por enemigo
        enemigosImpactados.entrySet().removeIf(entry -> entry.getKey() == null);

        for (Map.Entry<Enemigo, Float> entry : enemigosImpactados.entrySet()) {
            entry.setValue(entry.getValue() + delta);
        }

    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (activo) {
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        TEXTURE = null;
        efectoParticula.free();
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
        efectoParticula.allowCompletion();
        activo = false;
    }

    @Override
    public float getBaseDamage() {
        float rnd = MathUtils.random(3.35f);
        float base;
        if (phase == Phase.VIBRATE1 || phase == Phase.VIBRATE2 || phase == Phase.VIBRATE3) {
            if (MathUtils.random() < jugador.getCritico()) {
                critico = true;
                base = (DANYO_PEDO + rnd) * 1.5f;
            } else {
                critico = false;
                base = DANYO_PEDO + rnd;
            }
        } else {
            critico = false;
            base = DANYO_PEDO + rnd;
        }

        float dmg = base * powerFactor;
        return dmg > 0f ? dmg : 1f;
    }


    @Override
    public float getKnockbackForce() {
        return (phase == Phase.VIBRATE1 || phase == Phase.VIBRATE2 || phase == Phase.VIBRATE3) ? knockback : 0f;
    }

    @Override
    public boolean isPersistente() {
        return true;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        enemigosImpactados.put(enemigo, 0f);
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        Float tiempo = enemigosImpactados.get(enemigo);
        return tiempo != null && tiempo < INTERVALO_IMPACTO;
    }


    @Override
    public boolean esCritico() {
        return critico;
    }

    // Métodos adicionales
    public void setEscalaMax(float incremento) {
        sprite.setSize(sprite.getWidth() * incremento, sprite.getHeight() * incremento);
        sprite.setOriginCenter();
        efectoParticula.scaleEffect(incremento);
    }

    public void setMaxKnockBack(float inc) {
        this.knockback = this.knockback * inc;
    }

    public float getCooldownDuration() {
        return COOLDOWN_DURATION;
    }

    public float getTimeUntilNextShot() {
        return (phase == Phase.COOLDOWN) ? Math.max(0f, COOLDOWN_DURATION - phaseTimer) : 0f;
    }
}

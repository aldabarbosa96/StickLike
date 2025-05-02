package com.sticklike.core.entidades.objetos.armas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.DANYO_PEDO;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.ARMA_NUBE_PEDO;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.manager;

/**
 * Proyectil NubePedo optimizado: sigue al jugador y daña enemigos en pulsos vibratorios.
 */
public final class NubePedo implements Proyectiles {
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
    private static final float ROTATION_SPEED = 2500f;
    private static final float DEFAULT_SIZE = 50f;
    private static final float KNOCKBACK_BASE = 200f;

    // Recursos estáticos
    private static Texture TEXTURE;

    // Estado de instancia
    private final Sprite sprite;
    private final Rectangle collisionRect = new Rectangle();
    private final Set<Enemigo> impactados = new HashSet<>(8);
    private final GestorDeAudio audio;
    private final float powerFactor;
    private final Jugador jugador;

    private Phase phase = Phase.GROWING;
    private float phaseTimer = 0f;
    private boolean activo = true;
    private boolean critico = false;
    private float knockback = KNOCKBACK_BASE;

    public NubePedo(Jugador jugador) {
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

        // Inicializar rectángulo para colisión
        collisionRect.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        // Calcular progreso y alpha antes de incrementar timer
        float progress = Math.min(phaseTimer * INV_GROW_DURATION, 1f);
        float currentAlpha = MIN_ALPHA + progress * (MAX_ALPHA - MIN_ALPHA);
        phaseTimer += delta;

        // Posición base centrada en jugador
        float px = jugador.getSprite().getX();
        float py = jugador.getSprite().getY();
        float pw = jugador.getSprite().getWidth();
        float ph = jugador.getSprite().getHeight();
        float sw = sprite.getWidth();
        float sh = sprite.getHeight();

        float baseX = px + pw * 0.5f - sw * 0.5f;
        float baseY = py + ph * 0.5f - sh * 0.5f;

        switch (phase) {
            case GROWING:
                sprite.rotate(ROTATION_SPEED * delta);
                sprite.setScale(MIN_SCALE + progress * (MAX_SCALE - MIN_SCALE));
                sprite.setColor(1f, 0.82f, 0.5f, currentAlpha);
                sprite.setPosition(baseX, baseY);
                if (phaseTimer >= GROW_DURATION) {
                    phase = Phase.VIBRATE1;
                    phaseTimer = 0f;
                    impactados.clear();
                }
                break;
            case VIBRATE1:
                audio.reproducirEfecto("pedo", 0.45f);
                if (phaseTimer < delta) impactados.clear();
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
                sprite.setColor(1f, 0.82f, 0.5f, currentAlpha);
                sprite.setPosition(baseX, baseY);
                if (phaseTimer >= PAUSE_DURATION) {
                    phase = Phase.VIBRATE2;
                    phaseTimer = 0f;
                    impactados.clear();
                }
                break;
            case VIBRATE2:
                audio.reproducirEfecto("pedo", 0.45f);
                if (phaseTimer < delta) impactados.clear();
                sprite.setScale(MAX_SCALE);
                sprite.setColor(1f, 0.65f, 0.1f, MAX_ALPHA);
                sprite.setPosition(baseX + MathUtils.random(-1f, 1f) * VIBRATE_RANGE, baseY + MathUtils.random(-1f, 1f) * VIBRATE_RANGE);
                if (phaseTimer >= VIBRATE2_DURATION + VIBRATE2_EXTRA) {
                    phase = jugador.getAtaqueNubePedo().isEsTriple() ? Phase.VIBRATE3 : Phase.COOLDOWN;
                    phaseTimer = 0f;
                    sprite.setScale(MIN_SCALE);
                    impactados.clear();
                }
                break;
            case VIBRATE3:
                audio.reproducirEfecto("pedo", 0.45f);
                if (phaseTimer < delta) impactados.clear();
                sprite.setScale(MAX_SCALE);
                sprite.setColor(1f, 0.65f, 0.1f, MAX_ALPHA);
                sprite.setPosition(baseX + MathUtils.random(-1f, 1f) * VIBRATE_RANGE + 2f, baseY + MathUtils.random(-1f, 1f) * VIBRATE_RANGE + 2f);
                if (phaseTimer >= VIBRATE2_DURATION) {
                    phase = Phase.COOLDOWN;
                    phaseTimer = 0f;
                    sprite.setScale(MIN_SCALE);
                    impactados.clear();
                }
                break;
            case COOLDOWN:
                sprite.setPosition(-1000f, -1000f);
                sprite.setColor(0.75f, 0.75f, 0.75f, 0f);
                if (phaseTimer >= COOLDOWN_DURATION) {
                    phase = Phase.GROWING;
                    phaseTimer = 0f;
                    sprite.setColor(0.75f, 0.75f, 0.75f, MIN_ALPHA);
                }
                break;
        }

        // Actualizar colisión
        collisionRect.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (activo && phase != Phase.COOLDOWN) {
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        TEXTURE = null;
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
        activo = false;
    }

    @Override
    public float getBaseDamage() {
        if (phase == Phase.COOLDOWN) return 0f;
        float base = DANYO_PEDO;
        if (phase == Phase.VIBRATE1 || phase == Phase.VIBRATE2 || phase == Phase.VIBRATE3) {
            float rnd = MathUtils.random(3.35f);
            if (MathUtils.random() < jugador.getCritico()) {
                critico = true;
                base = (DANYO_PEDO + rnd) * 1.5f;
            } else {
                critico = false;
                base = DANYO_PEDO + rnd;
            }
        } else {
            critico = false;
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
        impactados.add(enemigo);
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return impactados.contains(enemigo);
    }

    @Override
    public boolean esCritico() {
        return critico;
    }

    // Métodos adicionales
    public void setEscalaMax(float incremento) {
        sprite.setSize(sprite.getWidth() * incremento, sprite.getHeight() * incremento);
        sprite.setOriginCenter();
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

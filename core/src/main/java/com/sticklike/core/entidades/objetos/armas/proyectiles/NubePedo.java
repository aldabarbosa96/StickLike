package com.sticklike.core.entidades.objetos.armas.proyectiles;

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
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Proyectil NubePedo; genera una nube de gas que sigue al jugador y daña enemigos en pulsos vibratorios, aplicando knockback.
 */

public class NubePedo implements Proyectiles {
    private Texture texture;
    private Sprite sprite;
    private Jugador jugador;
    private boolean proyectilActivo;
    private Set<Enemigo> enemigosImpactados = new HashSet<>();
    private float powerFactor;
    private boolean esCritico;


    // --- Estados para la animación ---
    public enum Phase {GROWING, VIBRATE1, PAUSE, VIBRATE2, VIBRATE3, COOLDOWN}

    private Phase phase = Phase.GROWING;
    private float phaseTimer = 0;

    private static final float GROW_DURATION = 0.15f;
    private static final float VIBRATE1_DURATION = 0.3f;
    private static final float PAUSE_DURATION = 0.25f;
    private static final float VIBRATE2_DURATION = 0.3f;
    private static final float COOLDOWN_DURATION = 2.5f;

    private static final float MIN_SCALE = 0.1f;
    private float maxScale = 1.35f;
    private static final float MIN_ALPHA = 0.1f;
    private static final float MAX_ALPHA = 0.65f;
    private static final float VIBRATE_RANGE = 8f;
    private float knockbackForce = 200f;
    private static final float ROTATION_SPEED = 2500f;

    public NubePedo(Jugador jugador) {
        this.texture = manager.get(ARMA_NUBE_PEDO, Texture.class);
        this.sprite = new Sprite(texture);
        sprite.setSize(50f, 50f);
        sprite.setOriginCenter();
        this.jugador = jugador;
        this.proyectilActivo = true;
        this.powerFactor = 1f + (Jugador.getPoderJugador() / 100f);

        sprite.setScale(MIN_SCALE);
        sprite.setColor(0.75f, 0.75f, 0.75f, MIN_ALPHA);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        float progress = Math.min(phaseTimer / GROW_DURATION, 1f);
        float currentAlpha = MIN_ALPHA + progress * (MAX_ALPHA - MIN_ALPHA);
        phaseTimer += delta;

        float jugadorCenterX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2 - sprite.getWidth() / 2;
        float jugadorCenterY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2 - sprite.getHeight() / 2;

        switch (phase) {
            case GROWING:
                sprite.rotate(ROTATION_SPEED * delta);
                float currentScale = MIN_SCALE + progress * (maxScale - MIN_SCALE);
                sprite.setScale(currentScale);
                sprite.setColor(1f, 0.82f, 0.5f, currentAlpha);
                sprite.setPosition(jugadorCenterX, jugadorCenterY);

                if (phaseTimer >= GROW_DURATION) {
                    phase = Phase.VIBRATE1;
                    phaseTimer = 0;
                    enemigosImpactados.clear();
                }
                break;

            case VIBRATE1:
                GestorDeAudio.getInstance().reproducirEfecto("pedo", 0.35f);
                if (phaseTimer < delta) {
                    enemigosImpactados.clear();
                }

                float offsetX1 = MathUtils.random(-1f, 1f) * VIBRATE_RANGE;
                float offsetY1 = MathUtils.random(-1f, 1f) * VIBRATE_RANGE;
                sprite.setScale(maxScale);
                sprite.setColor(1f, 0.65f, 0.1f, MAX_ALPHA);
                sprite.setPosition(jugadorCenterX + offsetX1, jugadorCenterY + offsetY1);

                if (phaseTimer >= VIBRATE1_DURATION) {
                    phase = Phase.PAUSE;
                    phaseTimer = 0;
                }
                break;

            case PAUSE:
                sprite.setScale(maxScale);
                sprite.setColor(1f, 0.82f, 0.5f, currentAlpha);
                sprite.setPosition(jugadorCenterX, jugadorCenterY);

                if (phaseTimer >= PAUSE_DURATION) {
                    phase = Phase.VIBRATE2;
                    phaseTimer = 0;
                    enemigosImpactados.clear();
                }
                break;

            case VIBRATE2:
                GestorDeAudio.getInstance().reproducirEfecto("pedo", 0.35f);
                if (phaseTimer < delta) {
                    enemigosImpactados.clear();
                }

                float offsetX2 = MathUtils.random(-1f, 1f) * VIBRATE_RANGE;
                float offsetY2 = MathUtils.random(-1f, 1f) * VIBRATE_RANGE;
                sprite.setScale(maxScale);
                sprite.setColor(1f, 0.65f, 0.1f, MAX_ALPHA);
                sprite.setPosition(jugadorCenterX + offsetX2, jugadorCenterY + offsetY2);

                if (phaseTimer >= VIBRATE2_DURATION + 0.2f) {
                    if (jugador.getAtaqueNubePedo().isEsTriple()) {
                        phase = Phase.VIBRATE3;
                    } else {
                        phase = Phase.COOLDOWN;
                        sprite.setScale(MIN_SCALE);
                    }
                    phaseTimer = 0;
                    enemigosImpactados.clear();
                }

                break;

            case VIBRATE3:
                GestorDeAudio.getInstance().reproducirEfecto("pedo", 0.35f);
                if (phaseTimer < delta) {
                    enemigosImpactados.clear();
                }

                float offsetX3 = MathUtils.random(-1f, 1f) * VIBRATE_RANGE + 2f;
                float offsetY3 = MathUtils.random(-1f, 1f) * VIBRATE_RANGE + 2f;
                sprite.setScale(maxScale);
                sprite.setColor(1f, 0.65f, 0.1f, MAX_ALPHA);
                sprite.setPosition(jugadorCenterX + offsetX3, jugadorCenterY + offsetY3);

                if (phaseTimer >= VIBRATE2_DURATION) {
                    phase = Phase.COOLDOWN;
                    phaseTimer = 0;
                    sprite.setScale(MIN_SCALE);
                    enemigosImpactados.clear();
                }
                break;


            case COOLDOWN:
                sprite.setPosition(-1000, -1000);
                sprite.setColor(0.75f, 0.75f, 0.75f, 0f);
                if (phaseTimer >= COOLDOWN_DURATION) {
                    phase = Phase.GROWING;
                    phaseTimer = 0;
                    sprite.setColor(0.75f, 0.75f, 0.75f, MIN_ALPHA);
                }
                break;
        }
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
        if (phase == Phase.COOLDOWN) {
            return new Rectangle(0, 0, 0, 0);
        }
        return new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }


    public void setEscalaMax(float incremento) {
        float nuevoAncho = sprite.getWidth() * incremento;
        float nuevoAlto = sprite.getHeight() * incremento;

        sprite.setSize(nuevoAncho, nuevoAlto);
        sprite.setOriginCenter();

    }

    public void setMaxKnockBack(float incremento) {
        this.knockbackForce = knockbackForce * incremento;
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo && phase != Phase.COOLDOWN) {
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        texture = null;
    }

    @Override
    public boolean isProyectilActivo() {
        return proyectilActivo;
    }

    @Override
    public void desactivarProyectil() {
        proyectilActivo = false;
    }

    @Override
    public float getBaseDamage() {
        if (phase == Phase.COOLDOWN) {
            return 0f;
        }
        float baseDamage = 0;

        if (phase == Phase.VIBRATE1 || phase == Phase.VIBRATE2 || phase == Phase.VIBRATE3) {
            if (MathUtils.random() < jugador.getCritico()) {
                esCritico = true;
                baseDamage = DANYO_PEDO + MathUtils.random(3.35f);
                baseDamage *= 1.5f;
            } else {
                esCritico = false;
                baseDamage = DANYO_PEDO + MathUtils.random(3.35f);
            }

        } else {
            esCritico = false;
            baseDamage = DANYO_PEDO;
        }
        float damageEscalado = baseDamage * powerFactor;

        return (damageEscalado > 0f) ? damageEscalado : 1f;
    }

    @Override
    public float getKnockbackForce() {
        // Se aplica knockback solo durante las vibraciones
        if (phase == Phase.VIBRATE1 || phase == Phase.VIBRATE2 || phase == Phase.VIBRATE3) {
            return knockbackForce;
        }
        return 0f;
    }


    @Override
    public boolean isPersistente() {
        return true;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        enemigosImpactados.add(enemigo);
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return enemigosImpactados.contains(enemigo);
    }

    @Override
    public boolean esCritico() {
        return esCritico;
    }
}

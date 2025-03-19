package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.renderizado.RenderParticulasProyectil;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueTazo;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Proyectil Tazo; orbita alrededor del jugador causando daño en ciclos alternos de crecimiento, fase activa y cooldown.
 */

public class ProyectilTazo implements Proyectiles {
    private static Texture textura;
    private Sprite sprite;
    private boolean proyectilActivo;
    private Set<Enemigo> enemigosImpactados = new HashSet<>();
    private float temporizadorDano = 0;
    private Jugador jugador;
    private AtaqueTazo ataqueTazo;
    private float radioColision;
    private float offsetAngle;
    private float radio;
    private float rotacionSprite = 0f;
    private GestorDeAudio gestorDeAudio;
    private RenderParticulasProyectil renderParticulasProyectil;
    private Vector2 centroSprite;
    private boolean esCritico;
    private static final float MIN_GROWTH_SCALE = 0.1f;
    private static final float MAX_GROWTH_SCALE = 0.9f;
    private static final float GROW_DURATION = 0.5f;  // Duración del efecto de crecimiento y fade-out

    // --- fases del ciclo del tazo ---
    public enum Phase {GROWING, ACTIVE, COOLDOWN}

    private Phase phase = Phase.GROWING;
    private float phaseTimer = 0f;  // Temporizador para la fase actual
    private float activeDuration = 8.5f;
    private static final float COOLDOWN_DURATION = 3.5f;
    private float growthTimer = 0f;
    private float powerFactor;

    public ProyectilTazo(Jugador jugador, AtaqueTazo ataqueTazo, float offsetAngle, float radio, GestorDeAudio gestorDeAudio) {
        if (textura == null) {
            textura = manager.get(ARMA_TAZOS, Texture.class);
        }
        this.jugador = jugador;
        this.sprite = new Sprite(textura);
        this.sprite.setSize(TAZO_SIZE, TAZO_SIZE);
        this.sprite.setOriginCenter();
        this.proyectilActivo = true;
        this.radioColision = RADIO_TAZOS;
        this.ataqueTazo = ataqueTazo;
        this.offsetAngle = offsetAngle;
        this.radio = radio;
        this.gestorDeAudio = gestorDeAudio;
        this.powerFactor = 1f + (jugador.getPoderJugador() / 100f);

        float scaleFactor = Gdx.graphics.getWidth() / REAL_WIDTH;
        int maxLength = (int) (15 * scaleFactor);
        float scaledWidth = 5 * scaleFactor;
        this.renderParticulasProyectil = new RenderParticulasProyectil(maxLength, scaledWidth, Color.RED);
        this.centroSprite = new Vector2();
        // Iniciamos con la escala mínima para el efecto de crecer
        sprite.setScale(MIN_GROWTH_SCALE);

    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;
        phaseTimer += delta;

        float jugadorCentroX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float jugadorCentroY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2 - 5f;

        // Calcula la posición del tazo en función del ángulo global + offset
        float currentAngle = ataqueTazo.getGlobalAngle() + offsetAngle;
        float radianes = currentAngle * MathUtils.degreesToRadians;
        float offsetX = MathUtils.cos(radianes) * radio;
        float offsetY = MathUtils.sin(radianes) * radio;

        switch (phase) {
            case GROWING:
                // Efecto de crecer: interpolamos la escala de MIN_GROWTH_SCALE a MAX_GROWTH_SCALE
                growthTimer += delta;
                float progress = Math.min(growthTimer / GROW_DURATION, 1f);
                float currentScale = MIN_GROWTH_SCALE + progress * (MAX_GROWTH_SCALE - MIN_GROWTH_SCALE);
                sprite.setScale(currentScale);
                rotacionSprite += 720f * delta;
                sprite.setRotation(rotacionSprite);
                // Posicionamos el sprite centrado
                sprite.setPosition(jugadorCentroX + offsetX - sprite.getWidth() / 2, jugadorCentroY + offsetY - sprite.getHeight() / 2);
                // Cuando termina el crecimiento, pasamos a la fase ACTIVE
                if (growthTimer >= GROW_DURATION) {
                    phase = Phase.ACTIVE;
                    phaseTimer = 0;
                }
                break;

            case ACTIVE:
                // Fase ACTIVE: el tazo se muestra normalmente
                sprite.setScale(MAX_GROWTH_SCALE);
                rotacionSprite += 1000f * delta;
                sprite.setRotation(rotacionSprite);
                sprite.setPosition(jugadorCentroX + offsetX - sprite.getWidth() / 2, jugadorCentroY + offsetY - sprite.getHeight() / 2);
                // Lógica de daño
                temporizadorDano += delta;
                if (temporizadorDano >= INTERVALO_TAZOS) {
                    enemigosImpactados.clear();
                    temporizadorDano = 0;
                }
                if (enemigosImpactados.isEmpty()) {
                    sprite.setColor(1, 1, 1, 1);
                }
                // Si transcurre el tiempo activo, pasamos a la fase COOLDOWN
                if (phaseTimer >= ataqueTazo.getDuracionActivaTazo()) {
                    phase = Phase.COOLDOWN;
                    phaseTimer = 0;
                }
                break;

            case COOLDOWN:
                // Durante COOLDOWN: se realiza un efecto de fade-out
                if (phaseTimer < GROW_DURATION) {
                    float cooldownProgress = Math.min(phaseTimer / GROW_DURATION, 1f);
                    float scaleDuringCooldown = MAX_GROWTH_SCALE - cooldownProgress * (MAX_GROWTH_SCALE - MIN_GROWTH_SCALE);
                    sprite.setScale(scaleDuringCooldown);
                    float alphaDuringCooldown = 1f - cooldownProgress;
                    sprite.setColor(1, 1, 1, alphaDuringCooldown);
                } else {
                    // Después del fade-out, el sprite permanece oculto
                    sprite.setColor(1, 1, 1, 0f);
                }
                // Mantenemos la posición
                sprite.setPosition(jugadorCentroX + offsetX - sprite.getWidth() / 2, jugadorCentroY + offsetY - sprite.getHeight() / 2);
                // Una vez completados los 3 segundos de cooldown, reiniciamos el ciclo
                if (phaseTimer >= COOLDOWN_DURATION) {
                    phase = Phase.GROWING;
                    phaseTimer = 0;
                    growthTimer = 0;
                    sprite.setScale(MIN_GROWTH_SCALE);
                    sprite.setColor(1, 1, 1, 1);
                }
                break;
        }
        centroSprite.set(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2);
        renderParticulasProyectil.update(centroSprite);
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo) {

            if (sprite.getColor().a > 0f) {
                renderParticulasProyectil.render(batch);
            }
            sprite.draw(batch);
        }
    }

    @Override
    public Rectangle getRectanguloColision() {
        if (phase != Phase.ACTIVE) {
            return new Rectangle(0, 0, 0, 0);
        }
        float visualWidth = sprite.getWidth() * sprite.getScaleX();
        float visualHeight = sprite.getHeight() * sprite.getScaleY();
        return new Rectangle(sprite.getX() + visualWidth / 2 - radioColision / 2, sprite.getY() + visualHeight / 2 - radioColision / 2, radioColision, radioColision);
    }

    @Override
    public void dispose() {
        textura = null;
        renderParticulasProyectil.dispose();
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
        return proyectilActivo;
    }

    @Override
    public void desactivarProyectil() {
        proyectilActivo = false;
    }

    @Override
    public float getBaseDamage() {
        if (phase == Phase.ACTIVE) {
            if (MathUtils.random() < jugador.getCritico()) {
                esCritico = true;
                float baseDamage = DANYO_TAZOS + MathUtils.random(3.5f);
                baseDamage *= 1.5f;
                return baseDamage * powerFactor;
            } else {
                esCritico = false;
                float baseDamage = DANYO_TAZOS + MathUtils.random(3.5f);
                return baseDamage * powerFactor;
            }
        }
        return 0f;
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
        gestorDeAudio.reproducirEfecto("tazo", 1);
        enemigosImpactados.add(enemigo);
        sprite.setColor(1f, 0, 0f, 1);
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return enemigosImpactados.contains(enemigo);
    }

    @Override
    public boolean esCritico() {
        return esCritico;
    }

    public AtaqueTazo getAtaqueTazo() {
        return ataqueTazo;
    }

    public void setPhase(Phase phase, float phaseTimer) {
        this.phase = phase;
        this.phaseTimer = phaseTimer;
    }

    public Phase getPhase() {
        return phase;
    }

    public float getPhaseTimer() {
        return phaseTimer;
    }

    public void setOffsetAngle(float nuevoOffset) {
        this.offsetAngle = nuevoOffset;
    }

    public void setActiveDuration(float activeDuration) {
        this.activeDuration = activeDuration;
    }
}

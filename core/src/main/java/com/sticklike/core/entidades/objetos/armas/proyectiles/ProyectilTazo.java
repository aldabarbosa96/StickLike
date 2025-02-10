package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueTazo;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

import java.util.HashSet;
import java.util.Set;

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

    // --- Campos para el efecto de "crecer" ---
    private static final float MIN_GROWTH_SCALE = 0.1f;
    private static final float MAX_GROWTH_SCALE = 0.9f;
    private static final float GROW_DURATION = 0.5f;  // Duración del efecto de crecimiento (y del fade-out)

    // --- fases del ciclo del tazo ---
    public enum Phase { GROWING, ACTIVE, COOLDOWN }
    private Phase phase = Phase.GROWING;
    private float phaseTimer = 0f;  // Temporizador para la fase actual

    // Duraciones para la fase ACTIVE y la fase COOLDOWN
    private float activeDuration = 8.5f;    // Tiempo que el tazo se muestra activo
    private static final float COOLDOWN_DURATION = 3.5f;  // Tiempo total de espera entre ataques

    private float growthTimer = 0f;
    private float powerFactor;

    public ProyectilTazo(Jugador jugador, AtaqueTazo ataqueTazo, float offsetAngle, float radio, GestorDeAudio gestorDeAudio) {
        if (textura == null) {
            textura = armaTazos;
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
        // Iniciamos con la escala mínima para el efecto de crecer
        sprite.setScale(MIN_GROWTH_SCALE);

    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        phaseTimer += delta;

        // Calcula la posición base relativa al jugador
        float jugadorCentroX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float jugadorCentroY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2 - 5f;

        // Calcula la posición del tazo en función del ángulo global + offset
        float currentAngle = ataqueTazo.getGlobalAngle() + offsetAngle;
        float radianes = (float) Math.toRadians(currentAngle);
        float offsetX = (float) (Math.cos(radianes) * radio);
        float offsetY = (float) (Math.sin(radianes) * radio);

        switch (phase) {
            case GROWING:
                // Efecto de crecer: interpolamos la escala de MIN_GROWTH_SCALE a MAX_GROWTH_SCALE
                growthTimer += delta;
                float progress = Math.min(growthTimer / GROW_DURATION, 1f);
                float currentScale = MIN_GROWTH_SCALE + progress * (MAX_GROWTH_SCALE - MIN_GROWTH_SCALE);
                sprite.setScale(currentScale);
                // Actualizamos la rotación
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
                    System.out.println("Tazo entrando en COOLDOWN después de: " + ataqueTazo.getDuracionActivaTazo() + " segundos.");
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
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo) {
            sprite.draw(batch);
        }
    }

    @Override
    public Rectangle getRectanguloColision() {
        // Solo se activa la colisión en la fase ACTIVE
        if (phase != Phase.ACTIVE) {
            return new Rectangle(0, 0, 0, 0);
        }
        return new Rectangle(sprite.getX() + sprite.getWidth() / 2 - radioColision / 2,
            sprite.getY() + sprite.getHeight() / 2 - radioColision / 2,
            radioColision, radioColision);
    }

    @Override
    public void dispose() {
        textura = null;
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
        // Se aplica daño únicamente en la fase ACTIVE
        if (phase == Phase.ACTIVE) {
            float baseDamage = (float)(DANYO_TAZOS + Math.random() * 3.5f); // Asegúrate de tener definida la constante DANYO_TAZO
            return baseDamage * powerFactor;
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
        gestorDeAudio.reproducirEfecto("tazo", 0.4f);
        enemigosImpactados.add(enemigo);
        sprite.setColor(0.8f, 0, 0.2f, 1);
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return enemigosImpactados.contains(enemigo);
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

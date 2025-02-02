package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.GestorConstantes.DANYO_PEDO;
import static com.sticklike.core.utilidades.GestorConstantes.DANYO_TAZOS;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class NubePedo implements Proyectiles {
    private Texture texture;
    private Sprite sprite;
    private Jugador jugador;
    private boolean proyectilActivo;
    private Set<Enemigo> enemigosImpactados = new HashSet<>();

    // --- Campos para la animación ---
    private enum Phase { GROWING, VIBRATING }
    private Phase phase = Phase.GROWING;
    private float phaseTimer = 0;

    private static final float GROW_DURATION = 2.5f;      // Duración en segundos de la fase de crecimiento
    private static final float VIBRATE_DURATION = 1f;     // Duración en segundos de la fase de vibración
    private static final float MIN_SCALE = 0.1f;          // Escala mínima al inicio del crecimiento
    private static final float MAX_SCALE = 1.3f;         // Escala máxima (tamaño completo)
    private static final float MIN_ALPHA = 0.1f;          // Opacidad mínima (más difuminada)
    private static final float MAX_ALPHA = 0.9f;          // Opacidad máxima cuando ya creció
    private static final float VIBRATE_RANGE = 5f;        // Rango de oscilación (en píxeles) durante la vibración
    private static final float KNOCKBACK_FORCE = 100f;    // Fuerza de knockback a aplicar
    private static final float ROTATION_SPEED = 1000f;      // Velocidad de rotación (grados por segundo)

    public NubePedo(Jugador jugador) {
        this.texture = armaNubePedo;
        this.sprite = new Sprite(texture);
        sprite.setSize(52.5f, 52.5f);
        sprite.setOriginCenter();
        this.jugador = jugador;
        this.proyectilActivo = true;

        sprite.setScale(MIN_SCALE);
        sprite.setColor(0.75f, 0.75f, 0.75f, MIN_ALPHA);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        phaseTimer += delta;

        // Posición base relativa al jugador (ajústala según tus necesidades)
        float jugadorCenterX = jugador.getSprite().getX() - jugador.getSprite().getWidth() / 2 - 15f;
        float jugadorCenterY = jugador.getSprite().getY() - jugador.getSprite().getHeight() / 2 + 5f;

        if (phase == Phase.GROWING) {
            // Hacemos que la nube rote sobre sí misma
            sprite.rotate(ROTATION_SPEED * delta);

            // Interpolación lineal entre MIN_SCALE y MAX_SCALE
            float progress = Math.min(phaseTimer / GROW_DURATION, 1f);
            float currentScale = MIN_SCALE + progress * (MAX_SCALE - MIN_SCALE);
            sprite.setScale(currentScale);
            // Interpolación de opacidad
            float currentAlpha = MIN_ALPHA + progress * (MAX_ALPHA - MIN_ALPHA);
            sprite.setColor(0.85f, 0.85f, 0.85f, currentAlpha);
            // Posición fija durante el crecimiento
            sprite.setPosition(jugadorCenterX, jugadorCenterY);

            // Cuando termina el crecimiento se pasa a la fase de vibración
            if (phaseTimer >= GROW_DURATION) {
                phase = Phase.VIBRATING;
                phaseTimer = 0;
            }
        } else if (phase == Phase.VIBRATING) {
            // Cada frame de vibración se limpia el conjunto para permitir que se aplique daño repetidamente
            enemigosImpactados.clear();

            // Efecto de vibración: se añade un offset aleatorio en X y Y
            float offsetX = ((float) Math.random() * 2 - 1) * VIBRATE_RANGE;
            float offsetY = ((float) Math.random() * 2 - 1) * VIBRATE_RANGE;
            sprite.setScale(MAX_SCALE);
            sprite.setColor(0.7f, 0.6f, 0.1f, MAX_ALPHA);
            sprite.setPosition(jugadorCenterX + offsetX, jugadorCenterY + offsetY);

            // Se mantienen aplicando daño y knockback en cada frame de vibración.
            if (phaseTimer >= VIBRATE_DURATION) {
                // Al finalizar la vibración se reinicia el ciclo
                phase = Phase.GROWING;
                phaseTimer = 0;
                sprite.setScale(MIN_SCALE);
                enemigosImpactados.clear();
            }
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo) {
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        texture = null;
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
        return new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
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
        float baseDamage;
        if (phase == Phase.VIBRATING) {
            baseDamage = (float) (DANYO_PEDO + Math.random() * 3.35f);
        } else {
            baseDamage = DANYO_PEDO;
        }
        if (baseDamage <= 0) {
            baseDamage = 1f;
        }
        return baseDamage;
    }

    @Override
    public float getKnockbackForce() {
        if (phase == Phase.VIBRATING) {
            return KNOCKBACK_FORCE;
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
}

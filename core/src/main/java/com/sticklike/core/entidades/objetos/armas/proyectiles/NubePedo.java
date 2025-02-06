package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.GestorDeAudio;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.GestorConstantes.DANYO_PEDO;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class NubePedo implements Proyectiles {
    private Texture texture;
    private Sprite sprite;
    private Jugador jugador;
    private boolean proyectilActivo;
    private Set<Enemigo> enemigosImpactados = new HashSet<>();

    // --- Estados para la animación ---
    private enum Phase { GROWING, VIBRATE1, PAUSE, VIBRATE2, COOLDOWN }
    private Phase phase = Phase.GROWING;
    private float phaseTimer = 0;

    private static final float GROW_DURATION = 0.3f; // Duración de la fase de crecimiento

    // Duraciones de las fases de vibración
    private static final float VIBRATE1_DURATION = 0.3f; // Primera vibración
    private static final float PAUSE_DURATION    = 0.25f; // Pausa entre vibraciones
    private static final float VIBRATE2_DURATION = 0.3f; // Segunda vibración

    // Duración de la fase de COOLDOWN (pausa entre ciclos)
    private static final float COOLDOWN_DURATION = 2.5f;

    private static final float MIN_SCALE    = 0.1f;  // Escala inicial
    private static final float MAX_SCALE    = 1.35f; // Escala máxima (tamaño completo)
    private static final float MIN_ALPHA    = 0.1f;  // Opacidad mínima
    private static final float MAX_ALPHA    = 0.75f; // Opacidad máxima
    private static final float VIBRATE_RANGE = 8f;    // Rango de oscilación (en píxeles) durante la vibración
    private static final float KNOCKBACK_FORCE = 200f; // Fuerza de knockback a aplicar
    private static final float ROTATION_SPEED  = 2500f;// Velocidad de rotación (grados por segundo)

    public NubePedo(Jugador jugador) {
        this.texture = armaNubePedo;
        this.sprite = new Sprite(texture);
        sprite.setSize(50f, 50f);
        sprite.setOriginCenter();
        this.jugador = jugador;
        this.proyectilActivo = true;

        // Iniciamos con la escala y opacidad mínimas para el efecto de crecimiento
        sprite.setScale(MIN_SCALE);
        sprite.setColor(0.75f, 0.75f, 0.75f, MIN_ALPHA);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        float progress = Math.min(phaseTimer / GROW_DURATION, 1f);
        float currentAlpha = MIN_ALPHA + progress * (MAX_ALPHA - MIN_ALPHA);
        phaseTimer += delta;

        // Posición base relativa al jugador (ajústala según necesites)
        float jugadorCenterX = jugador.getSprite().getX() - jugador.getSprite().getWidth() / 2 - 15f;
        float jugadorCenterY = jugador.getSprite().getY() - jugador.getSprite().getHeight() / 2 + 5f;

        switch (phase) {
            case GROWING:
                sprite.rotate(ROTATION_SPEED * delta);

                // Interpolación lineal entre MIN_SCALE y MAX_SCALE
                float currentScale = MIN_SCALE + progress * (MAX_SCALE - MIN_SCALE);
                sprite.setScale(currentScale);
                // Interpolación de opacidad
                sprite.setColor(1f, 0.82f, 0.5f, currentAlpha);
                // Posición fija en el centro del jugador
                sprite.setPosition(jugadorCenterX, jugadorCenterY);

                // Al finalizar el crecimiento, pasamos a la primera vibración
                if (phaseTimer >= GROW_DURATION) {
                    phase = Phase.VIBRATE1;
                    phaseTimer = 0;
                    // Limpieza una sola vez al inicio de la fase VIBRATE1
                    enemigosImpactados.clear();
                }
                break;

            case VIBRATE1:
                // Primera vibración: se aplica el efecto (audio, vibración y knockback)
                GestorDeAudio.getInstance().reproducirEfecto("pedo", 0.175f);
                // Limpia el conjunto solo al inicio de la fase
                if (phaseTimer < delta) {
                    enemigosImpactados.clear();
                }

                float offsetX1 = ((float) Math.random() * 2 - 1) * VIBRATE_RANGE;
                float offsetY1 = ((float) Math.random() * 2 - 1) * VIBRATE_RANGE;
                sprite.setScale(MAX_SCALE);
                sprite.setColor(1f, 0.65f, 0.1f, MAX_ALPHA);
                sprite.setPosition(jugadorCenterX + offsetX1, jugadorCenterY + offsetY1);

                if (phaseTimer >= VIBRATE1_DURATION) {
                    phase = Phase.PAUSE;
                    phaseTimer = 0;
                }
                break;

            case PAUSE:
                // Durante la pausa, el sprite se mantiene fijo en el centro
                sprite.setScale(MAX_SCALE);
                sprite.setColor(1f, 0.82f, 0.5f, currentAlpha);
                sprite.setPosition(jugadorCenterX, jugadorCenterY);

                if (phaseTimer >= PAUSE_DURATION) {
                    phase = Phase.VIBRATE2;
                    phaseTimer = 0;
                    // Limpieza una sola vez al inicio de la fase VIBRATE2
                    enemigosImpactados.clear();
                }
                break;

            case VIBRATE2:
                // Segunda vibración, similar a la primera
                GestorDeAudio.getInstance().reproducirEfecto("pedo", 0.175f);
                if (phaseTimer < delta) {
                    enemigosImpactados.clear();
                }

                float offsetX2 = ((float) Math.random() * 2 - 1) * VIBRATE_RANGE;
                float offsetY2 = ((float) Math.random() * 2 - 1) * VIBRATE_RANGE;
                sprite.setScale(MAX_SCALE);
                sprite.setColor(1f, 0.65f, 0.1f, MAX_ALPHA);
                sprite.setPosition(jugadorCenterX + offsetX2, jugadorCenterY + offsetY2);

                if (phaseTimer >= VIBRATE2_DURATION) {
                    // Una vez terminada la segunda vibración, iniciamos el COOLDOWN
                    phase = Phase.COOLDOWN;
                    phaseTimer = 0;
                    // Reiniciamos algunos parámetros para el siguiente ciclo
                    sprite.setScale(MIN_SCALE);
                    enemigosImpactados.clear();
                }
                break;

            case COOLDOWN:
                // Mover el sprite fuera del área de juego
                sprite.setPosition(-1000, -1000);
                sprite.setColor(0.75f, 0.75f, 0.75f, 0f);
                if (phaseTimer >= COOLDOWN_DURATION) {
                    // Reiniciamos el ciclo
                    phase = Phase.GROWING;
                    phaseTimer = 0;
                    sprite.setColor(0.75f, 0.75f, 0.75f, MIN_ALPHA);
                }
                break;

        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo) {
            // Durante COOLDOWN no se dibuja el sprite (está oculto)
            if (phase != Phase.COOLDOWN) {
                sprite.draw(batch);
            }
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
        if (phase == Phase.COOLDOWN) {
            // Retornamos un rectángulo vacío para desactivar la colisión
            return new Rectangle(0, 0, 0, 0);
        }
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
        if (phase == Phase.COOLDOWN) {
            return 0f;
        }
        float baseDamage;
        if (phase == Phase.VIBRATE1 || phase == Phase.VIBRATE2) {
            baseDamage = (float) (DANYO_PEDO + Math.random() * 3.35f);
        } else {
            baseDamage = DANYO_PEDO;
        }
        return baseDamage > 0 ? baseDamage : 1f;
    }


    @Override
    public float getKnockbackForce() {
        // Se aplica knockback solo durante las vibraciones
        if (phase == Phase.VIBRATE1 || phase == Phase.VIBRATE2) {
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

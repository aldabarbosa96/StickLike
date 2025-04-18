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
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.entidades.renderizado.RenderParticulasProyectil;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public final class ProyectilBoliBic implements Proyectiles {
    private static final float SPRITE_SIZE = 25f;
    private static final float SPRITE_ORIGIN = SPRITE_SIZE * 0.5f;
    private static final float OFFSET_ANGLE = 315f;
    private static final float DISTANCE_MAX = 333f;
    private static final float IMPACT_DURATION = 0.1f;
    private static final Color DEFAULT_PARTICLE_COLOR = new Color(0f, 0f, 0.75f, 1f);
    private static final float TIP_OFFSET_X = SPRITE_SIZE * 0.5f;
    private static final float TIP_OFFSET_Y = SPRITE_SIZE - 38f;
    private static final float PARTICLE_LEN_FACTOR = 17.5f;
    private static final float PARTICLE_WID_FACTOR = 5f;

    private static Texture TEXTURE;
    private final Sprite sprite;
    private final Vector2 center = new Vector2();
    private final Vector2 tipOffset = new Vector2();
    private final Vector2 tip = new Vector2();
    private final Rectangle collisionRect = new Rectangle();
    private final RenderParticulasProyectil particles;
    private final GestorDeAudio audio;
    private final Set<Enemigo> impactados = new HashSet<>(4);
    private final float powerFactor;

    private float direccionX, direccionY;
    private float velocidad;
    private float distanciaRecorrida;
    private boolean activo;
    private float impactoTimer;

    private boolean bounceEnabled = false;
    private int remainingBounces = 0;

    public ProyectilBoliBic(float x, float y, float dirX, float dirY, float velocidadProyectil) {
        if (TEXTURE == null) {
            TEXTURE = manager.get(ARMA_BOLIBIC, Texture.class);
        }

        this.sprite = new Sprite(TEXTURE);
        sprite.setSize(SPRITE_SIZE, SPRITE_SIZE);
        sprite.setOrigin(SPRITE_ORIGIN, SPRITE_ORIGIN);
        sprite.setPosition(x - SPRITE_ORIGIN, y - SPRITE_ORIGIN);
        sprite.flip(true, false);
        float angleDeg = MathUtils.atan2(dirY, dirX) * MathUtils.radiansToDegrees - OFFSET_ANGLE;
        sprite.setRotation(angleDeg);

        this.direccionX = dirX;
        this.direccionY = dirY;
        this.velocidad = velocidadProyectil;
        this.distanciaRecorrida = 0f;
        this.activo = true;
        this.impactoTimer = 0f;

        // Factor de potenciación de daño
        this.powerFactor = 1f + (Jugador.getPoderJugador() / 100f);
        this.audio = GestorDeAudio.getInstance();

        // Configuración de partículas
        float scaleFactor = Gdx.graphics.getWidth() / REAL_WIDTH;
        int maxLen = (int) (PARTICLE_LEN_FACTOR * scaleFactor);
        float partWid = PARTICLE_WID_FACTOR * scaleFactor;
        this.particles = new RenderParticulasProyectil(maxLen, partWid, DEFAULT_PARTICLE_COLOR);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        // Actualizar posición de partículas en la punta
        center.set(sprite.getX() + SPRITE_ORIGIN, sprite.getY() + SPRITE_ORIGIN);
        tipOffset.set(TIP_OFFSET_X, TIP_OFFSET_Y).rotateDeg(sprite.getRotation());
        tip.set(center).add(tipOffset);
        particles.update(tip);

        // Mover proyectil
        float despl = velocidad * delta;
        sprite.translate(direccionX * despl, direccionY * despl);
        distanciaRecorrida += despl;
        if (distanciaRecorrida >= DISTANCE_MAX) {
            desactivarProyectil();
        }

        // Actualizar colores tras impacto
        if (!impactados.isEmpty()) {
            impactoTimer += delta;
            if (impactoTimer >= IMPACT_DURATION) {
                impactoTimer = 0f;
                sprite.setColor(1f, 1f, 1f, 1f);
                particles.setColor(DEFAULT_PARTICLE_COLOR);
            }
        }

        // Actualizar rectángulo de colisión
        collisionRect.set(sprite.getX(), sprite.getY(), SPRITE_SIZE, SPRITE_SIZE);
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (activo) {
            particles.setAlphaMult(0.75f);
            particles.render(batch);
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        // Liberar recursos compartidos y de instancia
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
        float base = 5f + MathUtils.random(DANYO_BOLIBIC);
        return base * powerFactor;
    }

    @Override
    public float getKnockbackForce() {
        return EMPUJE_BASE_BOLI;
    }

    @Override
    public boolean isPersistente() {
        return true;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        if (impactados.add(enemigo)) {

            // feedback visual/sonoro que ya tenías
            sprite.setColor(Color.RED);
            particles.setColor(Color.RED);
            audio.reproducirEfecto("impactoBase", 1f);
            impactoTimer = 0f;

            /* ===  Rebote === */
            if (bounceEnabled && remainingBounces > 0) {
                remainingBounces--;

                // invertimos dirección
                direccionX = -direccionX;
                direccionY = -direccionY;

                // giramos el sprite para que apunte a la nueva dirección
                float ang = MathUtils.atan2(direccionY, direccionX) * MathUtils.radiansToDegrees - OFFSET_ANGLE;
                sprite.setRotation(ang);

                // vaciamos la lista para poder dañar a otros enemigos
                impactados.clear();
                return;
            }
        }
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return impactados.contains(enemigo);
    }

    @Override
    public boolean esCritico() {
        return MathUtils.random() < Jugador.getCritico();
    }
    public  void enableBounce(int bounces) {
        bounceEnabled      = true;
        remainingBounces   = bounces;
    }
}

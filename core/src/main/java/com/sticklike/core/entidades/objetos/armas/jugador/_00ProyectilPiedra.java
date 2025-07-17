package com.sticklike.core.entidades.objetos.armas.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.renderizado.particulas.ParticleManager;
import com.sticklike.core.entidades.renderizado.particulas.RenderParticulasProyectil;
import com.sticklike.core.entidades.renderizado.particulas.TrailRender;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public final class _00ProyectilPiedra implements Proyectiles {

    private static Texture TEXTURE;
    private static final float BASE_SPEED = PROJECTILE_PIEDRA_SPEED;
    private static final float PARTICLE_LEN = 18f;
    private static final float PARTICLE_WID = 3.5f;
    private static final Color PARTICLE_COLOR = Color.BLACK;
    private static final float MAX_DISTANCE = 1000f;
    private float distanciaRecorrida = 0f;
    private final Sprite sprite;
    private final RenderParticulasProyectil particles;
    private final Vector2 center = new Vector2();
    private final Rectangle collisionRect = new Rectangle();
    private final Jugador jugador;
    private final ParticleEffectPool.PooledEffect efecto;

    private final float speedMultiplier;
    private float dirX, dirY;
    private boolean activo = true;
    private boolean esCritico;
    private final GestorDeAudio audio = GestorDeAudio.getInstance();

    public _00ProyectilPiedra(float x, float y, float direccionX, float direccionY, float multiplicadorVelocidad, Jugador jugador) {

        // Carga única de textura
        if (TEXTURE == null) {
            TEXTURE = manager.get(ARMA_PIEDRA, Texture.class);
        }

        this.jugador = jugador;
        this.dirX = direccionX;
        this.dirY = direccionY;
        this.speedMultiplier = multiplicadorVelocidad;

        // Sprite
        sprite = new Sprite(TEXTURE);
        sprite.setSize(PIEDRA_SIZE, PIEDRA_SIZE);
        sprite.setPosition(x, y);

        // Partículas
        float scale = Gdx.graphics.getWidth() / REAL_WIDTH;
        particles = new RenderParticulasProyectil((int) (PARTICLE_LEN * scale), PARTICLE_WID * scale, PARTICLE_COLOR);
        particles.setAlphaMult(0.5f);   // mismo alpha que antes

        Vector2 initialCenter = new Vector2(x + PIEDRA_SIZE * 0.5f, y + PIEDRA_SIZE * 0.25f);
        efecto = ParticleManager.get().obtainEffect("piedra", initialCenter.x, initialCenter.y);


        // Hit-box inicial
        collisionRect.set(x, y, PIEDRA_SIZE, PIEDRA_SIZE);
    }

    /* -------------------------- Update -------------------------- */
    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;

        // Movimiento lineal
        float move = BASE_SPEED * speedMultiplier * delta;
        sprite.translate(dirX * move, dirY * move);
        distanciaRecorrida += move;

        if (distanciaRecorrida >= MAX_DISTANCE) {
            desactivarProyectil();
        }

        // Trail
        center.set(sprite.getX() + sprite.getWidth() * 0.5f, sprite.getY() + sprite.getHeight() * 0.5f);
        particles.update(center);
        TrailRender.get().submit(particles);
        efecto.setPosition(center.x, center.y);


        // Hit-box
        collisionRect.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    /* -------------------------- Render -------------------------- */
    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (!activo) return;
        sprite.draw(batch);
    }

    /* -------------------------- Limpieza ------------------------ */
    @Override
    public void dispose() {
        TEXTURE = null;
        particles.dispose();
        efecto.free();
    }

    /* -------------------------- Getters / lógica de daño -------- */
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
        particles.reset();
        efecto.allowCompletion();
        //ParticleManager.get().freeEffect(efecto);
        activo = false;
    }

    @Override
    public float getBaseDamage() {
        float base = MathUtils.random(21, 31);
        if (MathUtils.random() < jugador.getCritico()) {
            esCritico = true;
            return base * 1.5f;
        }
        esCritico = false;
        return base;
    }

    @Override
    public float getKnockbackForce() {
        return EMPUJE_BASE_PIEDRA;
    }

    @Override
    public boolean isPersistente() {
        return false;
    }

    /* -------------------------- Impacto ------------------------- */
    @Override
    public void registrarImpacto(Enemigo enemigo) {
        audio.reproducirEfecto("impactoBase", 0.33f);
        //desactivarProyectil();
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return false;
    }

    @Override
    public boolean esCritico() {
        return esCritico;
    }
}

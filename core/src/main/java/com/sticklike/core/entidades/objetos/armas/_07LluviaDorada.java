package com.sticklike.core.entidades.objetos.armas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.sticklike.core.entidades.renderizado.particulas.ParticleManager;
import com.sticklike.core.entidades.renderizado.particulas.RenderParticulasProyectil;
import com.sticklike.core.entidades.renderizado.particulas.TrailRender;   // ← nuevo helper global
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.ARMA_PIPI;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.ARMA_PIPI_GOTA;

/**
 * Proyectil «Lluvia de mocos».
 * Ahora el rastro se pinta a través de {@link TrailRender}.
 */
public final class _07LluviaDorada implements Proyectiles {

    public enum EstadoLluvia {FALLING, EXPLODED}

    private static final float GRAVITY = 150f;
    private static final float EXPLOSION_DURATION = 3.5f;
    private static final float FADE_START = 2.5f;
    private static final float FADE_DURATION = 0.5f;
    private static final int DROP_SIZE = 32;
    private static final int MAX_STAINS = 6;

    private static Texture texture;
    private static final IntMap<Texture> dropTextureCache = new IntMap<>();

    private final RenderParticulasProyectil renderParticulasProyectil;
    private final GestorDeAudio gestorDeAudio;
    private final Sprite sprite;
    private final Rectangle collisionRect = new Rectangle();
    private final Vector2 centroSprite = new Vector2();
    private static final Color COLOR_PIPI = new Color(0.75f, 0.65f, 0.05f, 1f);

    private float damage;
    private EstadoLluvia estadoLluvia;
    private float x, y;
    private float targetY;
    private float velocity;
    private boolean proyectilActivo;
    private float explosionTimer;
    private float width, height;
    private boolean stainsGenerated;
    private int numStains;
    private final float[] stainOffsetX = new float[MAX_STAINS];
    private final float[] stainOffsetY = new float[MAX_STAINS];
    private final float[] stainSizes = new float[MAX_STAINS];
    private final float[] stainScaleX = new float[MAX_STAINS];
    private final float[] stainScaleY = new float[MAX_STAINS];
    private boolean reboteActivado = false;
    private int bounceCount = 0;
    private int maxBounces = 0;
    private final float dampingFactor = 0.45f;
    private Texture dropTexture;
    private final Set<Enemigo> enemigosImpactados = new HashSet<>();
    private final ParticleEffectPool.PooledEffect efecto;

    public _07LluviaDorada(float x, float y, float fallSpeed, GestorDeAudio gestorDeAudio) {
        this.damage = MathUtils.random(8, 15);
        this.x = x;
        this.y = y;
        this.velocity = fallSpeed;
        this.targetY = y - (Gdx.graphics.getHeight() / 3f) + MathUtils.random(-100, 100);
        this.estadoLluvia = EstadoLluvia.FALLING;
        this.explosionTimer = 0f;
        float size = MathUtils.random(12.5f, 17.5f);
        this.width = size;
        this.height = size * 1.33f;
        this.proyectilActivo = true;
        this.stainsGenerated = false;
        if (texture == null) {
            texture = GestorDeAssets.manager.get(ARMA_PIPI, Texture.class);
        }
        this.sprite = new Sprite(texture);
        this.gestorDeAudio = gestorDeAudio;
        this.renderParticulasProyectil = new RenderParticulasProyectil(12, 2.5f, COLOR_PIPI);
        float centerX = x + width * 0.5f;
        float centerY = y + height * 0.5f;
        this.efecto = ParticleManager.get().obtainEffect("pipi", centerX, centerY, true);
        this.collisionRect.set(x, y, width, height);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        if (estadoLluvia == EstadoLluvia.FALLING) {
            velocity += GRAVITY * delta;
            y -= velocity * delta;

            centroSprite.set(x + width * 0.5f, y + height * 0.5f);
            renderParticulasProyectil.update(centroSprite);
            efecto.setPosition(centroSprite.x, centroSprite.y);
            TrailRender.get().submit(renderParticulasProyectil);

            collisionRect.set(x, y, width, height);

            if (y <= targetY) {
                y = targetY;
                if (reboteActivado && bounceCount < maxBounces) {
                    bounceCount++;
                    velocity = -velocity * dampingFactor;
                    Texture gotaTex = GestorDeAssets.manager.get(ARMA_PIPI_GOTA, Texture.class);
                    sprite.setTexture(gotaTex);

                    width = 10f;
                    height = 10f;

                    sprite.setSize(width, height);
                    sprite.setOriginCenter();
                } else {
                    explotar();
                }
            }

        } else {
            explosionTimer += delta;

            if (!stainsGenerated) {
                generarStains();
                stainsGenerated = true;
            }

            if (explosionTimer >= EXPLOSION_DURATION) {
                desactivarProyectil();
            }
        }
    }

    private void explotar() {
        if (estadoLluvia == EstadoLluvia.FALLING) {
            estadoLluvia = EstadoLluvia.EXPLODED;
            explosionTimer = 0f;
            gestorDeAudio.reproducirEfecto("moco", 0.33f);
            efecto.allowCompletion();
            dropTexture = getDropTexture();
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (!proyectilActivo) return;

        renderParticulasProyectil.setAlphaMult(0.8f);

        if (estadoLluvia == EstadoLluvia.FALLING) {
            sprite.setBounds(x, y, width, height);
            sprite.setOrigin(width * 0.5f, height * 0.5f);
            sprite.draw(batch);

        } else if (stainsGenerated) {
            float baseAlpha = 0.5f;
            float finalAlpha;
            if (explosionTimer < FADE_START) {
                finalAlpha = baseAlpha;
            } else if (explosionTimer < FADE_START + FADE_DURATION) {
                finalAlpha = baseAlpha * (1f - (explosionTimer - FADE_START) / FADE_DURATION);
            } else {
                finalAlpha = 0f;
            }

            float r = batch.getColor().r;
            float g = batch.getColor().g;
            float b = batch.getColor().b;
            float a = batch.getColor().a;
            batch.setColor(1f, 1f, 1f, finalAlpha);

            for (int i = 0; i < numStains; i++) {
                float w = stainSizes[i] * stainScaleX[i];
                float h = stainSizes[i] * stainScaleY[i];
                batch.draw(dropTexture, x + stainOffsetX[i], y + stainOffsetY[i], w, h);
            }

            batch.setColor(r, g, b, a);
        }
    }


    private static Texture getDropTexture() {
        int key = Color.rgba8888(_07LluviaDorada.COLOR_PIPI);
        Texture drop = dropTextureCache.get(key);
        if (drop == null) {
            Pixmap pixmap = new Pixmap(DROP_SIZE, DROP_SIZE, Pixmap.Format.RGBA8888);
            pixmap.setColor(_07LluviaDorada.COLOR_PIPI);
            pixmap.fillCircle(DROP_SIZE / 2, DROP_SIZE / 2, DROP_SIZE / 2);
            drop = new Texture(pixmap);
            pixmap.dispose();
            dropTextureCache.put(key, drop);
        }
        return drop;
    }

    private void generarStains() {
        numStains = MathUtils.random(3, MAX_STAINS);

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;

        for (int i = 0; i < numStains; i++) {
            stainOffsetX[i] = MathUtils.random(-7.5f, 7.5f);
            stainOffsetY[i] = MathUtils.random(-7.5f, 7.5f);
            stainSizes[i] = MathUtils.random(4f, 6f);
            stainScaleX[i] = MathUtils.random(1.3f, 1.7f);
            stainScaleY[i] = MathUtils.random(0.4f, 0.6f);

            float sx = x + stainOffsetX[i];
            float sy = y + stainOffsetY[i];
            float w = stainSizes[i] * stainScaleX[i];
            float h = stainSizes[i] * stainScaleY[i];

            if (sx < minX) minX = sx;
            if (sy < minY) minY = sy;
            if (sx + w > maxX) maxX = sx + w;
            if (sy + h > maxY) maxY = sy + h;
        }

        collisionRect.set(minX, minY, maxX - minX, maxY - minY);
    }

    /* ------------- Interface Proyectiles ------------------------- */

    @Override
    public Rectangle getRectanguloColision() {
        return collisionRect;
    }

    @Override
    public boolean isProyectilActivo() {
        return proyectilActivo;
    }

    @Override
    public void desactivarProyectil() {
        renderParticulasProyectil.reset();
        proyectilActivo = false;
    }

    @Override
    public float getBaseDamage() {
        return damage;
    }

    @Override
    public float getKnockbackForce() {
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
        return false;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public void setReboteActivado(boolean reboteActivado) {
        this.reboteActivado = reboteActivado;
    }

    public void setMaxBounces(int numBounces) {
        this.maxBounces = numBounces;
    }

    public void setDamage(float damage) {
        this.damage = damage;
        sprite.setColor(Color.RED);
        COLOR_PIPI.set(Color.RED);
    }

    @Override
    public void dispose() {
        renderParticulasProyectil.dispose();
        efecto.free();
        texture = null;
        COLOR_PIPI.set(0.75f, 0.65f, 0.05f, 1f);
    }

    public EstadoLluvia getEstadoMoco() {
        return estadoLluvia;
    }

    public float getExplosionTimer() {
        return explosionTimer;
    }
}

package com.sticklike.core.entidades.objetos.armas.proyectiles.proyectil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.sticklike.core.entidades.renderizado.RenderParticulasProyectil;
import com.sticklike.core.entidades.renderizado.TrailRender;   // ← nuevo helper global
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.ARMA_MOCO;

/**
 * Proyectil «Lluvia de mocos».
 * Ahora el rastro se pinta a través de {@link TrailRender}.
 */
public final class LluviaMocos implements Proyectiles {

    public enum EstadoMoco {FALLING, EXPLODED}

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
    private static final Color COLOR_VERDE_MOCO = new Color(0.15f, 0.75f, 0.15f, 1f);

    private float damage;
    private EstadoMoco estadoMoco;
    private float x, y;
    private float targetY;
    private float velocity;
    private float rotation;
    private float rotationSpeed;
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

    public LluviaMocos(float x, float y, float fallSpeed, GestorDeAudio gestorDeAudio) {
        this.damage = MathUtils.random(8, 15);
        this.x = x;
        this.y = y;
        this.velocity = fallSpeed;
        this.targetY = y - (Gdx.graphics.getHeight() / 3f) + MathUtils.random(-100, 100);
        this.estadoMoco = EstadoMoco.FALLING;
        this.explosionTimer = 0f;
        float size = MathUtils.random(12.5f, 17.5f);
        this.width = size;
        this.height = size;
        this.proyectilActivo = true;
        this.rotation = 0f;
        this.rotationSpeed = MathUtils.random(150, 350);
        this.stainsGenerated = false;
        if (texture == null) {
            texture = GestorDeAssets.manager.get(ARMA_MOCO, Texture.class);
        }
        this.sprite = new Sprite(texture);
        this.gestorDeAudio = gestorDeAudio;
        this.renderParticulasProyectil = new RenderParticulasProyectil(38, 4.5f, COLOR_VERDE_MOCO);
        this.collisionRect.set(x, y, width, height);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        if (estadoMoco == EstadoMoco.FALLING) {
            velocity += GRAVITY * delta;
            y -= velocity * delta;
            rotation += rotationSpeed * delta;

            centroSprite.set(x + width * 0.5f, y + height * 0.5f);
            renderParticulasProyectil.update(centroSprite);
            TrailRender.get().submit(renderParticulasProyectil);

            collisionRect.set(x, y, width, height);

            if (y <= targetY) {
                y = targetY;
                if (reboteActivado && bounceCount < maxBounces) {
                    bounceCount++;
                    velocity = -velocity * dampingFactor;
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
        if (estadoMoco == EstadoMoco.FALLING) {
            estadoMoco = EstadoMoco.EXPLODED;
            explosionTimer = 0f;
            gestorDeAudio.reproducirEfecto("moco", 0.33f);
            dropTexture = getDropTexture(COLOR_VERDE_MOCO);
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (!proyectilActivo) return;

        batch.begin();

        renderParticulasProyectil.setAlphaMult(0.5f);

        if (estadoMoco == EstadoMoco.FALLING) {
            sprite.setBounds(x, y, width, height);
            sprite.setOrigin(width * 0.5f, height * 0.5f);
            sprite.setRotation(rotation);
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
        batch.end();
    }


    private static Texture getDropTexture(Color color) {
        int key = Color.rgba8888(color);
        Texture drop = dropTextureCache.get(key);
        if (drop == null) {
            Pixmap pixmap = new Pixmap(DROP_SIZE, DROP_SIZE, Pixmap.Format.RGBA8888);
            pixmap.setColor(color);
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
        proyectilActivo = false;
        renderParticulasProyectil.reset();
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
        sprite.setColor(0.9f, 0.1f, 0.1f, 1f);
        COLOR_VERDE_MOCO.set(1, 0, 0, 1f);
    }

    @Override
    public void dispose() {
        renderParticulasProyectil.dispose();
    }

    public EstadoMoco getEstadoMoco() {
        return estadoMoco;
    }

    public float getExplosionTimer() {
        return explosionTimer;
    }
}

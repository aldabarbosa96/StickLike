package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.renderizado.RenderParticulasProyectil;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.ARMA_BOLIBIC;

public class BoliBic implements Proyectiles {

    public enum EstadoBoli {
        FALLING, EXPLODED
    }

    private EstadoBoli estadoBoli;
    private GestorDeAudio gestorDeAudio;
    private RenderParticulasProyectil renderParticulasProyectil;
    private float x, y;
    private float targetY;
    private float fallSpeed;
    private float explosionTimer;
    private static final float EXPLOSION_DURATION = 3.5f;
    private float width;
    private float height;
    private boolean proyectilActivo;
    private float rotation;
    private float rotationSpeed;
    private static Texture texture;
    private Sprite sprite;
    private static Texture dropTexture;
    private float[] stainOffsetX;
    private float[] stainOffsetY;
    private float[] stainSizes;
    private float[] stainScaleX;
    private float[] stainScaleY;
    private int numStains;
    private boolean stainsGenerated;
    private Set<Enemigo> enemigosImpactados = new HashSet<>();
    private Vector2 centroSprite;

    public BoliBic(float x, float y, float fallSpeed, GestorDeAudio gestorDeAudio) {
        this.x = x;
        this.y = y;
        this.fallSpeed = fallSpeed;
        this.targetY = y - (Gdx.graphics.getHeight() / 3f) + MathUtils.random(-100, 100);
        this.estadoBoli = EstadoBoli.FALLING;
        this.explosionTimer = 0;
        this.width = 25;
        this.height = 25;
        this.proyectilActivo = true;
        this.rotation = 0;
        this.rotationSpeed = MathUtils.random(150, 350);
        this.stainsGenerated = false;
        if (texture == null) {
            texture = GestorDeAssets.manager.get(ARMA_BOLIBIC, Texture.class);
        }
        this.sprite = new Sprite(texture);
        this.gestorDeAudio = gestorDeAudio;
        this.renderParticulasProyectil = new RenderParticulasProyectil(38, 5.25f, new Color(0.15f, 0.15f, 0.75f, 1));
        this.centroSprite = new Vector2();
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        if (estadoBoli == EstadoBoli.FALLING) {
            y -= fallSpeed * delta;
            rotation += rotationSpeed * delta;
            centroSprite.set(x + width / 2, y + height / 2);
            renderParticulasProyectil.update(centroSprite);

            if (y <= targetY) {
                y = targetY;
                explotar();
            }
        } else if (estadoBoli == EstadoBoli.EXPLODED) {
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

    public void explotar() {
        if (estadoBoli == EstadoBoli.FALLING) {
            estadoBoli = EstadoBoli.EXPLODED;
            explosionTimer = 0;
            gestorDeAudio.reproducirEfecto("boli", 0.33f);
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (!proyectilActivo) return;

        // Aquí configuramos el alpha que queremos para las partículas.
        renderParticulasProyectil.setAlphaMult(0.33f);

        if (estadoBoli == EstadoBoli.FALLING) {
            renderParticulasProyectil.render(batch);
            batch.draw(texture, x, y, width / 2, height / 2, width, height, 1, 1, rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        } else if (estadoBoli == EstadoBoli.EXPLODED && stainsGenerated) {
            if (dropTexture == null) {
                Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
                pixmap.setColor(0f, 0f, 0.5f, 1);
                pixmap.fillCircle(16, 16, 16);
                dropTexture = new Texture(pixmap);
                pixmap.dispose();
            }
            // Parámetros para el fade-out
            float baseAlpha = 0.5f;
            float fadeStart = 2.5f;
            float fadeDuration = 0.5f;
            float finalAlpha;
            if (explosionTimer < fadeStart) {
                finalAlpha = baseAlpha;
            } else if (explosionTimer < fadeStart + fadeDuration) {
                finalAlpha = baseAlpha * (1f - (explosionTimer - fadeStart) / fadeDuration);
            } else {
                finalAlpha = 0f;
            }

            // Guardamos el color actual del batch
            float r = batch.getColor().r;
            float g = batch.getColor().g;
            float b = batch.getColor().b;
            float a = batch.getColor().a;
            batch.setColor(1, 1, 1, finalAlpha);
            for (int i = 0; i < numStains; i++) {
                float stainX = x + stainOffsetX[i];
                float stainY = y + stainOffsetY[i];
                float dropWidth = stainSizes[i] * stainScaleX[i];
                float dropHeight = stainSizes[i] * stainScaleY[i];
                batch.draw(dropTexture, stainX, stainY, dropWidth, dropHeight);
            }
            batch.setColor(r, g, b, a);
        }
    }

    @Override
    public void dispose() {
        dropTexture.dispose();
    }

    private void generarStains() {
        numStains = MathUtils.random(3, 6);

        stainOffsetX = new float[numStains];
        stainOffsetY = new float[numStains];
        stainSizes = new float[numStains];

        stainScaleX = new float[numStains];
        stainScaleY = new float[numStains];
        for (int i = 0; i < numStains; i++) {
            stainOffsetX[i] = MathUtils.random(-10, 10);
            stainOffsetY[i] = MathUtils.random(-10, 10);
            stainSizes[i] = MathUtils.random(4, 8);
            // Para simular que se han "chafado" al explotar usamos factor de escala en X mayor a 1 (más ancho) y en Y menor que 1 (más bajo)
            stainScaleX[i] = MathUtils.random(1.3f, 1.7f);
            stainScaleY[i] = MathUtils.random(0.4f, 0.6f);
        }
    }

    @Override
    public Rectangle getRectanguloColision() {
        if (estadoBoli == EstadoBoli.FALLING) {
            return new Rectangle(x, y, width, height);
        } else if (estadoBoli == EstadoBoli.EXPLODED && stainsGenerated) {
            float firstStainX = x + stainOffsetX[0];
            float firstStainY = y + stainOffsetY[0];
            float dropWidth = stainSizes[0] * stainScaleX[0];
            float dropHeight = stainSizes[0] * stainScaleY[0];
            float minX = firstStainX;
            float minY = firstStainY;
            float maxX = firstStainX + dropWidth;
            float maxY = firstStainY + dropHeight;
            for (int i = 1; i < numStains; i++) {
                float stainX = x + stainOffsetX[i];
                float stainY = y + stainOffsetY[i];
                float w = stainSizes[i] * stainScaleX[i];
                float h = stainSizes[i] * stainScaleY[i];
                if (stainX < minX) minX = stainX;
                if (stainY < minY) minY = stainY;
                if (stainX + w > maxX) maxX = stainX + w;
                if (stainY + h > maxY) maxY = stainY + h;
            }
            return new Rectangle(minX, minY, maxX - minX, maxY - minY);
        }
        return new Rectangle(x, y, width, height);
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
        return MathUtils.random(8, 15);
    }

    @Override
    public float getKnockbackForce() {
        return 0;
    }

    @Override
    public boolean isPersistente() {
        return estadoBoli == EstadoBoli.EXPLODED;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        if (estadoBoli == EstadoBoli.FALLING) {
            Rectangle enemyRect = enemigo.getSprite().getBoundingRectangle();
            this.x = enemyRect.x + enemyRect.width / 2 - this.width / 2;
            this.y = enemyRect.y + enemyRect.height / 2 - this.height / 2;
            explotar();
        }
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
}

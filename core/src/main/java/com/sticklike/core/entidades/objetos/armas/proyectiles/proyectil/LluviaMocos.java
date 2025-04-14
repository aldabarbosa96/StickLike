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
import com.sticklike.core.entidades.renderizado.RenderParticulasProyectil;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import java.util.HashSet;
import java.util.Set;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.ARMA_MOCO;

public class LluviaMocos implements Proyectiles {

    public enum EstadoMoco {
        FALLING, EXPLODED
    }

    private EstadoMoco estadoMoco;
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
    private Set<Enemigo> enemigosImpactados = new HashSet<>();
    private Vector2 centroSprite;
    private float velocity;
    private float gravity = 90.8f; // Posible ajuste futuro
    private static final int MAX_STAINS = 6;
    private float[] stainOffsetX = new float[MAX_STAINS]; // preasignamos valores a los arrays de manchas para evitar cálculos innecesarios en tiempo de ejecución
    private float[] stainOffsetY = new float[MAX_STAINS];
    private float[] stainSizes = new float[MAX_STAINS];
    private float[] stainScaleX = new float[MAX_STAINS];
    private float[] stainScaleY = new float[MAX_STAINS];
    private int numStains;
    private boolean stainsGenerated;
    private Rectangle collisionRect;  // instancia interna de Rectangle para la colisión

    public LluviaMocos(float x, float y, float fallSpeed, GestorDeAudio gestorDeAudio) {
        this.x = x;
        this.y = y;
        this.velocity = fallSpeed;
        this.targetY = y - (Gdx.graphics.getHeight() / 3f) + MathUtils.random(-100, 100);
        this.estadoMoco = EstadoMoco.FALLING;
        this.explosionTimer = 0;
        this.width = 17.5f;
        this.height = 17.5f;
        this.proyectilActivo = true;
        this.rotation = 0;
        this.rotationSpeed = MathUtils.random(150, 350);
        this.stainsGenerated = false;
        if (texture == null) {
            texture = GestorDeAssets.manager.get(ARMA_MOCO, Texture.class);
        }
        this.sprite = new Sprite(texture);
        this.gestorDeAudio = gestorDeAudio;
        this.renderParticulasProyectil = new RenderParticulasProyectil(42, 4.5f, new Color(0.15f, 0.75f, 0.15f, 1));
        this.centroSprite = new Vector2();
        // Inicializamos el Rectangle de colisión con las coordenadas iniciales
        this.collisionRect = new Rectangle(x, y, width, height);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        if (estadoMoco == EstadoMoco.FALLING) {
            // Incrementamos la velocidad con la aceleración de la gravedad
            velocity += gravity * delta;
            y -= velocity * delta;
            rotation += rotationSpeed * delta;
            centroSprite.set(x + width / 2, y + height / 2);
            renderParticulasProyectil.update(centroSprite);
            // Actualizamos el rectángulo interno para el estado FALLING
            collisionRect.set(x, y, width, height);

            if (y <= targetY) {
                y = targetY;
                explotar();
            }
        } else if (estadoMoco == EstadoMoco.EXPLODED) {
            explosionTimer += delta;
            if (!stainsGenerated) {
                generarStains();
                stainsGenerated = true;
            }
            // Calculamos la hitbox a partir de las manchas y actualizamos el rectángulo
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
            collisionRect.set(minX, minY, maxX - minX, maxY - minY);

            if (explosionTimer >= EXPLOSION_DURATION) {
                desactivarProyectil();
            }
        }
    }

    public void explotar() {
        if (estadoMoco == EstadoMoco.FALLING) {
            estadoMoco = EstadoMoco.EXPLODED;
            explosionTimer = 0;
            gestorDeAudio.reproducirEfecto("moco", 0.33f);
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (!proyectilActivo) return;

        renderParticulasProyectil.setAlphaMult(0.5f);

        if (estadoMoco == EstadoMoco.FALLING) {
            renderParticulasProyectil.render(batch);
            batch.draw(texture, x, y, width / 2, height / 2, width, height, 1, 1, rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        } else if (estadoMoco == EstadoMoco.EXPLODED && stainsGenerated) {
            if (dropTexture == null) {
                Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
                pixmap.setColor(0f, 0.5f, 0f, 1);
                pixmap.fillCircle(16, 16, 16);
                dropTexture = new Texture(pixmap);
                pixmap.dispose();
            }
            // Efecto de fade-out
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
        if (dropTexture != null) {
            dropTexture.dispose();
            dropTexture = null;
        }
        if (renderParticulasProyectil != null){
            renderParticulasProyectil.dispose();
            renderParticulasProyectil = null;
        }

    }

    // Calculamos la configuración de las manchas solo una vez en la explosión
    private void generarStains() {
        numStains = MathUtils.random(3, MAX_STAINS);
        for (int i = 0; i < numStains; i++) {
            stainOffsetX[i] = MathUtils.random(-7.5f, 7.5f);
            stainOffsetY[i] = MathUtils.random(-7.5f, 7.5f);
            stainSizes[i] = MathUtils.random(4, 8);
            stainScaleX[i] = MathUtils.random(1.3f, 1.7f);
            stainScaleY[i] = MathUtils.random(0.4f, 0.6f);
        }
    }

    // En lugar de crear un nuevo objeto, devolvemos el Rectangle interno actualizado para reducir la carga en la ram
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
}

package com.sticklike.core.entidades.objetos.texto;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * TextoFlotante poolable para evitar GC.
 */
public class TextoFlotante implements Pool.Poolable {
    private static boolean alternarOffset = false;

    // Pool estático
    private static final Pool<TextoFlotante> POOL = new Pool<>() {
        @Override
        protected TextoFlotante newObject() {
            return new TextoFlotante();
        }
    };

    // Fields de instancia
    private String texto;
    private float x, y, duracion, tiempoTranscurrido;
    private BitmapFont fuente;
    private boolean esCritico;
    private float initialScaleX, initialScaleY, finalScaleX, finalScaleY;
    private Vector2[] positions;
    private int maxLengthPositions, posCount, nextIndex;
    private float extraOffsetX;

    // Constructor privado del pool
    private TextoFlotante() {
        // buffer fijo
        maxLengthPositions = 20;
        positions = new Vector2[maxLengthPositions];
        for (int i = 0; i < maxLengthPositions; i++) {
            positions[i] = new Vector2();
        }
    }

    /**
     * Obtiene y configura una instancia del pool
     */
    public static TextoFlotante obtain(String texto, float x, float y, float duracion, BitmapFont fuenteCompartida, boolean esCritico) {
        TextoFlotante t = POOL.obtain();
        // Llama al init con TEXTO_WIDTH y TEXTO_HEIGHT predefinidos
        t.init(texto, x, y, duracion, TEXTO_WIDTH, TEXTO_HEIGHT, esCritico, fuenteCompartida);
        return t;
    }

    /**
     * Devuelve al pool cuando termine
     */
    public void free() {
        POOL.free(this);
    }

    /**
     * Inicialización o reinicialización
     */
    private void init(String texto, float x, float y, float duracion, float fScaleX, float fScaleY, boolean esCritico, BitmapFont fuenteCompartida) {
        this.texto = texto;
        this.extraOffsetX = alternarOffset ? 4 : -4;
        alternarOffset = !alternarOffset;
        this.x = x + extraOffsetX;
        this.y = y;
        this.duracion = duracion;
        this.tiempoTranscurrido = 0;
        this.esCritico = esCritico;
        this.fuente = fuenteCompartida;
        this.finalScaleX = fScaleX;
        this.finalScaleY = fScaleY;
        this.initialScaleX = fScaleX * 0.25f;
        this.initialScaleY = fScaleY * 0.25f;
        fuente.getData().setScale(initialScaleX, initialScaleY);

        // buffer positions
        posCount = 0;
        nextIndex = 0;
        positions[nextIndex].set(this.x, this.y);
        nextIndex = (nextIndex + 1) % maxLengthPositions;
        posCount = 1;
    }

    @Override
    public void reset() {
        // poolable: no hace falta liberar referencias,
        // al init se sobreescriben todas las variables.
    }

    public boolean haDesaparecido() {
        return duracion <= 0;
    }

    public void actualizarTextoFlotante(float delta) {
        duracion -= delta;
        tiempoTranscurrido += delta;

        float t = Math.min(tiempoTranscurrido / 1f, 1f);
        float easedT = Interpolation.elasticOut.apply(t);
        float currentScaleX = initialScaleX + easedT * (finalScaleX - initialScaleX);
        float currentScaleY = initialScaleY + easedT * (finalScaleY - initialScaleY);
        fuente.getData().setScale(currentScaleX, currentScaleY);

        y += delta * DESPLAZAMIENTOY_TEXTO;
        positions[nextIndex].set(x, y);
        nextIndex = (nextIndex + 1) % maxLengthPositions;
        if (posCount < maxLengthPositions) posCount++;
    }

    public void renderizarTextoFlotante(SpriteBatch batch) {
        fuente.setColor(1f, esCritico ? 0f : 1f, esCritico ? 0f : 1f, 1f);
        fuente.draw(batch, texto, x, y);
    }
}

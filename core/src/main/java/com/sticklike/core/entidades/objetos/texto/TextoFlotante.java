package com.sticklike.core.entidades.objetos.texto;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Representa un texto flotante en el juego. Se muestra temporalmente con animación de aparición y desplazamiento vertical.
 */

public class TextoFlotante {
    private static boolean alternarOffset = false; // Variable para alternar desplazamiento

    private String texto;
    private float x, y;
    private float duracion;
    private BitmapFont fuente;

    private float tiempoTranscurrido;
    private final float animDuration = 0.2f;

    private final float initialScaleX;
    private final float initialScaleY;
    private final float finalScaleX;
    private final float finalScaleY;

    private float extraOffsetX, extraOffsetY;

    private boolean esCritico;

    public TextoFlotante(String texto, float x, float y, float duracion, float finalScaleX, float finalScaleY, boolean esCritico, BitmapFont fuenteCompartida) {
        this.texto = texto;
        // Alternar extraOffset para evitar superposición
        // Por ejemplo, 5 unidades a la izquierda o derecha
        this.extraOffsetX = alternarOffset ? 4 : -4;
        alternarOffset = !alternarOffset; // Cambia para el siguiente texto

        // Se suma el offset a la posición base para conseguir el efecto de apilado
        this.x = x + this.extraOffsetX;
        this.y = y; // Si se desea, también se puede alternar verticalmente
        this.duracion = duracion;
        this.tiempoTranscurrido = 0;
        this.esCritico = esCritico;
        this.fuente = fuenteCompartida;

        this.finalScaleX = finalScaleX;
        this.finalScaleY = finalScaleY;
        this.initialScaleX = finalScaleX * 0.2f;
        this.initialScaleY = finalScaleY * 0.2f;

        // Iniciamos con la escala mínima para el efecto "pop"
        fuente.getData().setScale(initialScaleX, initialScaleY);
    }

    // Constructor simplificado
    public TextoFlotante(String texto, float x, float y, float duracion, BitmapFont fuenteCompartida, boolean esCritico) {
        this(texto, x, y, duracion, TEXTO_WIDTH, TEXTO_HEIGHT, esCritico, fuenteCompartida);
    }

    public boolean haDesaparecido() {
        return duracion <= 0;
    }

    public void actualizarTextoFlotante(float delta) {
        duracion -= delta;
        tiempoTranscurrido += delta;
        float t = Math.min(tiempoTranscurrido / animDuration, 1f);
        float currentScaleX = initialScaleX + t * (finalScaleX - initialScaleX);
        float currentScaleY = initialScaleY + t * (finalScaleY - initialScaleY);
        fuente.getData().setScale(currentScaleX, currentScaleY);
        y += delta * DESPLAZAMIENTOY_TEXTO;
    }

    public void renderizarTextoFlotante(SpriteBatch batch) {
        dibujarReborde(batch);
        if (esCritico) {
            fuente.setColor(1f, 0.3f, 0.3f, 1f);
        } else {
            fuente.setColor(1f, 1f, 1f, 1f);
        }
        fuente.draw(batch, texto, x, y);
    }

    private void dibujarReborde(SpriteBatch batch) {
        float offset = 1;
        fuente.setColor(0f, 0f, 0f, 1f);
        fuente.draw(batch, texto, x - offset, y);
        fuente.draw(batch, texto, x + offset, y);
        fuente.draw(batch, texto, x, y - offset);
        fuente.draw(batch, texto, x, y + offset);
        fuente.draw(batch, texto, x - offset, y - offset);
        fuente.draw(batch, texto, x + offset, y - offset);
        fuente.draw(batch, texto, x - offset, y + offset);
        fuente.draw(batch, texto, x + offset, y + offset);
    }
}

package com.sticklike.core.entidades.objetos.texto;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Representa un texto flotante en el juego. Se muestra temporalmente con animación de aparición y desplazamiento vertical.
 */

public class TextoFlotante {
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

    public TextoFlotante(String texto, float x, float y, float duracion, float extraOffsetX, float extraOffsetY,
                         float finalScaleX, float finalScaleY, boolean esCritico, BitmapFont fuenteCompartida) {
        this.texto = texto;
        // Se suma el offset a la posición base para conseguir el efecto de apilado
        this.x = x + extraOffsetX;
        this.y = y + extraOffsetY;
        this.duracion = duracion;
        this.extraOffsetX = extraOffsetX;
        this.extraOffsetY = extraOffsetY;
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

    public TextoFlotante(String texto, float x, float y, float duracion, BitmapFont fuenteCompartida, boolean esCritico) {
        this(texto, x, y, duracion, 0, 0, TEXTO_WIDTH, TEXTO_HEIGHT, esCritico, fuenteCompartida);
    }


    public boolean haDesaparecido() {
        return duracion <= 0;
    }

    public void actualizarTextoFlotante(float delta) {
        // Reducir la duración del texto
        duracion -= delta;
        // Actualizar el tiempo transcurrido para la animación
        tiempoTranscurrido += delta;
        // Calcular el progreso de la animación (valor de 0 a 1)
        float t = Math.min(tiempoTranscurrido / animDuration, 1f);
        // Interpolación lineal de la escala en X e Y
        float currentScaleX = initialScaleX + t * (finalScaleX - initialScaleX);
        float currentScaleY = initialScaleY + t * (finalScaleY - initialScaleY);
        fuente.getData().setScale(currentScaleX, currentScaleY);
        // Actualizar la posición vertical (por ejemplo, subiendo)
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
        float offset = 1; // Desplazamiento para el borde
        fuente.setColor(0f, 0f, 0f, 1f);
        fuente.draw(batch, texto, x - offset, y); // Izquierda
        fuente.draw(batch, texto, x + offset, y); // Derecha
        fuente.draw(batch, texto, x, y - offset); // Abajo
        fuente.draw(batch, texto, x, y + offset); // Arriba
        fuente.draw(batch, texto, x - offset, y - offset); // Esquina inferior izquierda
        fuente.draw(batch, texto, x + offset, y - offset); // Esquina inferior derecha
        fuente.draw(batch, texto, x - offset, y + offset); // Esquina superior izquierda
        fuente.draw(batch, texto, x + offset, y + offset); // Esquina superior derecha
    }

}

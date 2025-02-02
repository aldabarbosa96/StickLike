package com.sticklike.core.entidades.objetos.texto;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class TextoFlotante {
    private String texto;
    private float x, y;          // Posición base (se ajusta con el offset extra)
    private float duracion;      // Tiempo (en segundos) que estará visible el texto
    private BitmapFont fuente;

    // Parámetros para la animación "pop" de escala
    private float elapsedAnimTime;      // Tiempo transcurrido desde que apareció el texto
    private final float animDuration = 0.2f;  // Duración (en segundos) de la animación de aparición
    private final float initialScale = 0.2f;
    private final float finalScale = 1f;

    // Offsets extra (para efecto de apilado y profundidad)
    private float extraOffsetX, extraOffsetY;

    /**
     * Constructor completo que recibe los offsets extra.
     *
     * @param texto         El texto a mostrar (por ejemplo, el daño)
     * @param x             Posición base en X (usualmente el centro del enemigo)
     * @param y             Posición base en Y (usualmente el centro del enemigo)
     * @param duracion      Duración de visualización
     * @param extraOffsetX  Desplazamiento extra en X para apilar textos
     * @param extraOffsetY  Desplazamiento extra en Y para apilar textos
     */
    public TextoFlotante(String texto, float x, float y, float duracion, float extraOffsetX, float extraOffsetY) {
        this.texto = texto;
        // Se suma el offset a la posición base para conseguir el efecto de apilado
        this.x = x + extraOffsetX;
        this.y = y + extraOffsetY;
        this.duracion = duracion;
        this.extraOffsetX = extraOffsetX;
        this.extraOffsetY = extraOffsetY;
        this.elapsedAnimTime = 0;

        fuente = new BitmapFont();
        // Iniciamos con la escala mínima para el efecto "pop"
        fuente.getData().setScale(initialScale);
    }

    /**
     * Constructor por defecto sin offsets extra (offset = 0).
     */
    public TextoFlotante(String texto, float x, float y, float duracion) {
        this(texto, x, y, duracion, 0, 0);
    }

    /**
     * Indica si el texto ha cumplido su duración y debe desaparecer.
     */
    public boolean haDesaparecido() {
        return duracion <= 0;
    }

    public void actualizarTextoFlotante(float delta) {
        // Reducir la duración del texto
        duracion -= delta;
        // Actualizar el tiempo transcurrido para la animación
        elapsedAnimTime += delta;
        // Calcular el progreso de la animación (valor de 0 a 1)
        float t = Math.min(elapsedAnimTime / animDuration, 1f);
        // Interpolación lineal de la escala
        float currentScale = initialScale + t * (finalScale - initialScale);
        fuente.getData().setScale(currentScale);
        y += delta * DESPLAZAMIENTOY_TEXTO;
    }


    public void renderizarTextoFlotante(SpriteBatch batch) {
        dibujarReborde(batch);
        fuente.setColor(1f, 1f, 1f, 1f);
        fuente.draw(batch, texto, x, y);
    }

    private void dibujarReborde(SpriteBatch batch) {
        float offset = 1; // Desplazamiento para el borde
        fuente.setColor(0.1f, 0.1f, 0.1f, 1f);
        fuente.draw(batch, texto, x - offset, y); // Izquierda
        fuente.draw(batch, texto, x + offset, y); // Derecha
        fuente.draw(batch, texto, x, y - offset); // Abajo
        fuente.draw(batch, texto, x, y + offset); // Arriba
        fuente.draw(batch, texto, x - offset, y - offset); // Esquina inferior izquierda
        fuente.draw(batch, texto, x + offset, y - offset); // Esquina inferior derecha
        fuente.draw(batch, texto, x - offset, y + offset); // Esquina superior izquierda
        fuente.draw(batch, texto, x + offset, y + offset); // Esquina superior derecha
    }

    public void dispose() {
        fuente.dispose();
    }
}

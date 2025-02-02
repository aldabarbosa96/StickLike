package com.sticklike.core.entidades.objetos.texto;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class TextoFlotante {
    private String texto;
    private float x, y;          // Posición base (se ajusta con el offset extra)
    private float duracion;      // Tiempo (en segundos) que estará visible el texto
    private BitmapFont fuente;

    private float tiempoTranscurrido;      // Tiempo transcurrido desde que apareció el texto
    private final float animDuration = 0.2f;

    // Escalas iniciales y finales para ancho y alto (individuales)
    private final float initialScaleX;
    private final float initialScaleY;
    private final float finalScaleX;
    private final float finalScaleY;

    // Offsets extra (para efecto de apilado y profundidad)
    private float extraOffsetX, extraOffsetY;

    // Nuevo campo para identificar si el golpe es crítico (y, por tanto, el texto se pintará de rojo)
    private boolean esCritico;

    /**
     * Constructor completo que recibe los offsets extra y la escala final.
     * @param texto         El texto a mostrar (por ejemplo, el daño)
     * @param x             Posición base en X (usualmente el centro del enemigo)
     * @param y             Posición base en Y (usualmente el centro del enemigo)
     * @param duracion      Duración de visualización
     * @param extraOffsetX  Desplazamiento extra en X para apilar textos
     * @param extraOffsetY  Desplazamiento extra en Y para apilar textos
     * @param finalScaleX   Escala final deseada en ancho
     * @param finalScaleY   Escala final deseada en alto
     * @param esCritico     Indica si este texto corresponde a un golpe crítico
     */
    public TextoFlotante(String texto, float x, float y, float duracion, float extraOffsetX, float extraOffsetY,
                         float finalScaleX, float finalScaleY, boolean esCritico) {
        this.texto = texto;
        // Se suma el offset a la posición base para conseguir el efecto de apilado
        this.x = x + extraOffsetX;
        this.y = y + extraOffsetY;
        this.duracion = duracion;
        this.extraOffsetX = extraOffsetX;
        this.extraOffsetY = extraOffsetY;
        this.tiempoTranscurrido = 0;
        this.esCritico = esCritico;

        // Asignamos la escala final (control individual)
        this.finalScaleX = finalScaleX;
        this.finalScaleY = finalScaleY;
        // Escala inicial: usamos un porcentaje (por ejemplo, 20%) de la escala final
        this.initialScaleX = finalScaleX * 0.2f;
        this.initialScaleY = finalScaleY * 0.2f;

        fuente = new BitmapFont();
        // Iniciamos con la escala mínima para el efecto "pop"
        fuente.getData().setScale(initialScaleX, initialScaleY);
    }

    /**
     * Constructor por defecto sin offsets extra y sin indicador crítico, usando TEXTO_WIDTH y TEXTO_HEIGHT.
     */
    public TextoFlotante(String texto, float x, float y, float duracion) {
        this(texto, x, y, duracion, 0, 0, TEXTO_WIDTH, TEXTO_HEIGHT, false);
    }

    /**
     * Sobrecarga del constructor para cuando se quiera indicar si es crítico.
     */
    public TextoFlotante(String texto, float x, float y, float duracion, boolean esCritico) {
        this(texto, x, y, duracion, 0, 0, TEXTO_WIDTH, TEXTO_HEIGHT, esCritico);
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
        // Si es crítico, pintar en rojo; de lo contrario, en blanco.
        if (esCritico) {
            fuente.setColor(1f, 0f, 0f, 1f);  // Rojo
        } else {
            fuente.setColor(1f, 1f, 1f, 1f);  // Blanco
        }
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

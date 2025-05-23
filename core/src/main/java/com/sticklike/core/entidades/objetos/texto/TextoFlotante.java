package com.sticklike.core.entidades.objetos.texto;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Representa un texto flotante en el juego. Se muestra temporalmente con animación de aparición y desplazamiento vertical.
 * Se optimiza el almacenamiento de posiciones usando un buffer circular para evitar asignaciones constantes.
 */
public class TextoFlotante {
    private static boolean alternarOffset = false; // Alterna el desplazamiento para evitar superposición

    private String texto;
    private float x, y;
    private float duracion;
    private BitmapFont fuente;
    private float tiempoTranscurrido;
    private final float animDuration = 1f; // duración de la animación "pop"

    private final float initialScaleX;
    private final float initialScaleY;
    private final float finalScaleX;
    private final float finalScaleY;

    private float extraOffsetX; // Offset horizontal para apilamiento

    private boolean esCritico;

    // Buffer circular para almacenar posiciones y evitar crear nuevos Vector2 en cada update
    private Vector2[] positions;
    private int maxLengthPositions;
    private int posCount;   // Número de elementos válidos en el buffer
    private int nextIndex;  // Índice donde se insertará la próxima posición

    /**
     * Constructor principal.
     *
     * @param texto            Texto a mostrar.
     * @param x                Posición horizontal base.
     * @param y                Posición vertical base.
     * @param duracion         Tiempo total a mostrar el texto.
     * @param finalScaleX      Escala final en X.
     * @param finalScaleY      Escala final en Y.
     * @param esCritico        Indica si el daño es crítico.
     * @param fuenteCompartida Fuente (BitmapFont) compartida.
     */
    public TextoFlotante(String texto, float x, float y, float duracion, float finalScaleX, float finalScaleY, boolean esCritico, BitmapFont fuenteCompartida) {
        this.texto = texto;
        // Alternamos extraOffset para evitar que se solapen
        this.extraOffsetX = alternarOffset ? 4 : -4;
        alternarOffset = !alternarOffset;
        // Sumamos el offset horizontal a la posición de inicio
        this.x = x + this.extraOffsetX;
        this.y = y;
        this.duracion = duracion;
        this.tiempoTranscurrido = 0;
        this.esCritico = esCritico;
        this.fuente = fuenteCompartida;

        this.finalScaleX = finalScaleX;
        this.finalScaleY = finalScaleY;
        this.initialScaleX = finalScaleX * 0.25f;
        this.initialScaleY = finalScaleY * 0.25f;

        // Iniciamos la fuente con la escala inicial
        fuente.getData().setScale(initialScaleX, initialScaleY);

        // Inicializamos el buffer circular para almacenar posiciones.
        maxLengthPositions = 20;
        positions = new Vector2[maxLengthPositions];
        for (int i = 0; i < maxLengthPositions; i++) {
            positions[i] = new Vector2();
        }
        posCount = 0;
        nextIndex = 0;
        // Guardamos la posición inicial en el buffer.
        positions[nextIndex].set(this.x, this.y);
        nextIndex = (nextIndex + 1) % maxLengthPositions;
        posCount = 1;
    }

    // Constructor simplificado que usa constantes de tamaño predefinido.
    public TextoFlotante(String texto, float x, float y, float duracion, BitmapFont fuenteCompartida, boolean esCritico) {
        this(texto, x, y, duracion, TEXTO_WIDTH, TEXTO_HEIGHT, esCritico, fuenteCompartida);
    }

    /**
     * Indica si el texto ha caducado.
     *
     * @return true si la duración ha terminado.
     */
    public boolean haDesaparecido() {
        return duracion <= 0;
    }

    /**
     * Actualiza el texto flotante: reduce la duración, actualiza la escala y desplaza verticalmente.
     * Además, almacena la posición actual en un buffer circular para potenciales efectos adicionales.
     *
     * @param delta Tiempo transcurrido desde el último frame.
     */
    public void actualizarTextoFlotante(float delta) {
        duracion -= delta;
        tiempoTranscurrido += delta;

        // t = progreso de la animación pop [0,1]
        float t = Math.min(tiempoTranscurrido / animDuration, 1f);

        // aplicamos elasticOut para el rebote
        float easedT = Interpolation.elasticOut.apply(t);

        // calculamos escala usando easedT en lugar de t
        float currentScaleX = initialScaleX + easedT * (finalScaleX - initialScaleX);
        float currentScaleY = initialScaleY + easedT * (finalScaleY - initialScaleY);
        fuente.getData().setScale(currentScaleX, currentScaleY);

        // desplazamiento vertical habitual
        y += delta * DESPLAZAMIENTOY_TEXTO;

        // actualizamos buffer de posiciones
        positions[nextIndex].set(x, y);
        nextIndex = (nextIndex + 1) % maxLengthPositions;
        if (posCount < maxLengthPositions) posCount++;
    }

    /**
     * Renderiza el texto y su reborde.
     *
     * @param batch SpriteBatch para dibujar.
     */
    public void renderizarTextoFlotante(SpriteBatch batch) {
        if (esCritico) {
            fuente.setColor(1f, 0f, 0f, 1f);
        } else {
            fuente.setColor(1f, 1f, 1f, 1f);
        }
        fuente.draw(batch, texto, x, y);
    }
}

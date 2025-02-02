package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Clase que gestiona las animaciones de parpadeo y fade-out de los enemigos
 */
public class AnimacionesEnemigos {
    private boolean enParpadeo = false;
    private float tiempoParpadeoRestante = TIEMPO_PARPADEO_RESTANTE;
    private boolean enFade = false;
    private float tiempoFadeRestante = TIEMPO_FADE_RESTANTE;
    private float tiempoTotalFade = TIEMPO_FADE_TOTAL;
    private float alphaActual = ALPHA_ACTUAL;

    // ================================
    // PARPADEO
    // ================================
    public void aplicarParpadeo1(Sprite sprite) {
        if (enParpadeo) {
            sprite.setColor(0.8f, 0.2f, 0.5f, alphaActual); // color morado/rojizo
        }
    }

    public void restaurarColor(Sprite sprite, Color originalColor) {
        if (enParpadeo || enFade) {
            sprite.setColor(originalColor);
        }
    }

    public void activarParpadeo(float duracion) {
        enParpadeo = true;
        tiempoParpadeoRestante = duracion;
    }

    public void actualizarParpadeo(float delta) {
        if (enParpadeo) {
            tiempoParpadeoRestante -= delta;
            if (tiempoParpadeoRestante <= 0) {
                enParpadeo = false;
            }
        }
    }
    public void aplicarParpadeoRojo(Sprite sprite) {
        if (enParpadeo) {
            // En este ejemplo se usa un color rojo intenso con el alpha actual
            sprite.setColor(0.9f, 0f, 0f, alphaActual);
        }
    }


    public boolean estaEnParpadeo() {
        return enParpadeo;
    }

    // ================================
    // FADE-OUT
    // ================================
    public void iniciarFadeMuerte(float duracion) {
        enFade = true;
        tiempoTotalFade = duracion;
        tiempoFadeRestante = duracion;
        alphaActual = ALPHA_ACTUAL;
    }

    // Actualiza el fade-out en cada frame
    public void actualizarFade(float delta) {
        if (enFade) {
            tiempoFadeRestante -= delta;
            if (tiempoFadeRestante <= 0) {
                enFade = false; // finaliza el fade-out
                alphaActual = 0f;
            } else {
                alphaActual = tiempoFadeRestante / tiempoTotalFade;
            }
        }
    }

    public boolean estaEnFade() {
        return enFade;
    }

    public float getAlphaActual() {
        return alphaActual;
    }
}

package com.sticklike.core.entidades.enemigos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimacionesEnemigos {
    private boolean enParpadeo = false;
    private float tiempoParpadeoRestante = 0;
    private boolean enFade = false;
    private float tiempoFadeRestante = 0f;
    private float tiempoTotalFade = 0.25f;
    private float alphaActual = 1f;

    // ================================
    // PARPADEO
    // ================================
    public void aplicarParpadeo1(Sprite sprite) {
        if (enParpadeo) {
            sprite.setColor(0.8f, 0.2f, 0.6f, alphaActual);
        }
    }
    public void aplicarParpadeo2(Sprite sprite) {
        if (enParpadeo) {
            sprite.setColor(1.0f, 0.5f, 0.0f, alphaActual);
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
        alphaActual = 1f;
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

package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Gestiona las animaciones visuales de los enemigos, como el efecto de parpadeo al recibir daño y el fade de muerte.
 */

public class AnimacionesBaseEnemigos {
    private boolean enParpadeo = false;
    private float tiempoParpadeoRestante = TIEMPO_PARPADEO_RESTANTE;
    private Texture texturaOriginal;
    private boolean enFade = false;
    private float tiempoFadeRestante = TIEMPO_FADE_RESTANTE;
    private float tiempoTotalFade = TIEMPO_FADE_TOTAL;
    private float alphaActual = ALPHA_ACTUAL;

    public void activarParpadeo(Sprite sprite, float duracion, Texture damageTexture) {
        if (!enParpadeo) {
            texturaOriginal = sprite.getTexture();
        }
        enParpadeo = true;
        tiempoParpadeoRestante = duracion;
        sprite.setTexture(damageTexture);
    }

    public void actualizarParpadeo(Sprite sprite, float delta) {
        if (enParpadeo) {
            tiempoParpadeoRestante -= delta;
            if (tiempoParpadeoRestante <= 0) {
                enParpadeo = false;
                sprite.setTexture(texturaOriginal);
            }
        }
    }

    public boolean estaEnParpadeo() {
        return enParpadeo;
    }

    public void iniciarFadeMuerte(float duracion) {
        enFade = true;
        tiempoTotalFade = duracion;
        tiempoFadeRestante = duracion;
        alphaActual = ALPHA_ACTUAL;
    }

    public void actualizarFade(float delta) {
        if (enFade) {
            tiempoFadeRestante -= delta;
            if (tiempoFadeRestante <= 0) {
                enFade = false;
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

    public void restaurarColor(Sprite sprite, Color originalColor) {
        if (enParpadeo || enFade) {
            sprite.setColor(originalColor);
        }
    }

    public void flipearEnemigo(Jugador jugador, Sprite sprite) {
        // Lógica para flipear el sprite del enemigo según la posición del jugador
        if (jugador != null) {
            boolean estaALaIzquierda = sprite.getX() + sprite.getWidth() / 2 > jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
            if (sprite.isFlipX() != estaALaIzquierda) {
                sprite.flip(true, false);
            }
        }
    }
}

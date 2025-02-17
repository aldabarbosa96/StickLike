package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class AnimacionesEnemigos {
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
}

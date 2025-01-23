package com.sticklike.core.entidades.enemigos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimacionesEnemigos {
    private boolean enParpadeo = false;
    private float tiempoParpadeoRestante = 0;

    public void aplicarParpadeo(Sprite sprite) {
        if (enParpadeo) {
            sprite.setColor(1, 0, 0, 0.8f); // Aplica color rojo semitransparente
        }
    }

    public void restaurarColor(Sprite sprite, Color originalColor) {
        if (enParpadeo) {
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
}

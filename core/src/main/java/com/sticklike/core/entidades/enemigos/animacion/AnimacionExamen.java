package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Levanta y baja los puños del enemigo Examen.
 */
public class AnimacionExamen {
    private final AnimacionesBaseEnemigos base;
    private final AnimacionDosFrames anim;

    public AnimacionExamen(AnimacionesBaseEnemigos base, Texture frame1, Texture frame2, float tiempoCambio) {
        this.base = base;
        this.anim = new AnimacionDosFrames(new TextureRegion(frame1), new TextureRegion(frame2), tiempoCambio, tiempoCambio, null, null);
    }

    public void actualizarAnimacion(float delta, Sprite sprite) {
        if (!base.estaEnParpadeo()) {
            anim.update(delta, sprite);
        }
    }
}

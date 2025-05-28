package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Alterna dos frames sólo si el enemigo es Crono (verde).
 */
public class AnimacionAlarma {
    private final AnimacionBaseEnemigos base;
    private final AnimacionBaseDosFrames anim;

    public AnimacionAlarma(AnimacionBaseEnemigos base, TextureRegion frameA, TextureRegion frameB, float tA, float tB) {
        this.base = base;
        this.anim = new AnimacionBaseDosFrames(frameA, frameB, tA, tB, null, null);
    }

    public void actualizar(float delta, Sprite sprite) {
        if (!base.estaEnParpadeo()) {
            anim.update(delta, sprite);
        }
    }
}


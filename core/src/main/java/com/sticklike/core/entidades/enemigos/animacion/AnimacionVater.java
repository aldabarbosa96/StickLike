package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Abre y cierra la tapa del váter.
 */
public class AnimacionVater {

    private final AnimacionBaseEnemigos base;
    private final AnimacionBaseDosFrames anim;

    public AnimacionVater(AnimacionBaseEnemigos base, Texture texturaTapaLevantada, Texture texturaTapaBajada, float tiempoAbierta, float tiempoCerrada) {

        this.base = base;
        this.anim = new AnimacionBaseDosFrames(new TextureRegion(texturaTapaBajada), new TextureRegion(texturaTapaLevantada), tiempoCerrada, tiempoAbierta, null, null);
    }

    public void actualizarAnimacion(float delta, Sprite sprite) {
        if (!base.estaEnParpadeo()) {
            anim.update(delta, sprite);
        }
    }
}

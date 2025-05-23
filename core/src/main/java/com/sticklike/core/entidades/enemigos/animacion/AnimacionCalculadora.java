package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimacionCalculadora {

    private final AnimacionesBaseEnemigos base;
    private final AnimacionDosFrames anim;

    public AnimacionCalculadora(AnimacionesBaseEnemigos base, Texture frameApagada, Texture frameEncendida, float duracionApagada, float duracionEncendida) {

        this.base = base;
        this.anim = new AnimacionDosFrames(new TextureRegion(frameApagada), new TextureRegion(frameEncendida), duracionApagada, duracionEncendida, null, null);
    }

    public void actualizarAnimacion(float delta, Sprite sprite) {
        if (!base.estaEnParpadeo()) {
            anim.update(delta, sprite);
        }
    }
}


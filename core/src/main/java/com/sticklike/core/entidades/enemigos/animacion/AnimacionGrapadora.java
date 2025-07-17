package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimacionGrapadora {

    private final AnimacionBaseEnemigos base;
    private final AnimacionBaseDosFrames anim;

    public AnimacionGrapadora(AnimacionBaseEnemigos base, Texture frameApagada, Texture frameEncendida, float duracionApagada, float duracionEncendida) {
        this.base = base;
        this.anim = new AnimacionBaseDosFrames(new TextureRegion(frameApagada), new TextureRegion(frameEncendida), duracionApagada, duracionEncendida, null, null);
    }

    public void actualizarAnimacion(float delta, Sprite sprite) {
        if (!base.estaEnParpadeo()){
            anim.update(delta,sprite);
        }
    }
}

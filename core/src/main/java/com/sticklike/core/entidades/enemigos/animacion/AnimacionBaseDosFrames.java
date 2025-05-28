package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Componente genérico para alternar entre dos frames con tiempos A/B independientes.
 * Opcionalmente disparamos callbacks al cambiar de frame.
 */
public class AnimacionBaseDosFrames {
    private final TextureRegion frameA;
    private final TextureRegion frameB;
    private final float duracionA;
    private final float duracionB;
    private float tiempo;
    private boolean usandoB;

    private final Runnable callbackA;
    private final Runnable callbackB;

    public AnimacionBaseDosFrames(TextureRegion frameA, TextureRegion frameB, float duracionA, float duracionB, Runnable callbackA, Runnable callbackB) {
        this.frameA = frameA;
        this.frameB = frameB;
        this.duracionA = duracionA;
        this.duracionB = duracionB;
        this.callbackA = callbackA != null ? callbackA : () -> {
        };
        this.callbackB = callbackB != null ? callbackB : () -> {
        };
    }

    public void update(float delta, Sprite sprite) {
        tiempo += delta;
        float limite = usandoB ? duracionB : duracionA;

        if (tiempo >= limite) {
            tiempo = 0;
            usandoB = !usandoB;
            if (usandoB) {
                sprite.setRegion(frameB);
                callbackB.run();
            } else {
                sprite.setRegion(frameA);
                callbackA.run();
            }
        }
    }

    public void reset(Sprite sprite) {
        usandoB = false;
        tiempo = 0;
        sprite.setRegion(frameA);
    }
}

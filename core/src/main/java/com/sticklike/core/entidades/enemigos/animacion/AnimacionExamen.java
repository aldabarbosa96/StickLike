package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sticklike.core.entidades.jugador.Jugador;

public class AnimacionExamen {
    private final AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private final Texture frame1;
    private final Texture frame2;
    private float tiempoAcumulado;
    private final float tiempoCambio;
    private boolean usandoFrame2;

    public AnimacionExamen(AnimacionesBaseEnemigos animacionesBaseEnemigos, Texture frame1, Texture frame2, float tiempoCambio) {
        this.animacionesBaseEnemigos = animacionesBaseEnemigos;
        this.frame1 = frame1;
        this.frame2 = frame2;
        this.tiempoCambio = tiempoCambio;
        this.tiempoAcumulado = 0;
        this.usandoFrame2 = false;
    }

    public void actualizarAnimacion(float delta, Jugador jugador, Sprite sprite) { // levanta o baja los puños
        if (!animacionesBaseEnemigos.estaEnParpadeo()) {
            tiempoAcumulado += delta;
            if (tiempoAcumulado >= tiempoCambio) {
                if (usandoFrame2) {
                    sprite.setRegion(frame1);
                    usandoFrame2 = false;
                } else {
                    sprite.setRegion(frame2);
                    usandoFrame2 = true;
                }
                tiempoAcumulado = 0;
            }
        }
        animacionesBaseEnemigos.flipearEnemigo(jugador,sprite);
    }
}

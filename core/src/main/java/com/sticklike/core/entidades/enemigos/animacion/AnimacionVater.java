package com.sticklike.core.entidades.enemigos.animacion;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoVater;

public class AnimacionVater {

    private final EnemigoVater enemigoVater;
    private final AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private final Texture texturaTapaLevantada;
    private final Texture texturaTapaBajada;

    // Enum para los estados de la tapa
    private enum EstadoTapa {
        ABIERTA,
        CERRADA
    }

    private EstadoTapa estadoActual;
    private float tiempoAnimacion;
    private final float tiempoAbierta;
    private final float tiempoCerrada;

    public AnimacionVater(EnemigoVater enemigoVater, AnimacionesBaseEnemigos animacionesBaseEnemigos, Sprite spriteTapaLevantada, Sprite spriteTapaBajada) {
        this.enemigoVater = enemigoVater;
        this.animacionesBaseEnemigos = animacionesBaseEnemigos;
        this.texturaTapaLevantada = spriteTapaLevantada.getTexture();
        this.texturaTapaBajada = spriteTapaBajada.getTexture();

        estadoActual = EstadoTapa.CERRADA;
        tiempoAnimacion = 0;
        tiempoAbierta = 0.15f;
        tiempoCerrada = 0.15f;
    }

    public void actualizarAnimacion(float delta, Sprite sprite) {
        if (animacionesBaseEnemigos.estaEnParpadeo()) return;

        tiempoAnimacion += delta;

        switch (estadoActual) {
            case CERRADA:
                if (tiempoAnimacion >= tiempoCerrada) {
                    estadoActual = EstadoTapa.ABIERTA;
                    tiempoAnimacion = 0;
                    sprite.setTexture(texturaTapaLevantada);
                }
                break;
            case ABIERTA:
                if (tiempoAnimacion >= tiempoAbierta) {
                    estadoActual = EstadoTapa.CERRADA;
                    tiempoAnimacion = 0;
                    sprite.setTexture(texturaTapaBajada);
                }
                break;
        }
    }
}

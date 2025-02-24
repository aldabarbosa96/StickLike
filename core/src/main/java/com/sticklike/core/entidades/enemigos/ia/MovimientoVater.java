package com.sticklike.core.entidades.enemigos.ia;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;
import  static com.sticklike.core.utilidades.GestorConstantes.*;

public class MovimientoVater extends MovimientoBaseEnemigos {
    private float velocidad;

    public MovimientoVater(boolean canKnockback) {
        super(canKnockback);
        this.velocidad = VEL_BASE_VATER;
    }

    @Override
    protected void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador) {
        float difX = jugador.getSprite().getX() - sprite.getX();
        float difY = jugador.getSprite().getY() - sprite.getY();

        float distancia = (float) Math.sqrt(difX * difX + difY * difY);
        if (distancia != 0) {
            difX /= distancia;
            difY /= distancia;
        }

        sprite.translate(difX * velocidad * delta, difY * velocidad * delta);
    }

    public float getVelocidad() {
        return velocidad;
    }
    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }
}

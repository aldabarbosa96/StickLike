package com.sticklike.core.entidades.enemigos.ia;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;

public class MovimientoLineal extends MovimientoBaseEnemigos {
    private float velocidad;

    public MovimientoLineal(boolean canKnockback, float velocidad) {
        super(canKnockback);
        this.velocidad = velocidad;
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

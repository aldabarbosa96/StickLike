package com.sticklike.core.entidades.enemigos.ia;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;

public class MovimientoAlarma extends MovimientoBaseEnemigos{
    public MovimientoAlarma(boolean canKnockback) {
        super(canKnockback);
    }

    @Override
    protected void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador) {

    }
}

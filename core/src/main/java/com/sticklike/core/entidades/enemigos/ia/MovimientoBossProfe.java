package com.sticklike.core.entidades.enemigos.ia;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.jugador.Jugador;

public class MovimientoBossProfe extends MovimientoBaseEnemigos {

    private final float velocidad;
    private float duracionActual;
    private float temporizador;
    private boolean enFaseChase;

    public MovimientoBossProfe(boolean puedeEmpujar) {
        super(puedeEmpujar);
        this.velocidad = 125;
        this.temporizador = 0f;
        this.enFaseChase = true;
        this.duracionActual = MathUtils.random(2,5);
    }

    @Override
    protected void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador) {
        temporizador += delta;
        if (temporizador >= duracionActual) {
            // Cambiamos de fase
            enFaseChase = !enFaseChase;
            temporizador = 0f;
            duracionActual = MathUtils.random(3, 6);
        }

        if (enFaseChase) {
            // Fase chase: mover hacia el jugador
            if (jugador == null) return;

            float bossCenterX = sprite.getX() + sprite.getWidth() / 2f;
            float bossCenterY = sprite.getY() + sprite.getHeight() / 2f;
            float playerCenterX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f;
            float playerCenterY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;

            float dx = playerCenterX - bossCenterX;
            float dy = playerCenterY - bossCenterY;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist != 0f) {
                float dirX = dx / dist;
                float dirY = dy / dist;
                sprite.translate(dirX * velocidad * delta, dirY * velocidad * delta);
            }
        }
    }

    public boolean isEnFaseChase() {
        return enFaseChase;
    }
}

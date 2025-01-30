package com.sticklike.core.entidades.enemigos.movimiento;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class MovimientoCuloEnemigos extends MovimientoBaseEnemigos {
    private float velocidadEnemigo;
    private float tempMovimiento;
    private float duracionPausa;
    private float duracionMovimiento;
    private boolean seMueve;

    public MovimientoCuloEnemigos(float velocidadEnemigo, boolean puedeEmpujar) {
        super(puedeEmpujar);
        this.velocidadEnemigo = velocidadEnemigo;
        this.tempMovimiento = 0;
        this.seMueve = true;
        calcularDuracionPausa();
        calcularDuracionMovimiento();
    }

    @Override
    protected void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador) {
        tempMovimiento += delta;

        if (seMueve) {
            if (tempMovimiento >= duracionMovimiento) {
                seMueve = false;
                tempMovimiento = 0;
                calcularDuracionPausa();
            } else {
                moverHaciaJugador(delta, sprite, jugador);
            }
        } else {
            if (tempMovimiento >= duracionPausa) {
                seMueve = true;
                tempMovimiento = 0;
                calcularDuracionMovimiento();
            }
        }
    }

    private void moverHaciaJugador(float delta, Sprite sprite, Jugador jugador) {
        float enemyPosX = sprite.getX();
        float enemyPosY = sprite.getY();

        float playerPosX = jugador.getSprite().getX();
        float playerPosY = jugador.getSprite().getY();

        float difX = playerPosX - enemyPosX;
        float difY = playerPosY - enemyPosY;

        // Añadimos un desplazamiento aleatorio
        float randomOffsetX = (float) Math.random() * MAX_OFFSET - AJUSTE_OFFSET_X;
        float randomOffsetY = (float) Math.random() * MAX_OFFSET - AJUSTE_OFFSET_Y;
        difX += randomOffsetX;
        difY += randomOffsetY;

        float distance = (float) Math.sqrt(difX * difX + difY * difY);
        if (distance != 0) {
            difX /= distance;
            difY /= distance;
        }

        float movementX = difX * velocidadEnemigo * delta;
        float movementY = difY * velocidadEnemigo * delta;

        sprite.translate(movementX, movementY);
    }

    private void calcularDuracionPausa() {
        duracionPausa = ENEMY_MIN_PAUSE + (float) Math.random() * (ENEMY_MAX_PAUSE - ENEMY_MIN_PAUSE);
    }

    private void calcularDuracionMovimiento() {
        duracionMovimiento = ENEMY_MIN_MOVE_DURATION + (float) Math.random() * (ENEMY_MAX_MOVE_DURATION - ENEMY_MIN_MOVE_DURATION);
    }

    public float getVelocidadEnemigo() {
        return velocidadEnemigo;
    }

    public void setVelocidadEnemigo(float velocidadEnemigo) {
        this.velocidadEnemigo = velocidadEnemigo;
    }
}

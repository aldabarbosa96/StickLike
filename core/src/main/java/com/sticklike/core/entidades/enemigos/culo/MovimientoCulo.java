package com.sticklike.core.entidades.enemigos.culo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.utilidades.GestorConstantes;

public class MovimientoCulo {
    private final float velocidadEnemigo;
    private float tempMovimiento;
    private float duracionPausa;
    private float duracionMovimiento;
    private boolean seMueve;

    public MovimientoCulo(float velocidadEnemigo) {
        this.velocidadEnemigo = velocidadEnemigo;
        this.tempMovimiento = 0;
        this.seMueve = true;
        calcularDuracionPausa();
        calcularDuracionMovimiento();
    }

    public void actualizarMovimiento(float delta, Sprite sprite, Jugador jugador) {
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

        // Añadimos un desplazamiento aleatorio para simular movimiento diagonal. todo --> valorar si se mantiene en un futuro (genera un efecto de vibración extraño)
       /* float randomOffsetX = (float) Math.random() * 100 - 65;
        float randomOffsetY = (float) Math.random() * 100 - 50;
        difX += randomOffsetX;
        difY += randomOffsetY;*/

        float distance = (float) Math.sqrt(difX * difX + difY * difY);

        if (distance != 0) {
            difX /= distance;
            difY /= distance;
        }

        float movementX = difX * velocidadEnemigo * delta;
        float movementY = difY * velocidadEnemigo * delta;

        // Cálculamos la posición vertical final del enemigo de manera aleatoria basándonos en el sprite del jugador
        sprite.translate(movementX,movementY);
    }

    private void calcularDuracionPausa() {
        this.duracionPausa = GestorConstantes.ENEMY_MIN_PAUSE +
            (float) Math.random() * (GestorConstantes.ENEMY_MAX_PAUSE - GestorConstantes.ENEMY_MIN_PAUSE);
    }

    private void calcularDuracionMovimiento() {
        this.duracionMovimiento = GestorConstantes.ENEMY_MIN_MOVE_DURATION +
            (float) Math.random() * (GestorConstantes.ENEMY_MAX_MOVE_DURATION - GestorConstantes.ENEMY_MIN_MOVE_DURATION);
    }
}

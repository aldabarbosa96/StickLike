package com.sticklike.core.entidades.enemigos.polla;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.enemigos.MovimientoBase;
import com.sticklike.core.entidades.jugador.Jugador;

public class MovimientoPolla extends MovimientoBase {
    private float velocidadEnemigo;
    private float tiempo; // Tiempo acumulado para el zigzag
    private float amplitudZigzag; // Amplitud del zigzag
    private float frecuenciaZigzag; // Frecuencia del zigzag

    public MovimientoPolla(float velocidadEnemigo, float amplitudZigzag, float frecuenciaZigzag, boolean puedeEmpujar) {
        super(puedeEmpujar);
        this.velocidadEnemigo = velocidadEnemigo;
        this.tiempo = 0;
        this.amplitudZigzag = amplitudZigzag;
        this.frecuenciaZigzag = frecuenciaZigzag;
    }

    @Override
    protected void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador) {
        tiempo += delta;

        // Posición actual del enemigo
        float enemyPosX = sprite.getX();
        float enemyPosY = sprite.getY();

        // Posición del jugador
        float playerPosX = jugador.getSprite().getX();
        float playerPosY = jugador.getSprite().getY();

        // Diferencia en las posiciones
        float difX = playerPosX - enemyPosX;
        float difY = playerPosY - enemyPosY;

        // Normaliza el vector hacia el jugador
        float distancia = (float) Math.sqrt(difX * difX + difY * difY);
        if (distancia != 0) {
            difX /= distancia;
            difY /= distancia;
        }

        // Agregar zigzag vertical al movimiento
        float zigzagOffset = (float) Math.sin(tiempo * frecuenciaZigzag) * amplitudZigzag;

        // Movimiento final (con zigzag aplicado al eje Y)
        float movimientoX = difX * velocidadEnemigo * delta;
        float movimientoY = (difY * velocidadEnemigo * delta) + zigzagOffset;

        // Mover el sprite
        sprite.translate(movimientoX, movimientoY);
    }

    public float getVelocidadEnemigo() {
        return velocidadEnemigo;
    }

    public void setVelocidadEnemigo(float velocidadEnemigo) {
        this.velocidadEnemigo = velocidadEnemigo;
    }
}

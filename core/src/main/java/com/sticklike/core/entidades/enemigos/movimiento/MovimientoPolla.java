package com.sticklike.core.entidades.enemigos.movimiento;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;

public class MovimientoPolla extends MovimientoBaseEnemigos {
    private float velocidadEnemigo;
    private float tiempo; // Tiempo acumulado para el zigzag
    private float amplitudZigzag; // Amplitud del zigzag
    private float frecuenciaZigzag; // Frecuencia del zigzag
    private float currentOffset;

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

        // Posición actual
        float enemyPosX = sprite.getX();
        float enemyPosY = sprite.getY();

        // Posición objetivo (jugador)
        float playerPosX = jugador.getSprite().getX();
        float playerPosY = jugador.getSprite().getY();

        // Vector hacia el jugador (normalizado)
        float difX = playerPosX - enemyPosX;
        float difY = playerPosY - enemyPosY;
        float distancia = (float) Math.sqrt(difX * difX + difY * difY);
        if (distancia != 0) {
            difX /= distancia;
            difY /= distancia;
        }

        currentOffset = (float) Math.sin(tiempo * frecuenciaZigzag) * amplitudZigzag;

        // Movimiento total
        float movimientoX = difX * velocidadEnemigo * delta;
        float movimientoY = (difY * velocidadEnemigo * delta) + currentOffset;

        sprite.translate(movimientoX, movimientoY);
    }

    public float getCurrentOffset() {
        return currentOffset;
    }

    public float getVelocidadEnemigo() {
        return velocidadEnemigo;
    }

    public void setVelocidadEnemigo(float velocidadEnemigo) {
        this.velocidadEnemigo = velocidadEnemigo;
    }

    public float getAmplitudZigzag() {
        return amplitudZigzag;
    }
}

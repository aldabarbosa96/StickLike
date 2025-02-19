package com.sticklike.core.entidades.enemigos.ia;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;
import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Movimiento del enemigo Culo; alterna entre pausas y desplazamientos hacia el jugador con oscilación rotacional.
 */

public class MovimientoCulo extends MovimientoBaseEnemigos {
    private float velocidadEnemigo;
    private float tempMovimiento;
    private float duracionPausa;
    private float duracionMovimiento;
    private boolean seMueve;
    private float currentRotationAngle = 0f;
    private int rotationDirection = 1;
    private final float rotationSpeed = 250;
    // Límites asimétricos: mayor inclinación hacia los grados positivos (derecha) que hacia los negativos (izquierda) debido al dibujo del sprite
    private final float rotationMaxPositive = 25f;
    private final float rotationMaxNegative = -5f;

    public MovimientoCulo(float velocidadEnemigo, boolean puedeEmpujar) {
        super(puedeEmpujar);
        this.velocidadEnemigo = velocidadEnemigo;
        this.tempMovimiento = 0;
        this.seMueve = true;
        calcularDuracionPausa();
        calcularDuracionMovimiento();
    }

    @Override
    protected void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador) {
        // Aseguramos que la rotación se haga sobre el centro del sprite
        sprite.setOriginCenter();

        tempMovimiento += delta;

        if (seMueve) {
            if (tempMovimiento >= duracionMovimiento) {
                seMueve = false;
                tempMovimiento = 0;
                calcularDuracionPausa();
                currentRotationAngle = 0;
                sprite.setRotation(0);
            } else {
                moverHaciaJugador(delta, sprite, jugador);
                actualizarOscilacionRotacion(delta, sprite, jugador);
            }
        } else {
            if (tempMovimiento >= duracionPausa) {
                seMueve = true;
                tempMovimiento = 0;
                calcularDuracionMovimiento();
            }
            currentRotationAngle = 0;
            sprite.setRotation(0);
        }
    }

    private void actualizarOscilacionRotacion(float delta, Sprite sprite, Jugador jugador) {
        // Actualizamos el ángulo acumulado según la velocidad y la dirección
        currentRotationAngle += rotationDirection * rotationSpeed * delta;

        // Si se excede el límite positivo, se fija y se invierte la dirección
        if (currentRotationAngle > rotationMaxPositive) {
            currentRotationAngle = rotationMaxPositive;
            rotationDirection = -1;
        }
        // Si se excede el límite negativo, se fija y se invierte la dirección
        else if (currentRotationAngle < rotationMaxNegative) {
            currentRotationAngle = rotationMaxNegative;
            rotationDirection = 1;
        }

        // Determinamos el ángulo efectivo en función de la posición del enemigo respecto al jugador
        // Si el enemigo está a la izquierda del jugador, invertimos el efecto para que la inclinación "del paso" se mantenga coherente
        float effectiveRotation = currentRotationAngle;
        if (sprite.getX() < jugador.getSprite().getX()) {
            effectiveRotation = -currentRotationAngle;
        }

        sprite.setRotation(effectiveRotation);
    }

    private void moverHaciaJugador(float delta, Sprite sprite, Jugador jugador) {
        float enemyPosX = sprite.getX();
        float enemyPosY = sprite.getY();

        float playerPosX = jugador.getSprite().getX();
        float playerPosY = jugador.getSprite().getY();

        float difX = playerPosX - enemyPosX;
        float difY = playerPosY - enemyPosY;

        // Añadimos un desplazamiento aleatorio para variar el movimiento
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

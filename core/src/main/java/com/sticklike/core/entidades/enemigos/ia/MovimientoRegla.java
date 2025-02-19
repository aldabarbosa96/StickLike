package com.sticklike.core.entidades.enemigos.ia;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;
import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Movimiento del enemigo Regla; rota constantemente y se lanza en línea recta hacia el jugador tras una fase de carga.
 */

public class MovimientoRegla extends MovimientoBaseEnemigos {
    private final float velocidadEnFaseDisparo;
    private final float velocidadRotacion;
    private float tiempoCarga;
    private boolean enFaseCarga;
    private boolean enFaseDisparo;

    private float direccionX;
    private float direccionY;
    private boolean direccionCalculada;
    private OrthographicCamera camara;

    public MovimientoRegla(float velocidadEnFaseDisparo, float velocidadRotacion, OrthographicCamera camara, boolean canKnockback) {
        super(canKnockback);
        this.velocidadEnFaseDisparo = velocidadEnFaseDisparo;
        this.velocidadRotacion = velocidadRotacion;
        this.camara = camara;

        this.tiempoCarga = TIEMPO_CARGA_REGLA;
        this.enFaseCarga = true;
        this.enFaseDisparo = false;
        this.direccionCalculada = false;
    }

    @Override
    protected void actualizarMovimientoEspecifico(float delta, Sprite sprite, Jugador jugador) {
        // Rotación siempre activa
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        sprite.rotate(velocidadRotacion * delta);

        if (enFaseCarga) {
            tiempoCarga -= delta;
            if (tiempoCarga <= 0) {
                enFaseCarga = false;
                enFaseDisparo = true;
                direccionCalculada = false;
            }
        } else if (enFaseDisparo) {
            dispararHaciaJugador(delta, sprite, jugador);

            if (fueraDeLimites(sprite)) {
                reiniciar(sprite);
            }
        }
    }

    private void dispararHaciaJugador(float delta, Sprite sprite, Jugador jugador) {
        if (!direccionCalculada) {
            calcularDireccion(sprite, jugador);
        }

        float movementX = direccionX * velocidadEnFaseDisparo * delta;
        float movementY = direccionY * velocidadEnFaseDisparo * delta;

        sprite.translate(movementX, movementY);
    }

    private void calcularDireccion(Sprite sprite, Jugador jugador) {
        float enemyPosX = sprite.getX();
        float enemyPosY = sprite.getY();

        float playerPosX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float playerPosY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2;

        float difX = playerPosX - enemyPosX;
        float difY = playerPosY - enemyPosY;
        float distance = (float) Math.sqrt(difX * difX + difY * difY);

        if (distance != 0) {
            direccionX = difX / distance;
            direccionY = difY / distance;
        }

        direccionCalculada = true;
    }

    private boolean fueraDeLimites(Sprite sprite) {
        float x = sprite.getX();
        float y = sprite.getY();

        float leftLimit = camara.position.x - camara.viewportWidth / 2;
        float rightLimit = camara.position.x + camara.viewportWidth / 2;
        float bottomLimit = camara.position.y - camara.viewportHeight / 2;
        float topLimit = camara.position.y + camara.viewportHeight / 2;

        return x < leftLimit - sprite.getWidth() || x > rightLimit + sprite.getWidth() ||
            y < bottomLimit - sprite.getHeight() || y > topLimit + sprite.getHeight();
    }

    private void reiniciar(Sprite sprite) {
        enFaseCarga = true;
        enFaseDisparo = false;
        tiempoCarga = TIEMPO_CARGA_REGLA;
        direccionCalculada = false;
    }
}

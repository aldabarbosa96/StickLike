package com.sticklike.core.entidades.enemigos.regla;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.entidades.jugador.Jugador;

public class MovimientoRegla {
    private final float velocidadEnFaseDisparo;
    private final float velocidadRotacion;
    private float tiempoCarga;
    private boolean enFaseCarga;
    private boolean enFaseDisparo;

    // Variables para la trayectoria en línea recta
    private float direccionX;
    private float direccionY;
    private boolean direccionCalculada;

    public MovimientoRegla(float velocidadEnFaseDisparo, float velocidadRotacion) {
        this.velocidadEnFaseDisparo = velocidadEnFaseDisparo;
        this.velocidadRotacion = velocidadRotacion;
        this.tiempoCarga = 0.3f; // Tiempo que permanece girando en la fase de carga
        this.enFaseCarga = true;
        this.enFaseDisparo = false;
        this.direccionCalculada = false;
    }

    public void actualizarMovimiento(float delta, Sprite sprite, Jugador jugador, OrthographicCamera camara) {
        // Rotación siempre activa
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        sprite.rotate(velocidadRotacion * delta);

        if (enFaseCarga) {
            // Fase de carga: solo rota y espera
            tiempoCarga -= delta;
            if (tiempoCarga <= 0) {
                // Cambiamos a la fase de disparo
                enFaseCarga = false;
                enFaseDisparo = true;
                direccionCalculada = false; // Reseteamos la dirección
            }
        } else if (enFaseDisparo) {
            // Fase de disparo: movimiento rápido hacia el jugador
            dispararHaciaJugador(delta, sprite, jugador);

            // Si sale de los límites, reinicia la animación
            if (fueraDeLimites(sprite, camara)) {
                reiniciar(sprite);
            }
        }
    }


    private void dispararHaciaJugador(float delta, Sprite sprite, Jugador jugador) {
        if (!direccionCalculada) {
            calcularDireccion(sprite, jugador);
        }

        // Movimiento rápido en la dirección calculada
        float movementX = direccionX * velocidadEnFaseDisparo * delta;
        float movementY = direccionY * velocidadEnFaseDisparo * delta;

        sprite.translate(movementX, movementY);
    }

    private void calcularDireccion(Sprite sprite, Jugador jugador) {
        // Calculamos la dirección inicial hacia el jugador
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

    private boolean fueraDeLimites(Sprite sprite, OrthographicCamera camara) {
        // Verifica si el sprite está fuera de los límites visibles de la cámara
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
        // Reinicia el estado para comenzar de nuevo
        enFaseCarga = true;
        enFaseDisparo = false;
        tiempoCarga = 0.6f; // Reinicia el tiempo de carga
        direccionCalculada = false; // Reseteamos la dirección para el próximo disparo
    }
}

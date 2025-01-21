package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sticklike.core.utilidades.GestorDeAssets;
import com.sticklike.core.entidades.jugador.InputsJugador.Direction;

public class AnimacionesJugador {
    private Direction direccionActual = Direction.IDLE;

    // Animaciones
    private final Animation<TextureRegion> animacionMovDerecha;
    private final Animation<TextureRegion> animacionMovIzquierda;
    private final Animation<TextureRegion> animacionIdle;
    private float temporizadorAnimacion = 0;

    // Estado de parpadeo
    private boolean enParpadeo = false;
    private float tiempoParpadeoRestante = 0;

    public AnimacionesJugador() {
        animacionIdle = GestorDeAssets.animations.get("idle");
        animacionMovDerecha = GestorDeAssets.animations.get("moveRight");
        animacionMovIzquierda = GestorDeAssets.animations.get("moveLeft");
    }

    public void renderizarJugador(SpriteBatch batch, Jugador jugador) {
        Animation<TextureRegion> animacionActual;
        switch (direccionActual) {
            case LEFT:
                animacionActual = animacionMovIzquierda;
                break;
            case RIGHT:
                animacionActual = animacionMovDerecha;
                break;
            default:
                animacionActual = animacionIdle;
                break;
        }

        TextureRegion currentFrame = animacionActual.getKeyFrame(temporizadorAnimacion, true);

        if (enParpadeo) {

            batch.setColor(1,1,1,0.1f);
        }

        batch.draw(currentFrame, jugador.getSprite().getX(), jugador.getSprite().getY(),
            jugador.getSprite().getWidth(), jugador.getSprite().getHeight());

        if (enParpadeo) {
            batch.setColor(1, 1, 1, 1); // Restauramos el color original
        }
    }

    public void actualizarAnimacion(float delta) {
        temporizadorAnimacion += delta;

        if (enParpadeo) {
            tiempoParpadeoRestante -= delta;
            if (tiempoParpadeoRestante <= 0) {
                enParpadeo = false; // Finaliza el parpadeo
            }
        }
    }

    public void activarParpadeo(float duracion) {
        enParpadeo = true;
        tiempoParpadeoRestante = duracion;
    }

    public void setDireccionActual(Direction direccion) {
        this.direccionActual = direccion;
    }
}

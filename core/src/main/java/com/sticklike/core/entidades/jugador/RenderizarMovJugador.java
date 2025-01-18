package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sticklike.core.utilidades.GestorDeAssets;
import com.sticklike.core.entidades.jugador.InputsJugador.Direction;

public class RenderizarMovJugador {
    private Direction direccionActual = Direction.IDLE;

    // Animaciones
    private final Animation<TextureRegion> animacionMovDerecha;
    private final Animation<TextureRegion> animacionMovIzquierda;
    private final Animation<TextureRegion> animacionIdle;
    private float temporizadorAnimacion = 0;

    public RenderizarMovJugador() {
        animacionIdle = GestorDeAssets.animations.get("idle");
        animacionMovDerecha = GestorDeAssets.animations.get("moveRight");
        animacionMovIzquierda = GestorDeAssets.animations.get("moveLeft");
    }

    public void renderizarJugador(SpriteBatch batch, Jugador jugador) {
        if (!jugador.estaVivo()) {
            TextureRegion currentFrame;
            switch (direccionActual) {
                case LEFT:
                    currentFrame = animacionMovIzquierda.getKeyFrame(temporizadorAnimacion, true);
                    break;
                case RIGHT:
                    currentFrame = animacionMovDerecha.getKeyFrame(temporizadorAnimacion, true);
                    break;
                default:
                    currentFrame = animacionIdle.getKeyFrame(temporizadorAnimacion, true);
                    break;
            }
            batch.draw(currentFrame, jugador.getSprite().getX(), jugador.getSprite().getY(),
                jugador.getSprite().getWidth(), jugador.getSprite().getHeight());
        }
    }

    public void actualizarAnimacion(float delta) {
        temporizadorAnimacion += delta;
    }

    public void setDireccionActual(Direction direccion) {
        this.direccionActual = direccion;
    }
}

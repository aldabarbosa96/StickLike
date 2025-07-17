package com.sticklike.core.entidades.renderizado;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;
import com.sticklike.core.entidades.jugador.InputsJugador.Direction;

import static com.sticklike.core.entidades.jugador.InputsJugador.Direction.*;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Renderiza al jugador, incluyendo su animación y barra de salud.
 * Gestiona el cambio de animaciones según la dirección del movimiento y aplica el efecto de parpadeo cuando el jugador recibe daño.
 */

public class RenderJugador {
    private Direction direccionActual = IDLE;

    // todo --> añadir animaciones N,S,NO,NE,SO,SE
    private final Animation<TextureRegion> animacionMovDerecha;
    private final Animation<TextureRegion> animacionMovIzquierda;
    private final Animation<TextureRegion> animacionIdle;
    private float temporizadorAnimacion = TEMPORIZADOR_ANIMACION_MOV;
    private boolean enParpadeo = false;
    private float tiempoParpadeoRestante = TIEMPO_PARPADEO_RESTANTE;

    public RenderJugador() {
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

            batch.setColor(1, 1, 1, 0.1f);
        }

        batch.draw(currentFrame, jugador.getSprite().getX(), jugador.getSprite().getY(), jugador.getSprite().getWidth(), jugador.getSprite().getHeight());

        if (enParpadeo) {
            batch.setColor(1, 1, 1, 1);
        }
    }

    public void renderizarBarraDeSalud(ShapeRenderer shapeRenderer, Jugador jugador) {
        float healthPercentage = jugador.obtenerPorcetajeVida();
        float barWidth = 15f;
        float barHeight = 2.5f;

        float barX = jugador.getSprite().getX() + (jugador.getSprite().getWidth() - barWidth) / 2f;
        float barY = jugador.getSprite().getY() - barHeight - 2.5f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (enParpadeo) {
            // Si el jugador está en parpadeo, la barra entera se pone blanca
            shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 0.5f);
            shapeRenderer.rect(barX - 0.5f, barY - 0.5f, barWidth + 1f, barHeight + 1f);
            shapeRenderer.rect(barX, barY, barWidth * healthPercentage, barHeight); // Fondo blanco
        } else {
            // Borde negro
            shapeRenderer.setColor(0, 0, 0, 1f);
            shapeRenderer.rect(barX - 0.5f, barY - 0.5f, barWidth + 1f, barHeight + 1f);

            // fondo negro
            shapeRenderer.setColor(0f, 0f, 0.15f, 0.5f);
            shapeRenderer.rect(barX, barY, barWidth, barHeight);

            // barra actual roja
            shapeRenderer.setColor(1f, 0f, 0.15f, 1f);
            shapeRenderer.rect(barX, barY, barWidth * healthPercentage, barHeight);
        }

        shapeRenderer.end();
    }

    public void actualizarAnimacion(float delta) {
        temporizadorAnimacion += delta;

        if (enParpadeo) {
            tiempoParpadeoRestante -= delta;
            if (tiempoParpadeoRestante <= 0) {
                enParpadeo = false;
            }
        }
    }

    public void activarParpadeoJugador(float duracion) {
        enParpadeo = true;
        tiempoParpadeoRestante = duracion;
    }

    public void setDireccionActual(Direction direccion) {
        this.direccionActual = direccion;
    }

    public boolean isEnParpadeo() {
        return enParpadeo;
    }
}

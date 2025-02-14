package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.interfaces.ObjetosXP;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public abstract class ObjetoBase implements ObjetosXP {

    private Sprite sprite;
    private boolean recolectado = false;

    // Enums para estados
    private enum EstadoRecolectable {
        INACTIVO,
        REBOTE,
        ATRACCION,
        RECOLECTADO
    }

    private EstadoRecolectable estado = EstadoRecolectable.INACTIVO;

    // Constantes de movimiento
    private final float distanciaActivacion = DISTANCIA_ACTIVACION;
    private final float velocidadAtraccion = VEL_ATRACCION;

    // Parámetros para el rebote
    private float tiempoRebote = 0f;
    private float duracionRebote = 0.1f;
    private float velocidadRebote = 200;

    // Posiciones
    private float x, y;

    // Dirección del rebote (se calcula solo una vez al iniciar el rebote)
    private float reboteDirX, reboteDirY;

    public ObjetoBase(float x, float y) {
        this.sprite = new Sprite(getTexture());
        this.sprite.setSize(getWidth(), getHeight());
        this.sprite.setPosition(x, y);

        this.x = x;
        this.y = y;
    }

    @Override
    public void actualizarObjetoXP(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        if (recolectado || estado == EstadoRecolectable.RECOLECTADO) {
            return;
        }

        // Coordenadas del jugador
        float jugadorX = jugador.getSprite().getX();
        float jugadorY = jugador.getSprite().getY();

        // Calcula la distancia al jugador
        float distancia = (float) Math.sqrt(Math.pow(jugadorX - x, 2) + Math.pow(jugadorY - y, 2));

        switch (estado) {

            case INACTIVO:
                // Si está en rango de activación, iniciamos el rebote
                if (distancia < distanciaActivacion) {
                    estado = EstadoRecolectable.REBOTE;
                    float dirX = x - jugadorX;
                    float dirY = y - jugadorY;
                    float longitud = (float) Math.sqrt(dirX * dirX + dirY * dirY);

                    if (longitud != 0) {
                        dirX /= longitud;
                        dirY /= longitud;
                    }
                    // Guardamos la dirección para usarla en el rebote
                    reboteDirX = dirX;
                    reboteDirY = dirY;
                    tiempoRebote = 0f;
                }
                break;

            case REBOTE:
                // Movemos el objeto en dirección contraria al jugador
                tiempoRebote += delta;
                if (tiempoRebote < duracionRebote) {
                    x += reboteDirX * velocidadRebote * delta;
                    y += reboteDirY * velocidadRebote * delta;
                    sprite.setPosition(x, y);
                } else {
                    estado = EstadoRecolectable.ATRACCION;
                }
                break;

            case ATRACCION:
                    // Movimiento hacia el jugador
                    float direccionX = jugadorX - x;
                    float direccionY = jugadorY - y;
                    float longitud = (float) Math.sqrt(direccionX * direccionX + direccionY * direccionY);

                    if (longitud != 0) {
                        direccionX /= longitud;
                        direccionY /= longitud;
                    }

                    x += direccionX * velocidadAtraccion * delta;
                    y += direccionY * velocidadAtraccion * delta;
                    sprite.setPosition(x, y);

                    // Si colisiona con el jugador, se recolecta
                    if (distancia < 10) {
                        recolectar(gestorDeAudio);

                }
                break;

            case RECOLECTADO:
                break;
        }
    }

    @Override
    public void renderizarObjetoXP(SpriteBatch batch) {
        if (!recolectado) {
            sprite.draw(batch);
        }
    }

    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) {
        gestorDeAudio.reproducirEfecto("recogerXP", AUDIO_RECOLECCION_CACA);
        recolectado = true;
        estado = EstadoRecolectable.RECOLECTADO;
        sprite = null;
    }

    @Override
    public boolean colisionaConOtroSprite(Sprite otherSprite) {
        return sprite != null && sprite.getBoundingRectangle().overlaps(otherSprite.getBoundingRectangle());
    }

    @Override
    public void dispose() {
        sprite = null;
    }

    protected void setSpriteTexture(Texture texture) {
        if (sprite != null) {
            sprite.setTexture(texture); // Actualiza la textura del sprite
        }
    }

    // Métodos abstractos que deben implementarse en clases hijas:
    protected abstract Texture getTexture();
    protected abstract float getWidth();
    protected abstract float getHeight();
}

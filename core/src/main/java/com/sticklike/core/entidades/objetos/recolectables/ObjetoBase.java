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
    private final float distanciaActivacion = DISTANCIA_ACTIVACION;
    private final float velocidadAtraccion = VEL_ATRACCION;
    private float x, y;

    public ObjetoBase(float x, float y) {
        this.sprite = new Sprite(getTexture());
        this.sprite.setSize(getWidth(), getHeight());
        this.sprite.setPosition(x, y);

        this.x = x;
        this.y = y;
    }

    @Override
    public void actualizarObjetoXP(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        if (recolectado) {
            return;
        }

        // Coordenadas del jugador
        float jugadorX = jugador.getSprite().getX();
        float jugadorY = jugador.getSprite().getY();

        // Calcula la distancia al jugador
        float distancia = (float) Math.sqrt(Math.pow(jugadorX - x, 2) + Math.pow(jugadorY - y, 2));

        if (distancia < distanciaActivacion) {
            // Movimiento hacia el jugador
            float direccionX = jugadorX - x;
            float direccionY = jugadorY - y;
            float longitud = (float) Math.sqrt(direccionX * direccionX + direccionY * direccionY);
            direccionX /= longitud; // Normaliza
            direccionY /= longitud;

            x += direccionX * velocidadAtraccion * delta;
            y += direccionY * velocidadAtraccion * delta;

            sprite.setPosition(x, y);

            // Si colisiona con el jugador, se recolecta
            if (distancia < 10) {
                recolectar(gestorDeAudio);
            }
        }
    }

    @Override
    public void renderizarObjetoXP(SpriteBatch batch) {
        if (!recolectado) {
            sprite.draw(batch);
        }
    }

    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) { // todo --> manejar los audios individualmente
        gestorDeAudio.reproducirEfecto("recogerXP",AUDIO_RECOLECCION_CACA);
        recolectado = true;
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

    protected abstract Texture getTexture();
    protected abstract float getWidth();
    protected abstract float getHeight();
}

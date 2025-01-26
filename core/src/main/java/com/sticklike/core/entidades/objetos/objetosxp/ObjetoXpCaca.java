package com.sticklike.core.entidades.objetos.objetosxp;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.interfaces.ObjetosXP;
import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

/**
 * La clase ObjetoXpCaca representa los objetos que sueltan los EnemigoCulo al morir y que otorgan experiencia
 */
public class ObjetoXpCaca implements ObjetosXP {
    private Sprite sprite;
    private boolean recolectado = false;

    private float x, y;

    private final float distanciaActivacion = DISTANCIA_ACTIVACION;
    private final float velocidadAtraccion = VEL_ATRACCION;


    public ObjetoXpCaca(float x, float y) {
        Texture texture = recolectableCaca;
        sprite = new Sprite(texture);
        sprite.setSize(OBJETO_CACA_WIDTH, OBJETO_CACA_HEIGHT);
        sprite.setPosition(x, y);

        // Inicializa las coordenadas
        this.x = x;
        this.y = y;
    }

    @Override
    public void renderizarObjetoXP(SpriteBatch batch) {
        if (!recolectado) {
            sprite.draw(batch);
        }
    }


    @Override
    public void actualizarObjetoXP(float delta, Jugador jugador, ControladorAudio controladorAudio) {
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
                recolectar(controladorAudio);
            }
        }
    }

    @Override
    public void recolectar(ControladorAudio controladorAudio) {
        controladorAudio.reproducirEfecto("recogerXP",AUDIO_RECOLECCION_CACA);
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
}

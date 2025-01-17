package com.sticklike.core.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.utils.GestorDeAssets;

/**
 * La clase ObjetoXP representa los objetos que sueltan los enemigos al morir
 * y que otorgan experiencia al Jugador
 */
public class ObjetoXP {
    private Sprite sprite;
    private boolean recolectado = false;

    /**
     * Genera un nuevo objeto de experiencia en una posición determinada
     * @param x,y coordenadas spawn gestionadas por enemigo
     */
    public ObjetoXP(float x, float y) {
        Texture texture = GestorDeAssets.recolectableCaca;
        sprite = new Sprite(texture);
        sprite.setSize(12, 12);
        sprite.setPosition(x, y);
    }


    /**
     * Actualiza la posición del objeto
     * todo --> podría implementarse en caso de requerir que el objeto se desplace o interaccione
     * @param delta
     */
    public void actualizarObjetoXP(float delta) {
    }

    /**
     * Renderiza el objeto cuando no ha sido recolectado
     * @param batch SpriteBatch para dibujar el objeto
     */
    public void renderizarObjetoXP(SpriteBatch batch) {
        if (!recolectado) {
            sprite.draw(batch);
        }
    }

    /**
     * Marca el objeto como recolectado y elimina el sprite
     */
    public void recolectar() {
        recolectado = true;
        sprite = null;
    }

    /**
     * Detecta colisión con cualquier otro sprite (Jugador por ahora)
     * @param otherSprite sprite con el que colisiona
     * @return devuelve true si existe colisión
     */
    public boolean colisionaConOtroSprite(Sprite otherSprite) {
        return sprite.getBoundingRectangle().overlaps(otherSprite.getBoundingRectangle());
    }

    public void dispose() {
        sprite = null;
    }
}


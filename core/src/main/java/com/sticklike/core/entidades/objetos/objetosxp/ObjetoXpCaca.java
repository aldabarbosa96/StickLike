package com.sticklike.core.entidades.objetos.objetosxp;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.entidades.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.GestorDeAssets;

/**
 * La clase ObjetoXP representa los objetos que sueltan los enemigos al morir
 * y que otorgan experiencia al Jugador
 */
public class ObjetoXpCaca implements ObjetosXP {
    private Sprite sprite;
    private boolean recolectado = false;

    /**
     * Genera un nuevo objeto de experiencia en una posición determinada
     * @param x,y coordenadas spawn gestionadas por enemigo
     */
    public ObjetoXpCaca(float x, float y) {
        Texture texture = GestorDeAssets.recolectableCaca;
        sprite = new Sprite(texture);
        sprite.setSize(12, 12);
        sprite.setPosition(x, y);
    }

    @Override
    public void renderizarObjetoXP(SpriteBatch batch) {
        if (!recolectado) {
            sprite.draw(batch);
        }
    }

    @Override
    public void actualizar() {

    }

    @Override
    public void recolectar() {
        recolectado = true;
        sprite = null;
    }


    /**
     * Detecta colisión con cualquier otro sprite (Jugador por ahora)
     * @param otherSprite sprite con el que colisiona
     * @return devuelve true si existe colisión
     */
    @Override
    public boolean colisionaConOtroSprite(Sprite otherSprite) {
        return sprite.getBoundingRectangle().overlaps(otherSprite.getBoundingRectangle());
    }

    public void dispose() {
        sprite = null;
    }
}


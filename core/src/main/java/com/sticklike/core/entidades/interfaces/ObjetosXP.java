package com.sticklike.core.entidades.interfaces;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface ObjetosXP {
    void actualizar();
    void renderizarObjetoXP(SpriteBatch batch);
    void recolectar();
    boolean colisionaConOtroSprite(Sprite sprite);
    void dispose();
}

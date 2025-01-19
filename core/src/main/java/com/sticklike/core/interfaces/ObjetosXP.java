package com.sticklike.core.interfaces;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.entidades.jugador.Jugador;

public interface ObjetosXP {
    void actualizar();
    void renderizarObjetoXP(SpriteBatch batch);
    void recolectar();
    boolean colisionaConOtroSprite(Sprite sprite);
    void dispose();
}

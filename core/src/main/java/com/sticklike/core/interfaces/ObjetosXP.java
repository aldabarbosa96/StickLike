package com.sticklike.core.interfaces;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;

public interface ObjetosXP {
    void actualizarObjetoXP(float delta, Jugador jugador, GestorDeAudio gestorDeAudio);
    void renderizarObjetoXP(SpriteBatch batch);
    void recolectar(GestorDeAudio gestorDeAudio);
    boolean colisionaConOtroSprite(Sprite sprite);
    void dispose();

}

package com.sticklike.core.interfaces;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.entidades.jugador.Jugador;

public interface ObjetosXP {
    void actualizarObjetoXP(float delta, Jugador jugador, ControladorAudio controladorAudio);
    void renderizarObjetoXP(SpriteBatch batch);
    void recolectar(ControladorAudio controladorAudio);
    boolean colisionaConOtroSprite(Sprite sprite);
    void dispose();

}

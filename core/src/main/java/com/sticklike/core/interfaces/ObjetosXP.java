package com.sticklike.core.interfaces;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.entidades.jugador.Jugador;

/**
 * Define el comportamiento de los objetos de experiencia en el juego.
 * Incluye métodos para actualizar, renderizar, recolectar y detectar colisiones.
 */

public interface ObjetosXP {
    void actualizarObjetoXP(float delta, Jugador jugador, GestorDeAudio gestorDeAudio);
    void renderizarObjetoXP(SpriteBatch batch);
    void recolectar(GestorDeAudio gestorDeAudio);
    boolean colisionaConOtroSprite(Sprite sprite);
    void dispose();
    void aplicarEfecto(Jugador jugador, GestorDeAudio audio, VentanaJuego1 game);
}

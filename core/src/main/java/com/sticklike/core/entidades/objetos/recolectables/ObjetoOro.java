package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Representa un objeto de oro recolectable (caca dorada).
 */

public class ObjetoOro extends ObjetoBase {
    private static final Texture TEXTURE = manager.get(RECOLECTABLE_CACA_DORADA, Texture.class);

    public ObjetoOro(float x, float y) {
        super(x, y, TEXTURE);
        setSpriteTexture(getTexture()); // Sincroniza la textura
    }

    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) {
        gestorDeAudio.reproducirEfecto("recogerOro", AUDIO_RECOLECCION_ORO);
        super.recolectar(gestorDeAudio);
    }

    @Override
    public void aplicarEfecto(Jugador jugador, GestorDeAudio gestorDeAudio, VentanaJuego1 juego) {
        jugador.setOroGanado(jugador.getOroGanado() + 1);
    }

    @Override
    public void particulas() {

    }

    @Override
    protected Texture getTexture() {
        return TEXTURE;
    }

    @Override
    protected float getWidth() {
        return OBJETO_ORO_WIDTH;
    }

    @Override
    protected float getHeight() {
        return OBJETO_ORO_HEIGHT;
    }
}

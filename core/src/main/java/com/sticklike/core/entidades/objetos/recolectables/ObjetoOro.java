package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.sticklike.core.utilidades.GestorDeAudio;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

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

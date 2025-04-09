package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Representa un objeto de vida recolectable (corazón).
 */

public class ObjetoVida extends ObjetoBase {
    private boolean recolectado = false;
    private final static Texture TEXTURE = manager.get(RECOLECTABLE_VIDA, Texture.class);

    public ObjetoVida(float x, float y) {
        super(x, y, TEXTURE);
    }


    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) {
        gestorDeAudio.reproducirEfecto("recogerVida", AUDIO_RECOLECCION_VIDA);
        recolectado = true;
    }

    @Override
    protected Texture getTexture() {
        return TEXTURE;
    }

    @Override
    protected float getWidth() {
        return OBJETO_VIDA_WIDTH;
    }

    @Override
    protected float getHeight() {
        return OBJETO_VIDA_HEIGHT;
    }
}

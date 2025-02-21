package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sticklike.core.utilidades.GestorDeAudio;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

/**
 * Representa un objeto de vida recolectable (corazón).
 */

public class ObjetoVida extends ObjetoBase {
    private Sprite sprite;
    private boolean recolectado = false;

    public ObjetoVida(float x, float y) {
        super(x, y);
    }


    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) {
        gestorDeAudio.reproducirEfecto("recogerVida", AUDIO_RECOLECCION_VIDA);
        recolectado = true;
        sprite = null;
    }

    @Override
    protected Texture getTexture() {
        return manager.get(RECOLECTABLE_VIDA, Texture.class);
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

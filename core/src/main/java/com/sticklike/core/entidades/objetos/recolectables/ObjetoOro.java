package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.sticklike.core.utilidades.GestorDeAudio;
import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class ObjetoOro extends ObjetoBase {

    public ObjetoOro(float x, float y) {
        super(x, y);
        setSpriteTexture(getTexture()); // Sincroniza la textura
    }

    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) {
        gestorDeAudio.reproducirEfecto("recogerOro", AUDIO_RECOLECCION_ORO);
        super.recolectar(gestorDeAudio);
    }

    @Override
    protected Texture getTexture() {
        return recolectableCacaDorada;
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

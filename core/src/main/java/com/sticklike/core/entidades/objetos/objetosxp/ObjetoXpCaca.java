package com.sticklike.core.entidades.objetos.objetosxp;

import com.badlogic.gdx.graphics.Texture;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class ObjetoXpCaca extends ObjetoXpBase {

    private boolean esCacaDorada = false;

    public ObjetoXpCaca(float x, float y) {
        super(x, y);
    }

    @Override
    public Texture getTexture() {
        esCacaDorada = false; // Reseteamos antes de asignar
        float randomCaca = (float) (Math.random() * 100f);
        if (!esCacaDorada && randomCaca < 80f) {
            return recolectableCaca; // Caca normal
        } else {
            esCacaDorada = true; // Caca dorada
            return recolectableCaca2;
        }
    }

    public boolean isEsCacaDorada() {
        return esCacaDorada;
    }

    @Override
    protected float getWidth() {
        return OBJETO_CACA_WIDTH;
    }

    @Override
    protected float getHeight() {
        return OBJETO_CACA_HEIGHT;
    }
}

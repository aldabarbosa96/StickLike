package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class ObjetoXp extends ObjetoBase {

    private final boolean esXPGorda;

    public ObjetoXp(float x, float y) {
        super(x, y);
        float randomXP = (float) (Math.random() * 100f);
        esXPGorda = randomXP >= 95f;
        setSpriteTexture(getTexture()); // Sincroniza el sprite con la textura
    }

    @Override
    public Texture getTexture() {
        return esXPGorda ? recolectableXP2 : recolectableXP;
    }

    public boolean isEsXPGorda() {
        return esXPGorda;
    }

    @Override
    protected float getWidth() {
        return OBJETO1_XP_WIDTH;
    }

    @Override
    protected float getHeight() {
        return OBJETO1_XP_HEIGHT;
    }
}

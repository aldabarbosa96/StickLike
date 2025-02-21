package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

/**
 * Representa un objeto de experiencia recolectable (gema). Tiene una probabilidad baja de generarse en una versión más grande.
 */

public class ObjetoXp extends ObjetoBase {

    private final boolean esXPGorda;

    public ObjetoXp(float x, float y) {
        super(x, y);
        float randomXP = MathUtils.random(100);
        esXPGorda = randomXP >= 95f;  // Aprox 5% de que sea gorda
        setSpriteTexture(getTexture());
    }

    @Override
    public Texture getTexture() {
        return esXPGorda ? manager.get(RECOLECTABLE_XP2, Texture.class) : manager.get(RECOLECTABLE_XP, Texture.class);
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

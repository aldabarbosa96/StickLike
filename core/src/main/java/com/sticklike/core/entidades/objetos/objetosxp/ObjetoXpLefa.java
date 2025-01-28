package com.sticklike.core.entidades.objetos.objetosxp;

import com.badlogic.gdx.graphics.Texture;
import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class ObjetoXpLefa extends ObjetoXpBase {

    private final boolean esXpGorda;
    public ObjetoXpLefa(float x, float y) {
        super(x,y);
        float randomCaca = (float) (Math.random() * 100f);
        esXpGorda = randomCaca >= 95f;
        setSpriteTexture(getTexture());
    }
    @Override
    public Texture getTexture() {
        return esXpGorda ? recolectableCacaOro : recolectableXP;
    }
    public boolean isEsXpGorda() {
        return esXpGorda;
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

package com.sticklike.core.entidades.objetos.objetosxp;

import com.badlogic.gdx.graphics.Texture;
import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class ObjetoXpCaca extends ObjetoXpBase {

    private final boolean esXPGorda;

    public ObjetoXpCaca(float x, float y) {
        super(x, y);
        float randomCaca = (float) (Math.random() * 100f);
        esXPGorda = randomCaca >= 95f;
        setSpriteTexture(getTexture()); // Sincroniza el sprite con la textura
    }

    @Override
    public Texture getTexture() {
        return esXPGorda ? recolectableCacaOro : recolectableXP;
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

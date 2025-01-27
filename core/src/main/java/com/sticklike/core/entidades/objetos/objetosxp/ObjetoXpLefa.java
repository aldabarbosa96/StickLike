package com.sticklike.core.entidades.objetos.objetosxp;

import com.badlogic.gdx.graphics.Texture;
import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class ObjetoXpLefa extends ObjetoXpBase {

    public ObjetoXpLefa(float x, float y) {
        super(x,y);
    }
    @Override
    protected Texture getTexture() {
        return recolectableLefa;
    }

    @Override
    protected float getWidth() {
        return OBJETO_LEFA_WIDTH;
    }
    @Override
    protected float getHeight() {
        return OBJETO_LEFA_HEIGHT;
    }
}

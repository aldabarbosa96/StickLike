package com.sticklike.core.entidades.objetos.objetosxp;

import com.badlogic.gdx.graphics.Texture;
import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

/**
 * La clase ObjetoXpCaca representa los objetos que sueltan los EnemigoCulo al morir y que otorgan experiencia
 */
public class ObjetoXpCaca extends ObjetoXpBase {

    public ObjetoXpCaca(float x, float y) {
        super(x,y);
    }

    @Override
    protected Texture getTexture() {
        return recolectableCaca;
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

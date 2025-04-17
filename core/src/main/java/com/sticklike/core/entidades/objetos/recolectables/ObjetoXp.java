package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class ObjetoXp extends ObjetoBase {
    // Usamos un entero para representar el tipo: 0 = normal, 1 = XP morada, 2 = XP dorada
    private int tipo;
    private static final Texture TEXTURE = manager.get(RECOLECTABLE_XP, Texture.class);
    private static final Texture TEXTURE2 = manager.get(RECOLECTABLE_XP2, Texture.class);
    private static final Texture TEXTURE3 = manager.get(RECOLECTABLE_XP3, Texture.class);

    // Constructor público: determina el tipo basándose en probabilidades y delega en el constructor privado
    public ObjetoXp(float x, float y) {
        this(x, y, determinarTipo());
    }

    private static int determinarTipo() {
        float r = MathUtils.random(100f);
        if (r < 85f) {
            return 0;
        } else if (r < 99f) {
            return 1;
        } else {
            return 2;
        }
    }

    // Constructor privado con el tipo ya determinado
    private ObjetoXp(float x, float y, int tipo) {
        super(x, y, (tipo == 0) ? TEXTURE : (tipo == 1 ? TEXTURE2 : TEXTURE3));
        this.tipo = tipo;
    }

    @Override
    public Texture getTexture() {
        return switch (tipo) {
            case 0 -> TEXTURE;
            case 1 -> TEXTURE2;
            case 2 -> TEXTURE3;
            default -> TEXTURE;
        };
    }

    public int getTipo() {
        return tipo;
    }
    public void setTipo(int nuevoTipo) {
        this.tipo = nuevoTipo;
        switch(nuevoTipo) {
            case 0 -> setSpriteTexture(TEXTURE);
            case 1 -> setSpriteTexture(TEXTURE2);
            case 2 -> setSpriteTexture(TEXTURE3);
            default -> setSpriteTexture(TEXTURE);
        }
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

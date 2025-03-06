package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class ObjetoXp extends ObjetoBase {
    private final boolean esXPGorda;
    private static final Texture TEXTURE = manager.get(RECOLECTABLE_XP, Texture.class);
    private static final Texture TEXTURE2 = manager.get(RECOLECTABLE_XP2, Texture.class);

    // Constructor público: calcula la probabilidad y delega en el constructor privado.
    public ObjetoXp(float x, float y) {
        this(x, y, MathUtils.random(100) >= 95f);
    }

    // Constructor privado que ya recibe la información necesaria.
    private ObjetoXp(float x, float y, boolean esXPGordaLocal) {
        super(x, y, esXPGordaLocal ? TEXTURE2 : TEXTURE);
        this.esXPGorda = esXPGordaLocal;
    }

    @Override
    public Texture getTexture() {
        return esXPGorda ? TEXTURE2 : TEXTURE;
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


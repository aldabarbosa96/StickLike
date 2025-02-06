package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.utilidades.GestorDeAudio;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class ObjetoPowerUp extends ObjetoBase {
    private static int contador = 0;

    public ObjetoPowerUp(float x, float y) {
        super(x, y);

    }

    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) {
        contador++;
        gestorDeAudio.reproducirEfecto("recogerPowerUP", AUDIO_RECOLECCION_PWUP);
        super.recolectar(gestorDeAudio);

    }
    @Override
    protected Texture getTexture() {
        return recolectablePowerUp;
    }

    @Override
    protected float getWidth() {
        return OBJETO_PWUP_WIDTH;
    }

    @Override
    protected float getHeight() {
        return OBJETO_PWUP_HEIGHT;
    }
    public static Array<ObjetoPowerUp> crearPowerUps(int cantidad, float minX, float maxX, float minY, float maxY) {
        Array<ObjetoPowerUp> powerUps = new Array<>();
        for (int i = 0; i < cantidad; i++) {
            float margen = 50f;
            float x = MathUtils.random(minX + margen, maxX - margen);
            float y = MathUtils.random(minY + margen, maxY - margen);
            powerUps.add(new ObjetoPowerUp(x, y));
        }
        return powerUps;
    }


    public static int getContador() {
        return contador;
    }
}

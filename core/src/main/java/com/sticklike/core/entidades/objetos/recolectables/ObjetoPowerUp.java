package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Representa un objeto recolectable de tipo Power-Up en el juego (lápiz).
 * Se genera en ubicaciones aleatorias dentro de un rango definido.
 */

public class ObjetoPowerUp extends ObjetoBase {
    private static final Texture TEXTURA = manager.get(RECOLECTABLE_POWER_UP, Texture.class);
    private int contador = 0;

    public ObjetoPowerUp(float x, float y) {
        super(x, y, TEXTURA);

    }

    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) {
        contador++;
        gestorDeAudio.reproducirEfecto("recogerPowerUP", AUDIO_RECOLECCION_PWUP);
        super.recolectar(gestorDeAudio);

    }

    @Override
    public void aplicarEfecto(Jugador jugador, GestorDeAudio audio, VentanaJuego1 game) {
        jugador.setTrazosGanados(Jugador.getTrazosGanados() + 1);
    }

    @Override
    protected Texture getTexture() {
        return TEXTURA;
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


    public int getContador() {
        return contador;
    }
}

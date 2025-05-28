package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.renderizado.particulas.ParticleManager;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class ObjetoXp extends ObjetoBase {
    private int tipo;
    private ParticleEffectPool.PooledEffect effect;
    private boolean efectoLanzado = false;
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

    public void setTipo(int nuevoTipo) {
        this.tipo = nuevoTipo;
        switch (nuevoTipo) {
            case 0 -> setSpriteTexture(TEXTURE);
            case 1 -> setSpriteTexture(TEXTURE2);
            case 2 -> setSpriteTexture(TEXTURE3);
            default -> setSpriteTexture(TEXTURE);
        }
    }

    @Override
    public void aplicarEfecto(Jugador jugador, GestorDeAudio audio, VentanaJuego1 game) {
        float xpOtorgada = switch (tipo) {
            case 0 -> 10f + MathUtils.random(15f);
            case 1 -> 50f + MathUtils.random(50f);
            case 2 -> 2 * (50f + MathUtils.random(50f));
            default -> 0f;
        };
        game.getSistemaDeNiveles().agregarXP(xpOtorgada);
    }

    @Override
    public void particulas() {
        EstadoRecolectable estadoRecolectable = super.getEstado();
        if (estadoRecolectable == EstadoRecolectable.REBOTE) {
            this.effect = ParticleManager.get().obtainEffect("xp", x, y);
            effect.allowCompletion();
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

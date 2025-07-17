package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.renderizado.particulas.ParticleManager;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * ObjetoXp poolable para evitar creación excesiva de instancias.
 */
public class ObjetoXp extends ObjetoBase implements Pool.Poolable {
    private int tipo;
    private ParticleEffectPool.PooledEffect effect;

    // 1) Texturas estáticas compartidas
    private static final Texture[] TEXTURAS = {
        manager.get(RECOLECTABLE_XP,  Texture.class),
        manager.get(RECOLECTABLE_XP2, Texture.class),
        manager.get(RECOLECTABLE_XP3, Texture.class)
    };

    // 2) Pool estático
    private static final Pool<ObjetoXp> POOL = new Pool<ObjetoXp>() {
        @Override
        protected ObjetoXp newObject() {
            // Constructor dummy, se reconfigura en init()
            return new ObjetoXp(0, 0, 0);
        }
    };

    /** Obtiene una instancia desde el pool y la inicializa. */
    public static ObjetoXp obtain(float x, float y) {
        int tipo = determinarTipo();
        ObjetoXp xp = POOL.obtain();
        xp.init(x, y, tipo);
        return xp;
    }

    private static int determinarTipo() {
        float r = MathUtils.random(100f);
        if (r < 85f) return 0;
        else if (r < 99f) return 1;
        else return 2;
    }

    /** Constructor privado usado por el pool. */
    private ObjetoXp(float x, float y, int tipo) {
        super(x, y, TEXTURAS[tipo]);
        this.tipo = tipo;
    }

    /** Inicializa o reinicializa todos los campos para reuse. */
    private void init(float x, float y, int tipo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.estado = EstadoRecolectable.INACTIVO;
        this.recolectado = false;
        this.atraccionForzada = false;
        this.tiempoRebote = 0f;
        sprite = new Sprite(TEXTURAS[tipo]);
        sprite.setSize(OBJETO1_XP_WIDTH, OBJETO1_XP_HEIGHT);
        sprite.setPosition(x, y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) {
        super.recolectar(gestorDeAudio);
        free();
    }

    public void free() {
        POOL.free(this);
    }

    @Override
    public Texture getTexture() {
        return TEXTURAS[tipo];
    }

    @Override
    public void aplicarEfecto(Jugador jugador, GestorDeAudio audio, VentanaJuego1 game) {
        float xpOtorgada;
        switch (tipo) {
            case 1: xpOtorgada = 50f + MathUtils.random(50f); break;
            case 2: xpOtorgada = 2 * (50f + MathUtils.random(50f)); break;
            default: xpOtorgada = 10f + MathUtils.random(15f);
        }
        game.getSistemaDeNiveles().agregarXP(xpOtorgada);
    }

    @Override
    public void particulas() {
        if (estado == EstadoRecolectable.REBOTE) {
            effect = ParticleManager.get().obtainEffect("xp", x, y);
            effect.allowCompletion();
        }
    }
    public void setTipo(int nuevoTipo) {
        this.tipo = nuevoTipo;
        setSpriteTexture(TEXTURAS[nuevoTipo]);
    }

    @Override
    protected float getWidth() {
        return OBJETO1_XP_WIDTH;
    }

    @Override
    protected float getHeight() {
        return OBJETO1_XP_HEIGHT;
    }

    @Override public void reset() {}
}

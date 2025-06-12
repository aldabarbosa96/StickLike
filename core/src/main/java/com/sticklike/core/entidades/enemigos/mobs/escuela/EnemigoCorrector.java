package com.sticklike.core.entidades.enemigos.mobs.escuela;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoLineal;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoBase;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Enemigo que aparece al caducar un proyectil corrector.
 * Su comportamiento es moverse en línea recta (IA lineal) persiguiendo al jugador.
 */
public class EnemigoCorrector extends EnemigoBase {

    private static Texture TEXTURE;
    private final MovimientoLineal movimiento;
    private static final float VELOCIDAD_BASE = 125f;
    private static final float VIDA_INICIAL = 333f;
    private static final float DANO = 15f;
    private static final float COOLDOWN_DANO = 0.8f;

    public EnemigoCorrector(Jugador jugador, float x, float y) {
        super(jugador);
        if (TEXTURE == null) TEXTURE = manager.get(ENEMIGO_CORRECTOR, Texture.class);

        this.vidaEnemigo = VIDA_INICIAL;
        this.damageAmount = DANO;
        this.coolDownDanyo = COOLDOWN_DANO;
        this.temporizadorDanyo = COOLDOWN_DANO; // para poder golpear de inmediato si colisiona

        sprite = new Sprite(TEXTURE);
        sprite.setSize(18f, 40f);
        sprite.setOrigin(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
        sprite.setPosition(x - sprite.getOriginX(), y - sprite.getOriginY());

        movimiento = new MovimientoLineal(true, VELOCIDAD_BASE);
        this.damageTexture = manager.get(DAMAGE_CORRECTOR, Texture.class);
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        movimiento.actualizarMovimiento(delta, sprite, jugador);
        animacionBaseEnemigos.flipearEnemigo(jugador, sprite);
    }

    @Override
    protected void actualizarKnockback(float delta) {
        movimiento.actualizarSoloKnockback(delta, sprite, true);
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        animacionBaseEnemigos.reproducirSonidoMuerteGenerico();
        animacionBaseEnemigos.iniciarAnimacionMuerte(GestorDeAssets.animations.get("correctorMuerte"));
        animacionBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimiento.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        if (!haSoltadoXP) {
            haSoltadoXP = true;
            return new ObjetoXp(getX(), getY());
        }
        return null;
    }

    @Override
    public boolean estaEnKnockback() {
        return movimiento.getKnockbackTimer() > 0f;
    }

    @Override
    public MovimientoBaseEnemigos getMovimiento() {
        return movimiento;
    }
}


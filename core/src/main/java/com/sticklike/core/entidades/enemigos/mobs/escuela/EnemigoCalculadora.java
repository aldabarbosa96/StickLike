package com.sticklike.core.entidades.enemigos.mobs.escuela;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionCalculadora;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoLineal;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoBase;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class EnemigoCalculadora extends EnemigoBase {
    private Texture encendida;
    private Texture apagada;
    private MovimientoLineal movimiento;
    private AnimacionCalculadora animacion;
    private static float velocidadBase = VEL_BASE_CALCULADORA;

    public EnemigoCalculadora(float x, float y, Jugador jugador) {
        super(jugador);
        this.vidaEnemigo = VIDA_ENEMIGO_CALCULADORA;
        this.damageAmount = DANYO_CALCULADORA;
        this.coolDownDanyo = COOLDOWN_ENEMIGOCULO;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;
        this.encendida = manager.get(ENEMIGO_CALCULADORA, Texture.class);
        this.apagada = manager.get(ENEMIGO_CALCULADORA_APAGADA, Texture.class);
        sprite = new Sprite(encendida);
        sprite.setSize(38, 56);
        sprite.setPosition(x, y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.movimiento = new MovimientoLineal(true, velocidadBase);
        this.animacion = new AnimacionCalculadora(animacionesBaseEnemigos, apagada, encendida, 0.2f, 1f);
        this.damageTexture = manager.get(DAMAGE_CALCULADORA, Texture.class);
    }


    @Override
    protected void actualizarMovimiento(float delta) {
        movimiento.actualizarMovimiento(delta, sprite, jugador);
        animacion.actualizarAnimacion(delta, sprite);
        animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);
    }

    @Override
    protected void actualizarKnockback(float delta) {
        movimiento.actualizarSoloKnockback(delta, sprite, true);
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        Animation<TextureRegion> animMuertePolla = GestorDeAssets.animations.get("calculadoraMuerte");
        animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuertePolla);
        animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimiento.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.25f) {
            haSoltadoXP = true;
            return new ObjetoVida(posXMuerte, posYMuerte);
        }
        if (!haSoltadoXP && randomXP >= 20f) {
            haSoltadoXP = true;
            return new ObjetoXp(posXMuerte, posYMuerte);
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

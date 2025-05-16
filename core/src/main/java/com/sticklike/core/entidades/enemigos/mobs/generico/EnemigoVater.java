package com.sticklike.core.entidades.enemigos.mobs.generico;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionVater;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoLineal;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoBase;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoOro;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoPowerUp;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Enemigo Vater; gestiona su comportamiento y daño.
 */
public class EnemigoVater extends EnemigoBase {
    private Texture spriteTapaLevantada;
    private Texture spriteTapaBajada;
    private MovimientoLineal movimientoLineal;
    private AnimacionVater animacionVater;

    public EnemigoVater(float x, float y, Jugador jugador) {
        super(jugador);
        spriteTapaBajada = manager.get(ENEMIGO_VATER, Texture.class);
        spriteTapaLevantada = manager.get(ENEMIGO_VATER2, Texture.class);
        damageTexture = manager.get(DAMAGE_VATER_TEXTURE, Texture.class);
        sprite = new Sprite(spriteTapaLevantada);
        sprite.setSize(55, 80);
        sprite.setPosition(x, y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.vidaEnemigo = VIDA_ENEMIGO_VATER;
        this.damageAmount = DANYO_VATER;
        this.coolDownDanyo = COOLDOWN_VATER;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;

        movimientoLineal = new MovimientoLineal(true, VEL_BASE_VATER);
        animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        animacionVater = new AnimacionVater(animacionesBaseEnemigos, spriteTapaLevantada, spriteTapaBajada,0.25f,1f);

        renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        movimientoLineal.actualizarMovimiento(delta, sprite, jugador);
        animacionVater.actualizarAnimacion(delta, sprite);
        animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);

    }

    @Override
    protected void actualizarKnockback(float delta) {
        movimientoLineal.actualizarSoloKnockback(delta, sprite, true);
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        Animation<TextureRegion> animMuerteVater = GestorDeAssets.animations.get("vaterMuerte");
        animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteVater);
        animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimientoLineal.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        if (Jugador.getOroGanado() >= 15) return null;
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.25f) {
            haSoltadoXP = true;
            return new ObjetoVida(posXMuerte, posYMuerte);
        }
        if (!haSoltadoXP && randomXP >= 15f) {
            haSoltadoXP = true;
            return new ObjetoOro(posXMuerte, posYMuerte);
        } else {
            haSoltadoXP = true;
            return new ObjetoPowerUp(posXMuerte, posYMuerte);
        }
    }
}

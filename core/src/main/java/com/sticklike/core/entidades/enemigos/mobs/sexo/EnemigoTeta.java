package com.sticklike.core.entidades.enemigos.mobs.sexo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sticklike.core.entidades.enemigos.ia.MovimientoPolla;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoBase;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class EnemigoTeta extends EnemigoBase {
    private MovimientoPolla movimientoPolla;
    private static final float ROTATION_SPEED = 333f;

    public EnemigoTeta(Jugador jugador, float x, float y, float velocidadBase) {
        super(jugador);
        sprite = new Sprite(manager.get(ENEMIGO_TETA, Texture.class));
        sprite.setSize(32,32);
        sprite.setOriginCenter();
        sprite.setPosition(x,y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        movimientoPolla = new MovimientoPolla(velocidadBase, 0.75f, 25f, true);
        this.damageTexture = manager.get(DAMAGE_TETA, Texture.class);
        this.vidaEnemigo = VIDA_ENEMIGO_TETA;
        this.coolDownDanyo = COOLDOWN_POLLA;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;
        this.damageAmount = DANYO_TETA;
        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        movimientoPolla.actualizarMovimiento(delta, sprite, jugador);
        animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);
        sprite.rotate(ROTATION_SPEED * delta);
    }

    @Override
    protected void actualizarKnockback(float delta) {
        movimientoPolla.actualizarSoloKnockback(delta, sprite, true);
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        Animation<TextureRegion> animMuerteTeta = GestorDeAssets.animations.get("tetaMuerte");
        animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteTeta);
        animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimientoPolla.aplicarKnockback(fuerza, dirX, dirY);
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

    public MovimientoPolla getMovimientoPolla() {
        return movimientoPolla;
    }
}

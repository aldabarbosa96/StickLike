package com.sticklike.core.entidades.enemigos.mobs.sexo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBotes;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoBase;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class EnemigoTeta extends EnemigoBase {
    private MovimientoBotes movimientoBotes;
    private static final float ROTATION_SPEED = 333f;

    public EnemigoTeta(Jugador jugador, float x, float y, float velocidadBase) {
        super(jugador);
        sprite = new Sprite(manager.get(ENEMIGO_TETA, Texture.class));
        sprite.setSize(32,32);
        sprite.setOriginCenter();
        sprite.setPosition(x,y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        movimientoBotes = new MovimientoBotes(velocidadBase, 0.75f, 25f, true);
        this.damageTexture = manager.get(DAMAGE_TETA, Texture.class);
        this.vidaEnemigo = VIDA_ENEMIGO_TETA;
        this.coolDownDanyo = COOLDOWN_POLLA;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;
        this.damageAmount = DANYO_TETA;
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        movimientoBotes.actualizarMovimiento(delta, sprite, jugador);
        animacionBaseEnemigos.flipearEnemigo(jugador, sprite);
        sprite.rotate(ROTATION_SPEED * delta);
    }

    @Override
    protected void actualizarKnockback(float delta) {
        movimientoBotes.actualizarSoloKnockback(delta, sprite, true);
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        Animation<TextureRegion> animMuerteTeta = GestorDeAssets.animations.get("tetaMuerte");
        animacionBaseEnemigos.reproducirSonidoMuerteGenerico();
        animacionBaseEnemigos.iniciarAnimacionMuerte(animMuerteTeta);
        animacionBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimientoBotes.aplicarKnockback(fuerza, dirX, dirY);
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
        return movimientoBotes.getKnockbackTimer() > 0f;
    }

    @Override
    public MovimientoBaseEnemigos getMovimiento() {
        return movimientoBotes;
    }

    public MovimientoBotes getMovimientoBotes() {
        return movimientoBotes;
    }
}

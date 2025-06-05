package com.sticklike.core.entidades.enemigos.mobs.escuela;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionGrapadora;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoOscilante;
import com.sticklike.core.entidades.enemigos.mobs.EnemigoBase;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class EnemigoPerforadora extends EnemigoBase {
    private Texture abierta;
    private Texture cerrada;
    private MovimientoOscilante movimientoOscilante;
    private AnimacionGrapadora animacionGrapadora;
    private static float velocidadBase = VEL_BASE_GRAPADORA;

    public EnemigoPerforadora(Jugador jugador, float x, float y) {
        super(jugador);
        this.vidaEnemigo = VIDA_ENEMIGO_LIBRO;
        this.damageAmount = DANYO_CALCULADORA;
        this.coolDownDanyo = COOLDOWN_ENEMIGOCULO;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;
        this.abierta = manager.get(ENEMIGO_PERFORADORA, Texture.class);
        this.cerrada = manager.get(ENEMIGO_PERFORADORA_CERRADA, Texture.class);
        sprite = new Sprite(abierta);
        sprite.setPosition(x, y);
        sprite.setSize(44, 40);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.movimientoOscilante = new MovimientoOscilante(velocidadBase, true);
        this.animacionGrapadora = new AnimacionGrapadora(animacionBaseEnemigos, abierta, cerrada, 0.2f, 0.2f);
        this.damageTexture = manager.get(DAMAGE_PERFORADORA, Texture.class);
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        movimientoOscilante.actualizarMovimiento(delta, sprite, jugador);
        animacionGrapadora.actualizarAnimacion(delta, sprite);
        animacionBaseEnemigos.flipearEnemigo(jugador, sprite);
    }

    @Override
    protected void actualizarKnockback(float delta) {
        movimientoOscilante.actualizarSoloKnockback(delta, sprite, true);
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        Animation<TextureRegion> animMuerteGrapadora = GestorDeAssets.animations.get("perforadoraMuerte");
        animacionBaseEnemigos.reproducirSonidoMuerteGenerico();
        animacionBaseEnemigos.iniciarAnimacionMuerte(animMuerteGrapadora);
        animacionBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimientoOscilante.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.15f) {
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
        return movimientoOscilante.getKnockbackTimer() > 0f;
    }

    @Override
    public MovimientoBaseEnemigos getMovimiento() {
        return movimientoOscilante;
    }
}

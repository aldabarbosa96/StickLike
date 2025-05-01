package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionCulo;
import com.sticklike.core.entidades.enemigos.ia.MovimientoCulo;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;
import com.sticklike.core.entidades.jugador.Jugador;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Enemigo Culo; puede aparecer con o sin ojo, lo que altera su vida y animación.
 * Esta clase extiende EnemigoBase y se encarga de implementar los comportamientos específicos.
 */
public class EnemigoCulo extends EnemigoBase {
    private Sprite spriteOjoAbierto;
    private Sprite spriteOjoCerrado;
    private MovimientoCulo movimientoCulo;
    private AnimacionCulo animacionCulo;
    private boolean tieneOjo = false;
    private boolean esConOjo;
    private static float velocidadBase = VEL_BASE_CULO;

    public EnemigoCulo(float x, float y, Jugador jugador) {
        super(jugador);
        this.vidaEnemigo = VIDA_ENEMIGOCULO;
        this.damageAmount = DANYO_CULO;
        this.coolDownDanyo = COOLDOWN_ENEMIGOCULO;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;
        esConOjo();
        sprite.setPosition(x, y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.movimientoCulo = new MovimientoCulo(velocidadBase, true);
        this.animacionCulo = new AnimacionCulo(this, animacionesBaseEnemigos, spriteOjoAbierto, spriteOjoCerrado);
        this.damageTexture = manager.get(DAMAGE_CULO_TEXTURE, Texture.class);

    }

    private void esConOjo() {
        float random = MathUtils.random(10);
        if (random >= 2.5f) {
            sprite = new Sprite(manager.get(ENEMIGO_CULO, Texture.class));
            sprite.setSize(36, 32);
            esConOjo = false;
        } else {
            tieneOjo = true;
            spriteOjoAbierto = new Sprite(manager.get(ENEMIGO_CULO_OJO, Texture.class));
            spriteOjoAbierto.setSize(40, 36);
            spriteOjoCerrado = new Sprite(manager.get(ENEMIGO_CULO_OJO_CERRADO, Texture.class));
            spriteOjoCerrado.setSize(40, 36);
            sprite = new Sprite(spriteOjoAbierto);
            // Con ojo, el enemigo tiene el doble de vida.
            this.vidaEnemigo = VIDA_ENEMIGOCULO * 2;
            esConOjo = true;
        }
    }

    @Override
    protected void actualizarMovimiento(float delta) {
        movimientoCulo.actualizarMovimiento(delta, sprite, jugador);
        animacionCulo.actualizarAnimacion(delta, sprite);
        animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);
    }


    @Override
    protected void actualizarKnockback(float delta) {
        movimientoCulo.actualizarSoloKnockback(delta, sprite, true);
    }

    @Override
    protected void iniciarAnimacionMuerte() {
        Animation<TextureRegion> animMuerteCulo;
        if (!esConOjo) {
            animMuerteCulo = GestorDeAssets.animations.get("muerteCulo");
        } else {
            animMuerteCulo = GestorDeAssets.animations.get("muerteCulo2");
        }
        animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteCulo);
        animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
    }

    @Override
    protected void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY) {
        movimientoCulo.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        float randomXP = (float)(Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.25f) {
            haSoltadoXP = true;
            return new ObjetoVida(posXMuerte, posYMuerte);
        }
        if (!haSoltadoXP && randomXP >= 15f) {
            haSoltadoXP = true;
            return new ObjetoXp(posXMuerte, posYMuerte);
        }
        return null;
    }

    public float getVelocidad() {
        return movimientoCulo.getVelocidadEnemigo();
    }

    public void setVelocidad(float nuevaVelocidad) {
        movimientoCulo.setVelocidadEnemigo(nuevaVelocidad);
    }

    public static void resetStats() {
        velocidadBase = VEL_BASE_CULO;
    }

    public boolean isTieneOjo() {
        return tieneOjo;
    }
}

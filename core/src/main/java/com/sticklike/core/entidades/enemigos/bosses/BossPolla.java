package com.sticklike.core.entidades.enemigos.bosses;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionBossPolla;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBossPolla;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Primer jefe enemigo del juego; gestiona su comportamiento, animaciones, daño y muerte.
 */

public class BossPolla implements Enemigo {
    private Sprite sprite;
    private Sprite spriteBocaAbierta;
    private Sprite spriteBocaCerrada;
    private Jugador jugador;
    private AnimacionesBaseEnemigos animaciones;
    private AnimacionBossPolla animacionBossPolla;
    private MovimientoBossPolla movimientoBoss;
    private float vida = 1350;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private float damageAmount = 9.5f;
    private float coolDownDanyo = 1.2f;
    private float temporizadorDanyo = 0f;
    private boolean estaMuerto = false;
    private final Texture damageTexture;
    private RenderBaseEnemigos renderBaseEnemigos;

    public BossPolla(Jugador jugador, float x, float y) {
        sprite = new Sprite(manager.get(BOSS_POLLA, Texture.class));
        sprite.setSize(90, 125);
        spriteBocaAbierta = new Sprite(manager.get(BOSS_POLLA, Texture.class));
        spriteBocaAbierta.setSize(90, 125);
        spriteBocaCerrada = new Sprite(manager.get(BOSS_POLLA_BOCACERRADA, Texture.class));
        spriteBocaCerrada.setSize(90, 125);
        sprite.setRegion(spriteBocaCerrada);
        sprite.setPosition(x, y);

        this.jugador = jugador;
        this.animaciones = new AnimacionesBaseEnemigos();
        this.movimientoBoss = new MovimientoBossPolla(true);
        this.animacionBossPolla = new AnimacionBossPolla(spriteBocaAbierta, spriteBocaCerrada, 0.5f, 2.5f);
        this.damageTexture = manager.get(DAMAGE_BOSS_POLLA_TEXTURE, Texture.class);
        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }


    @Override
    public void actualizar(float delta) {
        animaciones.actualizarFade(delta);

        if (vida > 0) {
            animaciones.actualizarParpadeo(sprite, delta);
            animaciones.actualizarFade(delta);
            movimientoBoss.actualizarMovimiento(delta, sprite, jugador);
            if (temporizadorDanyo > 0) {
                temporizadorDanyo -= delta;
            }
            animacionBossPolla.actualizarAnimacion(delta, sprite);
            animaciones.flipearEnemigo(jugador, sprite);
        } else {
            if (animaciones.enAnimacionMuerte()) {
                animaciones.actualizarAnimacionMuerteSinEscala(sprite, delta);
            }
        }
    }


    @Override
    public void renderizar(SpriteBatch batch) {
        if (vida > 0) {
            renderBaseEnemigos.dibujarEnemigos(batch, this);
        } else {
            if (animaciones.enAnimacionMuerte()) {
                sprite.draw(batch);
            }
        }
    }

    @Override
    public void reducirSalud(float amount) {
        vida -= amount;
        if (vida <= 0) {
            if (!animaciones.estaEnFade() && !animaciones.enAnimacionMuerte()) {
                Animation<TextureRegion> animMuerteBoss = GestorDeAssets.animations.get("bossPollaMuerte");
                animaciones.iniciarAnimacionMuerte(animMuerteBoss);
                animaciones.iniciarFadeMuerte(DURACION_FADE_BOSS);
                animaciones.activarParpadeo(sprite, DURACION_PARPADEO_ENEMIGO, damageTexture);
                GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPollaMuerte", 1);
            }
        }
    }

    @Override
    public boolean estaMuerto() {
        estaMuerto = true;
        return (vida <= 0 && !animaciones.estaEnFade());
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        Rectangle visualRect = sprite.getBoundingRectangle();
        return visualRect.overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
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
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void reseteaTemporizadorDanyo() {
        temporizadorDanyo = coolDownDanyo;
    }

    @Override
    public boolean puedeAplicarDanyo() {
        return vida > 0 && temporizadorDanyo <= 0;
    }

    @Override
    public boolean haSoltadoXP() {
        return haSoltadoXP;
    }

    @Override
    public void setProcesado(boolean procesado) {
        this.procesado = procesado;
    }

    @Override
    public boolean isProcesado() {
        return procesado;
    }

    @Override
    public void activarParpadeo(float duracion) {
        animaciones.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        // Para el boss no se le aplica knockback.
    }

    @Override
    public float getX() {
        return sprite.getX();
    }

    @Override
    public float getY() {
        return sprite.getY();
    }

    @Override
    public float getVida() {
        return vida;
    }

    @Override
    public float getDamageAmount() {
        return damageAmount;
    }

    public boolean isEstaMuerto() {
        return estaMuerto;
    }

    public AnimacionesBaseEnemigos getAnimaciones() {
        return animaciones;
    }

    @Override
    public void dispose() {
        sprite = null;
    }
}

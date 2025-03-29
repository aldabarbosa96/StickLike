package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoRegla;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Enemigo Regla; gestiona su comportamiento y daño.
 */

public class EnemigoRegla implements Enemigo {
    private Sprite sprite;
    private Jugador jugador;
    private float vidaEnemigo = VIDA_ENEMIGOREGLA;
    private MovimientoRegla movimientoRegla;
    private OrthographicCamera orthographicCamera;
    private float coolDownDanyo = COOLDOWN_ENEMIGOREGLA;
    private float temporizadorDanyo = TEMPORIZADOR_DANYO;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private float damageAmount = DANYO_REGLA;
    private RenderBaseEnemigos renderBaseEnemigos;

    private final Texture damageTexture;

    public EnemigoRegla(float x, float y, Jugador jugador, float velocidadEnemigo, OrthographicCamera orthographicCamera) {
        sprite = new Sprite(manager.get(ENEMIGO_REGLA_CRUZADA, Texture.class));
        sprite.setSize(23, 23);
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoRegla = new MovimientoRegla(velocidadEnemigo, 666, orthographicCamera, true);
        this.orthographicCamera = orthographicCamera;
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.damageTexture = manager.get(DAMAGE_REGLA_TEXTURE, Texture.class);
        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        if (vidaEnemigo > 0) {
            renderBaseEnemigos.dibujarEnemigos(batch, this);
        } else {
            if (animacionesBaseEnemigos.enAnimacionMuerte()) {
                sprite.draw(batch);
            }
        }
    }

    @Override
    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarFade(delta);

        if (vidaEnemigo > 0) {
            animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
            animacionesBaseEnemigos.actualizarFade(delta);
            movimientoRegla.actualizarMovimiento(delta, sprite, jugador);
            if (temporizadorDanyo > 0) {
                temporizadorDanyo -= delta;
            }
        } else {
            movimientoRegla.actualizarSoloKnockback(delta, sprite);
            if (animacionesBaseEnemigos.enAnimacionMuerte()) {
                animacionesBaseEnemigos.actualizarAnimacionMuerte(sprite, delta);
            }
        }
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(
            new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight)
        );
    }

    @Override
    public ObjetoVida sueltaObjetoXP() {
        float corazonONo = MathUtils.random(100);
        if (!haSoltadoXP && corazonONo <= 1f) {
            haSoltadoXP = true;
            return new ObjetoVida(getX(), getY());
        }
        return null;
    }

    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            if (!animacionesBaseEnemigos.estaEnFade() && !animacionesBaseEnemigos.enAnimacionMuerte()) {
                Animation<TextureRegion> animMuerteRegla = GestorDeAssets.animations.get("reglaMuerte");
                animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteRegla);
                animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                activarParpadeo(DURACION_PARPADEO_ENEMIGO);
            }
        }
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        movimientoRegla.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public float getVida() {
        return vidaEnemigo;
    }

    @Override
    public float getDamageAmount() {
        return damageAmount;
    }

    @Override
    public AnimacionesBaseEnemigos getAnimaciones() {
        return animacionesBaseEnemigos;
    }

    public void setDamageAmount(float damage) {
        this.damageAmount = damage;
    }

    @Override
    public boolean haSoltadoXP() {
        return haSoltadoXP;
    }

    @Override
    public boolean puedeAplicarDanyo() {
        return vidaEnemigo > 0 && temporizadorDanyo <= 0;
    }

    @Override
    public void reseteaTemporizadorDanyo() {
        temporizadorDanyo = coolDownDanyo;
    }

    @Override
    public boolean estaMuerto() {
        return (vidaEnemigo <= 0 && !animacionesBaseEnemigos.estaEnFade());
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
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public boolean isProcesado() {
        return procesado;
    }

    @Override
    public void setProcesado(boolean procesado) {
        this.procesado = procesado;
    }

    @Override
    public void activarParpadeo(float duracion) {
        animacionesBaseEnemigos.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public void dispose() {
        sprite = null;
    }

}

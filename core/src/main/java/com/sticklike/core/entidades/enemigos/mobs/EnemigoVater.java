package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionVater;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoVater;
import com.sticklike.core.entidades.pools.RectanglePoolManager;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoOro;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoPowerUp;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class EnemigoVater implements Enemigo {
    private Sprite sprite;
    private Sprite spriteTapaLevantada;
    private Sprite spriteTapaBajada;
    private Texture damageTexture;
    private float vidaEnemigo;
    private float damageAmount;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private Jugador jugador;
    private MovimientoVater movimientoVater;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private AnimacionVater animacionVater;       // Si tuvieras animaciones específicas (apertura de tapa, etc.)
    private RenderBaseEnemigos renderBaseEnemigos;
    private float coolDownDanyo;
    private float temporizadorDanyo;
    private Float posXMuerte = null;
    private Float posYMuerte = null;
    private boolean mostrandoDamageSprite = false;
    private float damageSpriteTimer = 0f;
    private boolean deathAnimationTriggered = false;

    public EnemigoVater(float x, float y, Jugador jugador) {
        spriteTapaBajada = new Sprite(manager.get(ENEMIGO_VATER, Texture.class));
        spriteTapaLevantada = new Sprite(manager.get(ENEMIGO_VATER2, Texture.class));
        damageTexture = manager.get(DAMAGE_VATER_TEXTURE, Texture.class);

        sprite = new Sprite(spriteTapaLevantada);
        sprite.setSize(52, 77.5f);
        sprite.setPosition(x, y);

        this.jugador = jugador;
        this.vidaEnemigo = VIDA_ENEMIGO_VATER;
        this.damageAmount = DANYO_VATER;
        this.coolDownDanyo = COOLDOWN_VATER;
        this.temporizadorDanyo = TEMPORIZADOR_DANYO;

        movimientoVater = new MovimientoVater(true);
        animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        animacionVater = new AnimacionVater(this, animacionesBaseEnemigos, spriteTapaLevantada, spriteTapaBajada);

        renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    @Override
    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarFade(delta);

        if (vidaEnemigo > 0) {
            movimientoVater.actualizarMovimiento(delta, sprite, jugador);
            animacionVater.actualizarAnimacion(delta, sprite);
            animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);

            if (temporizadorDanyo > 0) {
                temporizadorDanyo -= delta;
            }

        } else {
            movimientoVater.actualizarSoloKnockback(delta, sprite, true);

            if (mostrandoDamageSprite) {
                damageSpriteTimer -= delta;
                sprite.setTexture(damageTexture);
                if (damageSpriteTimer <= 0) {
                    mostrandoDamageSprite = false;
                    if (!deathAnimationTriggered) {
                        Animation<TextureRegion> animMuerteVater = GestorDeAssets.animations.get("vaterMuerte");
                        animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
                        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteVater);
                        animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                        deathAnimationTriggered = true;
                    }
                }
            } else {
                if (animacionesBaseEnemigos.enAnimacionMuerte()) {
                    animacionesBaseEnemigos.actualizarAnimacionMuerte(sprite, delta);
                }
            }
        }

        animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
    }


    @Override
    public void renderizar(SpriteBatch batch) {
        if (vidaEnemigo > 0 || mostrandoDamageSprite) {
            renderBaseEnemigos.dibujarEnemigos(batch, this);
        } else {
            if (animacionesBaseEnemigos.enAnimacionMuerte()) {
                sprite.draw(batch);
            }
        }
    }

    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            if (posXMuerte == null || posYMuerte == null) {
                posXMuerte = sprite.getX();
                posYMuerte = sprite.getY();
            }
            if (!mostrandoDamageSprite && !deathAnimationTriggered) {
                mostrandoDamageSprite = true;
                damageSpriteTimer = DAMAGE_SPRITE_MUERTE_TIMER;
            }
        }
    }

    @Override
    public boolean estaMuerto() {
        return (vidaEnemigo <= 0 && !animacionesBaseEnemigos.enAnimacionMuerte() && !animacionesBaseEnemigos.estaEnParpadeo());
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        Rectangle projectileRect = RectanglePoolManager.obtenerRectangulo(projectileX, projectileY, projectileWidth, projectileHeight);
        boolean overlaps = sprite.getBoundingRectangle().overlaps(projectileRect);
        RectanglePoolManager.liberarRectangulo(projectileRect);
        return overlaps;
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

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        movimientoVater.aplicarKnockback(fuerza, dirX, dirY);
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
        return vidaEnemigo;
    }

    @Override
    public float getDamageAmount() {
        return damageAmount;
    }

    @Override
    public boolean puedeAplicarDanyo() {
        return (vidaEnemigo > 0 && temporizadorDanyo <= 0);
    }

    @Override
    public void reseteaTemporizadorDanyo() {
        temporizadorDanyo = coolDownDanyo;
    }

    @Override
    public boolean haSoltadoXP() {
        return haSoltadoXP;
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
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void activarParpadeo(float duracion) {
        animacionesBaseEnemigos.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public boolean isMostrandoDamageSprite() {
        return mostrandoDamageSprite;
    }

    @Override
    public AnimacionesBaseEnemigos getAnimaciones() {
        return animacionesBaseEnemigos;
    }

    public float getFadeAlpha() {
        return animacionesBaseEnemigos.getAlphaActual();
    }

    @Override
    public void dispose() {
        sprite = null;
    }
}

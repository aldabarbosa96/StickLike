package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionVater;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoVater;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoOro;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoPowerUp;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;


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
    private boolean procesado = false; // Para evitar procesamiento múltiple en un mismo frame
    private Jugador jugador;
    private MovimientoVater movimientoVater;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private AnimacionVater animacionVater;
    private RenderBaseEnemigos renderBaseEnemigos;
    private float coolDownDanyo;
    private float temporizadorDanyo;

    public EnemigoVater(float x, float y, Jugador jugador) {
        spriteTapaBajada = new Sprite(manager.get(ENEMIGO_VATER, Texture.class));
        spriteTapaLevantada = new Sprite(manager.get(ENEMIGO_VATER2, Texture.class));
        damageTexture = manager.get(DAMAGE_VATER_TEXTURE, Texture.class);

        sprite = new Sprite(spriteTapaLevantada);
        sprite.setSize(50, 75);
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
        animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
        animacionesBaseEnemigos.actualizarFade(delta);
        movimientoVater.actualizarMovimiento(delta, sprite, jugador);
        animacionVater.actualizarAnimacion(delta, sprite);
        animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);

        if (temporizadorDanyo > 0) {
            temporizadorDanyo -= delta;
        }

    }

    @Override
    public void renderizar(SpriteBatch batch) {
        renderBaseEnemigos.dibujarEnemigos(batch, this);
    }


    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            if (!animacionesBaseEnemigos.estaEnFade()) {
                animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                activarParpadeo(DURACION_PARPADEO_ENEMIGO);
            }
        }
    }

    @Override
    public void activarParpadeo(float duracion) {
        animacionesBaseEnemigos.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        if (jugador.getCacasRecogidas() >= 1) return null;
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.25f) {
            haSoltadoXP = true;
            return new ObjetoVida(this.getX(), this.getY());
        }
        if (!haSoltadoXP && randomXP >= 5f) {
            haSoltadoXP = true;
            return new ObjetoOro(this.getX(), this.getY());
        } else {
            haSoltadoXP = true;
            return new ObjetoPowerUp(this.getX(), this.getY());
        }
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        movimientoVater.aplicarKnockback(fuerza, dirX, dirY);
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
    public boolean haSoltadoXP() {
        return haSoltadoXP;
    }

    @Override
    public boolean puedeAplicarDanyo() {
        return temporizadorDanyo <= 0;
    }

    @Override
    public void reseteaTemporizadorDanyo() {
        temporizadorDanyo = coolDownDanyo;
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
    public void dispose() {
        sprite = null;
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

    public float getFadeAlpha() {
        return animacionesBaseEnemigos.getAlphaActual();
    }
}

package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.Boost;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class Destructibles implements Enemigo {
    private Sprite sprite;
    private float vidaDestructible = VIDA_DESTRUCTIBLE;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private RenderBaseEnemigos renderBaseEnemigos;
    private Jugador jugador;
    private Texture damageTexture;

    public Destructibles(float x, float y, Jugador jugador, RenderBaseEnemigos renderBaseEnemigos) {
        this.sprite = new Sprite(manager.get(DESTRUCTIBLE, Texture.class));
        this.damageTexture = manager.get(DESTRUCTIBLE_DMG, Texture.class);
        sprite.setSize(ANCHO_DESTRUCT, ALTO_DESTRUCT);
        sprite.setPosition(x, y);
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.renderBaseEnemigos = renderBaseEnemigos;
    }

    @Override
    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
        animacionesBaseEnemigos.actualizarFade(delta);
        animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        renderBaseEnemigos.dibujarEnemigos(batch, this);
    }

    @Override
    public void reducirSalud(float amount) {
        vidaDestructible -= amount;
        if (vidaDestructible <= 0) {
            if (!animacionesBaseEnemigos.estaEnFade()) {
                animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                activarParpadeo(DURACION_PARPADEO_ENEMIGO);
            }
        }
    }

    public Boost sueltaBoost() {
        Boost.BoostType[] tipos = Boost.BoostType.values();
        int indiceAleatorio = MathUtils.random(tipos.length - 1);
        Boost.BoostType tipo = tipos[indiceAleatorio];

        float duracion = 10f;

        Texture boostTexture = null;
        switch (tipo) {
            case VELOCIDAD:
                boostTexture = manager.get(ICONO_VEL_MOV, Texture.class);
                break;
            case ATAQUE:
                boostTexture = manager.get(ICONO_FUERZA, Texture.class);
                break;
            case MUNICION:
                boostTexture = manager.get(ICONO_PROYECTILES, Texture.class);
                break;
            case INVULNERABILIDAD:
                boostTexture = manager.get(ICONO_RESISTENCIA, Texture.class);
                break;
            default:
                break;
        }
        return new Boost(boostTexture,duracion,tipo,getX(),getY());
    }

    @Override
    public boolean estaMuerto() {
        return (vidaDestructible <= 0 && !animacionesBaseEnemigos.estaEnFade());
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
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        return null; // todo --> se maneja desde el método sueltaBoost para mejor claridad del código (sirven para lo mismo)
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void reseteaTemporizadorDanyo() {
    }

    @Override
    public boolean puedeAplicarDanyo() {
        return false;
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
        animacionesBaseEnemigos.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public void dispose() {
        sprite = null;
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {

    }

    @Override
    public float getVida() {
        return vidaDestructible;
    }

    @Override
    public float getDamageAmount() {
        return 0;
    }

    @Override
    public AnimacionesBaseEnemigos getAnimaciones() {
        return animacionesBaseEnemigos;
    }
}

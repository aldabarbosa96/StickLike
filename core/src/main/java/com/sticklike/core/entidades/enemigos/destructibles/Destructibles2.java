package com.sticklike.core.entidades.enemigos.destructibles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoOro;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoPowerUp;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.pools.RectanglePoolManager;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class Destructibles2 implements Enemigo {
    private Sprite sprite;
    private float vidaDestructible = VIDA_DESTRUCTIBLE2;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private RenderBaseEnemigos renderBaseEnemigos;
    private Texture damageTexture;

    public Destructibles2(float x, float y, RenderBaseEnemigos renderBaseEnemigos) {
        this.sprite = new Sprite(manager.get(DESTRUCTIBLE_LATA, Texture.class));
        this.damageTexture = manager.get(DESTRUCTIBLE_LATA_DMG, Texture.class);
        sprite.setSize(ANCHO_DESTRUCT_LATA, ALTO_DESTRUCT_LATA);
        sprite.setPosition(x, y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.renderBaseEnemigos = renderBaseEnemigos;
    }

    @Override
    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
        animacionesBaseEnemigos.actualizarFade(delta);
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
        Rectangle projectileRect = RectanglePoolManager.obtenerRectangulo(projectileX, projectileY, projectileWidth, projectileHeight);
        boolean overlaps = sprite.getBoundingRectangle().overlaps(projectileRect);
        RectanglePoolManager.liberarRectangulo(projectileRect);
        return overlaps;
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        int dropChance = MathUtils.random(99);

        if (dropChance < 45) {
            return new ObjetoOro(getX(), getY());
        } else if (dropChance < 90) {
            return new ObjetoVida(getX(), getY());
        } else {
            return new ObjetoPowerUp(getX(), getY());
        }
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

    @Override
    public boolean isMostrandoDamageSprite() {
        return false;
    }
}

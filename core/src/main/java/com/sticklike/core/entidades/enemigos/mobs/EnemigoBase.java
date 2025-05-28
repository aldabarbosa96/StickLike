package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionBaseEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.pools.RectanglePoolManager;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;

/**
 * Clase abstracta que implementa la interfaz Enemigo.
 * Centraliza la lógica común a todos los enemigos (actualización, renderizado, reducción de salud, manejo de daño, efectos visuales, temporizadores, etc.)
 * y define métodos abstractos para aquellos comportamientos que varían entre distintos tipos.
 */
public abstract class EnemigoBase implements Enemigo {
    protected Sprite sprite;
    protected float vidaEnemigo;
    protected float damageAmount;
    protected float temporizadorDanyo;
    protected float coolDownDanyo;
    protected boolean haSoltadoXP = false;
    protected boolean procesado = false;
    protected boolean mostrandoDamageSprite = false;
    protected float damageSpriteTimer = 0f;
    protected boolean deathAnimationTriggered = false;
    protected Float posXMuerte = null;
    protected Float posYMuerte = null;
    protected AnimacionBaseEnemigos animacionBaseEnemigos;
    protected RenderBaseEnemigos renderBaseEnemigos;
    protected Jugador jugador;
    protected Texture damageTexture;
    protected static final float DAMAGE_SPRITE_MUERTE_TIMER = 0.08f;

    public EnemigoBase(Jugador jugador) {
        this.jugador = jugador;
        this.animacionBaseEnemigos = new AnimacionBaseEnemigos();
        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    @Override
    public void actualizar(float delta) {
        // Actualiza la animación de fade (común a todos los enemigos)
        animacionBaseEnemigos.actualizarFade(delta);

        // Si el enemigo sigue vivo, actualiza su movimiento específico
        if (vidaEnemigo > 0) {
            actualizarMovimiento(delta);
            if (temporizadorDanyo > 0) {
                temporizadorDanyo -= delta;
            }
        } else { // Cuando la vida es <= 0, se activa el knockback y la animación de muerte
            actualizarKnockback(delta);
            if (mostrandoDamageSprite) {
                damageSpriteTimer -= delta;
                sprite.setTexture(damageTexture);
                if (damageSpriteTimer <= 0) {
                    mostrandoDamageSprite = false;
                    if (!deathAnimationTriggered) {
                        iniciarAnimacionMuerte();
                        deathAnimationTriggered = true;
                    }
                }
            } else if (animacionBaseEnemigos.enAnimacionMuerte()) {
                animacionBaseEnemigos.actualizarAnimacionMuerte(sprite, delta);
            }
        }

        // Actualizamos el efecto de parpadeo
        animacionBaseEnemigos.actualizarParpadeo(sprite, delta);
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        if (vidaEnemigo > 0 || mostrandoDamageSprite) {
            renderBaseEnemigos.dibujarEnemigos(batch, this);
        } else {
            if (animacionBaseEnemigos.enAnimacionMuerte()) {
                sprite.draw(batch);
            }
        }
    }

    @Override
    public void reducirSalud(float amount) {
        if (vidaEnemigo <= 0) {
            return;
        }
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            // Guardamos la posición de la muerte si aún no se ha fijado
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
        return (vidaEnemigo <= 0 && !animacionBaseEnemigos.enAnimacionMuerte() && !animacionBaseEnemigos.estaEnParpadeo());
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        Rectangle projectileRect = RectanglePoolManager.obtenerRectangulo(projectileX, projectileY, projectileWidth, projectileHeight);
        boolean overlaps = sprite.getBoundingRectangle().overlaps(projectileRect);
        RectanglePoolManager.liberarRectangulo(projectileRect);
        return overlaps;
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
    public void activarParpadeo(float duracion) {
        animacionBaseEnemigos.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        aplicarKnockbackEnemigo(fuerza, dirX, dirY);
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
    public AnimacionBaseEnemigos getAnimaciones() {
        return animacionBaseEnemigos;
    }

    @Override
    public boolean isMostrandoDamageSprite() {
        return mostrandoDamageSprite;
    }

    @Override
    public void dispose() {
        sprite = null;
    }

    // Métodos abstractos que cada subclase deberá implementar

    protected abstract void actualizarMovimiento(float delta);

    protected abstract void actualizarKnockback(float delta);

    protected abstract void iniciarAnimacionMuerte();

    protected abstract void aplicarKnockbackEnemigo(float fuerza, float dirX, float dirY);

    @Override
    public abstract ObjetosXP sueltaObjetoXP();

}

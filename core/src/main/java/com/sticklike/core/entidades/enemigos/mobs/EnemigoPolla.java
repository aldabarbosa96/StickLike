package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoPolla;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Enemigo Polla; gestiona su comportamiento, daño.
 */

public class EnemigoPolla implements Enemigo {
    private Sprite sprite;
    private Jugador jugador;
    private float vidaEnemigo = VIDA_ENEMIGO_POLLA;
    private float coolDownDanyo = COOLDOWN_POLLA;
    private float temporizadorDanyo = TEMPORIZADOR_DANYO;
    private MovimientoPolla movimientoPolla;
    private static float velocidadBase = VEL_BASE_POLLA;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private float damageAmount = DANYO_POLLA;
    private final Texture damageTexture;
    private RenderBaseEnemigos renderBaseEnemigos;

    public EnemigoPolla(float x, float y, Jugador jugador, float velocidadEnemigo) {
        sprite = new Sprite(manager.get(ENEMIGO_POLLA, Texture.class));
        sprite.setSize(26.5f, 26.5f);
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoPolla = new MovimientoPolla(velocidadBase, 0.75f, 25f, true);
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.damageTexture = manager.get(DAMAGE_POLLA_TEXTURE, Texture.class);
        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    @Override
    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarFade(delta);

        if (vidaEnemigo > 0) {
            movimientoPolla.actualizarMovimiento(delta, sprite, jugador);
            animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
            animacionesBaseEnemigos.actualizarFade(delta);
            if (temporizadorDanyo > 0) {
                temporizadorDanyo -= delta;
            }
            animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);
        } else {
            movimientoPolla.actualizarSoloKnockback(delta, sprite);
            if (animacionesBaseEnemigos.enAnimacionMuerte()) {
                animacionesBaseEnemigos.actualizarAnimacionMuerte(sprite, delta);
            }
        }
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
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            if (!animacionesBaseEnemigos.estaEnFade() && !animacionesBaseEnemigos.enAnimacionMuerte()) {
                Animation<TextureRegion> animMuertePolla = GestorDeAssets.animations.get("muertePolla");
                animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuertePolla);
                animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                activarParpadeo(DURACION_PARPADEO_ENEMIGO);
                animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
            }
        }
    }

    @Override
    public boolean estaMuerto() {
        return vidaEnemigo <= 0 && !animacionesBaseEnemigos.estaEnFade();
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
        return sprite.getBoundingRectangle().overlaps(
            new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight)
        );
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.25f) {
            haSoltadoXP = true;
            return new ObjetoVida(getX(), getY());
        }
        if (!haSoltadoXP && randomXP >= 20f) {
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
        return vidaEnemigo > 0 && temporizadorDanyo <= 0;
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
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        movimientoPolla.aplicarKnockback(fuerza, dirX, dirY);
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

    public static void resetStats() {
        velocidadBase = VEL_BASE_POLLA;
    }

    public static void setVelocidadBase(float nuevaVelocidadBase) {
        velocidadBase = nuevaVelocidadBase;
    }

    public static float getVelocidadBase() {
        return velocidadBase;
    }

    public void setVelocidad(float nuevaVelocidad) {
        movimientoPolla.setVelocidadEnemigo(nuevaVelocidad);
    }

    public MovimientoPolla getMovimientoPolla() {
        return movimientoPolla;
    }

    public float getFadeAlpha() {
        return animacionesBaseEnemigos.getAlphaActual();
    }

    public AnimacionesBaseEnemigos getAnimacionesEnemigos() {
        return animacionesBaseEnemigos;
    }

    @Override
    public void dispose() {
        sprite = null;
    }
}

package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionCulo;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoCulo;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Enemigo Culo; puede aparecer con o sin ojo, alterando su vida y animación. Gestiona su comportamiento y daño.
 */

public class EnemigoCulo implements Enemigo {  // TODO --> (manejar el cambio de textura en animacionesEnemigos en un futuro)
    private Sprite sprite;
    private Sprite spriteOjoAbierto;
    private Sprite spriteOjoCerrado;
    private Jugador jugador;
    private float vidaEnemigo = VIDA_ENEMIGOCULO;
    private MovimientoCulo movimientoCulo;
    private float coolDownDanyo = COOLDOWN_ENEMIGOCULO;
    private float temporizadorDanyo = TEMPORIZADOR_DANYO;
    private static float velocidadBase = VEL_BASE_CULO;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private AnimacionCulo animacionCulo;
    private float damageAmount = DANYO_CULO;
    private boolean tieneOjo = false;
    private final Texture damageTexture;
    private boolean recibeImpacto = false; // puede ser útil en un futuro
    private RenderBaseEnemigos renderBaseEnemigos;
    private float deathPosX, deathPosY;
    private boolean deathPosCaptured = false;

    public EnemigoCulo(float x, float y, Jugador jugador) {
        esConOjo();
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoCulo = new MovimientoCulo(velocidadBase, true);
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.animacionCulo = new AnimacionCulo(this, animacionesBaseEnemigos, spriteOjoAbierto, spriteOjoCerrado);
        this.damageTexture = manager.get(DAMAGE_CULO_TEXTURE, Texture.class);
        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    private void esConOjo() {
        float random = MathUtils.random(10);
        if (random >= 2.5f) {
            sprite = new Sprite(manager.get(ENEMIGO_CULO, Texture.class));
            sprite.setSize(28, 24);
        } else {
            tieneOjo = true;
            spriteOjoAbierto = new Sprite(manager.get(ENEMIGO_CULO_OJO, Texture.class));
            spriteOjoAbierto.setSize(32, 28);
            spriteOjoCerrado = new Sprite(manager.get(ENEMIGO_CULO_OJO_CERRADO, Texture.class));
            spriteOjoCerrado.setSize(32, 28);
            sprite = new Sprite(spriteOjoAbierto);
            vidaEnemigo = VIDA_ENEMIGOCULO * 2;
        }
    }

    @Override
    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarFade(delta);

        if (vidaEnemigo > 0) {
            // Mientras el enemigo esté vivo se aplica el movimiento completo
            movimientoCulo.actualizarMovimiento(delta, sprite, jugador);
            animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
            animacionesBaseEnemigos.actualizarFade(delta);
            if (temporizadorDanyo > 0) {
                temporizadorDanyo -= delta;
            }
            animacionCulo.actualizarAnimacion(delta, sprite);
            animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);
        } else {
            movimientoCulo.actualizarSoloKnockback(delta, sprite);

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
    public void activarParpadeo(float duracion) {
        animacionesBaseEnemigos.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        recibeImpacto = true;
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.25f) {
            haSoltadoXP = true;
            return new ObjetoVida(this.getX(), this.getY());
        }
        if (!haSoltadoXP && randomXP >= 15f) {
            haSoltadoXP = true;
            return new ObjetoXp(this.getX(), this.getY());
        }
        return null;
    }

    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            if (!animacionesBaseEnemigos.estaEnFade() && !animacionesBaseEnemigos.enAnimacionMuerte()) {
                Animation<TextureRegion> animMuerteCulo = GestorDeAssets.animations.get("muerteCulo");
                animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteCulo);
                animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                activarParpadeo(DURACION_PARPADEO_ENEMIGO);
            }
        }
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        movimientoCulo.aplicarKnockback(fuerza, dirX, dirY);
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
        return (vidaEnemigo <= 0 && !animacionesBaseEnemigos.enAnimacionMuerte() && !animacionesBaseEnemigos.estaEnFade());
    }


    @Override
    public void dispose() {
        sprite = null;
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

    public float getVelocidad() {
        return movimientoCulo.getVelocidadEnemigo();
    }

    public void setVelocidad(float nuevaVelocidad) {
        movimientoCulo.setVelocidadEnemigo(nuevaVelocidad);
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

    public static void resetStats() {
        velocidadBase = VEL_BASE_CULO;
    }

    public void setDamageAmount(float damageAmount) {
        this.damageAmount = damageAmount;
    }

    public float getFadeAlpha() {
        return animacionesBaseEnemigos.getAlphaActual();
    }

    public boolean isTieneOjo() {
        return tieneOjo;
    }
}

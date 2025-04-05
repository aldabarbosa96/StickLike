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
    private boolean esConOjo;
    private Float posXMuerte = null;
    private Float posYMuerte = null;
    private boolean mostrandoDamageSprite = false;
    private float damageSpriteTimer = 0f;
    private boolean deathAnimationTriggered = false;


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
            sprite.setSize(32, 28);
            esConOjo = false;
        } else {
            tieneOjo = true;
            spriteOjoAbierto = new Sprite(manager.get(ENEMIGO_CULO_OJO, Texture.class));
            spriteOjoAbierto.setSize(36, 32);
            spriteOjoCerrado = new Sprite(manager.get(ENEMIGO_CULO_OJO_CERRADO, Texture.class));
            spriteOjoCerrado.setSize(36, 32);
            sprite = new Sprite(spriteOjoAbierto);
            vidaEnemigo = VIDA_ENEMIGOCULO * 2;
            esConOjo = true;
        }
    }

    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarFade(delta);

        if (vidaEnemigo > 0) {
            movimientoCulo.actualizarMovimiento(delta, sprite, jugador);
        } else {
            movimientoCulo.actualizarSoloKnockback(delta, sprite, true);
        }

        animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);

        if (animacionesBaseEnemigos.enAnimacionMuerte()) {
            animacionesBaseEnemigos.actualizarAnimacionMuerte(sprite, delta);
        }

        if (vidaEnemigo > 0) {
            if (temporizadorDanyo > 0) temporizadorDanyo -= delta;
            animacionCulo.actualizarAnimacion(delta, sprite);
            animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);
        }

        // Si el enemigo ya está muerto, mostramos el sprite de daño durante 0.09 segundos
        if (vidaEnemigo <= 0 && mostrandoDamageSprite) {
            damageSpriteTimer -= delta;
            sprite.setTexture(damageTexture);
            if (damageSpriteTimer <= 0) {
                mostrandoDamageSprite = false;
                if (!deathAnimationTriggered) {
                    Animation<TextureRegion> animMuerteCulo;
                    if (!esConOjo) {
                        animMuerteCulo = GestorDeAssets.animations.get("muerteCulo");
                    } else {
                        animMuerteCulo = GestorDeAssets.animations.get("muerteCulo2");
                    }
                    animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
                    animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteCulo);
                    animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                    deathAnimationTriggered = true;
                }
            }
        }
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
            return new ObjetoVida(posXMuerte, posYMuerte);
        }
        if (!haSoltadoXP && randomXP >= 15f) {
            haSoltadoXP = true;
            return new ObjetoXp(posXMuerte, posYMuerte);
        }
        return null;
    }

    @Override
    public void reducirSalud(float amount) {
        if (vidaEnemigo <= 0) return; // Ya está muerto, evitamos procesar más daño.
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            // Guardamos la posición de la muerte (si aún no se ha hecho)
            if (posXMuerte == null || posYMuerte == null) {
                posXMuerte = sprite.getX();
                posYMuerte = sprite.getY();
            }
            // Activamos el estado de sprite de daño si aún no se ha iniciado la animación de muerte
            if (!mostrandoDamageSprite && !deathAnimationTriggered) {
                mostrandoDamageSprite = true;
                damageSpriteTimer = 0.05f;
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
        return (vidaEnemigo <= 0 && !animacionesBaseEnemigos.enAnimacionMuerte()  && !animacionesBaseEnemigos.estaEnParpadeo());
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
    public boolean isMostrandoDamageSprite() {
        return mostrandoDamageSprite;
    }
}

package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoCulo;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.entidades.pools.RectanglePoolManager;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

/**
 * Enemigo Alarma; puede aparecer Crono (Cosmo) o Alarma (Wanda), alterando su vida y animación. Gestiona su comportamiento y daño.
 */
public class EnemigoAlarma implements Enemigo {

    private Sprite sprite;
    private Jugador jugador;
    private float vidaEnemigo = VIDA_ENEMIGO_ALARMA;
    private MovimientoCulo movimientoAlarma;
    private float cooldownDanyo = COOLDOWN_ENEMIGOCULO;
    private float tempDanyo = TEMPORIZADOR_DANYO;
    private static float velocidadBase = VEL_BASE_ALARMA;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private float damageAmount = DANYO_CULO;
    private boolean recibeImpacto = false;
    private RenderBaseEnemigos renderBaseEnemigos;
    private boolean esCrono;   // Determina si es la versión verde (true) o rosa (false)
    private Float posXMuerte = null;
    private Float posYMuerte = null;
    private boolean mostrandoDamageSprite = false;
    private float damageSpriteTimer = 0f;
    private boolean deathAnimationTriggered = false;

    private final Texture damageTexture;

    public EnemigoAlarma(float x, float y, Jugador jugador) {
        sprite = new Sprite(escogerTextura());
        sprite.setPosition(x, y);
        sprite.setSize(38, 38);

        this.jugador = jugador;
        this.movimientoAlarma = new MovimientoCulo(velocidadBase, true);
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.damageTexture = manager.get(DAMAGE_ALARMA_TEXTURE, Texture.class);

        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    private Texture escogerTextura() {
        float texturaAleatoria = MathUtils.random(10);
        if (texturaAleatoria <= 5) {
            esCrono = false; // Rosa
            return manager.get(ENEMIGO_ALARMA, Texture.class);
        } else {
            esCrono = true;  // Verde
            return manager.get(ENEMIGO_ALARMA2, Texture.class);
        }
    }

    @Override
    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarFade(delta); // todo --> revisar si es necesario

        if (vidaEnemigo > 0) {
            // Lógica de movimiento y ataque mientras está vivo
            movimientoAlarma.actualizarMovimiento(delta, sprite, jugador);
            if (tempDanyo > 0) {
                tempDanyo -= delta;
            }
            animacionesBaseEnemigos.flipearEnemigo(jugador, sprite);

        } else {
            // Lógica de knockback y animación de muerte
            movimientoAlarma.actualizarSoloKnockback(delta, sprite, true);

            if (mostrandoDamageSprite) {
                damageSpriteTimer -= delta;
                sprite.setTexture(damageTexture);

                if (damageSpriteTimer <= 0) {
                    mostrandoDamageSprite = false;
                    if (!deathAnimationTriggered) {
                        Animation<TextureRegion> animMuerteAlarma;
                        // Cosmo => "alarmaMuerte", Wanda => "alarma2Muerte"
                        if (!esCrono) {
                            animMuerteAlarma = GestorDeAssets.animations.get("alarmaMuerte2");
                        } else {
                            animMuerteAlarma = GestorDeAssets.animations.get("alarmaMuerte");
                        }
                        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteAlarma);
                        animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                        animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
                        deathAnimationTriggered = true;
                    }
                }

            } else if (animacionesBaseEnemigos.enAnimacionMuerte()) {
                animacionesBaseEnemigos.actualizarAnimacionMuerte(sprite, delta);
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
        // Si ya está a 0 o menos, no duplicamos eventos
        if (vidaEnemigo <= 0) return;

        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            // Guardamos la posición de la muerte para el posible drop
            if (posXMuerte == null || posYMuerte == null) {
                posXMuerte = sprite.getX();
                posYMuerte = sprite.getY();
            }
            // Activamos el sprite de daño si aún no hemos lanzado la animación de muerte
            if (!mostrandoDamageSprite && !deathAnimationTriggered) {
                mostrandoDamageSprite = true;
                damageSpriteTimer = DAMAGE_SPRITE_MUERTE;
            }
        }
    }

    @Override
    public boolean estaMuerto() {
        return (vidaEnemigo <= 0 && !animacionesBaseEnemigos.enAnimacionMuerte()  && !animacionesBaseEnemigos.estaEnParpadeo());
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
        recibeImpacto = true;
        Rectangle projectileRect = RectanglePoolManager.obtenerRectangulo(projectileX, projectileY, projectileWidth, projectileHeight);
        boolean overlaps = sprite.getBoundingRectangle().overlaps(projectileRect);
        RectanglePoolManager.liberarRectangulo(projectileRect);
        return overlaps;
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
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void reseteaTemporizadorDanyo() {
        tempDanyo = cooldownDanyo;
    }

    @Override
    public boolean puedeAplicarDanyo() {
        return vidaEnemigo > 0 && tempDanyo <= 0;
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
        movimientoAlarma.aplicarKnockback(fuerza, dirX, dirY);
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

    @Override
    public boolean isMostrandoDamageSprite() {
        return mostrandoDamageSprite;
    }

    public boolean isEsCrono() {
        return esCrono;
    }
}

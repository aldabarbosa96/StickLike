package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionExamen;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoExamen;
import com.sticklike.core.entidades.pools.RectanglePoolManager;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class EnemigoExamen implements Enemigo {
    private Sprite sprite;
    private Jugador jugador;
    private float vidaEnemigo = VIDA_ENEMIGO_EXAMEN;
    private float damageAmount = DANYO_EXAMEN;
    private float coolDownDanyo = COOLDOWN_EXAMEN;
    private float temporizadorDanyo = TEMPORIZADOR_DANYO;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private AnimacionExamen animacionExamen;
    private MovimientoExamen movimientoExamen;
    private static float velocidadBase = VEL_BASE_EXAMEN;
    private final Texture damageTexture;
    private RenderBaseEnemigos renderBaseEnemigos;
    private boolean mostrandoDamageSprite = false;
    private float damageSpriteTimer = 0f;
    private boolean deathAnimationTriggered = false;

    private Float posXMuerte = null;
    private Float posYMuerte = null;

    public EnemigoExamen(float x, float y, Jugador jugador, float velocidadEnemigo) {
        this.jugador = jugador;
        sprite = new Sprite(manager.get(ENEMIGO_EXAMEN, Texture.class));
        sprite.setSize(42f, 44f);
        sprite.setPosition(x, y);
        this.movimientoExamen = new MovimientoExamen();
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.animacionExamen = new AnimacionExamen(animacionesBaseEnemigos, manager.get(ENEMIGO_EXAMEN, Texture.class), manager.get(ENEMIGO_EXAMEN2, Texture.class), 0.25f);

        setVelocidad(velocidadEnemigo);

        this.damageTexture = manager.get(DAMAGE_EXAMEN_TEXTURE, Texture.class);
        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    @Override
    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarFade(delta);

        if (vidaEnemigo > 0) {
            if (temporizadorDanyo > 0) {
                temporizadorDanyo -= delta;
            }
            if (movimientoExamen != null) {
                movimientoExamen.actualizarMovimiento(delta, sprite, jugador);
            }
            animacionExamen.actualizarAnimacion(delta, jugador, sprite);
        } else {
            if (movimientoExamen != null) {
                movimientoExamen.actualizarSoloKnockback(delta, sprite, true);
            }

            if (mostrandoDamageSprite) {
                damageSpriteTimer -= delta;
                sprite.setTexture(damageTexture);

                if (damageSpriteTimer <= 0) {
                    mostrandoDamageSprite = false;
                    if (!deathAnimationTriggered) {
                        // Dispara la animación de muerte
                        Animation<TextureRegion> animMuerteExamen = GestorDeAssets.animations.get("examenMuerte");
                        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteExamen);
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
        if (vidaEnemigo <= 0) return;

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
        } else {
            if (!animacionesBaseEnemigos.estaEnParpadeo()) {
                animacionesBaseEnemigos.activarParpadeo(sprite, DURACION_PARPADEO_ENEMIGO, damageTexture);
            }
        }
    }

    @Override
    public boolean estaMuerto() {
        return (vidaEnemigo <= 0 && !animacionesBaseEnemigos.enAnimacionMuerte() && !animacionesBaseEnemigos.estaEnParpadeo());
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
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.75f) {
            haSoltadoXP = true;
            return new ObjetoVida(posXMuerte, posYMuerte);
        }
        if (!haSoltadoXP && randomXP >= 30f) {
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
        if (movimientoExamen != null) {
            movimientoExamen.aplicarKnockback(fuerza, dirX, dirY);
        }
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

    public void setDamageAmount(float newDamageAmount) {
        this.damageAmount = newDamageAmount;
    }

    public static void resetStats() {
        velocidadBase = VEL_BASE_EXAMEN;
    }

    public static void setVelocidadBase(float nuevaVelocidadBase) {
        velocidadBase = nuevaVelocidadBase;
    }

    public static float getVelocidadBase() {
        return velocidadBase;
    }

    public void setVelocidad(float nuevaVelocidad) {
        if (movimientoExamen != null) {
            movimientoExamen.setVelocidadEnemigo(nuevaVelocidad);
        }
    }

    public AnimacionesBaseEnemigos getAnimacionesEnemigos() {
        return animacionesBaseEnemigos;
    }

    public MovimientoExamen getMovimientoExamen() {
        return movimientoExamen;
    }

    public float getFadeAlpha() {
        return animacionesBaseEnemigos.getAlphaActual();
    }

    @Override
    public void dispose() {
        sprite = null;
    }
}

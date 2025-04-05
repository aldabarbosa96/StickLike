package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoRegla;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Enemigo Regla; gestiona su comportamiento y daño.
 */
public class EnemigoRegla implements Enemigo {

    private Sprite sprite;
    private Jugador jugador;
    private float vidaEnemigo = VIDA_ENEMIGOREGLA;
    private MovimientoRegla movimientoRegla;
    private OrthographicCamera orthographicCamera;
    private float coolDownDanyo = COOLDOWN_ENEMIGOREGLA;
    private float temporizadorDanyo = TEMPORIZADOR_DANYO;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesBaseEnemigos animacionesBaseEnemigos;
    private float damageAmount = DANYO_REGLA;
    private RenderBaseEnemigos renderBaseEnemigos;

    private Float posXMuerte = null;
    private Float posYMuerte = null;


    // Variables para la lógica de mostrar la textura de daño
    private boolean mostrandoDamageSprite = false;
    private float damageSpriteTimer = 0f;
    private boolean deathAnimationTriggered = false;

    private final Texture damageTexture;

    public EnemigoRegla(float x, float y, Jugador jugador, float velocidadEnemigo, OrthographicCamera orthographicCamera) {
        sprite = new Sprite(manager.get(ENEMIGO_REGLA_CRUZADA, Texture.class));
        sprite.setSize(25, 25);
        sprite.setPosition(x, y);

        this.jugador = jugador;
        this.orthographicCamera = orthographicCamera;

        this.movimientoRegla = new MovimientoRegla(velocidadEnemigo, 666, orthographicCamera, true);
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.damageTexture = manager.get(DAMAGE_REGLA_TEXTURE, Texture.class);

        // Para dibujar con la misma lógica (sombras, fade, etc.)
        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    @Override
    public void actualizar(float delta) {
        // Actualiza el fade siempre (por si estuviera en fase de desvanecerse)
        animacionesBaseEnemigos.actualizarFade(delta);

        if (vidaEnemigo > 0) {
            // Mientras siga vivo, movemos y aplicamos parpadeo si corresponde
            animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
            movimientoRegla.actualizarMovimiento(delta, sprite, jugador);

            if (temporizadorDanyo > 0) {
                temporizadorDanyo -= delta;
            }

        } else {
            // Ya no movemos, pero puede haber knockback residual
            movimientoRegla.actualizarSoloKnockback(delta, sprite, true);

            // Mostramos la textura de daño antes de iniciar la animación de muerte
            if (mostrandoDamageSprite) {
                damageSpriteTimer -= delta;
                sprite.setTexture(damageTexture); // Forzamos la textura de daño

                if (damageSpriteTimer <= 0) {
                    mostrandoDamageSprite = false;

                    // Lanzamos la animación de muerte si no lo hicimos aún
                    if (!deathAnimationTriggered) {
                        Animation<TextureRegion> animMuerteRegla = GestorDeAssets.animations.get("reglaMuerte");
                        animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteRegla);
                        animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                        animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
                        deathAnimationTriggered = true;
                    }
                }

            } else if (animacionesBaseEnemigos.enAnimacionMuerte()) {
                // Si ya hemos disparado la animación, la actualizamos
                animacionesBaseEnemigos.actualizarAnimacionMuerte(sprite, delta);
            }
        }
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        // Si sigue vivo o está mostrando el sprite de daño
        if (vidaEnemigo > 0 || mostrandoDamageSprite) {
            renderBaseEnemigos.dibujarEnemigos(batch, this);

        } else {
            // Si está en animación de muerte, dibujamos el sprite con la animación
            if (animacionesBaseEnemigos.enAnimacionMuerte()) {
                sprite.draw(batch);
            }
        }
    }

    @Override
    public void reducirSalud(float amount) {
        // Si ya está muerto, evitamos duplicar eventos
        if (vidaEnemigo <= 0) return;

        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            // Guardamos la posición de muerte para drops
            if (posXMuerte == null || posYMuerte == null) {
                posXMuerte = sprite.getX();
                posYMuerte = sprite.getY();
            }
            // Activamos el sprite de daño si aún no disparó la animación de muerte
            if (!mostrandoDamageSprite && !deathAnimationTriggered) {
                mostrandoDamageSprite = true;
                damageSpriteTimer = 0.05f; // El tiempo que mostramos la textura de daño
            }
        }
    }

    @Override
    public boolean estaMuerto() {
        return vidaEnemigo <= 0 && !animacionesBaseEnemigos.enAnimacionMuerte() && !animacionesBaseEnemigos.estaEnParpadeo();
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetoVida sueltaObjetoXP() {
        // todo --> añadir variedades de droppeo
        float corazonONo = MathUtils.random(100);
        if (!haSoltadoXP && corazonONo <= 1f) {
            haSoltadoXP = true;
            return new ObjetoVida(posXMuerte, posYMuerte);
        }
        return null;
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        movimientoRegla.aplicarKnockback(fuerza, dirX, dirY);
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

    public void setDamageAmount(float damage) {
        this.damageAmount = damage;
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
    public void activarParpadeo(float duracion) {
        animacionesBaseEnemigos.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public void dispose() {
        sprite = null;
    }
}

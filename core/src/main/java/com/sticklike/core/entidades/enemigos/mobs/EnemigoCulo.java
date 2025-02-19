package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoCulo;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

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
    private AnimacionesEnemigos animacionesEnemigos;
    private float damageAmount = DANYO_CULO;
    private boolean tieneOjo = false;
    private boolean ojoCerrado = false;
    private float tiempoAcumulado = 0;
    private float tiempoParpadeo = 0.5f; // referente al pestañeo de los ojos
    private float duracionCerrado = 0.1f;
    private final Texture damageTexture;
    private boolean recibeImpacto = false; // puede ser útil

    public EnemigoCulo(float x, float y, Jugador jugador, float velocidadEnemigo) {
        esConOjo();
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoCulo = new MovimientoCulo(velocidadBase, true);
        this.animacionesEnemigos = new AnimacionesEnemigos();
        this.damageTexture = damageCuloTexture;
    }

    private void esConOjo() {
        float random = MathUtils.random(10);
        if (random >= 2.5f) {
            sprite = new Sprite(enemigoCulo);
            sprite.setSize(26, 22);
        } else {
            tieneOjo = true;
            spriteOjoAbierto = new Sprite(enemigoCuloOjo);
            spriteOjoAbierto.setSize(30, 26);
            spriteOjoCerrado = new Sprite(enemigoCuloOjoCerrado);
            spriteOjoCerrado.setSize(30, 26);
            sprite = new Sprite(spriteOjoAbierto);
            vidaEnemigo = VIDA_ENEMIGOCULO * 2;
        }
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        movimientoCulo.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        boolean mostrarSprite = (vidaEnemigo > 0) || animacionesEnemigos.estaEnFade();

        if (mostrarSprite) {
            Color originalColor = sprite.getColor().cpy();

            // Si el fade está activo, aplicamos el alfa del fade
            if (animacionesEnemigos.estaEnFade()) {
                float alphaFade = animacionesEnemigos.getAlphaActual();
                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, alphaFade);
            } else if (animacionesEnemigos.estaEnParpadeo()) {
                // Si está en parpadeo, se cambia la textura, pero el alfa se deja en 1
                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, 1);
            } else {
                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, 1);
            }

            sprite.draw(batch);
            animacionesEnemigos.restaurarColor(sprite, originalColor);
        }
    }


    @Override
    public void actualizar(float delta) {
        animacionesEnemigos.actualizarParpadeo(sprite, delta);
        animacionesEnemigos.actualizarFade(delta);
        movimientoCulo.actualizarMovimiento(delta, sprite, jugador);

        if (temporizadorDanyo > 0) {
            temporizadorDanyo -= delta;
        }

        if (tieneOjo && !animacionesEnemigos.estaEnParpadeo()) {
            tiempoAcumulado += delta;
            if (!ojoCerrado && tiempoAcumulado >= tiempoParpadeo) {
                sprite.setRegion(spriteOjoCerrado);
                ojoCerrado = true;
                tiempoAcumulado = 0;
            } else if (ojoCerrado && tiempoAcumulado >= duracionCerrado) {
                sprite.setRegion(spriteOjoAbierto);
                ojoCerrado = false;
                tiempoAcumulado = 0;
            }
        }

        boolean estaALaIzquierda = sprite.getX() + sprite.getWidth() / 2 > jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        if (sprite.isFlipX() != estaALaIzquierda) {
            sprite.flip(true, false);
        }

    }

    @Override
    public void activarParpadeo(float duracion) {
        animacionesEnemigos.activarParpadeo(sprite, duracion, damageTexture);
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
            if (!animacionesEnemigos.estaEnFade()) {
                animacionesEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                activarParpadeo(DURACION_PARPADEO_ENEMIGO);
            }
        }
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
    public boolean estaMuerto() {
        return (vidaEnemigo <= 0 && !animacionesEnemigos.estaEnFade());
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

    public static void resetStats() {
        velocidadBase = VEL_BASE_CULO;
    }

    public void setDamageAmount(float damageAmount) {
        this.damageAmount = damageAmount;
    }

    public float getFadeAlpha() {
        return animacionesEnemigos.getAlphaActual();
    }

    public AnimacionesEnemigos getAnimacionesEnemigos() {
        return animacionesEnemigos;
    }
}

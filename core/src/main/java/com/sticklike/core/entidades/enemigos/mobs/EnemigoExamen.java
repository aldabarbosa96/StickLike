package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionExamen;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoExamen;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;

import static com.sticklike.core.utilidades.GestorDeAssets.*;
import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Enemigo Examen; gestiona su comportamiento, daño y cambio de textura
 */

public class EnemigoExamen implements Enemigo {  // TODO --> (manejar el cambio de textura en animacionesEnemigos en un futuro)
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
    private float tiempoAcumulado = 0;
    private float tiempoCambio = 0.25f; // Tiempo entre cambios de frame
    private boolean usandoFrame2 = false;

    public EnemigoExamen(float x, float y, Jugador jugador, float velocidadEnemigo) {
        this.jugador = jugador;
        sprite = new Sprite(manager.get(ENEMIGO_EXAMEN, Texture.class));
        sprite.setSize(36f, 38f);
        sprite.setPosition(x, y);
        this.movimientoExamen = new MovimientoExamen();
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.animacionExamen = new AnimacionExamen(animacionesBaseEnemigos, manager.get(ENEMIGO_EXAMEN, Texture.class), manager.get(ENEMIGO_EXAMEN2, Texture.class), 0.25f);
        setVelocidad(velocidadEnemigo);
        this.damageTexture = manager.get(DAMAGE_EXAMEN_TEXTURE, Texture.class);
    }

    @Override
    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
        animacionesBaseEnemigos.actualizarFade(delta);

        if (temporizadorDanyo > 0) temporizadorDanyo -= delta;

        if (movimientoExamen != null) movimientoExamen.actualizarMovimiento(delta, sprite, jugador);

        animacionExamen.actualizarAnimacion(delta, jugador, sprite);
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        boolean mostrarSprite = (vidaEnemigo > 0) || animacionesBaseEnemigos.estaEnFade();
        if (mostrarSprite) {
            Color originalColor = sprite.getColor().cpy();
            if (animacionesBaseEnemigos.estaEnParpadeo()) {
                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, 1);
            } else if (animacionesBaseEnemigos.estaEnFade()) {
                float alphaFade = animacionesBaseEnemigos.getAlphaActual();
                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, alphaFade);
            } else {
                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, 1);
            }

            sprite.draw(batch);
            animacionesBaseEnemigos.restaurarColor(sprite, originalColor);
        }
    }

    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (!animacionesBaseEnemigos.estaEnParpadeo()) {
            animacionesBaseEnemigos.activarParpadeo(sprite, DURACION_PARPADEO_ENEMIGO, damageTexture);
        }
        if (vidaEnemigo <= 0) {
            if (!animacionesBaseEnemigos.estaEnFade()) {
                animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
            }
        }
    }

    @Override
    public boolean estaMuerto() {
        return (vidaEnemigo <= 0 && !animacionesBaseEnemigos.estaEnFade());
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
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY,
                                          float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(
            new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight)
        );
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 0.75f) {
            haSoltadoXP = true;
            return new ObjetoVida(getX(), getY());
        }
        if (!haSoltadoXP && randomXP >= 30f) {
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
        return temporizadorDanyo <= 0;
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

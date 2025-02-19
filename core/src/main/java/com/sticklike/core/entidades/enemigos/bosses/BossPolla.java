package com.sticklike.core.entidades.enemigos.bosses;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBossPolla;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.GestorDeAudio;
import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

/**
 * Primer jefe enemigo del juego; gestiona su comportamiento, animaciones, daño y muerte.
 */

public class BossPolla implements Enemigo {
    private Sprite sprite;
    private Sprite spriteBocaAbierta;
    private Sprite spriteBocaCerrada;
    private Jugador jugador;
    private AnimacionesEnemigos animaciones;
    private MovimientoBossPolla movimientoBoss;
    private float vida = 1250f;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private float damageAmount = 12.5f;
    private float coolDownDanyo = 1.2f;
    private float temporizadorDanyo = 0f;
    private boolean bocaCerrada = false;
    private float tiempoAcumuladoBoca = 0;
    private float tiempoBocaAbierta = 0.5f;
    private float duracionBocaCerrada = 2.5f;
    private boolean estaMuerto = false;
    private final Texture damageTexture;

    public BossPolla(Jugador jugador, float x, float y) {
        sprite = new Sprite(bossPolla);
        sprite.setSize(90, 125);
        sprite.setPosition(x, y);

        spriteBocaAbierta = new Sprite(bossPolla);
        spriteBocaAbierta.setSize(90, 125);
        spriteBocaCerrada = new Sprite(bossPollaBocaCerrada);
        spriteBocaCerrada.setSize(90, 125);
        sprite.setRegion(spriteBocaCerrada);

        this.jugador = jugador;
        this.animaciones = new AnimacionesEnemigos();
        this.movimientoBoss = new MovimientoBossPolla(true);
        this.damageTexture = damageBossPollaTexture;
    }

    @Override
    public void actualizar(float delta) {
        animaciones.actualizarParpadeo(sprite, delta);
        animaciones.actualizarFade(delta);
        movimientoBoss.actualizarMovimiento(delta, sprite, jugador);

        if (temporizadorDanyo > 0) {
            temporizadorDanyo -= delta;
        }

        // Animación de apertura/cierre de boca
        tiempoAcumuladoBoca += delta;
        if (!bocaCerrada && tiempoAcumuladoBoca >= tiempoBocaAbierta) {
            sprite.setRegion(spriteBocaCerrada);
            bocaCerrada = true;
            tiempoAcumuladoBoca = 0;
        } else if (bocaCerrada && tiempoAcumuladoBoca >= duracionBocaCerrada) {
            sprite.setRegion(spriteBocaAbierta);
            int sonidoRandom = MathUtils.random(3);
            switch (sonidoRandom) {
                case 0:
                    GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla2", 0.65f);
                    break;
                case 1:
                    GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla", 0.65f);
                    break;
                case 2:
                    GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla3", 0.65f);
                    break;
                case 3:
                    GestorDeAudio.getInstance().reproducirEfecto("sonidoBossPolla4", 0.65f);
                    break;
            }
            bocaCerrada = false;
            tiempoAcumuladoBoca = 0;
        }

        if (jugador != null) {
            boolean estaALaIzquierda = sprite.getX() + sprite.getWidth() / 2
                < jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;

            if (sprite.isFlipX() != estaALaIzquierda) {
                sprite.flip(true, false);
            }
        }
    }


    @Override
    public void renderizar(SpriteBatch batch) {
        boolean mostrarSprite = (vida > 0) || animaciones.estaEnFade();
        if (!mostrarSprite) return;
        Color originalColor = sprite.getColor().cpy();
        // Si se está en fade-out, se aplica el alfa calculado; si se está en parpadeo, se mantiene alfa 1.
        if (animaciones.estaEnFade()) {
            float alphaFade = animaciones.getAlphaActual();
            sprite.setColor(originalColor.r, originalColor.g, originalColor.b, alphaFade);
        } else if (animaciones.estaEnParpadeo()) {
            sprite.setColor(originalColor.r, originalColor.g, originalColor.b, 1);
        } else {
            sprite.setColor(originalColor.r, originalColor.g, originalColor.b, 1);
        }
        sprite.draw(batch);
        animaciones.restaurarColor(sprite, originalColor);
    }

    @Override
    public void reducirSalud(float amount) {
        vida -= amount;
        if (vida <= 0 && !animaciones.estaEnFade()) {
            animaciones.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
            animaciones.activarParpadeo(sprite, DURACION_PARPADEO_ENEMIGO, damageTexture);
        }
    }

    @Override
    public boolean estaMuerto() {
        estaMuerto = true;
        return (vida <= 0 && !animaciones.estaEnFade());
    }

    @Override
    public boolean esGolpeadoPorProyectil(float px, float py, float pw, float ph) {
        Rectangle bossRect = sprite.getBoundingRectangle();
        Rectangle projRect = new Rectangle(px, py, pw, ph);
        return bossRect.overlaps(projRect);
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        if (!haSoltadoXP) {
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
        animaciones.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        // Para el boss no se le aplica knockback.
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
    public float getVida() {
        return vida;
    }

    @Override
    public float getDamageAmount() {
        return damageAmount;
    }

    public boolean isEstaMuerto() {
        return estaMuerto;
    }

    public AnimacionesEnemigos getAnimaciones() {
        return animaciones;
    }

    @Override
    public void dispose() {
        sprite = null;
    }
}

package com.sticklike.core.entidades.enemigos.bosses;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesEnemigos;
import com.sticklike.core.entidades.enemigos.movimiento.MovimientoBossPolla;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class BossPolla implements Enemigo {
    private Sprite sprite;
    private Jugador jugador;
    private AnimacionesEnemigos animaciones;
    private MovimientoBossPolla movimientoBoss;

    private float vida = 1000;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private float damageAmount = 12.5f;
    private float coolDownDanyo = 1.2f;
    private float temporizadorDanyo = 0f;

    public BossPolla(Jugador jugador, float x, float y) {
        sprite = new Sprite(bossPolla);
        sprite.setSize(90, 125);
        sprite.setPosition(x, y);

        this.jugador = jugador;
        this.animaciones = new AnimacionesEnemigos();
        this.movimientoBoss = new MovimientoBossPolla(true);
    }

    @Override
    public void actualizar(float delta) {
        animaciones.actualizarParpadeo(delta);
        animaciones.actualizarFade(delta);

        movimientoBoss.actualizarMovimiento(delta, sprite, jugador);

        if (temporizadorDanyo > 0) {
            temporizadorDanyo -= delta;
        }

        float bossCenterX = sprite.getX() + sprite.getWidth()/2f;
        float playerCenterX = jugador.getSprite().getX() + jugador.getSprite().getWidth()/2f;
        boolean estaALaIzquierda = bossCenterX < playerCenterX;
        if (!estaALaIzquierda && !sprite.isFlipX()) {
            sprite.flip(true, false);
        } else if (estaALaIzquierda && sprite.isFlipX()) {
            sprite.flip(true, false);
        }
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        boolean mostrarSprite = (vida > 0) || animaciones.estaEnFade();
        if (!mostrarSprite) return;

        Color originalColor = sprite.getColor().cpy();

        if (animaciones.estaEnParpadeo()) {
            animaciones.aplicarParpadeo1(sprite);
        } else {
            sprite.setColor(originalColor.r, originalColor.g, originalColor.b, animaciones.getAlphaActual());
        }

        sprite.draw(batch);
        animaciones.restaurarColor(sprite, originalColor);
    }

    @Override
    public void reducirSalud(float amount) {
        vida -= amount;
        if (vida <= 0 && !animaciones.estaEnFade()) {
            animaciones.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
            animaciones.activarParpadeo(DURACION_PARPADEO_ENEMIGO);
        }
    }

    @Override
    public boolean estaMuerto() {
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
            return new ObjetoXp(this.getX(), this.getY());
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
        animaciones.activarParpadeo(duracion);
    }

    @Override
    public void dispose() {
        sprite = null;
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        // de momento no se le aplica knockback al boss
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
}

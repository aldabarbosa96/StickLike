package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoPolla;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;

import static com.sticklike.core.utilidades.GestorDeAssets.*;
import static com.sticklike.core.utilidades.GestorConstantes.*;


import static com.sticklike.core.utilidades.GestorConstantes.DURACION_PARPADEO_ENEMIGO;

public class EnemigoPolla implements Enemigo {
    private Sprite sprite;
    private Jugador jugador;
    private float vidaEnemigo = VIDA_ENEMIGO_POLLA; // todo --> revisar variables de clase
    private float coolDownDanyo = COOLDOWN_POLLA;
    private float temporizadorDanyo = TEMPORIZADOR_DANYO;
    private MovimientoPolla movimientoPolla;
    private static float velocidadBase = VEL_BASE_POLLA;

    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesEnemigos animacionesEnemigos;
    private float damageAmount = DANYO_POLLA;

    public EnemigoPolla(float x, float y, Jugador jugador, float velocidadEnemigo) {
        sprite = new Sprite(enemigoPolla);
        sprite.setSize(26f, 26f);
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoPolla = new MovimientoPolla(velocidadBase, 0.75f, 25f, true);
        this.animacionesEnemigos = new AnimacionesEnemigos();
    }

    /*private void escogerPolla() {
        float pollaRandom = (float) (Math.random() * 140f);

        if (pollaRandom <= 20) sprite = new Sprite(enemigoPolla);
        else if (pollaRandom <= 40) sprite = new Sprite(enemigoPolla2);
        else if (pollaRandom <= 60) sprite = new Sprite(enemigoPolla3);
        else if (pollaRandom <= 80) sprite = new Sprite(enemigoPolla4);
        else if (pollaRandom <= 100) sprite = new Sprite(enemigoPolla5);
        else if (pollaRandom <= 120) sprite = new Sprite(enemigoPolla6);
        else sprite = new Sprite(enemigoPolla7);
    }*/


    @Override
    public void actualizar(float delta) {
        animacionesEnemigos.actualizarParpadeo(delta);
        animacionesEnemigos.actualizarFade(delta);
        movimientoPolla.actualizarMovimiento(delta, sprite, jugador);

        boolean estaALaIzquierda = sprite.getX() + sprite.getWidth() / 2 < jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;

        if ((estaALaIzquierda && !sprite.isFlipX()) || (!estaALaIzquierda && sprite.isFlipX())) {
            sprite.flip(true, false);
        }
    }


    @Override
    public void renderizar(SpriteBatch batch) {
        boolean mostrarSprite = (vidaEnemigo > 0) || animacionesEnemigos.estaEnFade();

        if (mostrarSprite) {
            Color originalColor = sprite.getColor().cpy();

            if (animacionesEnemigos.estaEnParpadeo()) {
                animacionesEnemigos.aplicarParpadeoRojo(sprite);
            } else {

                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, animacionesEnemigos.getAlphaActual());
            }

            sprite.draw(batch);

            animacionesEnemigos.restaurarColor(sprite, originalColor);
        }
    }

    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            if (!animacionesEnemigos.estaEnFade()) {
                animacionesEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO - 0.05f);
                animacionesEnemigos.activarParpadeo(DURACION_PARPADEO_ENEMIGO);
            }
        }
    }

    @Override
    public boolean estaMuerto() {
        return vidaEnemigo <= 0 && !animacionesEnemigos.estaEnFade();
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
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 1f) {
            haSoltadoXP = true;
            return new ObjetoVida(this.getX(), this.getY());
        }
        if (!haSoltadoXP && randomXP >= 20f) {
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
        animacionesEnemigos.activarParpadeo(duracion);
    }

    @Override
    public void dispose() {
        sprite = null;
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

    public static void resetStats() {
        velocidadBase = VEL_BASE_POLLA;
    }

    public void setDamageAmount(float damage){
        this.damageAmount = damage;
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
        return animacionesEnemigos.getAlphaActual();
    }

}

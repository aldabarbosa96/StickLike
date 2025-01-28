package com.sticklike.core.entidades.enemigos.regla;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.AnimacionesEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.objetosxp.ObjetoXpVida;
import static com.sticklike.core.utilidades.GestorDeAssets.*;
import com.sticklike.core.interfaces.Enemigo;

import static com.sticklike.core.utilidades.GestorConstantes.*;

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
    private AnimacionesEnemigos animacionesEnemigos;
    private float damageAmount = 3.5f;

    public EnemigoRegla(float x, float y, Jugador jugador, float velocidadEnemigo, OrthographicCamera orthographicCamera) {
        //esCruzada();
        sprite = new Sprite(enemigoReglaCruzada);
        sprite.setSize(25, 25);
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoRegla = new MovimientoRegla(velocidadEnemigo, 666, orthographicCamera, false);
        this.orthographicCamera = orthographicCamera;
        this.animacionesEnemigos = new AnimacionesEnemigos();
    }

    private void esCruzada() { // todo --> revisar si se necesitan los diferentes tipos de reglas al inicio
        float random = (float) (Math.random() * 10);
        if (random >= 3) {
            sprite = new Sprite(enemigoReglaCruzada);
            sprite.setSize(32, 32);
        }
        if (random <= 4 && random >=7) {
            sprite = new Sprite(enemigoReglaCasiEsvastica);
            sprite.setSize(32, 32);
        } else {
            sprite = new Sprite(enemigoRegla);
            sprite.setSize(6, 32);
        }
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
    public void setDamageAmount(float damage){
        this.damageAmount = damage;
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        boolean mostrarSprite = (vidaEnemigo > 0) || animacionesEnemigos.estaEnFade();

        if (mostrarSprite) {
            Color originalColor = sprite.getColor().cpy();

            if (animacionesEnemigos.estaEnParpadeo()) {
                animacionesEnemigos.aplicarParpadeo1(sprite);
            } else {

                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, animacionesEnemigos.getAlphaActual());
            }

            sprite.draw(batch);

            animacionesEnemigos.restaurarColor(sprite, originalColor);
        }
    }

    @Override
    public void actualizar(float delta) {
        animacionesEnemigos.actualizarParpadeo(delta);
        animacionesEnemigos.actualizarFade(delta);
        movimientoRegla.actualizarMovimiento(delta, sprite, jugador);

    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetoXpVida sueltaObjetoXP() {
        float corazonONo = (float) (Math.random());
        if (!haSoltadoXP && corazonONo < 0.25f) { // 25% de probabilidades de que suelte vida
            haSoltadoXP = true;
            return new ObjetoXpVida(this.getX(), this.getY());
        }
        return null;
    }

    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            if (!animacionesEnemigos.estaEnFade()) {
                //animacionesEnemigos.iniciarFadeMuerte(0.2f); no queda tan bien el fade-out en la regla todo --> crear efecto de muerte
                animacionesEnemigos.activarParpadeo(0.15f);
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
    public void activarParpadeo(float duracion) {
        animacionesEnemigos.activarParpadeo(duracion);
    }

    @Override
    public void setProcesado(boolean procesado) {
        this.procesado = procesado;
    }
}

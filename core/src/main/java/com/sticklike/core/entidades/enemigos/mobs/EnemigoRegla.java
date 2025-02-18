package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoRegla;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
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
    private float damageAmount = DANYO_REGLA;

    private final Texture damageTexture;

    public EnemigoRegla(float x, float y, Jugador jugador, float velocidadEnemigo, OrthographicCamera orthographicCamera) {
        sprite = new Sprite(enemigoReglaCruzada);
        sprite.setSize(23, 23);
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoRegla = new MovimientoRegla(velocidadEnemigo, 666, orthographicCamera, false);
        this.orthographicCamera = orthographicCamera;
        this.animacionesEnemigos = new AnimacionesEnemigos();
        this.damageTexture = damageReglaTexture;
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

    public void setDamageAmount(float damage) {
        this.damageAmount = damage;
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        boolean mostrarSprite = (vidaEnemigo > 0) || animacionesEnemigos.estaEnFade();
        if (mostrarSprite) {
            Color originalColor = sprite.getColor().cpy();
            // Si se está en fade-out, aplicamos el alfa calculado.
            if (animacionesEnemigos.estaEnFade()) {
                float alphaFade = animacionesEnemigos.getAlphaActual();
                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, alphaFade);
            }
            // Si se está en parpadeo (efecto de daño), dejamos el alfa en 1.
            else if (animacionesEnemigos.estaEnParpadeo()) {
                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, 1);
            }
            // Caso por defecto: sin efectos, se muestra con alfa 1.
            else {
                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, 1);
            }
            sprite.draw(batch);
            animacionesEnemigos.restaurarColor(sprite, originalColor);
        }
    }

    @Override
    public void actualizar(float delta) {
        // Actualizamos los efectos de parpadeo y fade con el delta de tiempo.
        animacionesEnemigos.actualizarParpadeo(sprite, delta);
        animacionesEnemigos.actualizarFade(delta);
        movimientoRegla.actualizarMovimiento(delta, sprite, jugador);
        if (temporizadorDanyo > 0) {
            temporizadorDanyo -= delta;
        }
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(
            new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight)
        );
    }

    @Override
    public ObjetoVida sueltaObjetoXP() {
        float corazonONo = MathUtils.random(100);
        if (!haSoltadoXP && corazonONo <= 1f) {
            haSoltadoXP = true;
            return new ObjetoVida(getX(), getY());
        }
        return null;
    }

    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            if (!animacionesEnemigos.estaEnFade()) {
                animacionesEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                activarParpadeo(0.15f);
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
        animacionesEnemigos.activarParpadeo(sprite, duracion, damageTexture);
    }

    @Override
    public void dispose() {
        sprite = null;
    }
}

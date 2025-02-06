package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesEnemigos;
import com.sticklike.core.entidades.enemigos.movimiento.MovimientoCulo;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;

import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

public class EnemigoCulo implements Enemigo {
    private Sprite sprite;
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

    public EnemigoCulo(float x, float y, Jugador jugador, float velocidadEnemigo) {
        esConOjo(); // Determina si el enemigo tiene ojo o no al crearse.
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoCulo = new MovimientoCulo(velocidadBase, true);
        this.animacionesEnemigos = new AnimacionesEnemigos();
    }

    private void esConOjo() {
        float random = (float) (Math.random() * 10);
        if (random >= 2.5f) {
            sprite = new Sprite(enemigoCulo);
            sprite.setSize(26, 22);
        } else {
            sprite = new Sprite(enemigoCuloOjo);
            sprite.setSize(30, 26);
            vidaEnemigo = VIDA_ENEMIGOCULO * 2; // Incrementa la vida si tiene ojo.
        }
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        movimientoCulo.aplicarKnockback(fuerza, dirX, dirY);
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        // Comprobamos si el enemigo sigue "vivo" o está en proceso de desvanecimiento (fade)
        boolean mostrarSprite = (vidaEnemigo > 0) || animacionesEnemigos.estaEnFade();

        if (mostrarSprite) {
            Color originalColor = sprite.getColor().cpy();

            // Aplicar efecto de parpadeo rojo si está activo
            if (animacionesEnemigos.estaEnParpadeo()) {
                animacionesEnemigos.aplicarParpadeo1(sprite);
            } else {
                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, animacionesEnemigos.getAlphaActual());
            }

            // Dibujar el sprite en pantalla
            sprite.draw(batch);

            // Restaurar el color original del sprite después de dibujar
            animacionesEnemigos.restaurarColor(sprite, originalColor);
        }
    }


    @Override
    public void actualizar(float delta) {
        animacionesEnemigos.actualizarParpadeo(delta);
        animacionesEnemigos.actualizarFade(delta);
        movimientoCulo.actualizarMovimiento(delta, sprite, jugador);

        if (temporizadorDanyo > 0) { // reseteamos el temporizador para que pueda aplicar daño más de 1 vez
            temporizadorDanyo -= delta;
        }

        // Verificar si el sprite debe voltearse
        boolean estaALaIzquierda = !(sprite.getX() + sprite.getWidth() / 2 < jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2);

        // Voltear el sprite horizontalmente si la dirección ha cambiado
        if ((estaALaIzquierda && !sprite.isFlipX()) || (!estaALaIzquierda && sprite.isFlipX())) {
            sprite.flip(true, false);
        }
    }

    @Override
    public void activarParpadeo(float duracion) {
        animacionesEnemigos.activarParpadeo(duracion);
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetoXp sueltaObjetoXP() {
        if (!haSoltadoXP) {
            haSoltadoXP = true;
            return new ObjetoXp(this.getX(), this.getY());
        }
        return null;
    }

    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            // el enemigo inicia el proceso de desvanecimiento y parpadeo al morir
            if (!animacionesEnemigos.estaEnFade()) {
                animacionesEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                animacionesEnemigos.activarParpadeo(DURACION_PARPADEO_ENEMIGO);
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
}

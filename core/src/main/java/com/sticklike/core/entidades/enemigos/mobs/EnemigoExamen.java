package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoExamen;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import static com.sticklike.core.utilidades.GestorDeAssets.*;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class EnemigoExamen implements Enemigo {

    private Sprite sprite;
    private Sprite sprite2;
    private Jugador jugador;

    private float vidaEnemigo = VIDA_ENEMIGO_EXAMEN;
    private float damageAmount = DANYO_EXAMEN;
    private float coolDownDanyo = COOLDOWN_EXAMEN;
    private float temporizadorDanyo = TEMPORIZADOR_DANYO;

    private boolean haSoltadoXP = false;
    private boolean procesado = false;

    private AnimacionesEnemigos animacionesEnemigos;
    private MovimientoExamen movimientoExamen;

    private static float velocidadBase = VEL_BASE_EXAMEN;

    public EnemigoExamen(float x, float y, Jugador jugador, float velocidadEnemigo) {
        this.jugador = jugador;

        sprite = new Sprite(enemigoExamen);
        sprite.setSize(32f, 32f);
        sprite.setPosition(x, y);

        sprite2 = new Sprite(enemigoExamen2);
        sprite2.setSize(32f, 32);
        sprite2.setPosition(x, y);

        this.movimientoExamen = new MovimientoExamen();
        this.animacionesEnemigos = new AnimacionesEnemigos();

        setVelocidad(velocidadEnemigo);
    }

    @Override
    public void actualizar(float delta) {
        animacionesEnemigos.actualizarParpadeo(delta);
        animacionesEnemigos.actualizarFade(delta);

        if (movimientoExamen != null) {
            movimientoExamen.actualizarMovimiento(delta, sprite, jugador);
        }

        if (jugador != null) {
            boolean estaALaIzquierda = sprite.getX() + sprite.getWidth() / 2
                    < jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;

            if ((estaALaIzquierda && !sprite.isFlipX()) || (!estaALaIzquierda && sprite.isFlipX())) {
                sprite.flip(true, false);
            }
        }

        if (temporizadorDanyo > 0) {
            temporizadorDanyo -= delta;
        }
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        boolean mostrarSprite = (vidaEnemigo > 0) || animacionesEnemigos.estaEnFade();
        if (mostrarSprite) {
            Color colorOriginal = sprite.getColor().cpy();

            if (animacionesEnemigos.estaEnParpadeo()) {
                animacionesEnemigos.aplicarParpadeo1(sprite);
                animacionesEnemigos.aplicarParpadeo1(sprite2);
            } else {
                sprite.setColor(colorOriginal.r, colorOriginal.g, colorOriginal.b, animacionesEnemigos.getAlphaActual());
                sprite2.setColor(colorOriginal.r, colorOriginal.g, colorOriginal.b, animacionesEnemigos.getAlphaActual());
            }

            if (movimientoExamen != null && movimientoExamen.isUsandoSprite2()) {
                sprite2.setPosition(sprite.getX(), sprite.getY());
                sprite2.draw(batch);
            } else {
                sprite.draw(batch);
            }

            animacionesEnemigos.restaurarColor(sprite, colorOriginal);
            animacionesEnemigos.restaurarColor(sprite2, colorOriginal);
        }
    }


    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            // Iniciar animación de fade y parpadeo si no se ha iniciado
            if (!animacionesEnemigos.estaEnFade()) {
                animacionesEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                animacionesEnemigos.activarParpadeo(DURACION_PARPADEO_ENEMIGO);
            }
        }
    }

    @Override
    public boolean estaMuerto() {
        // Muerto solo si vida <= 0 y terminó el fade
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
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(
                new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight)
        );
    }

    @Override
    public ObjetosXP sueltaObjetoXP() {
        // Ejemplo de probabilidad de soltar ítems: ajústala a tu gusto
        float randomXP = (float) (Math.random() * 100);
        if (!haSoltadoXP && randomXP <= 5f) {
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
        return (temporizadorDanyo <= 0);
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
        sprite2 = null;
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


    public MovimientoExamen getMovimientoExamen() {
        return movimientoExamen;
    }


    public float getFadeAlpha() {
        return animacionesEnemigos.getAlphaActual();
    }
}

package com.sticklike.core.entidades.enemigos.mobs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionExamen;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoExamen;
import com.sticklike.core.entidades.renderizado.RenderBaseEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoVida;
import com.sticklike.core.entidades.objetos.recolectables.ObjetoXp;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.gestores.GestorDeAssets;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

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
    private RenderBaseEnemigos renderBaseEnemigos;

    public EnemigoExamen(float x, float y, Jugador jugador, float velocidadEnemigo) {
        this.jugador = jugador;
        sprite = new Sprite(manager.get(ENEMIGO_EXAMEN, Texture.class));
        sprite.setSize(40f, 42f);
        sprite.setPosition(x, y);
        this.movimientoExamen = new MovimientoExamen();
        this.animacionesBaseEnemigos = new AnimacionesBaseEnemigos();
        this.animacionExamen = new AnimacionExamen(animacionesBaseEnemigos, manager.get(ENEMIGO_EXAMEN, Texture.class), manager.get(ENEMIGO_EXAMEN2, Texture.class), 0.25f);
        setVelocidad(velocidadEnemigo);
        this.damageTexture = manager.get(DAMAGE_EXAMEN_TEXTURE, Texture.class);
        this.renderBaseEnemigos = jugador.getControladorEnemigos().getRenderBaseEnemigos();
    }

    @Override
    public void actualizar(float delta) {
        animacionesBaseEnemigos.actualizarFade(delta);

        if (vidaEnemigo > 0) {
            animacionesBaseEnemigos.actualizarParpadeo(sprite, delta);
            animacionesBaseEnemigos.actualizarFade(delta);
            if (temporizadorDanyo > 0) temporizadorDanyo -= delta;
            if (movimientoExamen != null) {
                movimientoExamen.actualizarMovimiento(delta, sprite, jugador);
            }
            animacionExamen.actualizarAnimacion(delta, jugador, sprite);
        } else {
            if (movimientoExamen != null) {
                movimientoExamen.actualizarSoloKnockback(delta, sprite);
            }
            if (animacionesBaseEnemigos.enAnimacionMuerte()) {
                animacionesBaseEnemigos.actualizarAnimacionMuerte(sprite, delta);
            }
        }
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        if (vidaEnemigo > 0) {
            renderBaseEnemigos.dibujarEnemigos(batch, this);
        } else {
            if (animacionesBaseEnemigos.enAnimacionMuerte()) {
                sprite.draw(batch);
            }
        }
    }

    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        // Activar parpadeo si no se está ya parpadeando
        if (!animacionesBaseEnemigos.estaEnParpadeo()) {
            animacionesBaseEnemigos.activarParpadeo(sprite, DURACION_PARPADEO_ENEMIGO, damageTexture);
        }
        if (vidaEnemigo <= 0) {
            // Iniciar animación de muerte solo una vez (cuando aún no se ha iniciado)
            if (!animacionesBaseEnemigos.estaEnFade() && !animacionesBaseEnemigos.enAnimacionMuerte()) {
                Animation<TextureRegion> animMuerteExamen = GestorDeAssets.animations.get("examenMuerte");
                animacionesBaseEnemigos.iniciarAnimacionMuerte(animMuerteExamen);
                animacionesBaseEnemigos.iniciarFadeMuerte(DURACION_FADE_ENEMIGO);
                activarParpadeo(DURACION_PARPADEO_ENEMIGO);
                animacionesBaseEnemigos.reproducirSonidoMuerteGenerico();
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
        return vidaEnemigo > 0 && temporizadorDanyo <= 0;
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

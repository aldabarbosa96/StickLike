package com.sticklike.core.interfaces;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.entidades.enemigos.animacion.AnimacionesBaseEnemigos;
import com.sticklike.core.entidades.enemigos.ia.MovimientoBaseEnemigos;

/**
 * Define el comportamiento base de un enemigo en el juego.
 * Incluye métodos para actualizar, renderizar, recibir daño, soltar XP y gestionar interacciones con proyectiles.
 */

public interface Enemigo {
    void actualizar(float delta);
    void renderizar(SpriteBatch batch);
    void reducirSalud(float amount);
    boolean estaMuerto();
    float getX();
    float getY();
    boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight);
    ObjetosXP sueltaObjetoXP();
    Sprite getSprite();
    void reseteaTemporizadorDanyo();
    boolean puedeAplicarDanyo();
    boolean haSoltadoXP();
    void setProcesado(boolean procesado);
    boolean isProcesado();
    void activarParpadeo(float duracion);
    void dispose();
    void aplicarKnockback(float fuerza, float dirX, float dirY);
    float getVida();
    float getDamageAmount();
    AnimacionesBaseEnemigos getAnimaciones();
    boolean isMostrandoDamageSprite();
    boolean estaEnKnockback();
    MovimientoBaseEnemigos getMovimiento();
}

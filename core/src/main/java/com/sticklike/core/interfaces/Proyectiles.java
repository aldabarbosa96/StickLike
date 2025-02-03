package com.sticklike.core.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Interfaz base para todos los proyectiles del juego.
 * Cada proyectil define su daño y fuerza de empuje de forma independiente.
 */
public interface Proyectiles {
    void actualizarProyectil(float delta);

    void renderizarProyectil(SpriteBatch batch);

    void dispose();

    float getX();

    float getY();

    Rectangle getRectanguloColision();

    boolean isProyectilActivo();

    void desactivarProyectil();

    float getBaseDamage();

    float getKnockbackForce();

    boolean isPersistente();

    void registrarImpacto(Enemigo enemigo);

    boolean yaImpacto(Enemigo enemigo);
}

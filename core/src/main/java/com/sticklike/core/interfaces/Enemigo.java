package com.sticklike.core.interfaces;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
}

package com.sticklike.core.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public interface Proyectiles {
    void actualizarProyectil(float delta);
    void renderizarProyectil(SpriteBatch batch);
    void dispose();
    float getX();
    float getY();
    Rectangle getRectanguloColision();
    boolean isProyectilActivo();
    void desactivarProyectil();
}

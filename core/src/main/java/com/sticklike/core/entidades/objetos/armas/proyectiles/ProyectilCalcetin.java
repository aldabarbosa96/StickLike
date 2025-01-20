package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.GestorConstantes;
import com.sticklike.core.utilidades.GestorDeAssets;

public class ProyectilCalcetin implements Proyectiles {
    private static Texture textura;
    private Sprite sprite;
    private float velocidadProyectil = GestorConstantes.PROJECTILE_SPEED;
    private float multiplicadorVelocidad;
    private float direccionX, direccionY;
    private float distanciaMaxima;
    private float distanciaRecorrida;
    private boolean proyectilActivo;

    public ProyectilCalcetin(float x, float y,float direccionX, float direccionY, float velocidadProyectil, float multiplicadorVelocidad) {
        if (textura == null) {
            textura = GestorDeAssets.armaCalcetin;
        }
        this.distanciaMaxima = 250f;
        this.distanciaRecorrida = 0f;

        sprite = new Sprite(textura);
        sprite.setSize(GestorConstantes.PROJECTILE_SIZE + 15f, GestorConstantes.PROJECTILE_SIZE + 15f);
        sprite.setPosition(x, y);
        this.velocidadProyectil = velocidadProyectil;
        this.direccionX = direccionX;
        this.direccionY = direccionY;
        this.proyectilActivo = true;
        this.multiplicadorVelocidad = multiplicadorVelocidad;

    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!isProyectilActivo()) return;

        float desplazamiento = velocidadProyectil * multiplicadorVelocidad * delta;
        sprite.translate(direccionX * desplazamiento, direccionY * desplazamiento);
        distanciaRecorrida += desplazamiento;

        // Verificar si ha alcanzado la distancia máxima
        if (distanciaRecorrida >= distanciaMaxima) {
            desactivarProyectil(); // Desactivar el proyectil
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo){
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        textura = null;
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
    public Rectangle getRectanguloColision() {
        return sprite.getBoundingRectangle();
    }

    @Override
    public boolean isProyectilActivo() {
        return proyectilActivo;
    }

    @Override
    public void desactivarProyectil() {
        proyectilActivo = false;
    }
}

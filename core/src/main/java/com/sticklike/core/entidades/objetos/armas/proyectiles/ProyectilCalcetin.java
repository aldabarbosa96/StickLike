package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.GestorConstantes;
import com.sticklike.core.utilidades.GestorDeAssets;

/**
 * Proyectil tipo "Calcetín".
 * Define su propio daño base y fuerza de empuje (mayor que la piedra).
 */
public class ProyectilCalcetin implements Proyectiles {
    private static Texture textura;
    private Sprite sprite;
    private float velocidadProyectil;
    private float multiplicadorVelocidad;
    private float distanciaMaxima;
    private float distanciaRecorrida;
    private boolean proyectilActivo;
    private float direccionX, direccionY;
    private float rotationSpeed = GestorConstantes.VEL_ROTACION_CALCETIN;

    public ProyectilCalcetin(float x, float y, float direccionX, float direccionY, float velocidadProyectil, float multiplicadorVelocidad) {
        if (textura == null) {
            textura = GestorDeAssets.armaCalcetin;
        }
        this.distanciaMaxima = GestorConstantes.MAX_DISTANCIA;
        this.distanciaRecorrida = GestorConstantes.DISTANCIA_RECORRIDA;

        sprite = new Sprite(textura);
        sprite.setSize(GestorConstantes.CALCETIN_W_SIZE, GestorConstantes.CALCETIN_H_SIZE);
        sprite.setPosition(x, y);
        sprite.setOriginCenter();

        this.velocidadProyectil = velocidadProyectil;
        this.direccionX = direccionX;
        this.direccionY = direccionY;
        this.multiplicadorVelocidad = multiplicadorVelocidad;
        this.proyectilActivo = true;
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        float desplazamiento = velocidadProyectil * multiplicadorVelocidad * delta;
        sprite.translate(direccionX * desplazamiento, direccionY * desplazamiento);
        distanciaRecorrida += desplazamiento;

        sprite.rotate(rotationSpeed * delta);

        // Verificar si ha recorrido su distancia máxima
        if (distanciaRecorrida >= distanciaMaxima) {
            desactivarProyectil();
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo) {
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

    @Override
    public float getBaseDamage() {
        // daño base entre 13 y 24
        return  13 + (float) Math.random() * 11;
    }

    @Override
    public float getKnockbackForce() {
        // El calcetín empuja más que la piedra de base
        return GestorConstantes.EMPUJE_BASE_CALCETIN;
    }
}

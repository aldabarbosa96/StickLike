package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.GestorDeAssets;
import com.sticklike.core.utilidades.GestorConstantes;

/**
 * La clase Projectile representa un proyectil disparado por el jugador
 * (en un futuro también por enemigos). Se encarga de gestionar su
 * posición, movimiento y colisiones
 */
public class ProyectilPiedra implements Proyectiles {
    // Textura estática compartida por todos los proyectiles
    private static Texture textura;
    private Sprite sprite;
    private float velocidadProyectil = GestorConstantes.PROJECTILE_SPEED;
    private float multiplicadorVelocidad;
    private float direccionX, direccionY;
    private boolean proyectilActivo;

    /**
     * Crea un nuevo proyectil con los parámetros indicados
     *
     * @param x,y posición inicial X,Y del proyectil
     * @param direccionX,direccionY componente X,Y de la dirección normalizada
     * @param multiplicadorVelocidad factor de velocidad extra (para alterar la velocidad de los proyectiles)
     */
    public ProyectilPiedra(float x, float y, float direccionX, float direccionY, float multiplicadorVelocidad) {
        if (textura == null) {
            textura = GestorDeAssets.armaPiedra;
        }
        sprite = new Sprite(textura);
        sprite.setSize(GestorConstantes.PROJECTILE_SIZE, GestorConstantes.PROJECTILE_SIZE);
        sprite.setPosition(x, y);

        this.direccionX = direccionX;
        this.direccionY = direccionY;
        this.proyectilActivo = true;
        this.multiplicadorVelocidad = multiplicadorVelocidad;
    }

    /**
     * Actualiza la posición del proyectil en base a su velocidad y dirección
     *
     * @param delta tiempo transcurrido desde el último frame
     */
    @Override
    public void actualizarProyectil(float delta) {
        if (proyectilActivo) {
            // Aplicamos el multiplicador de velocidad
            sprite.translate(direccionX * velocidadProyectil * multiplicadorVelocidad * delta, direccionY * velocidadProyectil * multiplicadorVelocidad * delta);
        }
    }

    /**
     * Dibuja el proyectil, si está activo
     *
     * @param batch SpriteBatch para renderizar
     */
    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo) {
            sprite.draw(batch);
        }
    }

    public void desactivarProyectil() { // Para cuando colisiona
        proyectilActivo = false;
    }

    public boolean isProyectilActivo() {
        return proyectilActivo;
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
        return sprite.getBoundingRectangle(); // Devuelve el rectángulo de colisión para gestionar los impactos.
    }
}

package com.sticklike.core.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.personajes.Enemigo;
import com.sticklike.core.utilidades.GestorDeAssets;
import com.sticklike.core.utilidades.GestorConstantes;

/**
 * La clase Projectile representa un proyectil disparado por el jugador
 * (en un futuro también por enemigos). Se encarga de gestionar su
 * posición, movimiento y colisiones
 */
public class Proyectil {
    // Textura estática compartida por todos los proyectiles
    private static Texture textura;

    private Sprite sprite;
    private float velocidadProyectil = GestorConstantes.PROJECTILE_SPEED;
    private float multiplicadorVelocidad;
    private float direccionX, direccionY;
    private boolean proyectilActivo;
    private Enemigo enemigoObjetivo;

    /**
     * Crea un nuevo proyectil con los parámetros indicados
     *
     * @param x,y posición inicial X,Y del proyectil
     * @param direccionX,direccionY componente X,Y de la dirección normalizada
     * @param enemigoObjetivo referencia al enemigo objetivo (guiar o verificar colisiones)
     * @param multiplicadorVelocidad factor de velocidad extra (para alterar la velocidad de los proyectiles)
     */
    public Proyectil(float x, float y, float direccionX, float direccionY, Enemigo enemigoObjetivo, float multiplicadorVelocidad) {
        if (textura == null) {
            textura = GestorDeAssets.armaPiedra;
        }
        sprite = new Sprite(textura);
        sprite.setSize(GestorConstantes.PROJECTILE_SIZE, GestorConstantes.PROJECTILE_SIZE);
        sprite.setPosition(x, y);

        this.direccionX = direccionX;
        this.direccionY = direccionY;
        this.proyectilActivo = true;
        this.enemigoObjetivo = enemigoObjetivo;
        this.multiplicadorVelocidad = multiplicadorVelocidad;
    }

    /**
     * Actualiza la posición del proyectil en base a su velocidad y dirección
     *
     * @param delta tiempo transcurrido desde el último frame
     */
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

    public Enemigo getEnemigoObjetivo() {
        return enemigoObjetivo;
    }

    public void dispose() {
        textura = null;
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public Rectangle getBoundingRectangle() {
        return sprite.getBoundingRectangle(); // Devuelve el rectángulo de colisión para gestionar los impactos.
    }
}

package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import static com.sticklike.core.utilidades.GestorDeAssets.*;
import static com.sticklike.core.utilidades.GestorConstantes.*;

public class ProyectilPiedra implements Proyectiles {
    private static Texture textura;
    private Sprite sprite;
    private float velocidadProyectil = PROJECTILE_PIEDRA_SPEED;
    private float multiplicadorVelocidad;
    private float direccionX, direccionY;
    private boolean proyectilActivo;

    public ProyectilPiedra(float x, float y, float direccionX, float direccionY, float multiplicadorVelocidad) {
        if (textura == null) {
            textura = armaPiedra;
        }
        sprite = new Sprite(textura);
        sprite.setSize(PIEDRA_SIZE, PIEDRA_SIZE);
        sprite.setPosition(x, y);

        this.direccionX = direccionX;
        this.direccionY = direccionY;
        this.proyectilActivo = true;
        this.multiplicadorVelocidad = multiplicadorVelocidad;
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (proyectilActivo) {
            sprite.translate(
                direccionX * velocidadProyectil * multiplicadorVelocidad * delta,
                direccionY * velocidadProyectil * multiplicadorVelocidad * delta
            );
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
        // Daño base aleatorio entre 21 y 33
        return 21 + (float) Math.random() * 10;
    }

    @Override
    public float getKnockbackForce() {
        return EMPUJE_BASE_PIEDRA;
    }

    @Override
    public boolean isPersistente() { // piedra no persiste tras el impacto
        return false;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {}

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return false;
    }
}

package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.renderizado.RenderParticulasProyectil;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

/**
 * Proyectil Piedra; se lanza en línea recta con velocidad ajustable, causando daño y knockback a los enemigos impactados.
 */

public class ProyectilPiedra implements Proyectiles {
    private static Texture textura;
    private Sprite sprite;
    private float velocidadProyectil = PROJECTILE_PIEDRA_SPEED;
    private float multiplicadorVelocidad;
    private float direccionX, direccionY;
    private boolean proyectilActivo;
    private boolean esCritico;
    private Jugador jugador;
    private RenderParticulasProyectil renderParticulasProyectil;
    private Vector2 center;

    public ProyectilPiedra(float x, float y, float direccionX, float direccionY, float multiplicadorVelocidad, Jugador jugador) {
        if (textura == null) {
            textura = manager.get(ARMA_PIEDRA, Texture.class);
        }
        sprite = new Sprite(textura);
        sprite.setSize(PIEDRA_SIZE, PIEDRA_SIZE);
        sprite.setPosition(x, y);

        this.direccionX = direccionX;
        this.direccionY = direccionY;
        this.proyectilActivo = true;
        this.multiplicadorVelocidad = multiplicadorVelocidad;
        this.jugador = jugador;

        float scaleFactor = Gdx.graphics.getWidth() / REAL_WIDTH;
        int maxLength = (int) (18 * scaleFactor);
        float scaledWidth = 3 * scaleFactor;
        this.renderParticulasProyectil = new RenderParticulasProyectil(maxLength, scaledWidth, new Color(0.85f, 0.2f, 0.2f, 1f));
        center = new Vector2();
    }

    @Override
    public void actualizarProyectil(float delta) {
        // Actualizamos movimiento del sprite
        if (proyectilActivo) {
            sprite.translate(direccionX * velocidadProyectil * multiplicadorVelocidad * delta, direccionY * velocidadProyectil * multiplicadorVelocidad * delta);
            // Actualizamos rastro de partículas
            center.set(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2);
            renderParticulasProyectil.update(center);
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo) {
            renderParticulasProyectil.render(batch);
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        textura = null;
        renderParticulasProyectil.dispose();
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
        // Daño base aleatorio entre 21 y 31
        float base = 21 + MathUtils.random() * 10;
        if (MathUtils.random() < jugador.getCritico()) {
            esCritico = true;
            return base * 1.5f;
        } else {
            esCritico = false;
            return base;
        }
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
    public void registrarImpacto(Enemigo enemigo) {
        GestorDeAudio.getInstance().reproducirEfecto("impactoBase", 1);
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return false;
    }

    public boolean esCritico() {
        return esCritico;
    }
}

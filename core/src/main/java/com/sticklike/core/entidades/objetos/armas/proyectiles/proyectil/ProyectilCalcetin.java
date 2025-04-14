package com.sticklike.core.entidades.objetos.armas.proyectiles.proyectil;

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

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Proyectil Calcetín; se lanza en línea recta con rotación, causando daño y knockback a los enemigos en su trayectoria.
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
    private float rotationSpeed = VEL_ROTACION_CALCETIN;
    private Set<Enemigo> enemigosImpactados = new HashSet<>();
    private float damageEscalado;
    private boolean esCritico;
    private Jugador jugador;
    private RenderParticulasProyectil renderParticulasProyectil;
    private Vector2 centroSprite;
    private float impactoTimer = 0;
    private final Rectangle collisionRect = new Rectangle();

    public ProyectilCalcetin(float x, float y, float direccionX, float direccionY, float velocidadProyectil, float multiplicadorVelocidad, float poderJugador, float extraDamage, Jugador jugador) {
        if (textura == null) {
            textura = manager.get(ARMA_CALCETIN, Texture.class);
        }
        this.distanciaMaxima = MAX_DISTANCIA;
        this.distanciaRecorrida = 0;
        sprite = new Sprite(textura);
        sprite.setSize(CALCETIN_W_SIZE, CALCETIN_H_SIZE);
        sprite.setPosition(x, y);
        sprite.setOriginCenter();

        this.jugador = jugador;
        this.velocidadProyectil = velocidadProyectil;
        this.direccionX = direccionX;
        this.direccionY = direccionY;
        this.multiplicadorVelocidad = multiplicadorVelocidad;
        this.proyectilActivo = true;

        float scaleFactor = Gdx.graphics.getWidth() / REAL_WIDTH;
        int maxLength = (int) (17 * scaleFactor);
        float adjustWidth = 6f * scaleFactor;
        this.renderParticulasProyectil = new RenderParticulasProyectil(maxLength, adjustWidth, new Color(1, 1, 1, 0.1f));
        this.centroSprite = new Vector2();

        float baseDamage = DANYO_CALCETIN + extraDamage + MathUtils.random(8f);
        this.damageEscalado = baseDamage * (1f + (poderJugador / 100f));
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        // Actualizamos el centro del sprite y lo usamos para las partículas
        centroSprite.set(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2);
        renderParticulasProyectil.update(centroSprite);

        // Movemos el sprite según la dirección y velocidad
        float desplazamiento = velocidadProyectil * multiplicadorVelocidad * delta;
        sprite.translate(direccionX * desplazamiento, direccionY * desplazamiento);
        distanciaRecorrida += desplazamiento;

        // Actualizamos la rotación del sprite
        sprite.rotate(rotationSpeed * delta);

        // Actualizamos el rectángulo de colisión preasignado
        collisionRect.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());

        // Gestión de impacto y restauración de color
        if (!enemigosImpactados.isEmpty()) {
            impactoTimer += delta;
            if (impactoTimer >= IMPACTO_DURACION) {
                sprite.setColor(1, 1, 1, 1);
                renderParticulasProyectil.setColor(new Color(1, 1, 1, 0.1f));
                impactoTimer = 0;
            }
        } else {
            sprite.setColor(1, 1, 1, 1);
        }

        if (distanciaRecorrida >= distanciaMaxima) {
            desactivarProyectil();
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
        return collisionRect;
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
        if (MathUtils.random() < jugador.getCritico()) {
            esCritico = true;
            return damageEscalado * 1.5f;
        } else {
            esCritico = false;
            return damageEscalado;
        }
    }

    @Override
    public float getKnockbackForce() {
        return EMPUJE_BASE_CALCETIN;
    }

    @Override
    public boolean isPersistente() {
        return true;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        if (!enemigosImpactados.contains(enemigo)) {
            enemigosImpactados.add(enemigo);
            sprite.setColor(Color.RED);
            renderParticulasProyectil.setColor(Color.RED);
            impactoTimer = 0;
        }
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return enemigosImpactados.contains(enemigo);
    }

    @Override
    public boolean esCritico() {
        return esCritico;
    }
}

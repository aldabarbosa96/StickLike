package com.sticklike.core.entidades.objetos.armas.proyectiles.proyectil;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.renderizado.RenderParticulasProyectil;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Proyectil LatigoDildo; simula un latigazo rápido de color rosa en dos direcciones (derecha e izquierda)
 * con un movimiento en semiarco vertical: de arriba a abajo, pasando por el lateral según el lado.
 */

public class LatigoDildo implements Proyectiles {
    private static Texture textura;
    private Sprite sprite;
    private float duration = 0.33f;
    private float timer = 0;
    private boolean activo;
    private Jugador jugador;
    private int lado; // 1 para batida a la derecha, -1 para la izquierda.
    private float baseDamage;
    private float knockbackForce = EMPUJE_BASE_DILDO;
    private Rectangle collisionRect;
    private Set<Enemigo> enemigosImpactados = new HashSet<>();
    private boolean esCritico;
    private float swingDistance = 62.5f;
    private float impactColorDuration = IMPACTO_DURACION;
    private float impactColorTimer = 0;
    private final Color originalColor = new Color(1, 1, 1, 1);
    private final Color impactColor = new Color(0, 0, 1, 1);
    private RenderParticulasProyectil renderParticulasProyectil;
    private Vector2 center;

    public LatigoDildo(Jugador jugador, int lado, float poderJugador, float extraDamage) {
        if (textura == null) {
            textura = manager.get(ARMA_DILDO, Texture.class);
        }
        this.jugador = jugador;
        this.lado = lado;
        this.activo = true;
        sprite = new Sprite(textura);
        sprite.setSize(25f, 50f);
        sprite.setOrigin(sprite.getWidth() / 4, sprite.getHeight() / 2);
        if (lado == -1) {
            sprite.flip(false, true);
        }
        sprite.setColor(originalColor);
        baseDamage = (DANYO_DILDO + extraDamage + MathUtils.random(4f)) * (1f + (poderJugador / 100f));

        renderParticulasProyectil = new RenderParticulasProyectil(20, 45, new Color(0.85f, 0.4f, 0.7f, 1f));
        renderParticulasProyectil.setAlphaMult(0.65f);

        center = new Vector2();

        float radioColision = sprite.getWidth() * 0.5f * 1.1f;
        float colWidth = radioColision * 2;
        float centerX = sprite.getX() + sprite.getWidth() / 2f;
        float centerY = sprite.getY() + sprite.getHeight() / 2f;
        collisionRect = new Rectangle(centerX - colWidth / 2, centerY - colWidth / 2, colWidth, colWidth);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!activo) return;
        timer += delta;
        float progress = timer / duration;

        // el ángulo theta varía de π/2 a 3π/2
        float theta = MathUtils.PI / 2 + progress * MathUtils.PI;
        float R = swingDistance;

        float jugadorCenterX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2f - sprite.getWidth() / 2;
        float jugadorCenterY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2f;

        // calculamos la nueva posición basándose en el semiarco y el lado
        float newX = jugadorCenterX - lado * R * MathUtils.cos(theta);
        float newY = jugadorCenterY + R * MathUtils.sin(theta);

        sprite.setPosition(newX, newY - sprite.getHeight() / 2);

        // Calculamos la rotación del sprite en función de la dirección del movimiento
        float dx = lado * R * MathUtils.sin(theta);
        float dy = R * MathUtils.cos(theta);
        float angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;
        sprite.setRotation(angle);

        // actualizamos el círculo de colisión con la nueva posición del sprite
        float centerX = sprite.getX() + sprite.getWidth() / 2f;
        float centerY = sprite.getY() + sprite.getHeight() / 2f;
        collisionRect.setPosition(centerX - collisionRect.width / 2, centerY - collisionRect.height / 2);

        center.set(centerX, centerY);
        renderParticulasProyectil.update(center);

        if (impactColorTimer > 0) {
            impactColorTimer -= delta;
            if (impactColorTimer <= 0) {
                sprite.setColor(originalColor);
                renderParticulasProyectil.setColor(Color.PINK);
            }
        }

        if (timer >= duration) {
            desactivarProyectil();
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (activo) {
            renderParticulasProyectil.render(batch);
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
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
        return activo;
    }

    @Override
    public void desactivarProyectil() {
        activo = false;
    }

    @Override
    public float getBaseDamage() {
        if (MathUtils.random() < jugador.getCritico()) {
            esCritico = true;
            return baseDamage * 1.5f;
        } else {
            esCritico = false;
            return baseDamage;
        }
    }

    @Override
    public float getKnockbackForce() {
        return knockbackForce;
    }

    @Override
    public boolean isPersistente() {
        return true;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        if (!enemigosImpactados.contains(enemigo)) {
            enemigosImpactados.add(enemigo);
            sprite.setColor(impactColor);
            renderParticulasProyectil.setColor(new Color(0.051f, 0.596f, 1.0f, 1.0f));
            renderParticulasProyectil.setAlphaMult(0.75f);
            GestorDeAudio.getInstance().reproducirEfecto("dildo", 0.8f);
            impactColorTimer = impactColorDuration;
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

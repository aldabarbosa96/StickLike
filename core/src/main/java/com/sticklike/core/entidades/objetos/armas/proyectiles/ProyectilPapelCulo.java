package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

public class ProyectilPapelCulo implements Proyectiles {
    private static Texture textura;
    private Sprite sprite;
    private Animation<TextureRegion> impactoAnimation;
    private float animationStateTime = 0f;
    private boolean impactoAnimacionActiva = false;
    private float impactX, impactY;
    private float impactRotation;
    private float velocidadProyectil;
    private float anguloLanzamiento;
    private float velocidadHorizontal;
    private float velocidadVertical;
    private boolean proyectilActivo;
    private boolean aterrizado = false;
    private float alturaFinal;
    private Set<Enemigo> enemigosImpactados = new HashSet<>();
    private float damageEscalado;
    private boolean esCritico;
    private Jugador jugador;
    private RenderParticulasProyectil renderParticulasProyectil;
    private Vector2 centroSprite;
    private final float GRAVEDAD_ASCENSO = 450f;
    private final float GRAVEDAD_DESCENSO = 200f;
    private float rotationSpeed = 666f;
    private float rotationAngle = 0f;

    public ProyectilPapelCulo(float x, float y, float anguloLanzamiento, float velocidadProyectil, float poderJugador, float extraDamage, Jugador jugador, float direccionHorizontal) {
        if (textura == null) {
            textura = manager.get(ARMA_PAPELCULO, Texture.class);
        }
        sprite = new Sprite(textura);
        sprite.setSize(PAPELCULO_W_SIZE, PAPELCULO_H_SIZE);
        sprite.setPosition(x, y);
        sprite.setOriginCenter();

        impactoAnimation = animations.get("papelCuloImpacto");

        this.jugador = jugador;
        this.velocidadProyectil = velocidadProyectil;
        this.anguloLanzamiento = anguloLanzamiento;
        this.proyectilActivo = true;
        this.renderParticulasProyectil = new RenderParticulasProyectil(20,7,new Color(0.8f,0.75f,0.85f,1));
        this.centroSprite = new Vector2();

        this.velocidadVertical = velocidadProyectil * MathUtils.sinDeg(anguloLanzamiento);
        float factorHorizontal = 0.25f;
        this.velocidadHorizontal = direccionHorizontal * velocidadProyectil * MathUtils.cosDeg(anguloLanzamiento) * factorHorizontal;

        float limiteInferiorVisible = jugador.getControladorEnemigos().getVentanaJuego1().getOrtographicCamera().position.y - jugador.getControladorEnemigos().getVentanaJuego1().getOrtographicCamera().viewportHeight / 2;
        float playerTop = jugador.getSprite().getY() + jugador.getSprite().getHeight() - 10f;
        this.alturaFinal = MathUtils.random(limiteInferiorVisible, playerTop);

        float baseDamage = DANYO_PAPELCULO + extraDamage + MathUtils.random(15f);
        this.damageEscalado = baseDamage * (1f + (poderJugador / 100f));
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        centroSprite.set(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2);
        renderParticulasProyectil.update(centroSprite);

        // Si la animación de impacto está activa, actualizamos su temporizador y esperamos a que termine para desactivar el proyectil
        if (impactoAnimacionActiva) {
            animationStateTime += delta;
            if (impactoAnimation.isAnimationFinished(animationStateTime)) {
                desactivarProyectil();
                impactoAnimacionActiva = false;
            }
            return;
        }

        // Movimiento del proyectil en vuelo
        if (!aterrizado) {
            float nuevaPosX = sprite.getX() + velocidadHorizontal * delta;

            if (velocidadVertical > 0) {
                velocidadVertical -= GRAVEDAD_ASCENSO * delta;
            } else {
                velocidadVertical -= GRAVEDAD_DESCENSO * delta;
            }
            float nuevaPosY = sprite.getY() + velocidadVertical * delta;

            if (nuevaPosY <= alturaFinal) {
                nuevaPosY = alturaFinal;
                aterrizado = true;
                velocidadVertical = 0;
                velocidadHorizontal = 0;

                sprite.setRotation(-5f);
                rotationAngle = 0f;
            }

            sprite.setPosition(nuevaPosX, nuevaPosY);
            rotationAngle += rotationSpeed * delta;
            sprite.rotate(rotationSpeed * delta);
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo) {
            renderParticulasProyectil.render(batch);
            if (!aterrizado && !impactoAnimacionActiva) {
                sprite.draw(batch);
            } else if (impactoAnimacionActiva) {
                TextureRegion currentFrame = impactoAnimation.getKeyFrame(animationStateTime, false);
                float scaleFactorImpact = 5f;
                float scaleX = sprite.getScaleX() * scaleFactorImpact;
                float scaleY = sprite.getScaleY() * scaleFactorImpact;
                // Dibujamos la animación de impacto usando la posición y rotación almacenadas
                batch.draw(currentFrame, impactX, impactY, sprite.getOriginX(), sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), scaleX, scaleY, impactRotation);
            } else if (aterrizado && !impactoAnimacionActiva) {
                sprite.draw(batch);
            }
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
        if (impactoAnimacionActiva) {
            float scaleFactorImpact = 5f;
            float width = sprite.getWidth() * scaleFactorImpact;
            float height = sprite.getHeight() * scaleFactorImpact;
            float rectX = impactX - sprite.getScaleX() * scaleFactorImpact;
            float rectY = impactY - sprite.getScaleY() * scaleFactorImpact;
            return new Rectangle(rectX, rectY, width, height);
        }
        return sprite.getBoundingRectangle();
    }

    public Circle getCirculoColision() {
        if (impactoAnimacionActiva) {
            float scaleFactorImpact = 7.5f;
            float radius = (sprite.getWidth() * scaleFactorImpact) / 2f;
            return new Circle(impactX, impactY, radius);
        }
        // Si no está en fase de explosión, devolvemos un círculo aprox. en base al sprite
        return new Circle(sprite.getX() + sprite.getWidth()/2f, sprite.getY() + sprite.getHeight()/2f, sprite.getWidth()/2f);
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
        return EMPUJE_BASE_PAPELCULO;
    }

    @Override
    public boolean isPersistente() {
        return true;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
        // Si la animación de impacto aún no se ha activado, se activa una única vez
        if (!impactoAnimacionActiva) {
            GestorDeAudio.getInstance().reproducirEfecto("explosion", 0.75f);
            impactoAnimacionActiva = true;
            animationStateTime = 0f;
            impactX = sprite.getX();
            impactY = sprite.getY();
            impactRotation = sprite.getRotation();
        }
        // Luego, para cada enemigo que impacte (y que aún no se haya procesado)
        if (!enemigosImpactados.contains(enemigo)) {
            enemigosImpactados.add(enemigo);
            float enemyCenterX = enemigo.getX() + enemigo.getSprite().getWidth() / 2f;
            float enemyCenterY = enemigo.getY() + enemigo.getSprite().getHeight() / 2f;
            float dx = enemyCenterX - impactX;
            float dy = enemyCenterY - impactY;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist != 0) {
                dx /= dist;
                dy /= dist;
            }
            enemigo.aplicarKnockback(getKnockbackForce(), dx, dy);
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

    public boolean isImpactoAnimacionActiva() {
        return impactoAnimacionActiva;
    }
}

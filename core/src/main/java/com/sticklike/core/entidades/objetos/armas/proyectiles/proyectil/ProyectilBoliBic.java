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
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

import java.util.HashSet;
import java.util.Set;

public class ProyectilBoliBic implements Proyectiles {
    private static Texture textura;
    private Sprite sprite;
    private float velocidadProyectil;
    private float distanciaMaxima;
    private float distanciaRecorrida;
    private boolean proyectilActivo;
    private float direccionX, direccionY;
    private RenderParticulasProyectil renderParticulas;
    private Set<Enemigo> enemigosImpactados = new HashSet<>();
    private float impactoTimer = 0;
    private static final float IMPACTO_DURACION = 0.1f;
    private static final Color DEFAULT_PARTICLE_COLOR = new Color(0, 0, 0.75f, 1);

    public ProyectilBoliBic(float x, float y, float dirX, float dirY, float velocidadProyectil) {
        if (textura == null) {
            textura = manager.get(ARMA_BOLIBIC, Texture.class);
        }
        sprite = new Sprite(textura);
        sprite.setSize(25, 25);

        // Colocamos el sprite de modo que (x,y) sea la punta del proyectil (está orientado hacia la esquina inferior izquierda)
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

        sprite.flip(true, false);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);

        float offset = 315;
        float angle = (float) Math.toDegrees(Math.atan2(dirY, dirX)) - offset;
        sprite.setRotation(angle);

        this.direccionX = dirX;
        this.direccionY = dirY;
        this.velocidadProyectil = velocidadProyectil;
        this.distanciaMaxima = 333;
        this.distanciaRecorrida = 0f;
        this.proyectilActivo = true;

        float scaleFactor = Gdx.graphics.getWidth() / REAL_WIDTH;
        int maxLength = (int) (17.5f * scaleFactor);
        float scaleWidth = 5f * scaleFactor;
        this.renderParticulas = new RenderParticulasProyectil(maxLength, scaleWidth, DEFAULT_PARTICLE_COLOR);
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        // Calculamos el centro del sprite y la posición de la punta para el renderizado de partículas
        Vector2 center = new Vector2(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2);
        Vector2 tipOffset = new Vector2(sprite.getWidth() / 2, sprite.getHeight() - 38f);
        tipOffset.rotateDeg(sprite.getRotation());
        Vector2 tip = center.cpy().add(tipOffset);
        renderParticulas.update(tip);

        // Movemos el sprite según la dirección y velocidad
        float desplazamiento = velocidadProyectil * delta;
        sprite.translate(direccionX * desplazamiento, direccionY * desplazamiento);
        distanciaRecorrida += desplazamiento;
        if (distanciaRecorrida >= distanciaMaxima) {
            desactivarProyectil();
        }

        // Si hubo impacto, gestionamos el temporizador y reiniciamos el color de sprite y partículas
        if (!enemigosImpactados.isEmpty()) {
            impactoTimer += delta;
            if (impactoTimer >= IMPACTO_DURACION) {
                // Se reinician los colores, pero no limpiamos el conjunto, evitando reimpactar al mismo enemigo
                impactoTimer = 0;
                sprite.setColor(1, 1, 1, 1);
                renderParticulas.setColor(DEFAULT_PARTICLE_COLOR);
            }
        }
    }


    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo) {
            renderParticulas.setAlphaMult(0.75f);
            renderParticulas.render(batch);
            sprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        textura = null;
        renderParticulas.dispose();
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
        float baseDamage = 5 + MathUtils.random(DANYO_BOLIBIC);
        return baseDamage * (1f + (Jugador.getPoderJugador() / 100f));
    }

    @Override
    public float getKnockbackForce() {
        return EMPUJE_BASE_BOLI;
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
            renderParticulas.setColor(Color.RED);
            GestorDeAudio.getInstance().reproducirEfecto("impactoBase", 1);
            impactoTimer = 0;
        }
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return enemigosImpactados.contains(enemigo);
    }

    @Override
    public boolean esCritico() {
        return Math.random() < Jugador.getCritico();
    }
}

package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.renderizado.RenderParticulasProyectil;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;
import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;

public class ProyectilBoliBic implements Proyectiles {
    private static Texture textura;
    private Sprite sprite;
    private float velocidadProyectil;
    private float distanciaMaxima;
    private float distanciaRecorrida;
    private boolean proyectilActivo;
    private float direccionX, direccionY;
    private Jugador jugador;
    private RenderParticulasProyectil renderParticulas;


    public ProyectilBoliBic(float x, float y, float dirX, float dirY, float velocidadProyectil, Jugador jugador) {
        if (textura == null) {
            textura = manager.get(ARMA_BOLIBIC, Texture.class);
        }
        sprite = new Sprite(textura);
        sprite.setSize(25, 25);

        // Colocamos el sprite de modo que (x,y) sea la punta del proyectil
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

        sprite.flip(true, false);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);

        // Calculamos la rotación para que la punta apunte en la dirección de disparo
        float offset = 315;
        float angle = (float) Math.toDegrees(Math.atan2(dirY, dirX)) - offset;
        sprite.setRotation(angle);

        this.direccionX = dirX;
        this.direccionY = dirY;
        this.velocidadProyectil = velocidadProyectil;
        this.distanciaMaxima = 666;
        this.distanciaRecorrida = 0f;
        this.proyectilActivo = true;
        this.jugador = jugador;

        float scaleFactor = Gdx.graphics.getWidth() / REAL_WIDTH;
        int maxLength = (int) (22 * scaleFactor);
        float scaleWidth = 5f * scaleFactor;
        this.renderParticulas = new RenderParticulasProyectil(maxLength, scaleWidth, new Color(0, 0, 0.75f, 1));
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        // Calculamos el centro del sprite
        Vector2 center = new Vector2(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2);
        // Definimos un offset ajustado manualmente para que coincida con la punta del proyectil
        Vector2 tipOffset = new Vector2(sprite.getWidth() / 2, sprite.getHeight() - 38f);
        // Rotamos ese offset según la rotación actual del sprite
        tipOffset.rotateDeg(sprite.getRotation());
        Vector2 tip = center.cpy().add(tipOffset); // La posición de la punta es el centro más el offset
        renderParticulas.update(tip);

        // Mover el sprite según la dirección
        float desplazamiento = velocidadProyectil * delta;
        sprite.translate(direccionX * desplazamiento, direccionY * desplazamiento);
        distanciaRecorrida += desplazamiento;
        if (distanciaRecorrida >= distanciaMaxima) {
            desactivarProyectil();
        }
    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo) {
            renderParticulas.setAlphaMult(0.5f);
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
        return DANYO_BOLIBIC * (1f + (Jugador.getPoderJugador() / 100f));
    }

    @Override
    public float getKnockbackForce() {
        return EMPUJE_BASE_BOLI;
    }

    @Override
    public boolean isPersistente() {
        return false;
    }

    @Override
    public void registrarImpacto(Enemigo enemigo) {
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return false;
    }

    @Override
    public boolean esCritico() {
        return Math.random() < Jugador.getCritico();
    }
}

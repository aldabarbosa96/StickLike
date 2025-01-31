package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

import java.util.HashSet;
import java.util.Set;

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


    public ProyectilCalcetin(float x, float y, float direccionX, float direccionY, float velocidadProyectil, float multiplicadorVelocidad) {
        if (textura == null) {
            textura = armaCalcetin;
        }
        this.distanciaMaxima = MAX_DISTANCIA;
        this.distanciaRecorrida = DISTANCIA_RECORRIDA;

        sprite = new Sprite(textura);
        sprite.setSize(CALCETIN_W_SIZE, CALCETIN_H_SIZE);
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
        return  6 + (float) Math.random() * 7;
    }

    @Override
    public float getKnockbackForce() {
        return EMPUJE_BASE_CALCETIN;
    }

    @Override
    public boolean isPersistente() { // calcetín no desaparece al impactar
        return  true;
    }
    @Override
    public void registrarImpacto(Enemigo enemigo) {
        enemigosImpactados.add(enemigo);
    }

    @Override
    public boolean yaImpacto(Enemigo enemigo) {
        return enemigosImpactados.contains(enemigo);
    }
}

package com.sticklike.core.entidades.objetos.armas.proyectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaquePedo;
import com.sticklike.core.interfaces.Enemigo;
import com.sticklike.core.interfaces.Proyectiles;
import static com.sticklike.core.utilidades.GestorConstantes.*;
import static com.sticklike.core.utilidades.GestorDeAssets.*;

import java.util.HashSet;
import java.util.Set;

public class ProyectilPedo implements Proyectiles {
    private static Texture textura;
    private Sprite sprite;
    private boolean proyectilActivo;
    private Set<Enemigo> enemigosImpactados = new HashSet<>();
    private float temporizadorDano = 0;
    private Jugador jugador;
    private AtaquePedo ataquePedo;
    private float radioColision;
    private float offsetAngle;
    private float radio;
    private float rotacionSprite = 0f;

    public ProyectilPedo(Jugador jugador, AtaquePedo ataquePedo, float offsetAngle, float radio) {
        if (textura == null) {
            textura = armaPedo;
        }
        this.jugador = jugador;
        this.sprite = new Sprite(textura);
        this.sprite.setSize(NUBE_PEDO_SIZE, NUBE_PEDO_SIZE);
        this.sprite.setOriginCenter();
        this.proyectilActivo = true;
        this.radioColision = RADIO_NUBE_PEDO;
        this.ataquePedo = ataquePedo;
        this.offsetAngle = offsetAngle;
        this.radio = radio;
    }

    @Override
    public void actualizarProyectil(float delta) {
        if (!proyectilActivo) return;

        rotacionSprite += 360 * delta;

        // Calcula el ángulo actual sumando el ángulo global con el offset fijo de cada nube
        float currentAngle = ataquePedo.getGlobalAngle() + offsetAngle;
        float radianes = (float) Math.toRadians(currentAngle);
        float offsetX = (float) (Math.cos(radianes) * radio);
        float offsetY = (float) (Math.sin(radianes) * radio);

        // Posición relativa al centro del jugador
        float jugadorCentroX = jugador.getSprite().getX() + jugador.getSprite().getWidth() / 2;
        float jugadorCentroY = jugador.getSprite().getY() + jugador.getSprite().getHeight() / 2;

        sprite.setPosition(jugadorCentroX + offsetX - sprite.getWidth() / 2, jugadorCentroY + offsetY - sprite.getHeight() / 2);
        sprite.setRotation(rotacionSprite);


        temporizadorDano += delta;
        if (temporizadorDano >= INTERVALO_NUBE) {
            enemigosImpactados.clear();
            temporizadorDano = 0;
        }

    }

    @Override
    public void renderizarProyectil(SpriteBatch batch) {
        if (proyectilActivo) {
            sprite.draw(batch);
        }
    }

    @Override
    public Rectangle getRectanguloColision() {
        return new Rectangle(sprite.getX() + sprite.getWidth() / 2 - radioColision / 2,
            sprite.getY() + sprite.getHeight() / 2 - radioColision / 2, radioColision, radioColision);
    }

    @Override
    public void dispose() {
        textura = null;
    }

    @Override
    public float getX() { return sprite.getX(); }

    @Override
    public float getY() { return sprite.getY(); }

    @Override
    public boolean isProyectilActivo() { return proyectilActivo; }

    @Override
    public void desactivarProyectil() { proyectilActivo = false; }

    @Override
    public float getBaseDamage() { return DANYO_NUBE_PEDO; }

    @Override
    public float getKnockbackForce() { return 40f; }

    @Override
    public boolean isPersistente() { return true; }

    @Override
    public void registrarImpacto(Enemigo enemigo) { enemigosImpactados.add(enemigo); }

    @Override
    public boolean yaImpacto(Enemigo enemigo) { return enemigosImpactados.contains(enemigo); }

    public AtaquePedo getAtaquePedo() {
        return ataquePedo;
    }
}

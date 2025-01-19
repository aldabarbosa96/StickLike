package com.sticklike.core.entidades.enemigos.culo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.objetosxp.ObjetoXpCaca;
import com.sticklike.core.utilidades.GestorDeAssets;
import com.sticklike.core.entidades.interfaces.Enemigo;

/**
 * La clase Enemy gestiona el renderizado y actualizaciones de cada enemigo,
 * además de su estado y variables
 */
public class EnemigoCulo implements Enemigo {
    private Sprite sprite;
    private Jugador jugador;
    private float vidaEnemigo = 65f;
    private MovimientoCulo movimientoCulo;
    private float coolDownDanyo = 1f;
    private float temporizadorDanyo = 0f;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;

    /**
     * @param x,y              cordenadas gestionan el spawn de los enemigos
     * @param jugador          necesitamos acceder para calcular distancias y movimiento
     * @param velocidadEnemigo velocidad de movimiento del enemigo
     */
    public EnemigoCulo(float x, float y, Jugador jugador, float velocidadEnemigo) {
        sprite = new Sprite(GestorDeAssets.enemigoCulo);
        sprite.setSize(32, 27);
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoCulo = new MovimientoCulo(velocidadEnemigo);
    }

    /**
     * Renderiza el enemigo en pantalla, si no está muerto
     *
     * @param batch SpriteBatch para dibujar el sprite del enemigo
     */
    @Override
    public void renderizar(SpriteBatch batch) {
        if (!estaMuerto()) {
            sprite.draw(batch);
        }
    }

    @Override
    public void actualizar(float delta) {
        if (!estaMuerto()) {
            movimientoCulo.actualizarMovimiento(delta, sprite, jugador);
        }

        if (temporizadorDanyo > 0) {
            temporizadorDanyo -= delta;
        }
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetoXpCaca sueltaObjetoXP() {
        if (!haSoltadoXP) {
            haSoltadoXP = true;
            //System.out.println("Generando XPObject...");
            return new ObjetoXpCaca(this.getX(), this.getY());
        }
        return null;
    }

    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
    }
    @Override
    public boolean haSoltadoXP() {
        return haSoltadoXP;
    }

    @Override
    public boolean puedeAplicarDanyo() {
        return temporizadorDanyo <= 0;
    }

    @Override
    public void reseteaTemporizadorDanyo() {
        temporizadorDanyo = coolDownDanyo;
    }

    @Override
    public boolean estaMuerto() {
        boolean dead = vidaEnemigo <= 0;
        return dead;
    }
    @Override
    public void dispose() {
        sprite = null;
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
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public boolean isProcesado() {
        return procesado;
    }

    @Override
    public void setProcesado(boolean procesado) {
        this.procesado = procesado;
    }
}

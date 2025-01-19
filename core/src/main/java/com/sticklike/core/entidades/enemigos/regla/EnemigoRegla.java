package com.sticklike.core.entidades.enemigos.regla;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.jugador.ColisionesJugador;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.objetosxp.ObjetoXpCaca;
import com.sticklike.core.utilidades.GestorDeAssets;
import com.sticklike.core.interfaces.Enemigo;

/**
 * La clase EnemigoRegla gestiona el renderizado y actualizaciones de este enemigo,
 * además de su estado y comportamiento específico.
 */
public class EnemigoRegla implements Enemigo {
    private Sprite sprite;
    private Jugador jugador;
    private float vidaEnemigo = 100f; // Vida inicial específica para este enemigo
    private MovimientoRegla movimientoEnemigo;
    private ColisionesJugador colisionesJugador;
    private OrthographicCamera orthographicCamera;
    private float coolDownDanyo = 1.5f;
    private float temporizadorDanyo = 0f;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;

    /**
     * @param x,y              coordenadas gestionan el spawn de los enemigos
     * @param jugador          necesitamos acceder para calcular distancias y movimiento
     * @param velocidadEnemigo velocidad de movimiento del enemigo
     */
    public EnemigoRegla(float x, float y, Jugador jugador, float velocidadEnemigo, OrthographicCamera orthographicCamera) {
        sprite = new Sprite(GestorDeAssets.enemigoRegla);
        sprite.setSize(6, 38);
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoEnemigo = new MovimientoRegla(velocidadEnemigo,540);
        this.orthographicCamera = orthographicCamera;
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
            movimientoEnemigo.actualizarMovimiento(delta, sprite, jugador,orthographicCamera);
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
            return new ObjetoXpCaca(this.getX(), this.getY()); // Suelta el objeto XP específico
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
        return vidaEnemigo <= 0;
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

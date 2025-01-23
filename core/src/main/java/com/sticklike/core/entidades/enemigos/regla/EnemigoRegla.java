package com.sticklike.core.entidades.enemigos.regla;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.AnimacionesEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.objetosxp.ObjetoVida;
import com.sticklike.core.utilidades.GestorDeAssets;
import com.sticklike.core.interfaces.Enemigo;

/**
 * La clase EnemigoRegla gestiona el renderizado y actualizaciones de este enemigo,
 * además de su estado y comportamiento específico.
 */
public class EnemigoRegla implements Enemigo {
    private Sprite sprite;
    private Jugador jugador;
    private float vidaEnemigo = 100f; // Vida inicial
    private MovimientoRegla movimientoEnemigo;
    private OrthographicCamera orthographicCamera;
    private float coolDownDanyo = 1.5f;
    private float temporizadorDanyo = 0f;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesEnemigos animacionesEnemigos;
    /**
     * @param x,y              coordenadas gestionan el spawn de los enemigos
     * @param jugador          necesitamos acceder para calcular distancias y movimiento
     * @param velocidadEnemigo velocidad de movimiento del enemigo
     */
    public EnemigoRegla(float x, float y, Jugador jugador, float velocidadEnemigo, OrthographicCamera orthographicCamera) {
        esCruzada();
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoEnemigo = new MovimientoRegla(velocidadEnemigo,666);
        this.orthographicCamera = orthographicCamera;
        this.animacionesEnemigos = new AnimacionesEnemigos();
    }

    private void esCruzada(){
        float random = (float) (Math.random() * 10);
        if (random >= 5){
            sprite = new Sprite(GestorDeAssets.enemigoReglaCruzada);
            sprite.setSize(32, 32);
        } else {
            sprite = new Sprite(GestorDeAssets.enemigoRegla);
            sprite.setSize(6,32);
        }
    }

    /**
     * Renderiza el enemigo en pantalla, si no está muerto
     *
     * @param batch SpriteBatch para dibujar el sprite del enemigo
     */
    @Override
    public void renderizar(SpriteBatch batch) {
        if (!estaMuerto()) {
            // Guarda el color original del sprite
            Color originalColor = sprite.getColor().cpy();

            // Aplica el parpadeo si está activo
            if (animacionesEnemigos.estaEnParpadeo()) {
                animacionesEnemigos.aplicarParpadeo(sprite);
            }

            // Dibuja el sprite una sola vez
            sprite.draw(batch);

            // Restaura el color original
            if (animacionesEnemigos.estaEnParpadeo()) {
                animacionesEnemigos.restaurarColor(sprite, originalColor);
            }
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
        animacionesEnemigos.actualizarParpadeo(delta);
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetoVida sueltaObjetoXP() {
        if (!haSoltadoXP) {
            haSoltadoXP = true;
            return new ObjetoVida(this.getX(), this.getY()); // Suelta el objeto XP específico
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
    public void activarParpadeo(float duracion) {
        animacionesEnemigos.activarParpadeo(duracion);
    }
    @Override
    public void setProcesado(boolean procesado) {
        this.procesado = procesado;
    }
}

package com.sticklike.core.entidades.enemigos.culo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.enemigos.AnimacionesEnemigos;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.objetosxp.ObjetoXpCaca;
import com.sticklike.core.utilidades.GestorConstantes;
import com.sticklike.core.utilidades.GestorDeAssets;
import com.sticklike.core.interfaces.Enemigo;

/**
 * La clase EnemigoCulo gestiona el renderizado y actualizaciones de cada enemigo,
 * además de su estado y variables.
 */
public class EnemigoCulo implements Enemigo {
    private Sprite sprite;
    private Jugador jugador;
    private float vidaEnemigo = GestorConstantes.VIDA_ENEMIGOCULO;
    private MovimientoCulo movimientoCulo;
    private float coolDownDanyo = GestorConstantes.COOLDOWN_ENEMIGOCULO;
    private float temporizadorDanyo = GestorConstantes.TEMPORIZADOR_DANYO;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private AnimacionesEnemigos animacionesEnemigos;

    /**
     * @param x,y              Coordenadas que gestionan el spawn de los enemigos.
     * @param jugador          Necesitamos acceder para calcular distancias y movimiento.
     * @param velocidadEnemigo Velocidad de movimiento del enemigo.
     */
    public EnemigoCulo(float x, float y, Jugador jugador, float velocidadEnemigo) {
        esConOjo();
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.movimientoCulo = new MovimientoCulo(velocidadEnemigo, true);
        this.animacionesEnemigos = new AnimacionesEnemigos();
    }

    private void esConOjo() { // maneja la aleatoriedad de aparición de enemigoCulo con o sin ojo
        float random = (float) (Math.random() * 10);
        if (random >= 2.5f) {
            sprite = new Sprite(GestorDeAssets.enemigoCulo);
            sprite.setSize(32, 28);
        } else {
            sprite = new Sprite(GestorDeAssets.enemigoCuloOjo);
            sprite.setSize(40, 34);
            vidaEnemigo = GestorConstantes.VIDA_ENEMIGOCULO * 2;

        }
    }

    @Override
    public void aplicarKnockback(float fuerza, float dirX, float dirY) {
        movimientoCulo.aplicarKnockback(fuerza, dirX, dirY);
    }


    /**
     * Renderiza el enemigo en pantalla, si no está muerto.
     *
     * @param batch SpriteBatch para dibujar el sprite del enemigo.
     */
    @Override
    public void renderizar(SpriteBatch batch) {
        // 1) Comprobamos si seguimos “vivo” o en fade
        boolean mostrarSprite = (vidaEnemigo > 0) || animacionesEnemigos.estaEnFade();

        if (mostrarSprite) {
            Color originalColor = sprite.getColor().cpy();

            // 2) Aplicar parpadeo rojo si procede
            if (animacionesEnemigos.estaEnParpadeo()) {
                animacionesEnemigos.aplicarParpadeo1(sprite);
            } else {

                sprite.setColor(originalColor.r, originalColor.g, originalColor.b, animacionesEnemigos.getAlphaActual());
            }

            // 3) Dibujar
            sprite.draw(batch);

            // 4) Restaurar color
            animacionesEnemigos.restaurarColor(sprite, originalColor);
        }
    }


    @Override
    public void actualizar(float delta) {
        animacionesEnemigos.actualizarParpadeo(delta);
        animacionesEnemigos.actualizarFade(delta);
        movimientoCulo.actualizarMovimiento(delta, sprite, jugador);
    }


    @Override
    public void activarParpadeo(float duracion) {
        animacionesEnemigos.activarParpadeo(duracion);
    }

    @Override
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    @Override
    public ObjetoXpCaca sueltaObjetoXP() {
        if (!haSoltadoXP) {
            haSoltadoXP = true;
            return new ObjetoXpCaca(this.getX(), this.getY());
        }
        return null;
    }

    @Override
    public void reducirSalud(float amount) {
        vidaEnemigo -= amount;
        if (vidaEnemigo <= 0) {
            // En lugar de marcarlo como “muerto” final, activamos el fade
            if (!animacionesEnemigos.estaEnFade()) {
                animacionesEnemigos.iniciarFadeMuerte(0.2f);
                animacionesEnemigos.activarParpadeo(0.15f);
            }
        }
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
        return (vidaEnemigo <= 0 && !animacionesEnemigos.estaEnFade());
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

    // En EnemigoCulo:

    public float getVelocidad() {
        return movimientoCulo.getVelocidadEnemigo();
    }

    public void setVelocidad(float nuevaVelocidad) {
        movimientoCulo.setVelocidadEnemigo(nuevaVelocidad);
    }

    @Override
    public float getVida() {
        return vidaEnemigo;
    }
}

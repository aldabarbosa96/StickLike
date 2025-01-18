package com.sticklike.core.entidades.personajes;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.entidades.objetos.ObjetoXP;
import com.sticklike.core.utilidades.GestorDeAssets;
import com.sticklike.core.utilidades.GestorConstantes;

/**
 * La clase Enemy gestiona el renderizado y actualizaciones de cada enemigo,
 * además de su estado y variables
 */
public class Enemigo {
    private Sprite sprite;
    private Jugador jugador;
    private float vidaEnemigo = 65f;
    private float velocidadEnemigo;
    private float tempMovimiento, duracionPausa, duracionMovimiento;
    private boolean seMueve;
    private float coolDownDanyo = 1f;
    private float temporizadorDanyo = 0f;
    private boolean haSoltadoXP = false;
    private boolean procesado = false;
    private static final float MAX_PAUSE = GestorConstantes.ENEMY_MAX_PAUSE;
    private static final float MIN_PAUSE = GestorConstantes.ENEMY_MIN_PAUSE;
    private static final float MIN_MOVE_DURATION = GestorConstantes.ENEMY_MIN_MOVE_DURATION;
    private static final float MAX_MOVE_DURATION = GestorConstantes.ENEMY_MAX_MOVE_DURATION;

    /**
     * @param x,y cordenadas gestionan el spawn de los enemigos
     * @param jugador necesitamos acceder para calcular distancias y movimiento
     * @param velocidadEnemigo velocidad de movimiento del enemigo
     */
    public Enemigo(float x, float y, Jugador jugador, float velocidadEnemigo) {
        sprite = new Sprite(GestorDeAssets.enemigoCulo);
        sprite.setSize(32, 27);
        sprite.setPosition(x, y);
        this.jugador = jugador;
        this.velocidadEnemigo = velocidadEnemigo;
        this.tempMovimiento = 1;
        this.seMueve = true;
        calcularDuracionPausa();
        calcularDuracionMovimiento();
    }

    /**
     * Renderiza el enemigo en pantalla, si no está muerto
     *
     * @param batch SpriteBatch para dibujar el sprite del enemigo
     */
    public void renderizarEnemigo(SpriteBatch batch) {
        if (!estaMuerto()) {
            sprite.draw(batch);
        }
    }

    /**
     * Actualiza la posición y comportamiento del enemigo en base a la posición
     * del Player, añadiendo un pequeño factor de error (zig-zag). Incluye pausas y
     * velocidades aleatorias dentro de unos parámetros
     *
     * @param delta
     */
    public void actualizarEnemigo(float delta) {
        tempMovimiento += delta;

        if (seMueve) {
            if (tempMovimiento >= duracionMovimiento) {
                seMueve = false;
                tempMovimiento = 0;
                duracionPausa = MIN_PAUSE + (float) Math.random() * (MAX_PAUSE - MIN_PAUSE);
            } else {

                float enemyPosX = getX();
                float enemyPosY = getY();

                float playerPosX = jugador.getSprite().getX();
                float playerPosY = jugador.getSprite().getY();

                float difX = playerPosX - enemyPosX;
                float difY = playerPosY - enemyPosY;

                // Añadimos un desplazamiento aleatorio para simular movimiento diagonal.
                float randomOffsetX = (float) Math.random() * 100 - 50;
                float randomOffsetY = (float) Math.random() * 100 - 25;

                difX += randomOffsetX;
                difY += randomOffsetY;

                float distance = (float) Math.sqrt(difX * difX + difY * difY);

                if (distance != 0) {
                    difX /= distance;
                    difY /= distance;
                }
                float movementX = difX * velocidadEnemigo * delta;
                float movementY = difY * velocidadEnemigo * delta;

                sprite.translate(movementX, movementY);
            }
        } else {
            if (tempMovimiento >= duracionPausa) {
                seMueve = true;
                tempMovimiento = 0;
                duracionMovimiento = MIN_MOVE_DURATION + (float) Math.random() * (MAX_MOVE_DURATION - MIN_MOVE_DURATION);
            }
        }
        if (temporizadorDanyo > 0) {
            temporizadorDanyo -= delta;
        }
    }

    /**
     * Calcula de forma aleatoria dentro de unos parámetros la duración de la pausa del enemigo
     */
    public void calcularDuracionPausa() {
        this.duracionPausa = GestorConstantes.ENEMY_MIN_PAUSE + (float) Math.random() * (GestorConstantes.ENEMY_MAX_PAUSE - GestorConstantes.ENEMY_MIN_PAUSE);

    }

    /**
     * Calcula de forma aleatoria dentro de unos parámetros la duración del movimiento del enemigo
     */
    public void calcularDuracionMovimiento() {
        this.duracionMovimiento = GestorConstantes.ENEMY_MIN_MOVE_DURATION + (float) Math.random() * (GestorConstantes.ENEMY_MAX_MOVE_DURATION - GestorConstantes.ENEMY_MIN_MOVE_DURATION);

    }

    /**
     * Verifica si un proyectil con un bounding box especificado golpea al enemigo
     *
     * @param projectileX,projectileY coordenadas del proyectil
     * @param projectileWidth,projectileHeight ancho y alto del bounding box del proyectil
     * @return true si el proyectil golpea al enemigo
     */
    public boolean esGolpeadoPorProyectil(float projectileX, float projectileY, float projectileWidth, float projectileHeight) {
        return sprite.getBoundingRectangle().overlaps(new Rectangle(projectileX, projectileY, projectileWidth, projectileHeight));
    }

    /**
     * Genera un objeto de experiencia (XPobjects) si no ha sido generado antes
     *
     * @return el objeto de experiencia generado, o null si ya se generó previamente
     */
    public ObjetoXP sueltaObjetoXP() {
        if (!haSoltadoXP) {
            haSoltadoXP = true;
            //System.out.println("Generando XPObject...");
            return new ObjetoXP(this.getX(), this.getY());
        }
        return null;
    }

    public boolean haSoltadoXP() {
        return haSoltadoXP;
    }

    /**
     * Indica si el enemigo puede hacer daño al Player (cooldown cumplido).
     *
     * @return true si puede hacer daño, false si está en cooldown
     */
    public boolean puedeAplicarDanyo() {
        return temporizadorDanyo <= 0;
    }

    /**
     * Reinicia el cooldown de daño para evitar que el enemigo golpee continuamente.
     */
    public void reseteaTemporizadorDanyo() {
        temporizadorDanyo = coolDownDanyo;
    }

    /**
     * Aplica daño al enemigo.
     *
     * @param amount cantidad de daño a restar a la salud (health)
     */
    public void reduceHealth(float amount) {
        vidaEnemigo -= amount;
    }

    /**
     * Verifica si el enemigo está muerto.
     *
     * @return true si la salud es 0 o menor, false en caso contrario
     */
    public boolean estaMuerto() {
        boolean dead = vidaEnemigo <= 0;
        return dead;
    }

    public void dispose() {
        sprite = null;
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public float getVidaEnemigo() {
        return vidaEnemigo;
    }

    public void setVidaEnemigo(float vidaEnemigo) {
        this.vidaEnemigo = vidaEnemigo;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public boolean isProcesado() {
        return procesado;
    }

    public void setProcesado(boolean procesado) {
        this.procesado = procesado;
    }
}

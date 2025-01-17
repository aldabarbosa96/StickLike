package com.sticklike.core.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.managers.ControladorEnemigos;
import com.sticklike.core.managers.ControladorProyectiles;
import com.sticklike.core.utils.GestorDeAssets;
import com.sticklike.core.utils.GestorConstantes;

/**
 * La clase Player representa al personaje controlado por el jugador.
 * Gestiona la lógica de movimiento, la salud, las animaciones
 * y el disparo automático hacia los enemigos.
 */
public class Jugador {

    /**
     * Representa las direcciones posibles del jugador
     * todo --> mover a clase separada
     */
    private enum Direction { LEFT, RIGHT, IDLE }

    // Gestores
    private Sprite sprite;
    private ControladorEnemigos controladorEnemigos;
    private ControladorProyectiles controladorProyectiles;

    // Atributos
    private float velocidadJugador;
    private float vidaJugador;
    private float maxVidaJugador;
    private float rangoAtaqueJugador;
    private float intervaloDisparo;
    private float temporizadorDisparo = 0;
    private int proyectilesPorDisparo = 1;
    private boolean estaMuerto;
    private Direction direccionActual = Direction.IDLE;

    // Animaciones
    private Animation<TextureRegion> animacionMovDerecha;
    private Animation<TextureRegion> animacionMovIzquierda;
    private Animation<TextureRegion> animacionIdle;
    private float temporizadorAnimacion = 0;

    /**
     * Inicializa posiciones, variables de estado y carga animaciones
     * @param startX,startY coordenadas donde se visualiza al Jugador
     */
    public Jugador(float startX, float startY) {
        this.velocidadJugador = GestorConstantes.PLAYER_SPEED;
        this.vidaJugador = GestorConstantes.PLAYER_HEALTH;
        this.maxVidaJugador = GestorConstantes.PLAYER_MAX_HEALTH;
        this.rangoAtaqueJugador = GestorConstantes.PLAYER_ATTACK_RANGE;
        this.intervaloDisparo = GestorConstantes.PLAYER_SHOOT_INTERVAL;

        sprite = new Sprite(GestorDeAssets.stickman);
        sprite.setSize(15, 45);
        sprite.setPosition(startX, startY);

        controladorProyectiles = new ControladorProyectiles();
        estaMuerto = false;

        // Cargamos animaciones
        animacionIdle = GestorDeAssets.animations.get("idle");
        animacionMovDerecha = GestorDeAssets.animations.get("moveRight");
        animacionMovIzquierda = GestorDeAssets.animations.get("moveLeft");
    }

    /**
     * Actualiza la lógica del jugador (movimiento, disparo y colisiones con enemigos).
     *
     * @param delta tiempo transcurrido desde el último frame
     * @param paused indica si el juego está en pausa (pop-up de upgrades activo, por ejemplo)
     * @param dmgText array que gestiona textos flotantes (daño aplicado)
     */
    public void actualizarJugador(float delta, boolean paused, Array<TextoFlotante> dmgText) {
        if (estaMuerto) return;

        if (!paused) {
            // Actualizamos input y lógica
            controlarInputs(delta);
            temporizadorDisparo += delta;
            if (temporizadorDisparo >= intervaloDisparo) {
                temporizadorDisparo = 0;
                autoAtaque();
            }
            // Chequeo de colisión con enemigos
            if (controladorEnemigos != null) {
                for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
                    if (enColision(enemigo) && enemigo.puedeAplicarDanyo()) {
                        recibeDanyo(2);
                        enemigo.reseteaTemporizadorDanyo();
                    }
                }
            }
        } else {
            // Si está pausado, forzamos idle (aunque se pulse A o D).
            direccionActual = Direction.IDLE;
        }

        // Avanzamos el timer de animación para que Idle no se quede congelado
        temporizadorAnimacion += delta;

        // Actualizamos proyectiles
        controladorProyectiles.actualizarProyectiles(delta,
            (controladorEnemigos != null ? controladorEnemigos.getEnemigos() : null),
            dmgText);
    }

    /**
     * Maneja la entrada de teclado para mover al jugador y determinar su dirección (WASD)
     * todo --> manejar los inputs con mando
     *
     * @param delta tiempo transcurrido desde el último frame
     */
    private void controlarInputs(float delta) {
        float movX = 0;
        float movY = 0;

        boolean pressLeft  = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean pressRight = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean pressUp    = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean pressDown  = Gdx.input.isKeyPressed(Input.Keys.S);

        // Movimiento horizontal
        if (pressLeft) {
            movX -= velocidadJugador * delta;
            direccionActual = Direction.LEFT;
        } else if (pressRight) {
            movX += velocidadJugador * delta;
            direccionActual = Direction.RIGHT;
        } else {
            direccionActual = Direction.IDLE;
        }

        // Movimiento vertical
        if (pressUp) {
            movY += velocidadJugador * delta;
        }
        if (pressDown) {
            movY -= velocidadJugador * delta;
        }

        // Movimientos diagonales normalizados para igualar velocidad
        if (movX != 0 && movY != 0) {
            float factor = (float)(1 / Math.sqrt(2));
            movX *= factor;
            movY *= factor;
        }

        // Aplicamos la traslación al sprite
        sprite.translate(movX, movY);
    }

    /**
     * Lógica de disparo automático: busca el enemigo más cercano en el rango y
     * dispara proyectiles hacia él
     */
    private void autoAtaque() {
        if (controladorEnemigos == null) return;
        Enemigo target = localizarEnemigoMasCercanoEnRango();
        if (target == null) return;

        float startX = sprite.getX() + sprite.getWidth()/2;
        float startY = sprite.getY() + sprite.getHeight()/2;

        float targetX = target.getX() + target.getSprite().getWidth()/2;
        float targetY = target.getY() + target.getSprite().getHeight()/2;

        float[] dir = calcularDireccionNormalizada(startX, startY, targetX, targetY);

        for (int i = 0; i < proyectilesPorDisparo; i++) {
            float angleOffset = (i - (proyectilesPorDisparo - 1)/2f) * 5f;
            float adjustedX = (float)(dir[0]*Math.cos(Math.toRadians(angleOffset)) - dir[1]*Math.sin(Math.toRadians(angleOffset)));
            float adjustedY = (float)(dir[0]*Math.sin(Math.toRadians(angleOffset)) + dir[1]*Math.cos(Math.toRadians(angleOffset)));

            controladorProyectiles.anyadirNuevoProyectil(startX, startY, adjustedX, adjustedY, target);
        }
    }

    /**
     * Busca el enemigo más cercano dentro del rango de ataque
     *
     * @return el enemigo más cercano, o null si no hay ninguno en el rango
     */
    private Enemigo localizarEnemigoMasCercanoEnRango() {
        if (controladorEnemigos == null) return null;
        float closestDist = Float.MAX_VALUE;
        Enemigo closest = null;
        for (Enemigo e : controladorEnemigos.getEnemigos()) {
            if (!e.estaMuerto()) {
                float dx = e.getX() - sprite.getX();
                float dy = e.getY() - sprite.getY();
                float dist = (float)Math.sqrt(dx*dx + dy*dy);
                if (dist < closestDist && dist <= rangoAtaqueJugador) {
                    closestDist = dist;
                    closest = e;
                }
            }
        }
        return closest;
    }

    /**
     * Calcula la dirección normalizada entre dos puntos (sx, sy) y (tx, ty)
     *
     * @return un array de dos floats {dx, dy} representando la dirección
     */
    private float[] calcularDireccionNormalizada(float sx, float sy, float tx, float ty) {
        float dx = tx - sx;
        float dy = ty - sy;
        float dist = (float)Math.sqrt(dx*dx + dy*dy);
        if (dist == 0) dist = 1f;
        return new float[]{ dx/dist, dy/dist };
    }

    /**
     * Dibuja la animación actual del jugador (según la dirección) y los proyectiles
     *
     * @param batch SpriteBatch para renderizar
     */
    public void renderizarJugadorYProyectil(SpriteBatch batch) {
        if (!estaMuerto) {
            TextureRegion currentFrame;
            switch (direccionActual) {
                case LEFT:
                    currentFrame = animacionMovIzquierda.getKeyFrame(temporizadorAnimacion, true);
                    break;
                case RIGHT:
                    currentFrame = animacionMovDerecha.getKeyFrame(temporizadorAnimacion, true);
                    break;
                default:
                    currentFrame = animacionIdle.getKeyFrame(temporizadorAnimacion, true);
                    break;
            }
            batch.draw(currentFrame, sprite.getX(), sprite.getY(),
                sprite.getWidth(), sprite.getHeight());
        }
        controladorProyectiles.renderizarProyectiles(batch);
    }

    /**
     * Verifica si hay colisión con un Enemigo, comprobando si sus bounding rectangles se solapan
     *
     * @param enemigo enemigo con el que se evalúa la colisión
     * @return true si colisionan
     */
    private boolean enColision(Enemigo enemigo) {
        return sprite.getBoundingRectangle().overlaps(enemigo.getSprite().getBoundingRectangle());
    }

    /**
     * Aplica daño al jugador
     *
     * @param amount cantidad de daño a reducir de la salud
     */
    public void recibeDanyo(float amount) {
        if (estaMuerto) return;
        vidaJugador -= amount;
        if (vidaJugador <= 0) {
            vidaJugador = 0;
            muere();
        }
    }

    /**
     * Lógica de muerte del jugador. Marca estaMuerto en true y lanza un mensaje de game over
     */
    private void muere() {
        estaMuerto = true;
        System.out.println("GAME OVER (player died)");
        // todo --> añadir animación o interacción al morir
    }

    public void dispose() {
        // Liberamos la textura del sprite
        if (sprite != null && sprite.getTexture() != null) {
            sprite.getTexture().dispose();
        }
        // Liberamos manager de proyectiles
        if (controladorProyectiles != null) {
            controladorProyectiles.dispose();
        }
    }

    /**
     * @return fracción de salud actual / salud máxima (valor de 0 a 1)
     */
    public float obtenerPorcetajeVida() {
        return vidaJugador / maxVidaJugador;
    }


    // Métodos de mejora de stats
    public void aumentarVelocidad(float percentage) {
        velocidadJugador += velocidadJugador * percentage;
    }
    public void aumentarRangoAtaque(float percentage) {
        rangoAtaqueJugador += rangoAtaqueJugador * percentage;
    }
    public void aumentarDanyo(float amount) {
        controladorProyectiles.aumentarDanyoProyectil(amount);
    }
    public void reducirIntervaloDisparo(float percentage) {
        intervaloDisparo *= (1 - percentage);
        if (intervaloDisparo < 0.1f) {
            intervaloDisparo = 0.1f;
        }
    }
    public void aumentarProyectilesPorDisparo(int amount) {
        proyectilesPorDisparo += amount;
    }

    // Getters y Setters
    public float getVidaJugador() {
        return vidaJugador;
    }
    public float getMaxVidaJugador() {
        return maxVidaJugador;
    }

    public boolean estaMuerto() {
        return estaMuerto;
    }
    public Sprite getSprite() {
        return sprite;
    }
    public void estableceControladorEnemigos(ControladorEnemigos controladorEnemigos) {
        this.controladorEnemigos = controladorEnemigos;
    }
}

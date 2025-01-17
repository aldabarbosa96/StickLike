package com.sticklike.core.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.logics.actions.Colisiones;
import com.sticklike.core.logics.inputs.InputsJugador;
import com.sticklike.core.logics.inputs.InputsJugador.Direction;
import com.sticklike.core.logics.actions.DesplazamientoJugador;
import com.sticklike.core.logics.actions.AtaqueJugador;   // <-- Importamos la nueva clase de ataque

import com.sticklike.core.managers.ControladorEnemigos;
import com.sticklike.core.managers.ControladorProyectiles;
import com.sticklike.core.utils.GestorDeAssets;
import com.sticklike.core.utils.GestorConstantes;

/**
 * La clase Jugador representa al personaje controlado por el jugador.
 * Delegamos:
 * - Input en {@link InputsJugador}
 * - Movimiento en {@link DesplazamientoJugador}
 * - Ataque en {@link AtaqueJugador}
 */
public class Jugador {

    private Sprite sprite;
    private ControladorEnemigos controladorEnemigos;
    private ControladorProyectiles controladorProyectiles;
    private InputsJugador inputController;
    private DesplazamientoJugador movimientoController;
    private AtaqueJugador ataqueController;
    private DesplazamientoJugador desplazamientoJugador;
    private Colisiones colisiones;

    // Atributos de stats
    private float velocidadJugador;
    private float vidaJugador;
    private float maxVidaJugador;
    private float rangoAtaqueJugador;
    private float intervaloDisparo;
    public float temporizadorDisparo = 0;
    private int proyectilesPorDisparo = 1;
    private boolean estaMuerto;
    private Direction direccionActual = Direction.IDLE;

    // Animaciones
    private Animation<TextureRegion> animacionMovDerecha;
    private Animation<TextureRegion> animacionMovIzquierda;
    private Animation<TextureRegion> animacionIdle;
    private float temporizadorAnimacion = 0;

    /**
     * Constructor principal del Jugador.
     */
    public Jugador(float startX, float startY, InputsJugador inputController) {
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

        this.inputController = inputController;
        this.desplazamientoJugador = new DesplazamientoJugador();
        this.colisiones = new Colisiones();

        // Instanciamos la lógica de movimiento y ataque
        this.movimientoController = new DesplazamientoJugador();
        this.ataqueController = new AtaqueJugador();

        // Cargamos animaciones
        animacionIdle = GestorDeAssets.animations.get("idle");
        animacionMovDerecha = GestorDeAssets.animations.get("moveRight");
        animacionMovIzquierda = GestorDeAssets.animations.get("moveLeft");
    }

    /**
     * Actualiza la lógica del jugador: mover, disparar y colisiones
     */
    public void actualizarJugador(float delta, boolean paused, Array<TextoFlotante> dmgText) {
        if (estaMuerto) return;

        if (!paused) {
            inputController.procesarInputYMovimiento(delta, desplazamientoJugador, this);
            ataqueController.manejarDisparo(delta, this);
            verificarColisionesConEnemigos();
        } else {
            direccionActual = Direction.IDLE;
        }

        temporizadorAnimacion += delta; // Actualiza la animación
        controladorProyectiles.actualizarProyectiles(delta, (controladorEnemigos != null ? controladorEnemigos.getEnemigos() : null), dmgText);
    }

    /**
     * Verifica colisiones con los enemigos y aplica daño si es necesario.
     */
    private void verificarColisionesConEnemigos() {
        if (controladorEnemigos != null) {
            for (Enemigo enemigo : controladorEnemigos.getEnemigos()) {
                if (movimientoController.enColision(enemigo, this) && enemigo.puedeAplicarDanyo()) {
                    colisiones.recibeDanyo(2,this);
                    enemigo.reseteaTemporizadorDanyo();
                }
            }
        }
    }

    public void renderizarJugador(SpriteBatch batch) {
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
            batch.draw(currentFrame, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        }
    }


    public void muere() {
        estaMuerto = true;
        System.out.println("GAME OVER (player died)");
    }

    public void dispose() {
        if (sprite != null && sprite.getTexture() != null) {
            sprite.getTexture().dispose();
        }
        if (controladorProyectiles != null) {
            controladorProyectiles.dispose();
        }
    }

    // -------------------------------------------------------------------
    // Getters / Setters y métodos de stats
    // -------------------------------------------------------------------
    public float obtenerPorcetajeVida() {
        return vidaJugador / maxVidaJugador;
    }

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

    // Atributos para la lógica de combate
    public float getRangoAtaqueJugador() {
        return rangoAtaqueJugador;
    }

    public int getProyectilesPorDisparo() {
        return proyectilesPorDisparo;
    }

    public float getVelocidadJugador() {
        return velocidadJugador;
    }

    public float getVidaJugador() {
        return vidaJugador;
    }

    public void restarVidaJugador(float vidaJugador) {
        this.vidaJugador -= vidaJugador;
    }

    public void setVidaJugador(float vidaJugador) {
        this.vidaJugador = vidaJugador;
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

    public ControladorEnemigos getControladorEnemigos() {
        return controladorEnemigos;
    }

    public ControladorProyectiles getControladorProyectiles() {
        return controladorProyectiles;
    }

    public void estableceControladorEnemigos(ControladorEnemigos controladorEnemigos) {
        this.controladorEnemigos = controladorEnemigos;
    }

    public void setDireccionActual(Direction direccionActual) {
        this.direccionActual = direccionActual;
    }


    public float getIntervaloDisparo() {
        return intervaloDisparo;
    }

    public float getTemporizadorDisparo() {
        return temporizadorDisparo;
    }

    public void setTemporizadorDisparo(float temporizadorDisparo) {
        this.temporizadorDisparo = temporizadorDisparo;
    }
}

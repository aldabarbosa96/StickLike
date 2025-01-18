package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.objetos.TextoFlotante;
import com.sticklike.core.entidades.jugador.InputsJugador.Direction;
import com.sticklike.core.gameplay.managers.ControladorEnemigos;
import com.sticklike.core.gameplay.managers.ControladorProyectiles;
import com.sticklike.core.utilidades.GestorDeAssets;
import com.sticklike.core.utilidades.GestorConstantes;

/**
 * La clase Jugador representa al personaje controlado por el jugador.
 * Delegamos:
 * - Input en {@link InputsJugador}
 * - Movimiento en {@link DesplazamientoJugador}
 * - Ataque en {@link AtaqueJugador}
 * - Colisiones en {@link ColisionesJugador}
 * - Renderizado en {@link RenderizarMovJugador}
 */
public class Jugador {

    private Sprite sprite;
    private ControladorEnemigos controladorEnemigos;
    private ControladorProyectiles controladorProyectiles;
    private InputsJugador inputController;
    private AtaqueJugador ataqueController;
    private DesplazamientoJugador desplazamientoJugador;
    private ColisionesJugador colisionesJugador;
    private RenderizarMovJugador renderizarMovJugador;

    // Atributos de stats
    private float velocidadJugador;
    private float vidaJugador;
    private float maxVidaJugador;
    private float rangoAtaqueJugador;
    private float intervaloDisparo;
    public float temporizadorDisparo = 0;
    private int proyectilesPorDisparo = 1;
    private boolean estaVivo;
    private Direction direccionActual = Direction.IDLE;

    /**
     * Constructor principal del Jugador.
     */
    public Jugador(float startX, float startY, InputsJugador inputController, ColisionesJugador colisionesJugador,
                   DesplazamientoJugador desplazamientoJugador, AtaqueJugador ataqueJugador,
                   ControladorProyectiles controladorProyectiles) {
        this.velocidadJugador = GestorConstantes.PLAYER_SPEED;
        this.vidaJugador = GestorConstantes.PLAYER_HEALTH;
        this.maxVidaJugador = GestorConstantes.PLAYER_MAX_HEALTH;
        this.rangoAtaqueJugador = GestorConstantes.PLAYER_ATTACK_RANGE;
        this.intervaloDisparo = GestorConstantes.PLAYER_SHOOT_INTERVAL;
        this.estaVivo = true;

        // Inicializar el sprite del jugador
        this.sprite = new Sprite(GestorDeAssets.stickman);
        this.sprite.setSize(15, 45);
        this.sprite.setPosition(startX, startY);

        // Inicializar controladores
        this.inputController = inputController;
        this.colisionesJugador = colisionesJugador;
        this.desplazamientoJugador = desplazamientoJugador;
        this.ataqueController = ataqueJugador;
        this.controladorProyectiles = controladorProyectiles;
        this.renderizarMovJugador = new RenderizarMovJugador();
    }

    /**
     * Actualiza la lógica del jugador: movimiento, disparo, colisiones
     */
    public void actualizarLogicaJugador(float delta, boolean paused, Array<TextoFlotante> dmgText) {
        if (!estaVivo) return;

        if (!paused) {
            inputController.procesarInputYMovimiento(delta, desplazamientoJugador, this);
            ataqueController.manejarDisparo(delta, this);
            colisionesJugador.verificarColisionesConEnemigos(controladorEnemigos, this);
        } else {
            direccionActual = Direction.IDLE;
        }
        renderizarMovJugador.actualizarAnimacion(delta);
        controladorProyectiles.actualizarProyectiles(delta,
            (controladorEnemigos != null ? controladorEnemigos.getEnemigos() : null), dmgText);
    }

    /**
     * Aplica el renderizado de las animaciones al jugador
     * @param batch sprite que representa al jugador
     */
    public void aplicarRenderizadoJugador(SpriteBatch batch) {
        renderizarMovJugador.setDireccionActual(direccionActual);
        renderizarMovJugador.renderizarJugador(batch, this);
    }

    public void muere() {
        estaVivo = false;
        System.out.println("GAME OVER (player died)");
        // todo --> implementar ventana de "vida extra" en un futuro
    }

    /**
     * Libera los recursos usados por el jugador.
     */
    public void dispose() {
        if (sprite != null && sprite.getTexture() != null) {
            sprite.getTexture().dispose();
        }
        if (controladorProyectiles != null) {
            controladorProyectiles.dispose();
        }
    }

    // -------------------------------------------------------------------
    // Getters / Setters y métodos relacionados con los atributos del jugador
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
        if (intervaloDisparo < 0.1f) intervaloDisparo = 0.1f;

    }
    public void aumentarProyectilesPorDisparo(int amount) {
        proyectilesPorDisparo += amount;
    }

    // Getters y Setters básicos
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

    public boolean estaVivo() {
        return !estaVivo;
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

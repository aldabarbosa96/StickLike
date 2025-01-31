package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.audio.ControladorAudio;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueCalcetin;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaquePiedra;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.entidades.jugador.InputsJugador.Direction;
import com.sticklike.core.gameplay.managers.ControladorEnemigos;
import com.sticklike.core.gameplay.managers.ControladorProyectiles;
import com.sticklike.core.utilidades.GestorDeAssets;
import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * La clase Jugador representa al personaje controlado por el jugador.
 * Delegamos:
 * - Input en {@link InputsJugador}
 * - Movimiento en {@link MovimientoJugador}
 * - Ataque en {@link AtaquePiedra}
 * - Colisiones en {@link ColisionesJugador}
 * - Renderizado en {@link RenderJugador}
 */
public class Jugador {

    private Sprite sprite;
    private ControladorEnemigos controladorEnemigos;
    private ControladorProyectiles controladorProyectiles;
    private InputsJugador inputController;
    private AtaquePiedra pedrada;
    private AtaqueCalcetin calcetinazo;
    private MovimientoJugador movimientoJugador;
    private ColisionesJugador colisionesJugador;
    private RenderJugador renderJugador;

    // Atributos de stats
    private float velocidadJugador;
    private float vidaJugador;
    private float maxVidaJugador;
    private float rangoAtaqueJugador;
    private float danyoAtaqueJugador;
    private float velocidadAtaque;
    private float intervaloDisparo;
    private int proyectilesPorDisparo;
    private float resistenciaJugador;
    private float criticoJugador;
    private boolean estaVivo;
    private float oroGanado;
    private Direction direccionActual = Direction.IDLE;

    public Jugador(float startX, float startY, InputsJugador inputController, ColisionesJugador colisionesJugador,
                   MovimientoJugador movimientoJugador, AtaquePiedra ataquePiedra,
                   ControladorProyectiles controladorProyectiles) {
        this.danyoAtaqueJugador = DANYO;
        this.velocidadJugador = VEL_MOV_JUGADOR;
        this.vidaJugador = VIDA_JUGADOR;
        this.maxVidaJugador = VIDAMAX_JUGADOR;
        this.rangoAtaqueJugador = RANGO_ATAQUE;
        this.intervaloDisparo = INTERVALO_DISPARO;
        this.velocidadAtaque = VEL_ATAQUE_JUGADOR;
        this.proyectilesPorDisparo = NUM_PROYECTILES_INICIALES;
        this.resistenciaJugador = RESISTENCIA;
        this.criticoJugador = CRITICO;
        this.estaVivo = true;
        this.oroGanado = 0;

        // Inicializar el sprite del jugador
        this.sprite = new Sprite(GestorDeAssets.stickman);
        this.sprite.setSize(WIDTH_JUGADOR, HEIGHT_JUGADOR);
        this.sprite.setPosition(startX, startY);

        // Inicializar controladores
        this.inputController = inputController;
        this.colisionesJugador = colisionesJugador;
        this.movimientoJugador = movimientoJugador;
        this.pedrada = ataquePiedra;
        this.calcetinazo = null;
        this.controladorProyectiles = controladorProyectiles;
        this.renderJugador = new RenderJugador();
    }

    /**
     * Actualiza la lógica del jugador: movimiento, disparo, colisiones
     */
    public void actualizarLogicaDelJugador(float delta, boolean paused, Array<TextoFlotante> dmgText, ControladorAudio controladorAudio) {
        if (!estaVivo) return;

        if (!paused) {
            inputController.procesarInputYMovimiento(delta, movimientoJugador, this);
            pedrada.manejarDisparo(delta, this, controladorAudio);
            if (calcetinazo != null) {
                calcetinazo.manejarDisparo(delta, this, controladorAudio);
            }
            colisionesJugador.verificarColisionesConEnemigos(controladorEnemigos, this, controladorAudio);
        } else {
            direccionActual = Direction.IDLE;
        }
        renderJugador.actualizarAnimacion(delta);
        controladorProyectiles.actualizarProyectiles(delta, (controladorEnemigos != null ? controladorEnemigos.getEnemigos() : null), dmgText);
    }

    /**
     * Aplica el renderizado de las animaciones al jugador
     *
     * @param batch sprite que representa al jugador
     */
    public void aplicarRenderizadoAlJugador(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // Renderizar animaciones del jugador
        renderJugador.setDireccionActual(direccionActual);
        renderJugador.renderizarJugador(batch, this);
        batch.end();

        renderJugador.renderizarBarraDeSalud(shapeRenderer,this);
        // Vuelve a comenzar el SpriteBatch para el resto de las texturas
        batch.begin();
    }



    public void muere() {
        estaVivo = false;
        System.out.println("GAME OVER (player died)");
        // todo --> implementar ventana de "vida extra" en un futuro

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
        danyoAtaqueJugador *= amount;
    }

    public void reducirIntervaloDisparo(float percentage) {
        intervaloDisparo *= (1 - percentage);
        if (intervaloDisparo < INTERVALO_MIN_DISPARO) intervaloDisparo = INTERVALO_MIN_DISPARO;

        velocidadAtaque = 1 / intervaloDisparo;
        pedrada.setIntervaloDisparo(intervaloDisparo);

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

    public float getDanyoAtaqueJugador() {
        return danyoAtaqueJugador;
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

    public void setCalcetinazo(AtaqueCalcetin calcetinazo) {
        this.calcetinazo = calcetinazo;
    }

    public void setDireccionActual(Direction direccionActual) {
        this.direccionActual = direccionActual;
    }

    public void setVidaMax(float maxVidaJugador) {
        this.maxVidaJugador = maxVidaJugador;
    }

    public RenderJugador getAnimacionesJugador() {
        return renderJugador;
    }

    public float getVelocidadAtaque() {
        return velocidadAtaque;
    }

    public float getIntervaloDisparo() {
        return intervaloDisparo;
    }

    public float getResistenciaJugador() {
        return resistenciaJugador;
    }

    public void setResistenciaJugador(float resistenciaJugador) {
        this.resistenciaJugador = resistenciaJugador;
    }

    public float getCriticoJugador() {
        return criticoJugador;
    }

    public void setCriticoJugador(float criticoJugador) {
        this.criticoJugador = criticoJugador;
    }

    public float getOroGanado() {
        return oroGanado;
    }

    public void setOroGanado(float oroGanado) {
        this.oroGanado = oroGanado;
    }
}

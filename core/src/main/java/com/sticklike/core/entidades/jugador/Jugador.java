package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.utilidades.GestorDeAudio;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueCalcetin;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueNubePedo;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaqueTazo;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.AtaquePiedra;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.entidades.jugador.InputsJugador.Direction;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;

import static com.sticklike.core.utilidades.GestorDeAssets.*;

import static com.sticklike.core.utilidades.GestorConstantes.*;

/**
 * Representa al jugador en el juego. Contiene sus atributos, ataques, estado y delega la gestión del movimiento,
 * colisiones y animaciones a clases especializadas.
 */

public class Jugador {
    private Sprite sprite;
    private ControladorEnemigos controladorEnemigos;
    private ControladorProyectiles controladorProyectiles;
    private InputsJugador inputController;
    private AtaquePiedra pedrada;
    private AtaqueCalcetin calcetinazo;
    private AtaqueTazo ataqueTazo;
    private AtaqueNubePedo ataqueNubePedo;
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
    private float regVidaJugador;
    private float poderJugador;
    private boolean estaVivo;
    private int oroGanado;
    private Direction direccionActual = Direction.IDLE;

    public Jugador(float startX, float startY, InputsJugador inputController, ColisionesJugador colisionesJugador,
                   MovimientoJugador movimientoJugador, AtaquePiedra ataquePiedra, ControladorProyectiles controladorProyectiles) {
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
        this.regVidaJugador = REGENERACION_VIDA;
        this.poderJugador = PODER_JUGADOR;
        this.estaVivo = true;
        this.oroGanado = 0;

        // Inicializar el sprite del jugador
        this.sprite = new Sprite(manager.get(STICKMAN, Texture.class));
        this.sprite.setSize(WIDTH_JUGADOR, HEIGHT_JUGADOR);
        this.sprite.setPosition(startX, startY);

        // Inicializar controladores
        this.inputController = inputController;
        this.colisionesJugador = colisionesJugador;
        this.movimientoJugador = movimientoJugador;
        this.pedrada = ataquePiedra;
        this.calcetinazo = null;
        this.ataqueTazo = null;
        this.ataqueNubePedo = null;
        this.controladorProyectiles = controladorProyectiles;
        this.renderJugador = new RenderJugador();
    }


    public void actualizarLogicaDelJugador(float delta, boolean paused, Array<TextoFlotante> dmgText, GestorDeAudio gestorDeAudio) {
        if (!estaVivo) return;

        if (!paused) {
            regenerarVida(delta);
            inputController.procesarInputYMovimiento(delta, movimientoJugador, this);
            pedrada.manejarDisparo(delta, this, gestorDeAudio);

            if (calcetinazo != null) {
                calcetinazo.manejarDisparo(delta, this, gestorDeAudio);
            }
            if (ataqueTazo != null) {
                ataqueTazo.actualizar(delta, this, gestorDeAudio);
            }
            if (ataqueNubePedo != null) {
                ataqueNubePedo.procesarAtaque(delta);
            }

            colisionesJugador.verificarColisionesConEnemigos(controladorEnemigos, this, gestorDeAudio);
        } else {
            direccionActual = Direction.IDLE;
        }
        renderJugador.actualizarAnimacion(delta);
        controladorProyectiles.actualizarProyectiles(delta, (controladorEnemigos != null ? controladorEnemigos.getEnemigos() : null), dmgText);
    }


    public void aplicarRenderizadoAlJugador(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // Renderizar animaciones del jugador
        renderJugador.setDireccionActual(direccionActual);
        renderJugador.renderizarJugador(batch, this);
        batch.end();

        renderJugador.renderizarBarraDeSalud(shapeRenderer, this);
        // Vuelve a comenzar el SpriteBatch para el resto de las texturas
        batch.begin();
    }


    public void muere() {
        estaVivo = false;
        // todo --> valorar si se implementa ventana de "vida extra" en un futuro

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

    public void aumentarPoderJugador(float amount) {
        poderJugador *= amount;
    }

    public float getRangoAtaqueJugador() {
        return rangoAtaqueJugador;
    }

    public int getProyectilesPorDisparo() {
        return proyectilesPorDisparo;
    }

    public void aumentarRegVida(float percentage) {
        this.regVidaJugador += (this.maxVidaJugador * percentage / 100);
    }


    public void aumentarCritico(float percentage) {
        float criticalAmount = (percentage * criticoJugador) * 10;
        this.criticoJugador += criticalAmount;
    }

    public void aumentarResistencia(float percentage) {
        float resistenciaAmount = (percentage * resistenciaJugador) * 10;
        this.resistenciaJugador += resistenciaAmount;
    }

    public float getVelocidadJugador() {
        return velocidadJugador;
    }

    public float getVidaJugador() {
        if (vidaJugador < 0) vidaJugador = 0;
        else if (vidaJugador > maxVidaJugador) vidaJugador = maxVidaJugador;
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

    public boolean estaMuerto() {
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

    public void setTazo(AtaqueTazo tazo) {
        this.ataqueTazo = tazo;
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

    public float getPoderJugador() {
        return poderJugador;
    }

    public float getRegVidaJugador() {
        return regVidaJugador;
    }

    private void regenerarVida(float delta) {
        vidaJugador += maxVidaJugador * regVidaJugador * delta;
        if (vidaJugador > maxVidaJugador) vidaJugador = maxVidaJugador;
    }

    public float getCritico() {
        return criticoJugador;
    }

    public int getOroGanado() {
        return oroGanado;
    }

    public void setOroGanado(int oroGanado) {
        this.oroGanado = oroGanado;
    }

    public void setAtaqueNubePedo(AtaqueNubePedo ataqueNubePedo) {
        this.ataqueNubePedo = ataqueNubePedo;
    }

    public AtaqueCalcetin getAtaqueCalcetin() {
        return calcetinazo;
    }

    public AtaqueTazo getAtaqueTazo() {
        return ataqueTazo;
    }

    public AtaqueNubePedo getAtaqueNubePedo() {
        return ataqueNubePedo;
    }

    public RenderJugador getRenderJugador() {
        return renderJugador;
    }

}

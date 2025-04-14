package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.objetos.armas.proyectiles.comportamiento.*;
import com.sticklike.core.entidades.renderizado.RenderJugador;
import com.sticklike.core.entidades.renderizado.RenderParticulasSangre;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;
import com.sticklike.core.entidades.objetos.texto.TextoFlotante;
import com.sticklike.core.entidades.jugador.InputsJugador.Direction;
import com.sticklike.core.gameplay.controladores.ControladorEnemigos;
import com.sticklike.core.gameplay.controladores.ControladorProyectiles;

import static com.sticklike.core.utilidades.gestores.GestorDeAssets.*;
import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

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
    private AtaquePapelCulo ataquePapelCulo;
    private AtaqueMocos ataqueMocos;
    private AtaqueBoliBic ataqueBoliBic;
    private AtaqueDildo ataqueDildo;
    private MovimientoJugador movimientoJugador;
    private ColisionesJugador colisionesJugador;
    private RenderJugador renderJugador;
    private RenderParticulasSangre renderParticulasSangre;

    // Atributos de stats todo --> mover a clase modelo dedicada
    private static float velocidadJugador;
    private static float vidaJugador;
    private static float maxVidaJugador;
    private static float rangoAtaqueJugador;
    private static float danyoAtaqueJugador;
    private static float velocidadAtaque;
    private static float intervaloDisparo;
    private static int proyectilesPorDisparo;
    private static float resistenciaJugador;
    private static float criticoJugador;
    private static float regVidaJugador;
    private static float poderJugador;
    private boolean estaVivo;
    private static int oroGanado;
    private static int trazosGanados;
    private boolean invulnerable = false;
    private Direction direccionActual = Direction.IDLE;

    public Jugador(float startX, float startY, InputsJugador inputController, ColisionesJugador colisionesJugador, MovimientoJugador movimientoJugador, AtaquePiedra ataquePiedra, ControladorProyectiles controladorProyectiles) {
        danyoAtaqueJugador = DANYO;
        velocidadJugador = VEL_MOV_JUGADOR;
        vidaJugador = VIDA_JUGADOR;
        maxVidaJugador = VIDAMAX_JUGADOR;
        rangoAtaqueJugador = RANGO_ATAQUE;
        intervaloDisparo = INTERVALO_DISPARO;
        velocidadAtaque = VEL_ATAQUE_JUGADOR;
        proyectilesPorDisparo = NUM_PROYECTILES_INICIALES;
        resistenciaJugador = RESISTENCIA;
        criticoJugador = CRITICO;
        regVidaJugador = REGENERACION_VIDA;
        poderJugador = PODER_JUGADOR;
        this.estaVivo = true;
        oroGanado = 0;
        trazosGanados = 0;

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
        this.ataquePapelCulo = null;
        this.ataqueMocos = null;
        this.ataqueBoliBic = null;
        this.ataqueDildo = null;
        this.controladorProyectiles = controladorProyectiles;
        this.renderJugador = new RenderJugador();
        this.renderParticulasSangre = new RenderParticulasSangre();
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
            if (ataquePapelCulo != null) {
                ataquePapelCulo.manejarDisparo(delta, this, gestorDeAudio);
            }
            if (ataqueMocos != null) {
                ataqueMocos.manejarDisparo(delta, this, gestorDeAudio);
            }

            if (ataqueBoliBic != null) {
                ataqueBoliBic.manejarDisparo(delta, this, gestorDeAudio);
            }
            if (ataqueDildo != null){
                ataqueDildo.manejarDisparo(delta,this,gestorDeAudio);
            }

            colisionesJugador.verificarColisionesConEnemigos(controladorEnemigos, this, gestorDeAudio);
        } else {
            direccionActual = Direction.IDLE;
        }
        renderJugador.actualizarAnimacion(delta);
        controladorProyectiles.actualizarProyectiles(delta, (controladorEnemigos != null ? controladorEnemigos.getEnemigos() : null), dmgText);

        if (renderParticulasSangre != null) {
            renderParticulasSangre.update(delta);
        }
    }


    public void aplicarRenderizadoAlJugador(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // Renderizar animaciones del jugador
        renderJugador.setDireccionActual(direccionActual);
        renderJugador.renderizarJugador(batch, this);
        batch.end();

        renderJugador.renderizarBarraDeSalud(shapeRenderer, this);
        // Vuelve a comenzar el SpriteBatch para el resto de las texturas
        batch.begin();

        if (renderParticulasSangre != null) {
            renderParticulasSangre.render(batch);
        }
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
            controladorProyectiles = null;
        }
    }

    // -------------------------------------------------------------------
    // Getters / Setters y métodos relacionados con los atributos del jugador
    // -------------------------------------------------------------------
    public float obtenerPorcetajeVida() {
        return vidaJugador / maxVidaJugador;
    }

    public void aumentarVelocidad(float percentage) {
        velocidadJugador *= (1 + percentage);
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

    public static float getRangoAtaqueJugador() {
        return rangoAtaqueJugador;
    }

    public static int getProyectilesPorDisparo() {
        return proyectilesPorDisparo;
    }

    public void aumentarRegVida(float percentage) {
        this.regVidaJugador += (this.maxVidaJugador * percentage / 100);
    }


    public void aumentarCritico(float percentage) {
        this.criticoJugador += percentage;
    }

    public void aumentarResistencia(float percentage) {
        this.resistenciaJugador += percentage;
    }

    public static float getVelocidadJugador() {
        return velocidadJugador;
    }

    public static float getVidaJugador() {
        if (vidaJugador < 0) vidaJugador = 0;
        else if (vidaJugador > maxVidaJugador) vidaJugador = maxVidaJugador;
        return vidaJugador;
    }

    public static float getDanyoAtaqueJugador() {
        return danyoAtaqueJugador;
    }

    public void restarVidaJugador(float damage) {
        this.vidaJugador -= damage;
        if (this.vidaJugador <= 0) {
            muere();
        } else {
            if (renderParticulasSangre != null) {
                float cx = sprite.getX() + sprite.getWidth() / 2;
                float cy = sprite.getY() + sprite.getHeight() / 2;
                renderParticulasSangre.spawnBlood(new Vector2(cx, cy), 8);
            }
        }
    }

    public void setVidaJugador(float vidaJugador) {
        this.vidaJugador = vidaJugador;
    }

    public static float getMaxVidaJugador() {
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

    public static float getVelocidadAtaque() {
        return velocidadAtaque;
    }

    public AtaquePiedra getPedrada() {
        return pedrada;
    }

    public static float getIntervaloDisparo() {
        return intervaloDisparo;
    }

    public static float getResistenciaJugador() {
        return resistenciaJugador;
    }

    public static float getPoderJugador() {
        return poderJugador;
    }

    public static float getRegVidaJugador() {
        return regVidaJugador;
    }

    private void regenerarVida(float delta) {
        vidaJugador += maxVidaJugador * regVidaJugador * delta;
        if (vidaJugador > maxVidaJugador) vidaJugador = maxVidaJugador;
    }

    public static float getCritico() {
        return criticoJugador;
    }

    public static int getOroGanado() {
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

    public AtaqueMocos getAtaqueMocos() {
        return ataqueMocos;
    }

    public void setAtaqueMocos(AtaqueMocos ataqueMocos) {
        this.ataqueMocos = ataqueMocos;
    }

    public AtaqueBoliBic getAtaqueBoliBic() {
        return ataqueBoliBic;
    }

    public void setAtaqueBoliBic(AtaqueBoliBic ataqueBoliBic) {
        this.ataqueBoliBic = ataqueBoliBic;
    }

    public RenderJugador getRenderJugador() {
        return renderJugador;
    }


    public static int getTrazosGanados() {
        return trazosGanados;
    }

    public void setTrazosGanados(int trazosGanados) {
        this.trazosGanados = trazosGanados;
    }

    public void setPapelCulo(AtaquePapelCulo ataquePapelCulo) {
        this.ataquePapelCulo = ataquePapelCulo;
    }

    public void setResistenciaJugador(float resistenciaJugador) {
        this.resistenciaJugador = resistenciaJugador;
    }

    public void setVelocidadJugador(float velocidadJugador) {
        this.velocidadJugador = velocidadJugador;
    }

    public void setDanyoAtaqueJugador(float danyoAtaqueJugador) {
        this.danyoAtaqueJugador = danyoAtaqueJugador;
    }

    public void setProyectilesPorDisparo(int proyectilesPorDisparo) {
        this.proyectilesPorDisparo = proyectilesPorDisparo;
    }

    public AtaqueDildo getAtaqueDildo() {
        return ataqueDildo;
    }

    public void setAtaqueDildo(AtaqueDildo ataqueDildo) {
        this.ataqueDildo = ataqueDildo;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public void setIntervaloDisparo(float intervaloDisparo) {
        this.intervaloDisparo = intervaloDisparo;
    }

    public InputsJugador getInputController() {
        return inputController;
    }
}

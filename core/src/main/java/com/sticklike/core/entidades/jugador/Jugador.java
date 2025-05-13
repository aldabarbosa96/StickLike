package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.objetos.armas.comportamiento.*;
import com.sticklike.core.entidades.renderizado.RenderJugador;
import com.sticklike.core.entidades.renderizado.RenderParticulasSangre;
import com.sticklike.core.pantallas.menus.ventanas.MenuPersonaje;
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
    private AtaquePelota ataquePelota;
    private MovimientoJugador movimientoJugador;
    private ColisionesJugador colisionesJugador;
    private RenderJugador renderJugador;
    private RenderParticulasSangre renderParticulasSangre;
    private StatsJugador statsJugador;
    private MenuPersonaje menuPersonaje;
    private final Vector2 tmpVector = new Vector2();

    private boolean estaVivo;
    private static int oroGanado;
    private static int trazosGanados;
    private boolean invulnerable = false;
    private Direction direccionActual = Direction.IDLE;

    public Jugador(float startX, float startY, InputsJugador inputController, ColisionesJugador colisionesJugador, MovimientoJugador movimientoJugador, AtaquePiedra ataquePiedra, ControladorProyectiles controladorProyectiles, StatsJugador statsJugador) {
        this.statsJugador = statsJugador;
        this.estaVivo = true;
        oroGanado = 0;
        trazosGanados = 0;

        // Inicializar el sprite del jugador
        this.sprite = new Sprite(manager.get(STICKMAN, Texture.class));
        this.sprite.setSize(WIDTH_JUGADOR, HEIGHT_JUGADOR);
        this.sprite.setPosition(startX, startY);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

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
        this.ataquePelota = null;
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
            if (ataqueDildo != null) {
                ataqueDildo.manejarDisparo(delta, this, gestorDeAudio);
            }

            if (ataquePelota != null) {
                ataquePelota.manejarDisparo(delta, this, gestorDeAudio);
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
        return statsJugador.getVida() / statsJugador.getMaxVida();
    }

    public void aumentarVelocidad(float percentage) {
        statsJugador.setVelocidad(statsJugador.getVelocidad() * (1 + percentage));
    }

    public void aumentarRangoAtaque(float percentage) {
        statsJugador.setRangoAtaque(statsJugador.getRangoAtaque() * (1 + percentage));
    }

    public void aumentarDanyo(float amount) {
        controladorProyectiles.aumentarDanyoProyectil(amount);
        statsJugador.setDanyo(statsJugador.getDanyo() * amount);
    }

    public void reducirIntervaloDisparo(float percentage) {
        float nuevo = statsJugador.getIntervaloDisparo() * (1 - percentage);
        if (nuevo < INTERVALO_MIN_DISPARO) {
            nuevo = INTERVALO_MIN_DISPARO;
        }
        statsJugador.setIntervaloDisparo(nuevo);
        statsJugador.setVelocidadAtaque(1f / nuevo);
        pedrada.setIntervaloDisparo(nuevo);
    }

    public void aumentarProyectilesPorDisparo(int amount) {
        statsJugador.setProyectilesPorDisparo(statsJugador.getProyectilesPorDisparo() + amount);
    }

    public void aumentarPoderJugador(float amount) {
        statsJugador.setPoder(statsJugador.getPoder() * amount);
    }

    public float getRangoAtaqueJugador() {
        return statsJugador.getRangoAtaque();
    }

    public int getProyectilesPorDisparo() {
        return statsJugador.getProyectilesPorDisparo();
    }

    public void aumentarRegVida(float percentage) {
        float incremento = statsJugador.getMaxVida() * percentage / 100f;
        statsJugador.setRegVida(statsJugador.getRegVida() + incremento);
    }

    public void aumentarCritico(float percentage) {
        statsJugador.setCritico(statsJugador.getCritico() + percentage);
    }

    public void aumentarResistencia(float percentage) {
        statsJugador.setResistencia(statsJugador.getResistencia() + percentage);
    }

    public float getVelocidadJugador() {
        return statsJugador.getVelocidad();
    }

    public float getVidaJugador() {
        float v = statsJugador.getVida();
        if (v < 0) v = 0;
        else if (v > statsJugador.getMaxVida()) v = statsJugador.getMaxVida();
        return v;
    }

    public float getDanyoAtaqueJugador() {
        return statsJugador.getDanyo();
    }

    public void restarVidaJugador(float damage) {
        statsJugador.setVida(statsJugador.getVida() - damage);
        if (statsJugador.getVida() <= 0) {
            muere();
        } else {
            if (renderParticulasSangre != null) {
                float cx = sprite.getX() + sprite.getWidth() / 2;
                float cy = sprite.getY() + sprite.getHeight() / 2;
                tmpVector.set(cx, cy);
                renderParticulasSangre.spawnBlood(tmpVector, 8);

            }
        }
    }

    public void setVidaJugador(float vidaJugador) {
        statsJugador.setVida(vidaJugador);
    }

    public float getMaxVidaJugador() {
        return statsJugador.getMaxVida();
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
        statsJugador.setMaxVida(maxVidaJugador);
    }

    public RenderJugador getAnimacionesJugador() {
        return renderJugador;
    }

    public float getVelocidadAtaque() {
        return statsJugador.getVelocidadAtaque();
    }

    public AtaquePiedra getPedrada() {
        return pedrada;
    }

    public float getIntervaloDisparo() {
        return statsJugador.getIntervaloDisparo();
    }

    public float getResistenciaJugador() {
        return statsJugador.getResistencia();
    }

    public float getPoderJugador() {
        return statsJugador.getPoder();
    }

    public float getRegVidaJugador() {
        return statsJugador.getRegVida();
    }

    private void regenerarVida(float delta) {
        float nuevaVida = statsJugador.getVida() + statsJugador.getMaxVida() * statsJugador.getRegVida() * delta;
        statsJugador.setVida(Math.min(nuevaVida, statsJugador.getMaxVida()));
    }

    public float getCritico() {
        return statsJugador.getCritico();
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
        statsJugador.setResistencia(resistenciaJugador);
    }

    public void setVelocidadJugador(float velocidadJugador) {
        statsJugador.setVelocidad(velocidadJugador);
    }

    public void setDanyoAtaqueJugador(float danyoAtaqueJugador) {
        statsJugador.setDanyo(danyoAtaqueJugador);
    }

    public void setProyectilesPorDisparo(int proyectilesPorDisparo) {
        statsJugador.setProyectilesPorDisparo(proyectilesPorDisparo);
    }

    public AtaquePapelCulo getAtaquePapelCulo() {
        return ataquePapelCulo;
    }

    public AtaqueDildo getAtaqueDildo() {
        return ataqueDildo;
    }

    public void setAtaqueDildo(AtaqueDildo ataqueDildo) {
        this.ataqueDildo = ataqueDildo;
    }

    public AtaquePelota getAtaquePelota() {
        return ataquePelota;
    }

    public void setAtaquePelota(AtaquePelota ataquePelota) {
        this.ataquePelota = ataquePelota;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public void setIntervaloDisparo(float intervaloDisparo) {
        statsJugador.setIntervaloDisparo(intervaloDisparo);
    }

    public InputsJugador getInputController() {
        return inputController;
    }

    public boolean consumirOro(int oroARestar) {
        if (oroGanado >= oroARestar) {
            oroGanado -= oroARestar;
            return true;
        } else return false;

    }

    public boolean consumirTrazos(int trazosARestar) {
        if (trazosGanados >= trazosARestar) {
            trazosGanados -= trazosARestar;
            return true;
        } else return false;
    }
}

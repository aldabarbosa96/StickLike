package com.sticklike.core.entidades.jugador;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sticklike.core.entidades.objetos.armas.jugador.comportamiento.*;
import com.sticklike.core.entidades.renderizado.RenderJugador;
import com.sticklike.core.entidades.renderizado.RenderParticulasSangre;
import com.sticklike.core.entidades.renderizado.particulas.ParticleManager;
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
    private _00AtaquePiedra pedrada;
    private _01AtaqueCalcetin calcetinazo;
    private _03AtaqueTazo a03AtaqueTazo;
    private _02AtaqueNubePedo a02AtaqueNubePedo;
    private _04AtaquePapelCulo a04AtaquePapelCulo;
    private _07AtaquePipi a07AtaquePipi;
    private _05AtaqueBoliBic a05AtaqueBoliBic;
    private _06AtaqueDildo a06AtaqueDildo;
    private _08AtaquePelota a08AtaquePelota;
    private MovimientoJugador movimientoJugador;
    private ColisionesJugador colisionesJugador;
    private RenderJugador renderJugador;
    private RenderParticulasSangre renderParticulasSangre;
    private StatsJugador statsJugador;
    private final Vector2 tmpVector = new Vector2();
    private final Vector2 tmpPos = new Vector2();
    private ParticleEffectPool.PooledEffect efecto;

    private boolean estaVivo;
    private static int oroGanado;
    private static int trazosGanados;
    private boolean invulnerable = false;
    private Direction direccionActual = Direction.IDLE;

    public Jugador(float startX, float startY, InputsJugador inputController, ColisionesJugador colisionesJugador, MovimientoJugador movimientoJugador, _00AtaquePiedra a00AtaquePiedra, ControladorProyectiles controladorProyectiles, StatsJugador statsJugador) {
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
        this.pedrada = a00AtaquePiedra;
        this.calcetinazo = null;
        this.a03AtaqueTazo = null;
        this.a02AtaqueNubePedo = null;
        this.a04AtaquePapelCulo = null;
        this.a07AtaquePipi = null;
        this.a05AtaqueBoliBic = null;
        this.a06AtaqueDildo = null;
        this.a08AtaquePelota = null;
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
            if (a03AtaqueTazo != null) {
                a03AtaqueTazo.actualizar(delta, this, gestorDeAudio);
            }
            if (a02AtaqueNubePedo != null) {
                a02AtaqueNubePedo.procesarAtaque(delta);
            }
            if (a04AtaquePapelCulo != null) {
                a04AtaquePapelCulo.manejarDisparo(delta, this, gestorDeAudio);
            }
            if (a07AtaquePipi != null) {
                a07AtaquePipi.manejarDisparo(delta, this, gestorDeAudio);
            }

            if (a05AtaqueBoliBic != null) {
                a05AtaqueBoliBic.manejarDisparo(delta, this, gestorDeAudio);
            }
            if (a06AtaqueDildo != null) {
                a06AtaqueDildo.manejarDisparo(delta, this, gestorDeAudio);
            }

            if (a08AtaquePelota != null) {
                a08AtaquePelota.manejarDisparo(delta, this, gestorDeAudio);
            }

            colisionesJugador.verificarColisionesConEnemigos(controladorEnemigos, this, gestorDeAudio);
        } else {
            direccionActual = Direction.IDLE;
        }
        renderJugador.actualizarAnimacion(delta);
        controladorProyectiles.actualizarProyectiles(delta, (controladorEnemigos != null ? controladorEnemigos.getEnemigos() : null), dmgText);

        if (efecto != null) {
            if (!efecto.isComplete()) {
                float cx = sprite.getX() + sprite.getWidth() / 2;
                float cy = sprite.getY() + sprite.getHeight() / 2;
                efecto.setPosition(cx, cy);
            } else {
                efecto = null;
            }
        }

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
                efecto = ParticleManager.get().obtainEffect("sangre",cx,cy, true);
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

    public void setCalcetinazo(_01AtaqueCalcetin calcetinazo) {
        this.calcetinazo = calcetinazo;
    }

    public void setTazo(_03AtaqueTazo tazo) {
        this.a03AtaqueTazo = tazo;
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

    public _00AtaquePiedra getPedrada() {
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

    public void setAtaqueNubePedo(_02AtaqueNubePedo a02AtaqueNubePedo) {
        this.a02AtaqueNubePedo = a02AtaqueNubePedo;
    }

    public _01AtaqueCalcetin getAtaqueCalcetin() {
        return calcetinazo;
    }

    public _03AtaqueTazo getAtaqueTazo() {
        return a03AtaqueTazo;
    }

    public _02AtaqueNubePedo getAtaqueNubePedo() {
        return a02AtaqueNubePedo;
    }

    public _07AtaquePipi getAtaquePipi() {
        return a07AtaquePipi;
    }

    public void setAtaquePipi(_07AtaquePipi a07AtaquePipi) {
        this.a07AtaquePipi = a07AtaquePipi;
    }

    public _05AtaqueBoliBic getAtaqueBoliBic() {
        return a05AtaqueBoliBic;
    }

    public void setAtaqueBoliBic(_05AtaqueBoliBic a05AtaqueBoliBic) {
        this.a05AtaqueBoliBic = a05AtaqueBoliBic;
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

    public void setPapelCulo(_04AtaquePapelCulo a04AtaquePapelCulo) {
        this.a04AtaquePapelCulo = a04AtaquePapelCulo;
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

    public _04AtaquePapelCulo getAtaquePapelCulo() {
        return a04AtaquePapelCulo;
    }

    public _06AtaqueDildo getAtaqueDildo() {
        return a06AtaqueDildo;
    }

    public void setAtaqueDildo(_06AtaqueDildo a06AtaqueDildo) {
        this.a06AtaqueDildo = a06AtaqueDildo;
    }

    public _08AtaquePelota getAtaquePelota() {
        return a08AtaquePelota;
    }

    public void setAtaquePelota(_08AtaquePelota a08AtaquePelota) {
        this.a08AtaquePelota = a08AtaquePelota;
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

    public Vector2 getPosicionTmp() {
        tmpPos.set(sprite.getX(), sprite.getY());
        return tmpPos;
    }

    public ColisionesJugador getColisionesJugador() {
        return colisionesJugador;
    }
}

package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.pantallas.overlay.BoostIconEffectManager;
import com.sticklike.core.utilidades.GestorDeAudio;

import static com.sticklike.core.utilidades.GestorConstantes.*;

public class Boost extends ObjetoBase {

    private Texture texture;
    private float duracion;
    private int amount;
    private BoostType tipo;
    private float tiempoRestante;
    private boolean aplicado;
    private float valorOriginal;
    private boolean collected;
    private int totalBonus; // Acumulador del bonus aplicado

    public Boost(Texture texture, float duracion, BoostType tipo, float x, float y) {
        super(x, y, texture);
        setSpriteTexture(texture);
        this.texture = texture;
        this.duracion = duracion;
        this.tipo = tipo;
        this.amount = obtenerCantidadBase(tipo);
        this.tiempoRestante = duracion;
        this.aplicado = false;
        this.collected = false;
        this.totalBonus = 0;
    }

    private static int obtenerCantidadBase(BoostType tipo) {
        switch (tipo) {
            case VELOCIDAD:
                return 75;
            case ATAQUE:
                return 666;
            case MUNICION:
                return 10;
            case INVULNERABILIDAD:
                return 5;
            default:
                return 4;
        }
    }

    public void aplicarBoost(Jugador jugador, GestorDeAudio gestorDeAudio) {
        if (!aplicado) {
            // Primera aplicación: almacenar el valor original y fijar el nuevo valor sumando el bonus
            switch (tipo) {
                case VELOCIDAD:
                    valorOriginal = jugador.getVelocidadJugador();
                    totalBonus = amount;
                    jugador.setVelocidadJugador(valorOriginal + amount);
                    break;
                case ATAQUE:
                    valorOriginal = jugador.getDanyoAtaqueJugador();
                    totalBonus = amount;
                    // Fijar el nuevo daño como el original + bonus
                    jugador.setDanyoAtaqueJugador(valorOriginal + amount);
                    break;
                case MUNICION:
                    valorOriginal = jugador.getProyectilesPorDisparo();
                    totalBonus = amount;
                    jugador.setProyectilesPorDisparo((int) (valorOriginal + amount));
                    break;
                case INVULNERABILIDAD:
                    valorOriginal = jugador.getResistenciaJugador();
                    totalBonus = amount;
                    jugador.aumentarResistencia(amount);
                    break;
                default:
                    break;
            }
            aplicado = true;
            collected = true;
            recolectar(gestorDeAudio);
        } else {
            // Si ya está activo, acumulamos el bonus y extendemos el tiempo
            switch (tipo) {
                case VELOCIDAD:
                    totalBonus += amount;
                    jugador.setVelocidadJugador(jugador.getVelocidadJugador() + amount);
                    break;
                case ATAQUE:
                    totalBonus += amount;
                    jugador.setDanyoAtaqueJugador(jugador.getDanyoAtaqueJugador() + amount);
                    break;
                case MUNICION:
                    totalBonus += amount;
                    jugador.setProyectilesPorDisparo(jugador.getProyectilesPorDisparo() + amount);
                    break;
                case INVULNERABILIDAD:
                    totalBonus += amount;
                    jugador.aumentarResistencia(amount);
                    break;
                default:
                    break;
            }
            tiempoRestante += duracion;
            // Acumular tiempo en el efecto visual
            BoostIconEffectManager.getInstance().getEffect().addTime(duracion);
        }
    }

    public void update(float delta, Jugador jugador) {
        if (!aplicado) return;
        tiempoRestante -= delta;
        if (tiempoRestante <= 0) {
            revertirBoost(jugador);
        }
    }

    public void revertirBoost(Jugador jugador) {
        if (!aplicado) return;
        // Al revertir, restamos únicamente el bonus acumulado, permitiendo conservar upgrades permanentes.
        switch (tipo) {
            case VELOCIDAD:
                jugador.setVelocidadJugador(jugador.getVelocidadJugador() - totalBonus);
                break;
            case ATAQUE:
                jugador.setDanyoAtaqueJugador(jugador.getDanyoAtaqueJugador() - totalBonus);
                break;
            case MUNICION:
                jugador.setProyectilesPorDisparo(jugador.getProyectilesPorDisparo() - totalBonus);
                break;
            case INVULNERABILIDAD:
                jugador.setResistenciaJugador(jugador.getResistenciaJugador() - totalBonus);
                break;
            default:
                break;
        }
        aplicado = false;
    }

    public boolean isActivo() {
        return aplicado && tiempoRestante > 0;
    }

    public boolean isCollected() {
        return collected;
    }

    @Override
    public void actualizarObjetoXP(float delta, Jugador jugador, GestorDeAudio gestorDeAudio) {
        if (aplicado) {
            update(delta, jugador);
            return;
        }
        super.actualizarObjetoXP(delta, jugador, gestorDeAudio);
        if (colisionaConOtroSprite(jugador.getSprite())) {
            aplicarBoost(jugador, gestorDeAudio);
        }
    }

    @Override
    public void renderizarObjetoXP(SpriteBatch batch) {
        if (!collected) {
            super.renderizarObjetoXP(batch);
        }
    }

    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) {
        float iconX = (float) VentanaJuego1.worldWidth / 2;
        float desiredIconSize = 40f;
        float iconY = 155 + (desiredIconSize / 2f);

        switch (tipo) {
            case VELOCIDAD:
                gestorDeAudio.reproducirEfecto("boostVel", 0.75f);
                BoostIconEffectManager.getInstance().getEffect().activate(
                    texture, new Color(1, 1, 0, 1f), duracion, iconX, iconY, desiredIconSize);
                break;
            case ATAQUE:
                gestorDeAudio.reproducirEfecto("boostAttack", 0.75f);
                BoostIconEffectManager.getInstance().getEffect().activate(
                    texture, new Color(1f, 0f, 1f, 1f), duracion, iconX, iconY, desiredIconSize);
                break;
            case MUNICION:
                gestorDeAudio.reproducirEfecto("boostAmo", 0.75f);
                BoostIconEffectManager.getInstance().getEffect().activate(
                    texture, new Color(0.2f, 1f, 0.2f, 1f), duracion, iconX, iconY, desiredIconSize);
                break;
            case INVULNERABILIDAD:
                gestorDeAudio.reproducirEfecto("boostRes", 0.75f);
                BoostIconEffectManager.getInstance().getEffect().activate(
                    texture, new Color(0.5f, 0.5f, 1f, 1f), duracion, iconX, iconY, desiredIconSize);
                break;
        }

        super.recolectar(gestorDeAudio);
    }

    @Override
    protected Texture getTexture() {
        return texture;
    }

    @Override
    protected float getWidth() {
        return ANCHO_BOOST;
    }

    @Override
    protected float getHeight() {
        return ALTO_BOOST;
    }

    public enum BoostType {
        VELOCIDAD, ATAQUE, MUNICION, INVULNERABILIDAD,
    }

    public float getDuracion() {
        return duracion;
    }

    public int getAmount() {
        return amount;
    }

    public BoostType getTipo() {
        return tipo;
    }
}

package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.pantallas.juego.VentanaJuego1;
import com.sticklike.core.pantallas.overlay.BoostIconEffectManager;
import com.sticklike.core.ui.RenderHUDComponents;
import com.sticklike.core.utilidades.gestores.GestorDeAudio;

import static com.sticklike.core.utilidades.gestores.GestorConstantes.*;

public class Boost extends ObjetoBase {

    private RenderHUDComponents renderHUDComponents;
    private Texture texture;
    private float duracion;
    private int amount;
    private BoostType tipo;
    private float tiempoRestante;
    private boolean aplicado;
    private float valorOriginal;
    private float valorBaseConMejora;
    private boolean collected;
    private float totalBonus;
    private float originalMultiplier;

    public Boost(Texture texture, float duracion, BoostType tipo, float x, float y, RenderHUDComponents renderHUDComponents) {
        super(x, y, texture);
        setSpriteTexture(texture);
        this.renderHUDComponents = renderHUDComponents;
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
            case VELATAQUE:
                return 0;
            default:
                return 4;
        }
    }

    public void aplicarBoost(Jugador jugador, GestorDeAudio gestorDeAudio) {
        if (!aplicado) {
            switch (tipo) {
                case VELOCIDAD:
                    valorOriginal = jugador.getVelocidadJugador();
                    valorBaseConMejora = valorOriginal;
                    totalBonus = amount;
                    jugador.setVelocidadJugador(valorBaseConMejora + totalBonus);
                    renderHUDComponents.setStatBoosted("Velocidad", true);
                    break;
                case ATAQUE:
                    valorOriginal = jugador.getDanyoAtaqueJugador();
                    valorBaseConMejora = valorOriginal;
                    totalBonus = amount;
                    jugador.setDanyoAtaqueJugador(valorBaseConMejora + totalBonus);
                    originalMultiplier = jugador.getControladorProyectiles().getMultiplicadorDeDanyo();
                    float upgradeFactor = valorBaseConMejora / valorOriginal;
                    jugador.getControladorProyectiles().setMultiplicadorDeDanyo(originalMultiplier * upgradeFactor * ((valorBaseConMejora + totalBonus) / valorBaseConMejora));
                    renderHUDComponents.setStatBoosted("Fuerza", true);
                    break;
                case MUNICION:
                    valorOriginal = jugador.getProyectilesPorDisparo();
                    valorBaseConMejora = valorOriginal;
                    totalBonus = amount;
                    jugador.setProyectilesPorDisparo((int) (valorBaseConMejora + totalBonus));
                    renderHUDComponents.setStatBoosted("Munición", true);
                    break;
                case INVULNERABILIDAD:
                    valorOriginal = jugador.getResistenciaJugador();
                    valorBaseConMejora = valorOriginal;
                    totalBonus = amount;
                    jugador.aumentarResistencia(totalBonus);
                    renderHUDComponents.setStatBoosted("Resistencia", true);
                    jugador.setInvulnerable(true);
                    break;
                case VELATAQUE:
                    valorOriginal = jugador.getIntervaloDisparo();
                    valorBaseConMejora = valorOriginal;
                    totalBonus = valorBaseConMejora - INTERVALO_MIN_DISPARO;
                    jugador.getPedrada().setIntervaloDisparo(INTERVALO_MIN_DISPARO);
                    renderHUDComponents.setStatBoosted("Vel. Ataque", true);
                    break;
                default:
                    break;
            }
            aplicado = true;
            collected = true;
            recolectar(gestorDeAudio);
        } else {
            if (tipo != BoostType.VELATAQUE) {
                totalBonus += amount;
            }
            tiempoRestante += duracion;
            BoostIconEffectManager.getInstance().getEffect().addTime(duracion);
        }
    }

    public void update(float delta, Jugador jugador) {
        if (!aplicado) return;
        tiempoRestante -= delta;
        if (tiempoRestante <= 0) {
            revertirBoost(jugador);
            return;
        }
        switch (tipo) {
            case VELOCIDAD: {
                float m = jugador.getVelocidadJugador() / (valorOriginal + totalBonus);
                float nuevaBase = m * valorOriginal;
                if (nuevaBase > valorBaseConMejora) {
                    valorBaseConMejora = nuevaBase;
                }
                jugador.setVelocidadJugador(valorBaseConMejora + totalBonus);
                break;
            }


            case ATAQUE: {
                float currentDamage = jugador.getDanyoAtaqueJugador();
                // Calcula el factor m aplicado sobre (B + T)
                float m = currentDamage / (valorOriginal + totalBonus);
                // Corrige la base: quita el efecto multiplicativo sobre el bonus
                float correctedBase = currentDamage - totalBonus * m;
                if (correctedBase > valorBaseConMejora) {
                    valorBaseConMejora = correctedBase;
                }
                jugador.setDanyoAtaqueJugador(valorBaseConMejora + totalBonus);
                // Actualizamos el multiplicador en función de las mejoras permanentes:
                float upgradeFactor = valorBaseConMejora / valorOriginal;
                jugador.getControladorProyectiles().setMultiplicadorDeDanyo(
                    originalMultiplier * upgradeFactor * ((valorBaseConMejora + totalBonus) / valorBaseConMejora)
                );
                break;
            }

            case MUNICION: {
                int nuevaBase = (int) (jugador.getProyectilesPorDisparo() - totalBonus);
                if (nuevaBase > valorBaseConMejora) {
                    valorBaseConMejora = nuevaBase;
                }
                jugador.setProyectilesPorDisparo((int) (valorBaseConMejora + totalBonus));
                break;
            }
            case INVULNERABILIDAD: {
                float nuevaBase = jugador.getResistenciaJugador() - totalBonus;
                if (nuevaBase > valorBaseConMejora) {
                    valorBaseConMejora = nuevaBase;
                }
                jugador.setResistenciaJugador(valorBaseConMejora + totalBonus);
                break;
            }
            case VELATAQUE: {
                float currentBase = jugador.getIntervaloDisparo();
                if (currentBase < valorBaseConMejora) {
                    valorBaseConMejora = currentBase;
                    totalBonus = valorBaseConMejora - INTERVALO_MIN_DISPARO;
                }
                jugador.getPedrada().setIntervaloDisparo(INTERVALO_MIN_DISPARO);
                break;
            }

            default:
                break;
        }
    }

    public void revertirBoost(Jugador jugador) {
        if (!aplicado) return;
        switch (tipo) {
            case VELOCIDAD:
                jugador.setVelocidadJugador(valorBaseConMejora);
                renderHUDComponents.setStatBoosted("Velocidad", false);
                break;
            case ATAQUE:
                jugador.setDanyoAtaqueJugador(valorBaseConMejora);
                float upgradeFactor = valorBaseConMejora / valorOriginal;
                jugador.getControladorProyectiles().setMultiplicadorDeDanyo(originalMultiplier * upgradeFactor);
                renderHUDComponents.setStatBoosted("Fuerza", false);
                break;
            case MUNICION:
                jugador.setProyectilesPorDisparo((int) valorBaseConMejora);
                renderHUDComponents.setStatBoosted("Munición", false);
                break;
            case INVULNERABILIDAD:
                jugador.setResistenciaJugador(valorBaseConMejora);
                renderHUDComponents.setStatBoosted("Resistencia", false);
                jugador.setInvulnerable(false);
                break;
            case VELATAQUE:
                jugador.getPedrada().setIntervaloDisparo(valorBaseConMejora);
                renderHUDComponents.setStatBoosted("Vel. Ataque", false);
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
        float iconY = 200f;
        switch (tipo) {
            case VELOCIDAD:
                gestorDeAudio.reproducirEfecto("boostVel", 0.8f);
                BoostIconEffectManager.getInstance().getEffect().activate(texture, new Color(0.75f, 0.75f, 0, 1f), duracion, iconX, iconY, desiredIconSize);
                break;
            case ATAQUE:
                gestorDeAudio.reproducirEfecto("boostAttack", 0.8f);
                BoostIconEffectManager.getInstance().getEffect().activate(texture, new Color(0.75f, 0f, 0.75f, 1f), duracion, iconX, iconY, desiredIconSize);
                break;
            case MUNICION:
                gestorDeAudio.reproducirEfecto("boostAmo", 0.8f);
                BoostIconEffectManager.getInstance().getEffect().activate(texture, new Color(0f, 0.25f, 0f, 1f), duracion, iconX, iconY, desiredIconSize);
                break;
            case INVULNERABILIDAD:
                gestorDeAudio.reproducirEfecto("boostRes", 0.8f);
                BoostIconEffectManager.getInstance().getEffect().activate(texture, new Color(0.5f, 0.5f, 1f, 1f), duracion, iconX, iconY, desiredIconSize);
                break;
            case VELATAQUE:
                gestorDeAudio.reproducirEfecto("boostVelAttack", 0.8f);
                BoostIconEffectManager.getInstance().getEffect().activate(texture, new Color(0.5f, 0.5f, 0f, 1f), duracion, iconX, iconY, desiredIconSize);
                break;
            default:
                break;
        }
        super.recolectar(gestorDeAudio);
    }

    @Override
    public void dispose() {
        // no hace falta hacer dispose de las texturas aquí porque se encarga el assetManager todo --> testear a fondo
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
        VELOCIDAD, ATAQUE, MUNICION, INVULNERABILIDAD, VELATAQUE
    }

    public float getDuracion() {
        return duracion;
    }

    public int getAmount() {
        return amount;
    }
}

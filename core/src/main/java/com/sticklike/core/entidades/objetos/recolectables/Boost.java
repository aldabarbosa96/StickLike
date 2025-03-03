package com.sticklike.core.entidades.objetos.recolectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.sticklike.core.entidades.jugador.Jugador;
import com.sticklike.core.interfaces.ObjetosXP;
import com.sticklike.core.utilidades.GestorDeAudio;

public class Boost implements ObjetosXP {

    private Texture texture;
    private float duracion;
    private int amount;
    private BoostType tipo;
    private float tiempoRestante;
    private boolean aplicado;
    private float valorOriginal;
    private float x;
    private float y;
    private float width;
    private float height;
    private boolean collected;

    public Boost(Texture texture, float duracion, BoostType tipo, float x, float y) {
        this.texture = texture;
        this.duracion = duracion;
        this.tipo = tipo;
        this.amount = getDefaultAmount(tipo);
        this.tiempoRestante = duracion;
        this.aplicado = false;
        this.collected = false;
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
    }

    private static int getDefaultAmount(BoostType tipo) {
        switch (tipo) {
            case VELOCIDAD:
                return 75;
            case ATAQUE:
                return 50;
            case MUNICION:
                return 6;
            case INVULNERABILIDAD:
                return 5;
            default:
                return 4;
        }
    }

    public void aplicarBoost(Jugador jugador) {
        if (!aplicado) {
            switch (tipo) {
                case VELOCIDAD:
                    valorOriginal = jugador.getVelocidadJugador();
                    jugador.setVelocidadJugador(valorOriginal + amount);
                    break;
                case ATAQUE:
                    valorOriginal = jugador.getDanyoAtaqueJugador();
                    jugador.setDanyoAtaqueJugador(valorOriginal + amount);
                    break;
                case MUNICION:
                    valorOriginal = jugador.getProyectilesPorDisparo();
                    jugador.aumentarProyectilesPorDisparo((int) (valorOriginal + amount));
                    break;
                case INVULNERABILIDAD:
                    valorOriginal = jugador.getResistenciaJugador();
                    jugador.setResistenciaJugador(valorOriginal + amount);
                    break;
                default:
                    break;
            }
            aplicado = true;
            collected = true;
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
        switch (tipo) {
            case VELOCIDAD:
                jugador.setVelocidadJugador(valorOriginal);
                break;
            case ATAQUE:
                jugador.setDanyoAtaqueJugador(valorOriginal);
                break;
            case MUNICION:
                jugador.setProyectilesPorDisparo((int) valorOriginal);
                break;
            case INVULNERABILIDAD:
                jugador.setResistenciaJugador(valorOriginal);
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
        }
    }

    @Override
    public void renderizarObjetoXP(SpriteBatch batch) {
        if (!collected) {
            batch.draw(texture, x, y, width, height);
        }
    }

    @Override
    public void recolectar(GestorDeAudio gestorDeAudio) {
        // todo --> gestionar efectos de sonido (y efectos de pantalla; brillos, cambio de color, etc)
    }

    @Override
    public boolean colisionaConOtroSprite(com.badlogic.gdx.graphics.g2d.Sprite sprite) {
        Rectangle boostRect = new Rectangle(x, y, width, height);
        return boostRect.overlaps(sprite.getBoundingRectangle());
    }

    @Override
    public void dispose() {
        // No es necesario liberar la textura aquí; se gestiona desde el AssetManager
    }

    public enum BoostType {
        VELOCIDAD,
        ATAQUE,
        MUNICION,
        INVULNERABILIDAD,
    }

    public Texture getTexture() {
        return texture;
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
